package com.example.data.model

enum class AlertType {
    NORMAL_STATUS,
    CHARGING_ISSUE,
    HIGH_TEMPERATURE,
    STORAGE_WARNING,
    BATTERY_WARNING,
    NETWORK_ISSUE,
    MEMORY_PRESSURE,
    SENSOR_FAULT,
    SOFTWARE_WARNING,
    CLEANUP_RESULT
}

data class UrduVoiceAlert(
    val id: String,
    val type: AlertType,
    val severity: DiagnosticSeverity,
    val titleUrdu: String,
    val spokenTextUrdu: String,
    val englishSummary: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val PHRASE_NORMAL = "نیازی ڈاکٹر کہہ رہا ہے، ابھی تک دستیاب معلومات میں کوئی خاص مسئلہ نظر نہیں آیا۔"
        const val PHRASE_CHARGING = "توجہ دیں! چارجنگ میں ممکنہ مسئلہ سامنے آیا ہے۔ چارجر، کیبل اور چارجنگ پورٹ کو محفوظ طریقے سے چیک کریں۔"
        const val PHRASE_TEMPERATURE = "خبردار! موبائل کا درجۂ حرارت زیادہ رپورٹ ہو رہا ہے۔ اگر فون بہت گرم ہے تو چارجنگ روک دیں، استعمال کم کریں اور اسے ٹھنڈا ہونے دیں۔"
        const val PHRASE_STORAGE = "توجہ دیں! آپ کے موبائل میں اسٹوریج کم ہو رہی ہے۔ نیازی ڈاکٹر کھول کر اسٹوریج کی جانچ کریں اور غیر ضروری فائلوں کو محفوظ طریقے سے صاف کریں۔"
        const val PHRASE_BATTERY = "موبائل کی بیٹری سے متعلق ایک ممکنہ مسئلہ سامنے آیا ہے۔ دستیاب معلومات چیک کریں اور ضرورت پڑنے پر بیٹری کی جانچ کروائیں۔"
        const val PHRASE_NETWORK = "موبائل کے نیٹ ورک میں مسئلہ ہو سکتا ہے۔ سگنل، سم اور نیٹ ورک سیٹنگز چیک کریں۔"
        const val PHRASE_SOFTWARE_WARNING = "نیازی ڈاکٹر نے موبائل کی ایک سیٹنگ یا سسٹم سے متعلق ممکنہ مسئلہ نوٹ کیا ہے۔ تفصیلات اور تجویز کردہ حل دیکھنے کے لیے نوٹیفکیشن کھولیں۔"
        const val PHRASE_CLEANUP_RESULT = "اسٹوریج کی جانچ مکمل ہو گئی ہے۔ نتائج دیکھیں اور دستیاب کیش صاف کرنے کے اختیارات استعمال کریں۔"
    }
}
