package com.example.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.local.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.Locale

class UrduVoiceAssistant(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _lastSpokenMessage = MutableStateFlow<String?>(null)
    val lastSpokenMessage: StateFlow<String?> = _lastSpokenMessage.asStateFlow()

    // Deduplication map: AlertType/Key -> lastSpokenTimestamp
    private val alertCooldowns = mutableMapOf<String, Long>()
    private val cooldownDurationMs = 15 * 60 * 1000L // 15 minutes

    init {
        initTts()
    }

    private fun initTts() {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {
            _isInitialized.value = false
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsEngine = tts ?: return

            // Prefer Urdu, then Hindi, then device default
            val urduLocale = Locale("ur", "PK")
            val urduResult = ttsEngine.setLanguage(urduLocale)

            if (urduResult == TextToSpeech.LANG_MISSING_DATA || urduResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                val hindiLocale = Locale("hi", "IN")
                val hiResult = ttsEngine.setLanguage(hindiLocale)
                if (hiResult == TextToSpeech.LANG_MISSING_DATA || hiResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ttsEngine.language = Locale.getDefault()
                }
            }

            ttsEngine.setPitch(1.0f)
            ttsEngine.setSpeechRate(preferencesManager.speechRate.value)

            ttsEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })

            _isInitialized.value = true
        } else {
            _isInitialized.value = false
        }
    }

    fun speakUrdu(
        text: String,
        dedupKey: String? = null,
        isCritical: Boolean = false,
        force: Boolean = false
    ): Boolean {
        if (!preferencesManager.voiceAlertsEnabled.value && !force) {
            return false
        }

        // Quiet Hours check (skip if not critical and not forced)
        if (!force && !isCritical && preferencesManager.quietHoursEnabled.value) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (hour >= 22 || hour < 7) {
                return false
            }
        }

        // Deduplication check
        val now = System.currentTimeMillis()
        if (dedupKey != null && !force) {
            val lastSpoken = alertCooldowns[dedupKey] ?: 0L
            if (now - lastSpoken < cooldownDurationMs) {
                return false // Suppress duplicate
            }
        }

        val engine = tts
        if (engine == null || !_isInitialized.value) {
            return false
        }

        engine.setSpeechRate(preferencesManager.speechRate.value)
        val utteranceId = "niazi_${System.currentTimeMillis()}"
        val result = engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)

        if (result == TextToSpeech.SUCCESS) {
            if (dedupKey != null) {
                alertCooldowns[dedupKey] = now
            }
            _lastSpokenMessage.value = text
            return true
        }
        return false
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _isInitialized.value = false
    }
}
