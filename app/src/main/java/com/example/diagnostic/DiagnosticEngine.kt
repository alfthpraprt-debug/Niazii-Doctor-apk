package com.example.diagnostic

import android.content.Context
import android.os.Build
import com.example.data.model.AlertType
import com.example.data.model.DeviceFullHealth
import com.example.data.model.DiagnosticFinding
import com.example.data.model.DiagnosticSeverity
import com.example.data.model.UrduVoiceAlert

class DiagnosticEngine(private val context: Context) {
    private val reader = DeviceHardwareReader(context)

    fun runFullCheckup(): DeviceFullHealth {
        val (brand, model, manufacturer) = reader.getDeviceIdentifiers()
        val battery = reader.readBattery()
        val performance = reader.readPerformance()
        val display = reader.readDisplay()
        val audio = reader.readAudio()
        val connectivity = reader.readConnectivity()
        val sensors = reader.readSensors()
        val camera = reader.readCamera()

        val findings = mutableListOf<DiagnosticFinding>()

        // 1. Battery Temperature Check
        if (battery.temperatureCelsius >= 45f) {
            findings.add(
                DiagnosticFinding(
                    id = "temp_crit_${System.currentTimeMillis()}",
                    titleUrdu = "درجہ حرارت کا شدید خطرہ",
                    titleEnglish = "Critical Battery Overheating",
                    severity = DiagnosticSeverity.CRITICAL,
                    evidenceUrdu = "بیٹری کا درجہ حرارت ${battery.temperatureCelsius}°C نوٹ کیا گیا جو کہ محفوظ حد (40°C) سے کہیں زیادہ ہے۔",
                    possibleCauseUrdu = "مسلسل بھاری گیمنگ، براہ راست دھوپ، ناقص چارجر، یا بیک گراؤنڈ میں غیر معمولی پروسیسنگ۔",
                    confidenceUrdu = "95% (ہارڈویئر سنسر ریڈنگ کی بنیاد پر)",
                    homeSafeActionUrdu = "فوری طور پر چارجنگ روک دیں، بھاری ایپس بند کریں، کور اتاریں اور موبائل کو پنکھے یا سایہ دار ٹھنڈی جگہ پر رکھیں۔",
                    technicianNeedUrdu = "اگر عام حالت میں بھی درجہ حرارت کم نہ ہو تو بیٹری شارٹ سرکٹ کی جانچ کے لیے ٹیکنیشن سے رجوع کریں۔",
                    priorityUrdu = "انتہائی فوری (High Priority)"
                )
            )
        } else if (battery.temperatureCelsius >= 40f) {
            findings.add(
                DiagnosticFinding(
                    id = "temp_warn_${System.currentTimeMillis()}",
                    titleUrdu = "درجۂ حرارت معمول سے زیادہ",
                    titleEnglish = "Elevated Device Temperature",
                    severity = DiagnosticSeverity.WARNING,
                    evidenceUrdu = "بیٹری کا درجہ حرارت ${battery.temperatureCelsius}°C ہے جو معیاری حد سے قدرے بلند ہے۔",
                    possibleCauseUrdu = "چارجنگ کے دوران بیک وقت استعمال یا تیز رفتار نیٹ ورک ڈیٹا کا مسلسل تبادلہ۔",
                    confidenceUrdu = "90% (تھرمسٹر سگنل)",
                    homeSafeActionUrdu = "کچھ دیر کے لیے اسکرین آف کر دیں اور چارجنگ مکمل ہونے کا انتظار کریں۔",
                    technicianNeedUrdu = "اگر موبائل بار بار گرم ہو تو بیٹری کی اندرونی صحت ٹیسٹ کروائیں۔",
                    priorityUrdu = "درمیانہ (Medium)"
                )
            )
        }

        // 2. Battery Health State
        if (battery.healthStatus != "Good" && battery.healthStatus != "Normal") {
            findings.add(
                DiagnosticFinding(
                    id = "battery_health_${System.currentTimeMillis()}",
                    titleUrdu = "بیٹری کی حالت میں خرابی کا انتباہ",
                    titleEnglish = "Abnormal Battery State",
                    severity = DiagnosticSeverity.WARNING,
                    evidenceUrdu = "اینڈرائیڈ بیٹری مینیجر کی جانب سے بیٹری اسٹیٹس '${battery.healthStatusUrdu}' موصول ہوا ہے۔",
                    possibleCauseUrdu = "بیٹری کے کیمیائی سیلز کی تنزلی یا وولٹیج میں غیر متوازن اتار چڑھاؤ۔",
                    confidenceUrdu = "85% (سسٹم مانیٹرنگ)",
                    homeSafeActionUrdu = "مستند اور تصدیق شدہ چارجر استعمال کریں، 100% تک مکمل چارج کر کے دیکھیں کہ کیا مسئلہ حل ہوتا ہے۔",
                    technicianNeedUrdu = "اگر بیٹری جلد ختم ہو جائے یا فون اچانک بند ہو جائے تو بیٹری تبدیل کروانا ضروری ہے۔",
                    priorityUrdu = "اہم (High)"
                )
            )
        }

        // 3. Storage Capacity Check
        val freeGb = batteryFreeGb(performance.freeStorageBytes)
        if (performance.freeStorageBytes < 1024L * 1024L * 1024L) { // < 1 GB
            findings.add(
                DiagnosticFinding(
                    id = "storage_crit_${System.currentTimeMillis()}",
                    titleUrdu = "اسٹوریج انتہائی کم ہے",
                    titleEnglish = "Critical Low Storage Space",
                    severity = DiagnosticSeverity.CRITICAL,
                    evidenceUrdu = "موبائل میں صرف ${freeGb} GB اسٹوریج خالی بچی ہے (${performance.usedStoragePercentage}% استعمال شدہ)۔",
                    possibleCauseUrdu = "بڑی وڈیوز، غیر ضروری کیش فائلیں اور ڈاؤن لوڈ شدہ ڈیٹا کا جمع ہونا۔",
                    confidenceUrdu = "100% (فائل سسٹم ناپ)",
                    homeSafeActionUrdu = "غیر ضروری ویڈیوز، واٹس ایپ میڈیا ڈیلیٹ کریں اور کیشے فائلز صاف کریں۔",
                    technicianNeedUrdu = "کسی ٹیکنیشن کی ضرورت نہیں، گھر بیٹھے خود فائلیں ڈیلیٹ کر سکتے ہیں۔",
                    priorityUrdu = "فوری (Urgent)"
                )
            )
        } else if (performance.isLowStorage) {
            findings.add(
                DiagnosticFinding(
                    id = "storage_warn_${System.currentTimeMillis()}",
                    titleUrdu = "اسٹوریج میں کمی کا خطرہ",
                    titleEnglish = "Low Internal Storage",
                    severity = DiagnosticSeverity.WARNING,
                    evidenceUrdu = "خالی جگہ ${freeGb} GB رہ گئی ہے۔ اسٹوریج 90 فیصد سے زیادہ بھر چکی ہے۔",
                    possibleCauseUrdu = "ایپ اپڈیٹس اور ڈاونلوڈ شدہ فائلز۔",
                    confidenceUrdu = "100% (فائل سسٹم)",
                    homeSafeActionUrdu = "غیر ضروری ایپس ان انسٹال کریں اور اہم ڈیٹا کلاؤڈ یا کمپیوٹر پر بیک اپ کریں۔",
                    technicianNeedUrdu = "کسی ٹیکنیشن کی ضرورت نہیں۔",
                    priorityUrdu = "درمیانہ (Medium)"
                )
            )
        }

        // 4. Memory Pressure Check
        if (performance.isLowRam) {
            findings.add(
                DiagnosticFinding(
                    id = "ram_pressure_${System.currentTimeMillis()}",
                    titleUrdu = "ریم (RAM) پر شدید دباؤ",
                    titleEnglish = "High Memory Pressure",
                    severity = DiagnosticSeverity.WARNING,
                    evidenceUrdu = "ریم کا ${performance.usedRamPercentage}% حصہ مصروف ہے، جس کی وجہ سے فون سست محسوس ہو سکتا ہے۔",
                    possibleCauseUrdu = "بیک گراؤنڈ میں بہت سی ایپس کا ایک ساتھ کھلے رہنا۔",
                    confidenceUrdu = "90% (ایکٹیوٹی مینیجر میموری انفو)",
                    homeSafeActionUrdu = "حالیہ کھلی ہوئی ایپس (Recent Apps) کو بند کریں اور موبائل کو ری اسٹارٹ کریں۔",
                    technicianNeedUrdu = "کوئی ہارڈویئر مسئلہ نہیں ہے۔",
                    priorityUrdu = "معمولی (Low)"
                )
            )
        }

        // 5. Connectivity Issues
        if (!connectivity.isInternetConnected && (connectivity.isWifiConnected || connectivity.isCellularConnected)) {
            findings.add(
                DiagnosticFinding(
                    id = "net_no_internet_${System.currentTimeMillis()}",
                    titleUrdu = "نیٹ ورک بغیر انٹرنیٹ سگنل",
                    titleEnglish = "Connected with No Internet",
                    severity = DiagnosticSeverity.WARNING,
                    evidenceUrdu = "موبائل نیٹ ورک سے جڑا ہے لیکن انٹرنیٹ سرور تک رسائی تصدیق نہیں ہو سکی۔",
                    possibleCauseUrdu = "راؤٹر کا انٹرنیٹ منقطع ہونا، ڈیٹا بنڈل ختم ہونا، یا APN سیٹنگز کی خرابی۔",
                    confidenceUrdu = "85% (نیٹ ورک پنگ ٹیسٹ)",
                    homeSafeActionUrdu = "ایروپلین موڈ (Airplane Mode) آن کر کے 10 سیکنڈ بعد آف کریں یا وائی فائی راؤٹر دوبارہ چلائیں۔",
                    technicianNeedUrdu = "سم فراہم کنندہ (Network Carrier) سے ڈیٹا پیکج اور سگنل کی تصدیق کریں۔",
                    priorityUrdu = "درمیانہ (Medium)"
                )
            )
        }

        // Calculate Overall Health Score
        var score = 100
        for (f in findings) {
            when (f.severity) {
                DiagnosticSeverity.CRITICAL -> score -= 30
                DiagnosticSeverity.WARNING -> score -= 15
                DiagnosticSeverity.INFO -> score -= 5
                DiagnosticSeverity.NORMAL -> {}
            }
        }
        score = score.coerceIn(20, 100)

        val (badgeUrdu, badgeEnglish) = when {
            score >= 90 -> Pair("سب ٹھیک چل رہا ہے", "All Systems Optimal")
            score >= 70 -> Pair("ایک ممکنہ مسئلہ ملا ہے", "Minor Issues Observed")
            score >= 50 -> Pair("مسئلے کی مزید جانچ ضروری ہے", "Attention Required")
            else -> Pair("فوری توجہ درکار ہے", "Critical Action Required")
        }

        val software = reader.readSoftware()

        return DeviceFullHealth(
            brand = brand,
            model = model,
            manufacturer = manufacturer,
            androidVersion = Build.VERSION.RELEASE,
            sdkInt = Build.VERSION.SDK_INT,
            battery = battery,
            performance = performance,
            display = display,
            audio = audio,
            connectivity = connectivity,
            sensors = sensors,
            camera = camera,
            software = software,
            findings = findings,
            healthScore = score,
            statusBadgeUrdu = badgeUrdu,
            statusBadgeEnglish = badgeEnglish,
            lastCheckTime = System.currentTimeMillis()
        )
    }

