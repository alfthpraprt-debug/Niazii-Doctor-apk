package com.example

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CacheCleanupEntity
import com.example.data.local.DiagnosticEventEntity
import com.example.data.local.PreferencesManager
import com.example.data.model.AppStorageItem
import com.example.data.model.CacheCleanupRecord
import com.example.data.model.DeviceFullHealth
import com.example.data.model.DiagnosticFinding
import com.example.data.model.RepairAssistantMessage
import com.example.data.model.UrduVoiceAlert
import com.example.diagnostic.CacheCleanerEngine
import com.example.diagnostic.DeviceHardwareReader
import com.example.diagnostic.DiagnosticEngine
import com.example.service.NiaziDoctorService
import com.example.voice.UrduVoiceAssistant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val preferencesManager = PreferencesManager(application)
    val voiceAssistant = UrduVoiceAssistant(application, preferencesManager)
    val diagnosticEngine = DiagnosticEngine(application)
    val hardwareReader = DeviceHardwareReader(application)
    val cacheCleanerEngine = CacheCleanerEngine(application)
    private val database = AppDatabase.getDatabase(application)

    private val _deviceHealth = MutableStateFlow(diagnosticEngine.runFullCheckup())
    val deviceHealth: StateFlow<DeviceFullHealth> = _deviceHealth.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _scanStageText = MutableStateFlow("تیار")
    val scanStageText: StateFlow<String> = _scanStageText.asStateFlow()

    private val _selectedFinding = MutableStateFlow<DiagnosticFinding?>(null)
    val selectedFinding: StateFlow<DiagnosticFinding?> = _selectedFinding.asStateFlow()

    val eventHistory: StateFlow<List<DiagnosticEventEntity>> = database.diagnosticDao()
        .getAllEvents()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val cleanupHistory: StateFlow<List<CacheCleanupEntity>> = database.diagnosticDao()
        .getCleanupHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isSpeaking: StateFlow<Boolean> = voiceAssistant.isSpeaking
    val lastSpokenMessage: StateFlow<String?> = voiceAssistant.lastSpokenMessage

    // Storage and Cache States
    private val _installedApps = MutableStateFlow<List<AppStorageItem>>(emptyList())
    val installedApps: StateFlow<List<AppStorageItem>> = _installedApps.asStateFlow()

    private val _internalCacheBytes = MutableStateFlow(0L)
    val internalCacheBytes: StateFlow<Long> = _internalCacheBytes.asStateFlow()

    private val _isCleaning = MutableStateFlow(false)
    val isCleaning: StateFlow<Boolean> = _isCleaning.asStateFlow()

    private val _lastCleanupRecord = MutableStateFlow<CacheCleanupRecord?>(null)
    val lastCleanupRecord: StateFlow<CacheCleanupRecord?> = _lastCleanupRecord.asStateFlow()

    // Smart AI Repair Assistant Messages
    private val _repairMessages = MutableStateFlow<List<RepairAssistantMessage>>(emptyList())
    val repairMessages: StateFlow<List<RepairAssistantMessage>> = _repairMessages.asStateFlow()

    init {
        refreshHealth()
        loadInstalledApps()
        refreshInternalCacheSize()
        initRepairAssistantIntro()
    }

    fun refreshHealth() {
        viewModelScope.launch {
            val health = diagnosticEngine.runFullCheckup()
            _deviceHealth.value = health
        }
    }

    fun refreshInternalCacheSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val size = cacheCleanerEngine.getAppInternalCacheSize()
            _internalCacheBytes.value = size
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val apps = hardwareReader.getInstalledApps()
            _installedApps.value = apps
        }
    }

    fun runModeACleanup(onComplete: ((CacheCleanupRecord) -> Unit)? = null) {
        if (_isCleaning.value) return
        viewModelScope.launch {
            _isCleaning.value = true
            delay(800) // Visual progress feedback

            val record = withContext(Dispatchers.IO) {
                cacheCleanerEngine.performModeACleanup()
            }

            _lastCleanupRecord.value = record
            _internalCacheBytes.value = 0L

            // Record to Room Database
            database.diagnosticDao().insertCleanup(
                CacheCleanupEntity(
                    mode = record.mode,
                    bytesCleaned = record.bytesCleaned,
                    freeSpaceBefore = record.freeSpaceBefore,
                    freeSpaceAfter = record.freeSpaceAfter,
                    success = record.success,
                    notesUrdu = record.notesUrdu,
                    timestamp = record.timestamp
                )
            )

            // Spoken announcement in Urdu
            voiceAssistant.speakUrdu(
                text = UrduVoiceAlert.PHRASE_CLEANUP_RESULT,
                dedupKey = "cleanup_result",
                force = true
            )

            refreshHealth()
            _isCleaning.value = false
            onComplete?.invoke(record)
        }
    }

    private fun initRepairAssistantIntro() {
        if (_repairMessages.value.isEmpty()) {
            _repairMessages.value = listOf(
                RepairAssistantMessage(
                    questionUrdu = "تعارف",
                    answerUrdu = "خوش آمدید! میں نیازی ڈاکٹر کا اسمارٹ اے آئی ریپیر اسسٹنٹ ہوں۔ میں آپ کے موبائل کے لائیو ہارڈویئر سینسرز اور اینڈرائیڈ ڈیٹا کی بنیاد پر مسائل کی حقیقی وجوہات تلاش کر کے محفوظ حل بتاتا ہوں۔ نیچے دیئے گئے سوالات میں سے انتخاب کریں یا مدد لیں۔",
                    evidenceUrdu = "سسٹم مانیٹرنگ اور لائیو ہارڈویئر سینسرز آن لائن ہیں۔",
                    recommendedActionUrdu = "اپنا مطلوبہ مسئلہ منتخب کریں۔",
                    confidenceUrdu = "100%"
                )
            )
        }
    }

    fun askRepairAssistant(questionUrdu: String) {
        val health = _deviceHealth.value
        val battery = health.battery
        val perf = health.performance
        val conn = health.connectivity

        val (answer, evidence, action, route, confidence) = when {
            questionUrdu.contains("سست") || questionUrdu.contains("Slow") -> {
                val ramPct = perf.usedRamPercentage
                val freeGb = String.format(java.util.Locale.US, "%.1f", perf.freeStorageBytes.toDouble() / (1024.0 * 1024.0 * 1024.0))
                val isStorageLow = perf.isLowStorage
                val isRamHigh = perf.isLowRam

                val reason = when {
                    isStorageLow && isRamHigh -> "آپ کے موبائل کی ریم $ramPct% مصروف ہے اور اسٹوریج بھی صرف $freeGb GB خالی ہے، جس سے اینڈرائیڈ سسٹم فائل کیشے نہیں بنا پا رہا۔"
                    isRamHigh -> "موبائل کی ریم پر دباؤ زیادہ ہے ($ramPct% ریم مصروف ہے)۔ بیک گراؤنڈ میں بہت سی ایپس ایک ساتھ کام کر رہی ہیں۔"
                    isStorageLow -> "انٹرنل اسٹوریج 90% سے زیادہ بھر چکی ہے جس کی وجہ سے ایپس سست چل رہی ہیں۔"
                    else -> "موجودہ دستیاب اعداد و شمار کے مطابق ریم اور اسٹوریج کی صورتحال تسلی بخش ہے۔"
                }

                val safeStep = "حالیہ کھلی ہوئی ایپس کو بند کریں، فون کو ایک بار ری اسٹارٹ کریں، اور اسمارٹ کیش کلینر سے غیر ضروری عارضی فائلیں صاف کریں۔"
                Pair5(
                    reason,
                    "ریم کا استعمال: $ramPct% • خالی اسٹوریج: $freeGb GB",
                    safeStep,
                    "cache_cleaner",
                    "94% (اینڈرائیڈ ایکٹیوٹی مینیجر ڈیٹا)"
                )
            }

            questionUrdu.contains("بیٹری") || questionUrdu.contains("Battery") -> {
                val temp = battery.temperatureCelsius
                val volt = battery.voltageMv
                val healthStr = battery.healthStatusUrdu

                val reason = if (temp >= 40f) {
                    "بیٹری کا درجہ حرارت $temp°C ہے جو کہ معمول سے زیادہ ہے۔ زیادہ گرمی کی وجہ سے بیٹری کے کیمیائی سیلز تیزی سے خارج ہوتے ہیں۔"
                } else {
                    "بیٹری کا موجودہ وولٹیج $volt mV اور اسٹیٹس '$healthStr' ہے۔ اگر بیٹری تیزی سے گرتی ہے تو ہائی برائٹنس یا بیک گراؤنڈ لوکیشن ایپس اس کی عام وجہ ہوتی ہیں۔"
                }

                val safeStep = "اسکرین کی چمک آٹو پر رکھیں، غیر ضروری لوکیشن اور بلوٹوتھ آف کریں، اور چارجنگ کے دوران موبائل کو استعمال نہ کریں۔"
                Pair5(
                    reason,
                    "بیٹری درجہ حرارت: $temp°C • وولٹیج: $volt mV • حالت: $healthStr",
                    safeStep,
                    "battery",
                    "90% (اینڈرائیڈ بیٹری ٹیلی میٹری)"
                )
            }

            questionUrdu.contains("گرم") || questionUrdu.contains("Overheat") -> {
                val temp = battery.temperatureCelsius
                val isCharging = battery.isCharging
                val reason = if (isCharging) {
                    "فون چارجنگ پر لگا ہے جس سے قدرتی طور پر گرمی پیدا ہوتی ہے۔ چارجنگ کے ساتھ گیمنگ یا بھاری ویڈیو چلانے سے فون زیادہ گرم ہو جاتا ہے۔"
                } else {
                    "بیٹری کا درجہ حرارت $temp°C ہے۔ تیز دھوپ، موٹا کور، یا بیک گراؤنڈ پروسیسنگ گرمی کی بڑی وجوہات ہیں۔"
                }
                val safeStep = "چارجر الگ کریں، موبائل کا بیک کور ہٹا دیں اور اسے 10 منٹ کے لیے پنکھے کے آگے سایہ دار جگہ پر ٹھنڈا ہونے دیں۔"
                Pair5(
                    reason,
                    "موجودہ بیٹری درجہ حرارت: $temp°C",
                    safeStep,
                    "battery",
                    "95% (ہارڈویئر تھرمسٹر ریڈنگ)"
                )
            }

            questionUrdu.contains("اسٹوریج") || questionUrdu.contains("Storage") -> {
                val freeGb = String.format(java.util.Locale.US, "%.1f", perf.freeStorageBytes.toDouble() / (1024.0 * 1024.0 * 1024.0))
                val usedPct = perf.usedStoragePercentage
                val reason = "موبائل کی کل انٹرنل اسٹوریج کا $usedPct% حصہ استعمال ہو چکا ہے اور صرف $freeGb GB جگہ باقی ہے۔ بڑی ویڈیوز، واٹس ایپ میڈیا اور ایپس کا کیشے اسٹوریج بھر دیتے ہیں۔"
                val safeStep = "اسمارٹ کیش کلینر سے عارضی فائلیں ہٹائیں اور بڑی ایپس کے کیشے کا جائزہ لے کر صفائی کریں۔"
                Pair5(
                    reason,
                    "استعمال شدہ اسٹوریج: $usedPct% • باقی: $freeGb GB",
                    safeStep,
                    "cache_cleaner",
                    "100% (فائل سسٹم ناپ)"
                )
            }

            questionUrdu.contains("وائی فائی") || questionUrdu.contains("Wi-Fi") || questionUrdu.contains("انٹرنیٹ") -> {
                val isWifi = conn.isWifiConnected
                val isOnline = conn.isInternetConnected
                val reason = if (isWifi && !isOnline) {
                    "موبائل وائی فائی راؤٹر سے تو جڑا ہوا ہے لیکن راؤٹر کا اپنا انٹرنیٹ کنکشن خراب یا منقطع ہے۔"
                } else if (!isWifi) {
                    "وائی فائی بند ہے یا سگنل بہت کمزور ہیں۔"
                } else {
                    "وائی فائی اور انٹرنیٹ دونوں کامیابی سے منسلک ہیں۔"
                }
                val safeStep = "ایروپلین موڈ 10 سیکنڈ آن کر کے بند کریں یا اپنے وائی فائی راؤٹر کا پاور پلگ نکال کر دوبارہ لگائیں۔"
                Pair5(
                    reason,
                    "وائی فائی اسٹیٹس: ${if (isWifi) "منسلک" else "منقطع"} • انٹرنیٹ توثیق: ${if (isOnline) "پاس" else "فیل"}",
                    safeStep,
                    "network",
                    "92% (اینڈرائیڈ نیٹ ورک اسٹیٹس)"
                )
            }

            questionUrdu.contains("چارج") || questionUrdu.contains("Charging") -> {
                val isCharging = battery.isCharging
                val plugType = battery.plugType
                val reason = if (!isCharging) {
                    "اس وقت کوئی چارجر منسلک نہیں ملا۔ اگر کیبل لگانے کے باوجود چارج نہیں ہو رہا تو کیبل میں کٹ، خراب اڈاپٹر یا پورٹ میں دھول ہو سکتی ہے۔"
                } else {
                    "موبائل $plugType سے چارج ہو رہا ہے اور کنکشن نارمل ہے۔"
                }
                val safeStep = "کیبل اور اڈاپٹر تبدیل کر کے دیکھیں، اور چارجنگ پورٹ کو لکڑی کے نرم ٹوتھ پک سے نرمی سے صاف کریں۔"
                Pair5(
                    reason,
                    "چارجنگ پورٹ اسٹیٹس: $plugType",
                    safeStep,
                    "charging",
                    "95% (پاور مینیجر براڈکاسٹ)"
                )
            }

            else -> {
                Pair5(
                    "نیازی ڈاکٹر نے آپ کے موبائل کے تمام ہارڈویئر سینسرز کا جائزہ لیا ہے۔ اگر کوئی مخصوص علامت سامنے آ رہی ہو تو متعلقہ آپشن کا انتخاب کریں۔",
                    "تمام بنیادی سینسرز اور بیٹری ڈیٹا فعال ہیں۔",
                    "خودکار اسکین چلائیں تاکہ تمام ماڈیولز کی جامع رپورٹ مل سکے۔",
                    "scan",
                    "85%"
                )
            }
        }

        val newMsg = RepairAssistantMessage(
            questionUrdu = questionUrdu,
            answerUrdu = answer,
            evidenceUrdu = evidence,
            recommendedActionUrdu = action,
            recommendedRoute = route,
            confidenceUrdu = confidence
        )

        _repairMessages.value = _repairMessages.value + newMsg
    }

    fun startFullCheckup(onComplete: (() -> Unit)? = null) {
        if (_isScanning.value) return
        viewModelScope.launch {
            _isScanning.value = true
            _scanProgress.value = 0.05f
            _scanStageText.value = "موبائل کی ساخت اور ہارڈویئر کی شناخت..."
            delay(400)

            _scanProgress.value = 0.25f
            _scanStageText.value = "بیٹری اور چارجنگ سسٹم کی پیمائش..."
            delay(500)

            _scanProgress.value = 0.50f
            _scanStageText.value = "ریم (RAM) اور اسٹوریج پروسیسنگ جانچ..."
            delay(500)

            _scanProgress.value = 0.75f
            _scanStageText.value = "نیٹ ورک، سگنلز اور انٹرنیٹ کا تجزیہ..."
            delay(450)

            _scanProgress.value = 0.90f
            _scanStageText.value = "ہارڈویئر سنسرز اور کیمرہ اسٹیٹس..."
            delay(400)

            val health = diagnosticEngine.runFullCheckup()
            _deviceHealth.value = health
            _scanProgress.value = 1.0f
            _scanStageText.value = "مکمل تجزیہ تیار ہے!"
            delay(300)

            _isScanning.value = false

            // Announce in Urdu if voice enabled
            val alert = diagnosticEngine.determineVoiceAlert(health)
            voiceAssistant.speakUrdu(
                text = alert.spokenTextUrdu,
                dedupKey = "scan_result",
                isCritical = health.healthScore < 70,
                force = false
            )

            // Record to history
            database.diagnosticDao().insertEvent(
                DiagnosticEventEntity(
                    alertType = alert.type.name,
                    severity = alert.severity.name,
                    titleUrdu = alert.titleUrdu,
                    messageUrdu = alert.spokenTextUrdu,
                    englishSummary = alert.englishSummary,
                    wasSpoken = true,
                    timestamp = System.currentTimeMillis()
                )
            )

            onComplete?.invoke()
        }
    }

    fun selectFinding(finding: DiagnosticFinding?) {
        _selectedFinding.value = finding
    }

    fun speakUrduMessage(text: String, isCritical: Boolean = false) {
        voiceAssistant.speakUrdu(text = text, isCritical = isCritical, force = true)
    }

    fun stopSpeaking() {
        voiceAssistant.stopSpeaking()
    }

    fun toggleVoiceAlerts(enabled: Boolean) {
        preferencesManager.setVoiceAlertsEnabled(enabled)
    }

    fun setSpeechRate(rate: Float) {
        preferencesManager.setSpeechRate(rate)
    }

    fun setSpeechLanguage(lang: String) {
        preferencesManager.setSpeechLanguage(lang)
    }

    fun toggleQuietHours(enabled: Boolean) {
        preferencesManager.setQuietHoursEnabled(enabled)
    }

    fun toggleBackgroundMonitoring(enabled: Boolean) {
        preferencesManager.setBackgroundMonitoringEnabled(enabled)
        val context = getApplication<Application>()
        val serviceIntent = Intent(context, NiaziDoctorService::class.java)
        if (enabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } catch (_: Exception) {}
        } else {
            try {
                context.stopService(serviceIntent)
            } catch (_: Exception) {}
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        preferencesManager.setDarkTheme(isDark)
    }

    fun setFirstRunCompleted() {
        preferencesManager.setFirstRunCompleted(true)
    }

    fun clearHistory() {
        viewModelScope.launch {
            database.diagnosticDao().clearAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceAssistant.release()
    }
}

private data class Pair5(
    val first: String,
    val second: String,
    val third: String,
    val fourth: String,
    val fifth: String
)
