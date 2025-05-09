package com.evginozan.medireminder.service

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.evginozan.medireminder.core.notification.NotificationHelper
import com.evginozan.medireminder.domain.model.MealRelation
import com.evginozan.medireminder.domain.model.Medicine
import com.evginozan.medireminder.domain.usecase.GetAllMedicinesUseCase
import com.evginozan.medireminder.domain.usecase.UpdateMedicineUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class MedicineTrackerWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent
{
    private val getAllMedicinesUseCase: GetAllMedicinesUseCase by inject()
    private val updateMedicineUseCase: UpdateMedicineUseCase by inject()
    private val notificationHelper = NotificationHelper(context)

    companion object {
        private const val LOW_STOCK_THRESHOLD = 6 // İlaç sayısı bu değerin altına düştüğünde uyarı ver
        const val WORK_NAME = "medicine_tracker_work"

        fun scheduleWork(context: Context) {
            // Her 15 dakikada bir çalışacak periyodik görev
            val workRequest = PeriodicWorkRequestBuilder<MedicineTrackerWorker>(
                15, TimeUnit.MINUTES
            )
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10, TimeUnit.MINUTES
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    override suspend fun doWork(): Result {
        try {
            // Tüm ilaçları getir
            val medicines = getAllMedicinesUseCase().first()
            val now = LocalDateTime.now()

            medicines.forEach { medicine ->
                // 1. İlaç alma zamanı kontrol et
                checkMedicationTime(medicine, now)

                // 2. Düşük stok durumunu kontrol et
                checkLowStock(medicine)
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private suspend fun checkMedicationTime(medicine: Medicine, now: LocalDateTime) {
        val currentTime = now.toLocalTime()
        val today = LocalDate.now()
        var doseTimesUpdated = false
        val updatedDoseTimes = medicine.doseTimes.map { doseTime ->
            // Doz zamanı kontrolü
            val doseTimeDateTime = LocalDateTime.of(today, doseTime.time)

            // Eğer bu doz zamanı bugün için alınmadıysa ve zaman geçmişse
            if (!doseTime.takenForToday && now.isAfter(doseTimeDateTime)) {
                // Bildirim gönderme
                val relationText = when (doseTime.relation) {
                    MealRelation.BEFORE_MEAL -> "Yemekten önce"
                    MealRelation.AFTER_MEAL -> "Yemekten sonra"
                    MealRelation.WITH_MEAL -> "Yemekle birlikte"
                    MealRelation.ANY -> ""
                }

                val dosageInfo = if (relationText.isNotEmpty()) "$relationText alınız" else ""

                notificationHelper.showMedicationReminder(
                    medicine.name,
                    dosageInfo
                )

                // Bu doz alındı olarak işaretle
                doseTimesUpdated = true
                doseTime.copy(takenForToday = true)
            } else {
                doseTime
            }
        }

        // Eğer herhangi bir doz zamanı güncellenmiş ve ilaç stoğu varsa
        if (doseTimesUpdated && medicine.totalCount > 0) {
            // İlaç sayısını azalt
            val updatedMedicine = medicine.copy(
                doseTimes = updatedDoseTimes,
                totalCount = medicine.totalCount - 1
            )
            updateMedicineUseCase(updatedMedicine)
        }

        // Günün sonunda (23:55'ten sonra), tüm doz zamanlarını sıfırla
        // Bu sayede yarın için hazır olur
        if (currentTime.isAfter(LocalTime.of(23, 55))) {
            val resetDoseTimes = medicine.doseTimes.map { it.copy(takenForToday = false) }
            val resetMedicine = medicine.copy(doseTimes = resetDoseTimes)
            updateMedicineUseCase(resetMedicine)
        }
    }

    private suspend fun checkLowStock(medicine: Medicine) {
        if (medicine.totalCount <= LOW_STOCK_THRESHOLD && medicine.totalCount > 0 && !medicine.lowStockNotificationSent) {
            notificationHelper.showLowStockAlert(
                medicine.name,
                medicine.totalCount
            )

            // Bildirim gönderildi durumunu güncelle
            val updatedMedicine = medicine.copy(lowStockNotificationSent = true)
            updateMedicineUseCase(updatedMedicine)
        } else if (medicine.totalCount > LOW_STOCK_THRESHOLD && medicine.lowStockNotificationSent) {
            // Stok yeterli seviyeye geldiğinde, bildirim durumunu sıfırla
            val updatedMedicine = medicine.copy(lowStockNotificationSent = false)
            updateMedicineUseCase(updatedMedicine)
        }
    }

    private fun isTimeWithinRange(currentTime: LocalTime, targetTime: LocalTime, minutesRange: Int): Boolean {
        val currentSeconds = currentTime.toSecondOfDay()
        val targetSeconds = targetTime.toSecondOfDay()
        val rangeInSeconds = minutesRange * 60

        return Math.abs(currentSeconds - targetSeconds) <= rangeInSeconds
    }
}