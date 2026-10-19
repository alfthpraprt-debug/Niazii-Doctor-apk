package com.example.diagnostic

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.example.data.model.AudioInfo
import com.example.data.model.BatteryInfo
import com.example.data.model.CameraHardwareInfo
import com.example.data.model.ConnectivityInfo
import com.example.data.model.DisplayInfo
import com.example.data.model.PerformanceInfo
import com.example.data.model.SensorItem
import com.example.data.model.SensorsSummary

class DeviceHardwareReader(private val context: Context) {

    fun readBattery(): BatteryInfo {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val bStatus = context.registerReceiver(null, iFilter)

        if (bStatus == null) {
            return BatteryInfo()
        }

        val level = bStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = bStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val pct = if (level >= 0 && scale > 0) ((level * 100) / scale) else 0

        val status = bStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val statusText = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full (Charged)"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            else -> "Unknown"
        }

        val chargePlug = bStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val plugType = when (chargePlug) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Wall Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Cable / PC"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Dock"
            else -> "Disconnected"
        }

        val rawTemp = bStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val tempCelsius = rawTemp / 10.0f
        val voltageMv = bStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        val technology = bStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

        val health = bStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val (healthText, healthUrdu) = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> Pair("Good", "بہترین اور نارمل")
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> Pair("Overheat", "درجہ حرارت زیادہ")
            BatteryManager.BATTERY_HEALTH_DEAD -> Pair("Dead", "بیٹری ناکارہ")
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> Pair("Over Voltage", "وولٹیج زیادہ")
            BatteryManager.BATTERY_HEALTH_COLD -> Pair("Cold", "بہت زیادہ سرد")
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> Pair("Failure", "خرابی کی علامت")
            else -> Pair("Normal", "معمول کے مطابق")
        }

        return BatteryInfo(
            level = pct,
            isCharging = isCharging,
            plugType = plugType,
            temperatureCelsius = tempCelsius,
            voltageMv = voltageMv,
            healthStatus = healthText,
            healthStatusUrdu = healthUrdu,
            technology = technology,
            statusText = statusText
        )
    }

    fun readPerformance(): PerformanceInfo {
        // RAM Info
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)

        val totalRam = memInfo.totalMem
        val availRam = memInfo.availMem
        val usedRam = (totalRam - availRam).coerceAtLeast(0L)
        val usedRamPct = if (totalRam > 0) ((usedRam * 100) / totalRam).toInt() else 0
        val isLowRam = memInfo.lowMemory || (usedRamPct > 88)

        // Storage Info
        val stat = StatFs(Environment.getDataDirectory().path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availBlocks = stat.availableBlocksLong

        val totalStorage = totalBlocks * blockSize
        val freeStorage = availBlocks * blockSize
        val usedStorage = (totalStorage - freeStorage).coerceAtLeast(0L)
        val usedStoragePct = if (totalStorage > 0) ((usedStorage * 100) / totalStorage).toInt() else 0
        val isLowStorage = freeStorage < (1024L * 1024L * 1024L * 2L) // Less than 2GB free

        return PerformanceInfo(
            totalRamBytes = totalRam,
            availableRamBytes = availRam,
            usedRamPercentage = usedRamPct,
            totalStorageBytes = totalStorage,
            freeStorageBytes = freeStorage,
            usedStoragePercentage = usedStoragePct,
            isLowRam = isLowRam,
            isLowStorage = isLowStorage
        )
    }

    fun readDisplay(): DisplayInfo {
        val dm = context.resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        val dpi = dm.densityDpi

        return DisplayInfo(
            widthPx = width,
            heightPx = height,
            refreshRate = 60f,
            densityDpi = dpi,
            isHdrSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        )
    }

    fun readAudio(): AudioInfo {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val ringer = when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> "Silent"
            AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
            else -> "Normal"
        }

        var isBluetoothConnected = false
        var isWiredHeadsetConnected = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (device in devices) {
                when (device.type) {
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                    AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> isBluetoothConnected = true
                    AudioDeviceInfo.TYPE_WIRED_HEADSET,
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                    AudioDeviceInfo.TYPE_USB_HEADSET -> isWiredHeadsetConnected = true
                }
            }
        }

        val hasMic = context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)

        return AudioInfo(
            isSpeakerAvailable = true,
            isMicAvailable = hasMic,
            ringerMode = ringer,
            isBluetoothConnected = isBluetoothConnected,
            isWiredHeadsetConnected = isWiredHeadsetConnected
        )
    }

    fun readConnectivity(): ConnectivityInfo {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNet = cm.activeNetwork
        val caps = if (activeNet != null) cm.getNetworkCapabilities(activeNet) else null

        if (caps == null) {
            return ConnectivityInfo(
                isInternetConnected = false,
                isWifiConnected = false,
                isCellularConnected = false,
                networkType = "Offline / No Signal"
            )
        }

        val isInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val isWifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

        val netType = when {
            isWifi -> "Wi-Fi High Speed"
            isCellular -> "Cellular 4G/5G"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Connected Network"
        }

        return ConnectivityInfo(
            isInternetConnected = isInternet,
            isWifiConnected = isWifi,
            isCellularConnected = isCellular,
            networkType = netType,
            linkSpeedMbps = caps.linkDownstreamBandwidthKbps / 1000
        )
    }

    fun readSensors(): SensorsSummary {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val allSensors = sm.getSensorList(Sensor.TYPE_ALL)

        val hasAccel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        val hasGyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
        val hasProx = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null
        val hasLight = sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null
        val hasMag = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
        val hasBaro = sm.getDefaultSensor(Sensor.TYPE_PRESSURE) != null
        val hasStep = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null

        val sensorItems = listOf(
            SensorItem("Accelerometer", "Motion & Gravity", hasAccel),
            SensorItem("Gyroscope", "Rotation & Orientation", hasGyro),
            SensorItem("Proximity", "Ear/Screen Detection", hasProx),
            SensorItem("Ambient Light", "Auto Brightness", hasLight),
            SensorItem("Magnetometer", "Digital Compass", hasMag),
            SensorItem("Barometer", "Atmospheric Pressure", hasBaro),
            SensorItem("Step Counter", "Pedometer Hardware", hasStep)
        )

        return SensorsSummary(
            totalSensors = allSensors.size,
            hasAccelerometer = hasAccel,
            hasGyroscope = hasGyro,
            hasProximity = hasProx,
            hasLight = hasLight,
            hasMagnetometer = hasMag,
            hasBarometer = hasBaro,
            hasStepCounter = hasStep,
            sensorList = sensorItems
        )
    }

    fun readCamera(): CameraHardwareInfo {
        return try {
            val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val idList = cm.cameraIdList
            var hasBack = false
            var hasFront = false
            var hasFlash = false

            for (id in idList) {
                val chars = cm.getCameraCharacteristics(id)
                val facing = chars.get(CameraCharacteristics.LENS_FACING)
                if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                    hasBack = true
                } else if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    hasFront = true
                }
                val flashAvailable = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                if (flashAvailable) {
                    hasFlash = true
                }
            }

            CameraHardwareInfo(
                backCameraAvailable = hasBack,
                frontCameraAvailable = hasFront,
                flashAvailable = hasFlash,
                totalCameras = idList.size
            )
        } catch (_: Exception) {
            CameraHardwareInfo(
                backCameraAvailable = true,
                frontCameraAvailable = true,
                flashAvailable = true,
                totalCameras = 2
            )
        }
    }

    fun getDeviceIdentifiers(): Triple<String, String, String> {
        val brand = Build.BRAND.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        return Triple(brand, model, manufacturer)
    }

    fun readSoftware(): com.example.data.model.SystemSoftwareInfo {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
        val isPowerSave = pm?.isPowerSaveMode ?: false
        val isIdle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) pm?.isDeviceIdleMode ?: false else false

        val secPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH
        } else "N/A"

        var vName = "1.0"
        var vCode = 1L
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            vName = pInfo.versionName ?: "1.0"
            vCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode.toLong()
            }
        } catch (_: Exception) {}

        return com.example.data.model.SystemSoftwareInfo(
            androidVersion = Build.VERSION.RELEASE,
            sdkInt = Build.VERSION.SDK_INT,
            securityPatch = secPatch,
            buildFingerprint = Build.FINGERPRINT,
            kernelArch = System.getProperty("os.arch") ?: "Unknown",
            isPowerSaveMode = isPowerSave,
            isDeviceIdleMode = isIdle,
            appVersionName = vName,
            appVersionCode = vCode
        )
    }

    fun getInstalledApps(): List<com.example.data.model.AppStorageItem> {
        val pm = context.packageManager
        val packages = try {
            pm.getInstalledPackages(0)
        } catch (_: Exception) {
            emptyList()
        }

        return packages.mapNotNull { pInfo ->
            try {
                val appInfo = pInfo.applicationInfo ?: return@mapNotNull null
                val isSystem = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                val appLabel = pm.getApplicationLabel(appInfo).toString()
                com.example.data.model.AppStorageItem(
                    packageName = pInfo.packageName,
                    appName = appLabel,
                    isSystemApp = isSystem,
                    versionName = pInfo.versionName ?: "1.0",
                    installTime = pInfo.firstInstallTime
                )
            } catch (_: Exception) {
                null
            }
        }.sortedWith(compareBy({ it.isSystemApp }, { it.appName.lowercase() }))
    }
}
