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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
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

@Composable
fun BatteryScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val health by viewModel.deviceHealth.collectAsState()
    val battery = health.battery

    val tempColor = when {
        battery.temperatureCelsius >= 42f -> AlertCritical
        battery.temperatureCelsius >= 39f -> AlertWarning
        else -> MedicalGreen
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("battery_screen")
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
                        text = "بیٹری اور پاور ڈائیگنوسٹکس",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Battery Health & Thermal Telemetry",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Live Battery Gauge Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(NavySurfaceVariant, NavySurface)
                        )
                    )
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary.copy(alpha = 0.15f))
                            .border(2.dp, CyanPrimary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "${battery.level}%",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = battery.statusText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TurquoiseAccent
                        )
                        Text(
                            text = "چارجر کی قسم: ${battery.plugType}",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        }

        // Telemetry Metrics Grid
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Temperature Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NavySurfaceVariant)
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = tempColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("درجہ حرارت", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${battery.temperatureCelsius}°C",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = tempColor
                        )
                        Text(
                            text = if (battery.temperatureCelsius < 39f) "معمول کے مطابق" else "گرم",
                            fontSize = 11.sp,
                            color = tempColor
                        )
                    }
                }

                // Voltage Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NavySurfaceVariant)
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("وولٹیج", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${battery.voltageMv} mV",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = battery.technology,
                            fontSize = 11.sp,
                            color = TurquoiseAccent
                        )
                    }
                }
            }
        }

        // Battery Health Details in Urdu
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
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = MedicalGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "اینڈرائیڈ سسٹم بیٹری رپورٹ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "بیٹری کی حالت: ${battery.healthStatusUrdu} (${battery.healthStatus})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "اگر موبائل کا درجہ حرارت 40 ڈگری سے زیادہ ہو تو چارجنگ روک کر موبائل کو ٹھنڈا ہونے دیں۔ مستند فاسٹ چارجر استعمال کریں تاکہ بیٹری کی عمر طویل رہے۔",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val msg = if (battery.temperatureCelsius >= 40f) {
                            "خبردار، موبائل کا درجۂ حرارت زیادہ ہو رہا ہے۔ اگر موبائل بہت گرم ہے تو چارجنگ روک دیں، اسے استعمال کرنا بند کریں اور ٹھنڈا ہونے دیں۔"
                        } else {
                            "نیازی ڈاکٹر کہہ رہا ہے، موبائل کی بیٹری کا درجہ حرارت اور وولٹیج بالکل نارمل اور محفوظ حد کے اندر ہے۔"
                        }
                        viewModel.speakUrduMessage(msg)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("صوتی رپورٹ سنیں (Speak Battery Status)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
