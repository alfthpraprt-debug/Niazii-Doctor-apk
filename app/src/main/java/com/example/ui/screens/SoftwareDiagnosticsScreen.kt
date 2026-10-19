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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
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

@Composable
fun SoftwareDiagnosticsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val health by viewModel.deviceHealth.collectAsState()
    val software = health.software

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("software_diagnostics_screen")
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
                        text = "سافٹ ویئر اور سسٹم تشخیص",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Android OS & System Configuration Diagnostics",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // OS Specifications Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "اینڈرائیڈ آپریٹنگ سسٹم تفصیلات",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                SoftwareItemRow("ورژن:", "Android ${software.androidVersion}")
                SoftwareItemRow("API لیول (SDK):", "API ${software.sdkInt}")
                SoftwareItemRow("سیکیورٹی پیچ لیول:", software.securityPatch)
                SoftwareItemRow("سی پی یو آرکیٹیکچر:", software.kernelArch)
                SoftwareItemRow("ڈیوائس برانڈ / ماڈل:", "${health.brand} ${health.model}")
                SoftwareItemRow("سسٹم مینوفیکچرر:", health.manufacturer)
            }
        }

        // Power Management & System State
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BatterySaver, contentDescription = null, tint = TurquoiseAccent, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "پاور مینیجمنٹ اور سسٹم اسٹیٹس",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                SoftwareItemRow(
                    label = "بیٹری سیور موڈ:",
                    value = if (software.isPowerSaveMode) "آن ہے (کارکردگی محدود)" else "آف ہے (مکمل رفتار)",
                    highlightColor = if (software.isPowerSaveMode) AlertWarning else MedicalGreen
                )

                SoftwareItemRow(
                    label = "ڈیوائس آئیڈل (Doze) اسٹیٹس:",
                    value = if (software.isDeviceIdleMode) "آئیڈل موڈ میں ہے" else "فعال استعمال میں ہے"
                )

                SoftwareItemRow(
                    label = "اسٹوریج براڈکاسٹ وارننگ:",
                    value = if (health.performance.isLowStorage) "کم اسٹوریج کی انتباہ فعال ہے" else "معمول کے مطابق ہے",
                    highlightColor = if (health.performance.isLowStorage) AlertWarning else MedicalGreen
                )
            }
        }

        // Niazi Doctor Package Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "نیازی ڈاکٹر ایپ پیکج سالمیت",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                SoftwareItemRow("ایپ ورژن:", "v${software.appVersionName} (Build ${software.appVersionCode})")
                SoftwareItemRow("رن ٹائم آرکیٹیکچر:", "Native Kotlin / Jetpack Compose")
                SoftwareItemRow("ڈیٹا پروسیسنگ:", "100% On-Device Local Privacy")
            }
        }
    }
}

@Composable
fun SoftwareItemRow(
    label: String,
    value: String,
    highlightColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TurquoiseAccent, modifier = Modifier.width(160.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = highlightColor)
    }
}
