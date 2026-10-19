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
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.VolumeUp
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
fun ChargingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val health by viewModel.deviceHealth.collectAsState()
    val battery = health.battery

    val isPlugged = battery.isCharging || battery.plugType != "Disconnected"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("charging_screen")
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
                        text = "چارجنگ اور کیبل تشخیص",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Charging Port & Circuit Diagnostics",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Connection Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(18.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isPlugged) MedicalGreen.copy(alpha = 0.2f) else NavySurface)
                            .border(1.5.dp, if (isPlugged) MedicalGreen else TextMutedDark, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlugged) Icons.Default.Power else Icons.Default.PowerOff,
                            contentDescription = null,
                            tint = if (isPlugged) MedicalGreen else TextMutedDark,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = if (isPlugged) "چارجر منسلک ہے" else "چارجر منسلک نہیں",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = battery.plugType,
                            fontSize = 14.sp,
                            color = TurquoiseAccent
                        )
                        Text(
                            text = "حالت: ${battery.statusText}",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        }

        // Diagnostic Troubleshooting Guide in Urdu
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
                        imageVector = Icons.Default.Cable,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "چارجنگ مسائل کا حل اور ہدایات",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TroubleshootStepItem(
                    stepNum = "1",
                    titleUrdu = "کیبل اور اڈاپٹر کی تصدیق",
                    descUrdu = "ہمیشہ معیاری کیبل استعمال کریں۔ اگر کیبل مڑی ہوئی ہو یا کٹی ہو تو چارجنگ بار بار رکتی ہے۔"
                )

                TroubleshootStepItem(
                    stepNum = "2",
                    titleUrdu = "چارجنگ پورٹ کی صفائی",
                    descUrdu = "پورٹ میں اکثر جیب کی دھول جمع ہو جاتی ہے جس سے کنکشن ڈھیلا ہو جاتا ہے۔ لکڑی کے نرم ٹوتھ پک سے نرمی سے صاف کریں۔"
                )

                TroubleshootStepItem(
                    stepNum = "3",
                    titleUrdu = "شارٹ سرکٹ سے حفاظت",
                    descUrdu = "بھیگے ہوئے یا نم چارجنگ پورٹ میں کیبل نہ لگائیں۔ خشک ہونے کا انتظار کریں۔"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val msg = "توجہ دیں، موبائل کی چارجنگ میں مسئلہ ہو سکتا ہے۔ پہلے چارجر اور کیبل چیک کریں۔ اگر مسئلہ برقرار رہے تو چارجنگ پورٹ کی جانچ کروائیں۔"
                        viewModel.speakUrduMessage(msg)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("چارجنگ صوتی انتباہ سنیں", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TroubleshootStepItem(
    stepNum: String,
    titleUrdu: String,
    descUrdu: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(CyanPrimary.copy(alpha = 0.2f))
                .border(1.dp, CyanPrimary, CircleShape)
        ) {
            Text(text = stepNum, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = titleUrdu,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = descUrdu,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = TextSecondaryDark
            )
        }
    }
}
