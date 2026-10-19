package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("niazi_doctor_prefs", Context.MODE_PRIVATE)

    private val _voiceAlertsEnabled = MutableStateFlow(prefs.getBoolean("voice_alerts_enabled", true))
    val voiceAlertsEnabled: StateFlow<Boolean> = _voiceAlertsEnabled.asStateFlow()

    private val _speechRate = MutableStateFlow(prefs.getFloat("speech_rate", 1.0f))
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _speechLanguage = MutableStateFlow(prefs.getString("speech_language", "ur") ?: "ur")
    val speechLanguage: StateFlow<String> = _speechLanguage.asStateFlow()

    private val _speakImportantOnly = MutableStateFlow(prefs.getBoolean("speak_important_only", false))
    val speakImportantOnly: StateFlow<Boolean> = _speakImportantOnly.asStateFlow()

    private val _quietHoursEnabled = MutableStateFlow(prefs.getBoolean("quiet_hours_enabled", false))
    val quietHoursEnabled: StateFlow<Boolean> = _quietHoursEnabled.asStateFlow()

    private val _backgroundMonitoringEnabled = MutableStateFlow(prefs.getBoolean("bg_monitoring_enabled", true))
    val backgroundMonitoringEnabled: StateFlow<Boolean> = _backgroundMonitoringEnabled.asStateFlow()

    private val _highTempThreshold = MutableStateFlow(prefs.getFloat("high_temp_threshold", 41.0f))
    val highTempThreshold: StateFlow<Float> = _highTempThreshold.asStateFlow()

    private val _lowStorageThresholdGb = MutableStateFlow(prefs.getFloat("low_storage_threshold", 2.0f))
    val lowStorageThresholdGb: StateFlow<Float> = _lowStorageThresholdGb.asStateFlow()

    private val _firstRunCompleted = MutableStateFlow(prefs.getBoolean("first_run_completed", false))
    val firstRunCompleted: StateFlow<Boolean> = _firstRunCompleted.asStateFlow()

    private val _darkTheme = MutableStateFlow(prefs.getBoolean("dark_theme", true))
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    fun setVoiceAlertsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("voice_alerts_enabled", enabled).apply()
        _voiceAlertsEnabled.value = enabled
    }

    fun setSpeechRate(rate: Float) {
        prefs.edit().putFloat("speech_rate", rate).apply()
        _speechRate.value = rate
    }

    fun setSpeechLanguage(lang: String) {
        prefs.edit().putString("speech_language", lang).apply()
        _speechLanguage.value = lang
    }

    fun setSpeakImportantOnly(importantOnly: Boolean) {
        prefs.edit().putBoolean("speak_important_only", importantOnly).apply()
        _speakImportantOnly.value = importantOnly
    }

    fun setQuietHoursEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("quiet_hours_enabled", enabled).apply()
        _quietHoursEnabled.value = enabled
    }

    fun setBackgroundMonitoringEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("bg_monitoring_enabled", enabled).apply()
        _backgroundMonitoringEnabled.value = enabled
    }

    fun setHighTempThreshold(threshold: Float) {
        prefs.edit().putFloat("high_temp_threshold", threshold).apply()
        _highTempThreshold.value = threshold
    }

    fun setLowStorageThresholdGb(threshold: Float) {
        prefs.edit().putFloat("low_storage_threshold", threshold).apply()
        _lowStorageThresholdGb.value = threshold
    }

    fun setFirstRunCompleted(completed: Boolean) {
        prefs.edit().putBoolean("first_run_completed", completed).apply()
        _firstRunCompleted.value = completed
    }

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean("dark_theme", isDark).apply()
        _darkTheme.value = isDark
    }
}
