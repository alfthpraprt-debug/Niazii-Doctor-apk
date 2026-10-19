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
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MedicalGreen
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TurquoiseAccent

@Composable
fun MonitoringSettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prefs = viewModel.preferencesManager
    val monitoringEnabled by prefs.backgroundMonitoringEnabled.collectAsState()
    val highTempThreshold by prefs.highTempThreshold.collectAsState()
    val lowStorageThreshold by prefs.lowStorageThresholdGb.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("monitoring_settings_screen")
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
                        text = "بیک گراؤنڈ مانیٹرنگ اور الرٹس",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Continuous Health Guard & Thresholds",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Monitoring Master Switch
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
                    Icon(Icons.Default.Security, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("مستقل پسِ منظر نگہبانی", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            text = if (monitoringEnabled) "فعال ہے • نوٹیفکیشن میں لائیو اسٹیٹس موجود ہے" else "غیر فعال ہے",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                    Switch(
                        checked = monitoringEnabled,
                        onCheckedChange = { viewModel.toggleBackgroundMonitoring(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary, checkedTrackColor = NavySurface)
                    )
                }
            }
        }

        // Temperature Alert Threshold
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
                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "زیادہ درجہ حرارت کا انتباہ: ${highTempThreshold.toInt()}°C",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "جب بیٹری کا درجہ حرارت اس حد سے تجاوز کرے گا تو اردو صوتی انتباہ سنائی دے گا۔",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = highTempThreshold,
                        onValueChange = { prefs.setHighTempThreshold(it) },
                        valueRange = 38f..46f,
                        steps = 7,
                        colors = SliderDefaults.colors(thumbColor = CyanPrimary, activeTrackColor = CyanPrimary)
                    )
                }
            }
        }

        // Low Storage Threshold
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
                        Icon(Icons.Default.SdStorage, contentDescription = null, tint = TurquoiseAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "کم اسٹوریج کی حد: ${String.format(java.util.Locale.US, "%.1f", lowStorageThreshold)} GB",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "خالی جگہ اس حد سے کم ہونے پر وارننگ جاری ہوگی۔",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = lowStorageThreshold,
                        onValueChange = { prefs.setLowStorageThresholdGb(it) },
                        valueRange = 1f..5f,
                        steps = 7,
                        colors = SliderDefaults.colors(thumbColor = CyanPrimary, activeTrackColor = CyanPrimary)
                    )
                }
            }
        }

        // Battery Optimization Guidance
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
                        Icon(Icons.Default.Info, contentDescription = null, tint = TurquoiseAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "اینڈرائیڈ بیٹری آپٹیمائزیشن اور پسِ منظر پالیسی",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "جدید اسمارٹ فونز میں ایپ بند ہونے کے بعد پسِ منظر میں کام جاری رکھنے کے لیے ضروری ہے کہ ایپ کو 'Battery Saver' میں 'Unrestricted' پر سیٹ کیا جائے تاکہ چارجنگ اور بیٹری کے انتباہات بروقت مل سکیں۔",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }
    }
}
