package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class NiaziDoctorNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_MONITORING_ID = "niazi_monitoring_channel"
        const val CHANNEL_ALERTS_ID = "niazi_alerts_channel"
        const val NOTIFICATION_MONITORING_ID = 1001
        const val NOTIFICATION_ALERT_ID = 1002
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val monitoringChannel = NotificationChannel(
                CHANNEL_MONITORING_ID,
                "Device Health Guard (نگہبان)",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows continuous phone monitoring status and battery health"
                setShowBadge(false)
            }

            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS_ID,
                "Diagnostic Warnings (تشخیصی انتباہات)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts for battery overheat, charging faults, and critical issues"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(monitoringChannel)
            notificationManager.createNotificationChannel(alertsChannel)
        }
    }

    fun buildMonitoringNotification(
        statusText: String = "سب ٹھیک چل رہا ہے • بیٹری اور چارجنگ مانیٹر ہو رہی ہے",
        batteryLevel: Int = 100,
        tempCelsius: Float = 32f
    ): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_MONITORING_ID)
            .setContentTitle("نیازی ڈاکٹر — AI مانیٹر فعال ہے")
            .setContentText("بیٹری: $batteryLevel% | درجہ حرارت: ${tempCelsius}°C")
            .setStyle(NotificationCompat.BigTextStyle().bigText(statusText))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    fun showAlertNotification(title: String, messageUrdu: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS_ID)
            .setContentTitle("نیازی ڈاکٹر: $title")
            .setContentText(messageUrdu)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageUrdu))
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ALERT_ID, notification)
    }
}