    fun determineVoiceAlert(health: DeviceFullHealth): UrduVoiceAlert {
        // Find highest priority finding
        val criticalFinding = health.findings.firstOrNull { it.severity == DiagnosticSeverity.CRITICAL }
        val warningFinding = health.findings.firstOrNull { it.severity == DiagnosticSeverity.WARNING }

        return when {
            health.battery.temperatureCelsius >= 41f -> {
                UrduVoiceAlert(
                    id = "alert_temp_${System.currentTimeMillis()}",
                    type = AlertType.HIGH_TEMPERATURE,
                    severity = DiagnosticSeverity.CRITICAL,
                    titleUrdu = "درجہ حرارت کا انتباہ",
                    spokenTextUrdu = UrduVoiceAlert.PHRASE_TEMPERATURE,
                    englishSummary = "High temperature detected (${health.battery.temperatureCelsius}°C)"
                )
            }
            health.performance.isLowStorage -> {
                UrduVoiceAlert(
                    id = "alert_storage_${System.currentTimeMillis()}",
                    type = AlertType.STORAGE_WARNING,
                    severity = DiagnosticSeverity.WARNING,
                    titleUrdu = "کم اسٹوریج کی وارننگ",
                    spokenTextUrdu = UrduVoiceAlert.PHRASE_STORAGE,
                    englishSummary = "Low storage space remaining"
                )
            }
            health.battery.healthStatus != "Good" && health.battery.healthStatus != "Normal" -> {
                UrduVoiceAlert(
                    id = "alert_battery_${System.currentTimeMillis()}",
                    type = AlertType.BATTERY_WARNING,
                    severity = DiagnosticSeverity.WARNING,
                    titleUrdu = "بیٹری خرابی کی تنبیہ",
                    spokenTextUrdu = UrduVoiceAlert.PHRASE_BATTERY,
                    englishSummary = "Possible battery health issue observed"
                )
            }
            !health.connectivity.isInternetConnected && health.connectivity.isWifiConnected -> {
                UrduVoiceAlert(
                    id = "alert_network_${System.currentTimeMillis()}",
                    type = AlertType.NETWORK_ISSUE,
                    severity = DiagnosticSeverity.WARNING,
                    titleUrdu = "نیٹ ورک کا مسئلہ",
                    spokenTextUrdu = UrduVoiceAlert.PHRASE_NETWORK,
                    englishSummary = "Network connectivity problem detected"
                )
            }
            else -> {
                UrduVoiceAlert(
                    id = "alert_normal_${System.currentTimeMillis()}",
                    type = AlertType.NORMAL_STATUS,
                    severity = DiagnosticSeverity.NORMAL,
                    titleUrdu = "سب ٹھیک ہے",
                    spokenTextUrdu = UrduVoiceAlert.PHRASE_NORMAL,
                    englishSummary = "All monitored hardware parameters are healthy"
                )
            }
        }
    }

    private fun batteryFreeGb(bytes: Long): String {
        val gb = bytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
        return String.format(java.util.Locale.US, "%.1f", gb)
    }
}
