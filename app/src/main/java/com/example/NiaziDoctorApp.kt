package com.example

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.data.local.PreferencesManager
import com.example.service.NiaziDoctorNotificationManager
import com.example.service.NiaziDoctorService

class NiaziDoctorApp : Application() {

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var notificationManager: NiaziDoctorNotificationManager
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)
        notificationManager = NiaziDoctorNotificationManager(this)

        // Automatically start background health service if enabled
        if (preferencesManager.backgroundMonitoringEnabled.value) {
            val serviceIntent = Intent(this, NiaziDoctorService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, serviceIntent)
                } else {
                    startService(serviceIntent)
                }
            } catch (_: Exception) {
                // Ignore if restricted before first launch activity
            }
        }
    }
}
