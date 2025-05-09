package com.evginozan.medireminder.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.evginozan.medireminder.MainActivity
import com.evginozan.medireminder.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID_MEDICATION_REMINDER = "medication_reminder_channel"
        const val CHANNEL_ID_LOW_STOCK = "low_stock_channel"

        const val NOTIFICATION_ID_MEDICATION = 1001
        const val NOTIFICATION_ID_LOW_STOCK = 2001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // İlaç Hatırlatma Kanalı
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_MEDICATION_REMINDER,
                "İlaç Hatırlatıcı",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "İlaç alma zamanlarını hatırlatır"
                enableVibration(true)
            }

            // Düşük Stok Kanalı
            val lowStockChannel = NotificationChannel(
                CHANNEL_ID_LOW_STOCK,
                "Düşük Stok Uyarısı",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "İlaç stoğu azaldığında bildirim verir"
            }

            // Kanalları sisteme kaydet
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(lowStockChannel)
        }
    }

    fun showMedicationReminder(medicationName: String, dosageInfo: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_MEDICATION_REMINDER)
            .setSmallIcon(R.drawable.ic_medication)
            .setContentTitle("İlaç Hatırlatıcı")
            .setContentText("$medicationName alma zamanı geldi")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$medicationName alma zamanı geldi. $dosageInfo"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))

        notifyWithPermissionCheck(NOTIFICATION_ID_MEDICATION, builder.build())
    }

    fun showLowStockAlert(medicationName: String, remainingCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_LOW_STOCK)
            .setSmallIcon(R.drawable.ic_warning)
            .setContentTitle("İlaç Stoğu Az")
            .setContentText("$medicationName ilacından $remainingCount adet kaldı")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notifyWithPermissionCheck(NOTIFICATION_ID_LOW_STOCK, builder.build())
    }

    /**
     * İzin kontrolü yaparak bildirimi gönderen güvenli metot
     */
    private fun notifyWithPermissionCheck(notificationId: Int, notification: android.app.Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(context).notify(notificationId, notification)
            } else {
                logNotificationPermissionMissing()
            }
        } else {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }

    /**
     * Bildirim izni olmadığında log çıktısı verme (geliştirme amaçlı)
     */
    private fun logNotificationPermissionMissing() {
        android.util.Log.w(
            "NotificationHelper",
            "Notification permission is not granted. Notifications won't be shown."
        )
    }
}