package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
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
fun ReportScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val health by viewModel.deviceHealth.collectAsState()
    val timeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
    val timestampStr = timeFormat.format(Date(health.lastCheckTime))

    val battery = health.battery
    val perf = health.performance
    val freeGb = String.format(Locale.US, "%.1f", perf.freeStorageBytes.toDouble() / (1024.0 * 1024.0 * 1024.0))
    val totalGb = String.format(Locale.US, "%.1f", perf.totalStorageBytes.toDouble() / (1024.0 * 1024.0 * 1024.0))

    val reportText = """
        === نیازی ڈاکٹر (NIAZI DOCTOR) DIAGNOSTIC REPORT ===
        Date: $timestampStr
        Device: ${health.brand} ${health.model} (Android ${health.androidVersion})
        Health Score: ${health.healthScore}/100 (${health.statusBadgeUrdu})
        
        [BATTERY & POWER]
        - Level: ${battery.level}% (${battery.statusText})
        - Temperature: ${battery.temperatureCelsius}°C
        - Voltage: ${battery.voltageMv} mV
        - Health: ${battery.healthStatus} (${battery.healthStatusUrdu})
        - Charger: ${battery.plugType}
        
        [PERFORMANCE & STORAGE]
        - Free Storage: $freeGb GB / $totalGb GB (${perf.usedStoragePercentage}% used)
        - RAM Usage: ${perf.usedRamPercentage}%
        
        [CONNECTIVITY]
        - Network: ${health.connectivity.networkType}
        - Internet Validated: ${health.connectivity.isInternetConnected}
        
        [SENSORS & HARDWARE]
        - Total Hardware Sensors: ${health.sensors.totalSensors}
        - Total Cameras: ${health.camera.totalCameras}
        - Active Warnings: ${health.findings.size}
        ==================================================
    """.trimIndent()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("report_screen")
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "مکمل تشخیصی میڈیکل رپورٹ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Full Device Certificate & Diagnostic Summary",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }

                IconButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, reportText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "تشخیصی رپورٹ شیئر کریں"))
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = CyanPrimary)
                }
            }
        }

        // Summary Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("فون ہیلتھ سرٹیفکیٹ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("تاریخ: $timestampStr", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${health.healthScore}/100",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (health.healthScore >= 90) MedicalGreen else AlertWarning
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ReportDetailRow("موبائل ماڈل:", "${health.brand} ${health.model}")
                    ReportDetailRow("اینڈرائیڈ ورژن:", "Android ${health.androidVersion} (API ${health.sdkInt})")
                    ReportDetailRow("بیٹری کی حالت:", "${battery.level}% • ${battery.temperatureCelsius}°C • ${battery.healthStatusUrdu}")
                    ReportDetailRow("اسٹوریج اور میموری:", "$freeGb GB خالی • ریم: ${perf.usedRamPercentage}% مصروف")
                    ReportDetailRow("انٹرنیٹ کنکشن:", "${health.connectivity.networkType} (${if (health.connectivity.isInternetConnected) "آن لائن" else "آف لائن"})")
                    ReportDetailRow("ہارڈویئر سنسرز:", "${health.sensors.totalSensors} سنسرز فعال")
                }
            }
        }

        // Share CTA Button
        item {
            Button(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, reportText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "شیئر تشخیصی رپورٹ"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("یہ تشخیصی رپورٹ شیئر / محفوظ کریں", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReportDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TurquoiseAccent, modifier = Modifier.width(130.dp))
        Text(text = value, fontSize = 12.sp, color = Color.White)
    }
}
