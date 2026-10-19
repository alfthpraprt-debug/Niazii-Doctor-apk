package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.ui.components.DiagnosticPulseIndicator
import com.example.ui.components.MetricCard
import com.example.ui.components.UrduAlertBanner
import com.example.ui.theme.AlertCritical
import com.example.ui.theme.AlertWarning
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MedicalGreen
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TurquoiseAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val health by viewModel.deviceHealth.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isCleaning by viewModel.isCleaning.collectAsState()
    val internalCacheBytes by viewModel.internalCacheBytes.collectAsState()
    val voiceEnabled by viewModel.preferencesManager.voiceAlertsEnabled.collectAsState()

    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val lastCheckStr = timeFormat.format(Date(health.lastCheckTime))

    val battery = health.battery
    val perf = health.performance
    val conn = health.connectivity

    val batteryColor = when {
        battery.temperatureCelsius >= 41f -> AlertCritical
        battery.level <= 15 -> AlertWarning
        else -> MedicalGreen
    }

    val storageColor = if (perf.isLowStorage) AlertWarning else CyanPrimary
    val connColor = if (conn.isInternetConnected) MedicalGreen else AlertWarning
    val freeGb = String.format(Locale.US, "%.1f", perf.freeStorageBytes.toDouble() / (1024.0 * 1024.0 * 1024.0))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Bar: Brand, Device Name, Quick Settings
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "نیازی ڈاکٹر",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyanPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AI Doctor",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseAccent
                            )
                        }
                    }

                    Text(
                        text = "${health.brand} ${health.model} (Android ${health.androidVersion})",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }

                // Voice Toggle button
                IconButton(
                    onClick = { viewModel.toggleVoiceAlerts(!voiceEnabled) },
                    modifier = Modifier
                        .testTag("btn_toggle_voice")
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (voiceEnabled) CyanPrimary.copy(alpha = 0.2f) else NavySurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Voice Alerts",
                        tint = if (voiceEnabled) CyanPrimary else TextMutedDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Refresh scan button
                IconButton(
                    onClick = {
                        viewModel.refreshHealth()
                        viewModel.refreshInternalCacheSize()
                    },
                    modifier = Modifier
                        .testTag("btn_refresh")
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(NavySurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. Central Animated AI Diagnostic Pulse
        item {
            DiagnosticPulseIndicator(
                statusTextUrdu = health.statusBadgeUrdu,
                score = health.healthScore,
                isScanning = isScanning,
                isSpeaking = isSpeaking,
                onClick = { onNavigate("scan") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 3. Spoken Urdu Advisory Banner
        item {
            val alert = viewModel.diagnosticEngine.determineVoiceAlert(health)
            UrduAlertBanner(
                titleUrdu = alert.titleUrdu,
                messageUrdu = alert.spokenTextUrdu,
                severityUrdu = if (health.healthScore < 70) "شدید" else "معمول",
                isSpeaking = isSpeaking,
                onSpeakClick = {
                    if (isSpeaking) {
                        viewModel.stopSpeaking()
                    } else {
                        viewModel.speakUrduMessage(alert.spokenTextUrdu, isCritical = health.healthScore < 70)
                    }
                },
                onDetailClick = {
                    val finding = health.findings.firstOrNull()
                    viewModel.selectFinding(finding)
                    onNavigate("diagnosis")
                }
            )
        }

        // 4. Primary Actions (Full Scan & AI Repair Assistant)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onNavigate("scan") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanPrimary,
                        contentColor = Color(0xFF040814)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مکمل فون اسکین", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onNavigate("repair_assistant") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TurquoiseAccent,
                        contentColor = Color(0xFF040814)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI ریپیر اسسٹنٹ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 5. Smart Cache Cleaner Flagship Banner Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(NavySurfaceVariant, NavySurface)
                        )
                    )
                    .border(1.2.dp, CyanPrimary.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                    .clickable { onNavigate("cache_cleaner") }
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("اسمارٹ کیش کلینر AI", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                text = "دستیاب کیش: ${viewModel.cacheCleanerEngine.formatBytes(internalCacheBytes)} • مفت جگہ: $freeGb GB",
                                fontSize = 11.sp,
                                color = TurquoiseAccent
                            )
                        }

                        Button(
                            onClick = { viewModel.runModeACleanup() },
                            enabled = !isCleaning,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isCleaning) {
                                CircularProgressIndicator(color = Color(0xFF040814), strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            } else {
                                Text("صاف کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 6. Hardware Status Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ہارڈویئر اور سسٹم ڈائیگنوسٹکس",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "آخری چیک: $lastCheckStr",
                    fontSize = 11.sp,
                    color = TextMutedDark
                )
            }
        }

        // 7. Metric Cards Grid
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricCard(
                    title = "Battery & Power",
                    titleUrdu = "بیٹری اسٹیٹس",
                    value = "${battery.level}% (${battery.statusText})",
                    subValue = "${battery.temperatureCelsius}°C • ${battery.voltageMv} mV",
                    icon = Icons.Default.BatteryChargingFull,
                    statusColor = batteryColor,
                    onClick = { onNavigate("battery") },
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Charging Health",
                    titleUrdu = "چارجنگ پورٹ",
                    value = battery.plugType,
                    subValue = "صحت: ${battery.healthStatusUrdu}",
                    icon = Icons.Default.Cable,
                    statusColor = if (battery.isCharging) CyanPrimary else TextSecondaryDark,
                    onClick = { onNavigate("charging") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricCard(
                    title = "Smart Cache Cleaner",
                    titleUrdu = "اسمارٹ کیش کلینر",
                    value = "$freeGb GB خالی",
                    subValue = "موڈ A, B, C صفائی",
                    icon = Icons.Default.CleaningServices,
                    statusColor = storageColor,
                    onClick = { onNavigate("cache_cleaner") },
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "App Storage Review",
                    titleUrdu = "ایپس اسٹوریج",
                    value = "${viewModel.installedApps.value.size} ایپس",
                    subValue = "کیشے سیٹنگز جائزہ",
                    icon = Icons.Default.Apps,
                    statusColor = TurquoiseAccent,
                    onClick = { onNavigate("app_storage") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricCard(
                    title = "Software & OS",
                    titleUrdu = "سافٹ ویئر و سسٹم",
                    value = "Android ${health.androidVersion}",
                    subValue = "پیچ: ${health.software.securityPatch}",
                    icon = Icons.Default.SystemUpdate,
                    statusColor = MedicalGreen,
                    onClick = { onNavigate("software") },
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Network & Wi-Fi",
                    titleUrdu = "انٹرنیٹ سگنلز",
                    value = conn.networkType,
                    subValue = if (conn.isInternetConnected) "آن لائن (فعال)" else "منقطع",
                    icon = Icons.Default.Wifi,
                    statusColor = connColor,
                    onClick = { onNavigate("network") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricCard(
                    title = "Display & Touch",
                    titleUrdu = "اسکرین اور ٹچ",
                    value = "${health.display.widthPx}x${health.display.heightPx}",
                    subValue = "انٹرایکٹو ڈیجٹائزر ٹیسٹ",
                    icon = Icons.Default.TouchApp,
                    statusColor = CyanPrimary,
                    onClick = { onNavigate("display_touch") },
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Sensors & Hardware",
                    titleUrdu = "سنسرز لسٹ",
                    value = "${health.sensors.totalSensors} سنسرز",
                    subValue = "تمام ہارڈویئر حاضر",
                    icon = Icons.Default.Sensors,
                    statusColor = TurquoiseAccent,
                    onClick = { onNavigate("sensors") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 8. Interactive Audio & Mic Test
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricCard(
                    title = "Speaker & Mic",
                    titleUrdu = "اسپیکر و مائیک ٹیسٹ",
                    value = "آڈیو لوپ بیک",
                    subValue = "440Hz ٹون اور ریکارڈنگ",
                    icon = Icons.Default.GraphicEq,
                    statusColor = MedicalGreen,
                    onClick = { onNavigate("audio_test") },
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "System History",
                    titleUrdu = "انتباہات کی تاریخ",
                    value = "لاگز اور الرٹس",
                    subValue = "ماضی کے تمام ریکارڈز",
                    icon = Icons.Default.History,
                    statusColor = TextSecondaryDark,
                    onClick = { onNavigate("history") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 9. Bottom Action Bar: Monitoring, Voice Settings, Permissions, Help
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "سیٹنگز اور نگہبانی",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { onNavigate("voice_settings") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("صوتی سیٹنگز", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { onNavigate("monitoring_settings") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("بیک گراؤنڈ", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { onNavigate("permissions") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("اجازتیں", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { onNavigate("report") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("میڈیکل رپورٹ", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = { onNavigate("help") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("رہنمائی اور اکثر پوچھے جانے والے سوالات (Help & FAQs)", fontSize = 11.sp)
                }
            }
        }
    }
}
