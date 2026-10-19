package com.example.data.model

data class BatteryInfo(
    val level: Int = 0,
    val isCharging: Boolean = false,
    val plugType: String = "Not Plugged",
    val temperatureCelsius: Float = 0f,
    val voltageMv: Int = 0,
    val healthStatus: String = "Good",
    val healthStatusUrdu: String = "بہترین",
    val technology: String = "Li-ion",
    val statusText: String = "Discharging"
)

data class PerformanceInfo(
    val totalRamBytes: Long = 0L,
    val availableRamBytes: Long = 0L,
    val usedRamPercentage: Int = 0,
    val totalStorageBytes: Long = 0L,
    val freeStorageBytes: Long = 0L,
    val usedStoragePercentage: Int = 0,
    val isLowRam: Boolean = false,
    val isLowStorage: Boolean = false
)

data class DisplayInfo(
    val widthPx: Int = 0,
    val heightPx: Int = 0,
    val refreshRate: Float = 60f,
    val densityDpi: Int = 0,
    val isHdrSupported: Boolean = false
)

data class AudioInfo(
    val isSpeakerAvailable: Boolean = true,
    val isMicAvailable: Boolean = true,
    val ringerMode: String = "Normal",
    val isBluetoothConnected: Boolean = false,
    val isWiredHeadsetConnected: Boolean = false
)

data class ConnectivityInfo(
    val isInternetConnected: Boolean = false,
    val isWifiConnected: Boolean = false,
    val isCellularConnected: Boolean = false,
    val networkType: String = "Disconnected",
    val wifiSsid: String? = null,
    val linkSpeedMbps: Int = 0
)

data class SensorItem(
    val name: String,
    val typeName: String,
    val isAvailable: Boolean,
    val currentReading: String = "N/A"
)

data class SensorsSummary(
    val totalSensors: Int = 0,
    val hasAccelerometer: Boolean = false,
    val hasGyroscope: Boolean = false,
    val hasProximity: Boolean = false,
    val hasLight: Boolean = false,
    val hasMagnetometer: Boolean = false,
    val hasBarometer: Boolean = false,
    val hasStepCounter: Boolean = false,
    val sensorList: List<SensorItem> = emptyList()
)

data class CameraHardwareInfo(
    val backCameraAvailable: Boolean = false,
    val frontCameraAvailable: Boolean = false,
    val flashAvailable: Boolean = false,
    val totalCameras: Int = 0
)

data class SystemSoftwareInfo(
    val androidVersion: String = "",
    val sdkInt: Int = 0,
    val securityPatch: String = "Unknown",
    val buildFingerprint: String = "",
    val kernelArch: String = "",
    val isPowerSaveMode: Boolean = false,
    val isDeviceIdleMode: Boolean = false,
    val appVersionName: String = "1.0",
    val appVersionCode: Long = 1L
)

data class AppStorageItem(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean,
    val versionName: String,
    val installTime: Long = 0L
)

data class CacheCleanupRecord(
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String,
    val bytesCleaned: Long,
    val freeSpaceBefore: Long,
    val freeSpaceAfter: Long,
    val success: Boolean,
    val notesUrdu: String
)

enum class DiagnosticSeverity {
    NORMAL,
    INFO,
    WARNING,
    CRITICAL
}

data class DiagnosticFinding(
    val id: String,
    val titleUrdu: String,
    val titleEnglish: String,
    val severity: DiagnosticSeverity,
    val evidenceUrdu: String,
    val possibleCauseUrdu: String,
    val confidenceUrdu: String,
    val homeSafeActionUrdu: String,
    val technicianNeedUrdu: String,
    val priorityUrdu: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class RepairAssistantMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val questionUrdu: String,
    val answerUrdu: String,
    val evidenceUrdu: String,
    val recommendedActionUrdu: String,
    val recommendedRoute: String? = null,
    val confidenceUrdu: String = "95% (حقیقی سنسرز کی بنیاد پر)",
    val timestamp: Long = System.currentTimeMillis()
)

data class DeviceFullHealth(
    val brand: String = "",
    val model: String = "",
    val manufacturer: String = "",
    val androidVersion: String = "",
    val sdkInt: Int = 0,
    val battery: BatteryInfo = BatteryInfo(),
    val performance: PerformanceInfo = PerformanceInfo(),
    val display: DisplayInfo = DisplayInfo(),
    val audio: AudioInfo = AudioInfo(),
    val connectivity: ConnectivityInfo = ConnectivityInfo(),
    val sensors: SensorsSummary = SensorsSummary(),
    val camera: CameraHardwareInfo = CameraHardwareInfo(),
    val software: SystemSoftwareInfo = SystemSoftwareInfo(),
    val findings: List<DiagnosticFinding> = emptyList(),
    val healthScore: Int = 100,
    val statusBadgeUrdu: String = "سب ٹھیک چل رہا ہے",
    val statusBadgeEnglish: String = "All Systems Healthy",
    val lastCheckTime: Long = System.currentTimeMillis()
)
