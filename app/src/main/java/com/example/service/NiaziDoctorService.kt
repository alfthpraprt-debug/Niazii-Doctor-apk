package com.example.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import com.example.data.local.AppDatabase
import com.example.data.local.DiagnosticEventEntity
import com.example.data.local.PreferencesManager
import com.example.data.model.AlertType
import com.example.data.model.UrduVoiceAlert
import com.example.diagnostic.DeviceHardwareReader
import com.example.diagnostic.DiagnosticEngine
import com.example.voice.UrduVoiceAssistant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NiaziDoctorService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var notificationManager: NiaziDoctorNotificationManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var voiceAssistant: UrduVoiceAssistant
    private lateinit var diagnosticEngine: DiagnosticEngine
    private lateinit var hardwareReader: DeviceHardwareReader
    private val database by lazy { AppDatabase.getDatabase(this) }

    // Charging disconnect instability tracking
    private val chargingTimestamps = mutableListOf<Long>()

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_BATTERY_CHANGED -> handleBatteryChanged(intent)
                Intent.ACTION_POWER_CONNECTED -> handlePowerConnected()
                Intent.ACTION_POWER_DISCONNECTED -> handlePowerDisconnected()
                Intent.ACTION_BATTERY_LOW -> handleBatteryLow()
                Intent.ACTION_DEVICE_STORAGE_LOW -> handleStorageLow()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = NiaziDoctorNotificationManager(this)
        preferencesManager = PreferencesManager(this)
        voiceAssistant = UrduVoiceAssistant(this, preferencesManager)
        diagnosticEngine = DiagnosticEngine(this)
        hardwareReader = DeviceHardwareReader(this)

        val initialNotification = notificationManager.buildMonitoringNotification()
        startForeground(NiaziDoctorNotificationManager.NOTIFICATION_MONITORING_ID, initialNotification)

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_DEVICE_STORAGE_LOW)
        }
        registerReceiver(batteryReceiver, filter)

        startPeriodicHealthCheck()
    }

    private fun handleBatteryChanged(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val tempCelsius = rawTemp / 10.0f
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)

        // Update foreground notification
        val notif = notificationManager.buildMonitoringNotification(
            statusText = "سب ٹھیک چل رہا ہے • بیٹری: $level% • درجہ حرارت: ${tempCelsius}°C",
            batteryLevel = level,
            tempCelsius = tempCelsius
        )
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.notify(NiaziDoctorNotificationManager.NOTIFICATION_MONITORING_ID, notif)

        // Check high temperature
        val tempThreshold = preferencesManager.highTempThreshold.value
        if (tempCelsius >= tempThreshold) {
            triggerUrduAlert(
                type = AlertType.HIGH_TEMPERATURE,
                title = "زیادہ درجہ حرارت",
                spokenText = UrduVoiceAlert.PHRASE_TEMPERATURE,
                summary = "Battery temp reached ${tempCelsius}°C (exceeds ${tempThreshold}°C)",
                isCritical = true,
                dedupKey = "temp_overheat"
            )
        }

        // Check abnormal battery health
        if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT ||
            health == BatteryManager.BATTERY_HEALTH_DEAD ||
            health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE
        ) {
            triggerUrduAlert(
                type = AlertType.BATTERY_WARNING,
                title = "بیٹری کی غیر معمولی حالت",
                spokenText = UrduVoiceAlert.PHRASE_BATTERY,
                summary = "System battery health warning state: $health",
                isCritical = true,
                dedupKey = "battery_health_fault"
            )
        }
    }

    private fun handlePowerConnected() {
        recordChargingEvent()
    }

    private fun handlePowerDisconnected() {
        recordChargingEvent()
    }

    private fun recordChargingEvent() {
        val now = System.currentTimeMillis()
        chargingTimestamps.add(now)

        // Prune older than 30 seconds
        chargingTimestamps.removeAll { now - it > 30_000L }

        // If connected and disconnected 3+ times within 30s -> loose cable / faulty port
        if (chargingTimestamps.size >= 4) {
            triggerUrduAlert(
                type = AlertType.CHARGING_ISSUE,
                title = "چارجنگ میں بار بار رکاوٹ",
                spokenText = UrduVoiceAlert.PHRASE_CHARGING,
                summary = "Detected frequent charging connect/disconnect cycles (loose port or faulty cable)",
                isCritical = false,
                dedupKey = "charging_instability"
            )
            chargingTimestamps.clear()
        }
    }

    private fun handleBatteryLow() {
        triggerUrduAlert(
            type = AlertType.BATTERY_WARNING,
            title = "بیٹری کم ہو گئی",
            spokenText = "توجہ دیں، موبائل کی بیٹری کم ہو گئی ہے۔ جلد چارجر لگائیں تاکہ فون بند نہ ہو۔",
            summary = "Battery level reached critical low threshold",
            isCritical = false,
            dedupKey = "battery_low_level"
        )
    }

    private fun handleStorageLow() {
        triggerUrduAlert(
            type = AlertType.STORAGE_WARNING,
            title = "اسٹوریج کم ہے",
            spokenText = UrduVoiceAlert.PHRASE_STORAGE,
            summary = "System broadcast: low internal storage remaining",
            isCritical = false,
            dedupKey = "system_low_storage"
        )
    }

    private fun triggerUrduAlert(
        type: AlertType,
        title: String,
        spokenText: String,
        summary: String,
        isCritical: Boolean,
        dedupKey: String
    ) {
        val wasSpoken = voiceAssistant.speakUrdu(
            text = spokenText,
            dedupKey = dedupKey,
            isCritical = isCritical
        )

        notificationManager.showAlertNotification(title, spokenText)

        serviceScope.launch {
            database.diagnosticDao().insertEvent(
                DiagnosticEventEntity(
                    alertType = type.name,
                    severity = if (isCritical) "CRITICAL" else "WARNING",
                    titleUrdu = title,
                    messageUrdu = spokenText,
                    englishSummary = summary,
                    wasSpoken = wasSpoken,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    private fun startPeriodicHealthCheck() {
        serviceScope.launch {
            while (isActive) {
                delay(5 * 60 * 1000L) // every 5 minutes
                if (preferencesManager.backgroundMonitoringEnabled.value) {
                    val perf = hardwareReader.readPerformance()
                    if (perf.isLowStorage) {
                        triggerUrduAlert(
                            type = AlertType.STORAGE_WARNING,
                            title = "اسٹوریج وارننگ",
                            spokenText = UrduVoiceAlert.PHRASE_STORAGE,
                            summary = "Free storage is below 2GB threshold",
                            isCritical = false,
                            dedupKey = "periodic_storage_check"
                        )
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        try {
            unregisterReceiver(batteryReceiver)
        } catch (_: Exception) {}
        serviceScope.cancel()
        voiceAssistant.release()
        super.onDestroy()
    }
}
