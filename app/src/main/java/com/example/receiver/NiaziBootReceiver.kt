package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.data.local.PreferencesManager
import com.example.service.NiaziDoctorService

class NiaziBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val prefs = PreferencesManager(context)
            if (prefs.backgroundMonitoringEnabled.value) {
                val serviceIntent = Intent(context, NiaziDoctorService::class.java)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(context, serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                } catch (_: Exception) {
                    // Handled gracefully if background restrictions apply
                }
            }
        }
    }
}
