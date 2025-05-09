package com.evginozan.medireminder.domain.model

import java.time.LocalTime

data class Medicine(
    val id: Long = 0,
    val name: String,
    val totalCount: Int,
    val dailyDoseCount: Int,
    val doseTimes: List<DoseTime>,
    val imageUri: String? = null,
    val notes: String? = null,
    val lowStockNotificationSent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class DoseTime(
    val time: LocalTime,
    val relation: MealRelation = MealRelation.ANY,
    val takenForToday: Boolean = false // Bugün alınıp alınmadığını takip eder
)


enum class MealRelation {
    BEFORE_MEAL,
    AFTER_MEAL,
    WITH_MEAL,
    ANY
}