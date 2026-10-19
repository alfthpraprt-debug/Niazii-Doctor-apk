package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.data.model.UrduVoiceAlert
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TurquoiseAccent

@Composable
fun VoiceSettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prefs = viewModel.preferencesManager
    val voiceEnabled by prefs.voiceAlertsEnabled.collectAsState()
    val speechRate by prefs.speechRate.collectAsState()
    val language by prefs.speechLanguage.collectAsState()
    val quietHours by prefs.quietHoursEnabled.collectAsState()
    val importantOnly by prefs.speakImportantOnly.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("voice_settings_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "اردو وائس اسسٹنٹ سیٹنگز",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Conversational Hindustani-Urdu TTS Controls",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Master Voice Switch
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("صوتی انتباہات (Voice Alerts)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(if (voiceEnabled) "فعال ہے • آواز کے ذریعے خبردار کرے گا" else "خاموش موڈ", fontSize = 12.sp, color = TextSecondaryDark)
                    }
                    Switch(
                        checked = voiceEnabled,
                        onCheckedChange = { viewModel.toggleVoiceAlerts(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary, checkedTrackColor = NavySurface)
                    )
                }
            }
        }

        // Language Selector
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = TurquoiseAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("صوتی لہجہ اور زبان (Voice Dialect)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.setSpeechLanguage("ur") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (language == "ur") CyanPrimary else NavySurface,
                                contentColor = if (language == "ur") Color(0xFF040814) else Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("اردو (پاکستان)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.setSpeechLanguage("hi") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (language == "hi") CyanPrimary else NavySurface,
                                contentColor = if (language == "hi") Color(0xFF040814) else Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ہندوستانی / ہندی", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Speech Rate Slider
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("بولنے کی رفتار (Speed: ${String.format(java.util.Locale.US, "%.1f", speechRate)}x)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = speechRate,
                        onValueChange = { viewModel.setSpeechRate(it) },
                        valueRange = 0.7f..1.5f,
                        steps = 7,
                        colors = SliderDefaults.colors(thumbColor = CyanPrimary, activeTrackColor = CyanPrimary)
                    )
                }
            }
        }

        // Quiet Hours Switch
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bedtime, contentDescription = null, tint = TurquoiseAccent, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("رات کے اوقات میں خاموشی (Quiet Hours)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("رات 10 بجے سے صبح 7 بجے تک غیر ضروری الرٹس خاموش رہیں گے", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                    Switch(
                        checked = quietHours,
                        onCheckedChange = { viewModel.toggleQuietHours(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary, checkedTrackColor = NavySurface)
                    )
                }
            }
        }

        // Test Voice Buttons
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("صوتی پیغامات کا لائیو ٹیسٹ", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))

                TestVoiceItem(
                    label = "1. معمول کی حالت",
                    phrase = UrduVoiceAlert.PHRASE_NORMAL,
                    onClick = { viewModel.speakUrduMessage(UrduVoiceAlert.PHRASE_NORMAL) }
                )

                TestVoiceItem(
                    label = "2. چارجنگ کا مسئلہ",
                    phrase = UrduVoiceAlert.PHRASE_CHARGING,
                    onClick = { viewModel.speakUrduMessage(UrduVoiceAlert.PHRASE_CHARGING) }
                )

                TestVoiceItem(
                    label = "3. زیادہ درجۂ حرارت",
                    phrase = UrduVoiceAlert.PHRASE_TEMPERATURE,
                    onClick = { viewModel.speakUrduMessage(UrduVoiceAlert.PHRASE_TEMPERATURE) }
                )

                TestVoiceItem(
                    label = "4. کم اسٹوریج کا انتباہ",
                    phrase = UrduVoiceAlert.PHRASE_STORAGE,
                    onClick = { viewModel.speakUrduMessage(UrduVoiceAlert.PHRASE_STORAGE) }
                )
            }
        }
    }
}

@Composable
fun TestVoiceItem(
    label: String,
    phrase: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TurquoiseAccent)
            Text(phrase, fontSize = 11.sp, maxLines = 1, color = TextSecondaryDark)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814))
        ) {
            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("سنیں", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
