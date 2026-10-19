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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
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
fun NetworkScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val health by viewModel.deviceHealth.collectAsState()
    val conn = health.connectivity

    val isOnline = conn.isInternetConnected
    val onlineColor = if (isOnline) MedicalGreen else AlertWarning

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("network_screen")
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
                        text = "نیٹ ورک اور انٹرنیٹ جانچ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Connectivity & Signal Diagnostics",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Active State Card
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
                            .background(onlineColor.copy(alpha = 0.2f))
                            .border(1.5.dp, onlineColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.Public else Icons.Default.Warning,
                            contentDescription = null,
                            tint = onlineColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = if (isOnline) "انٹرنیٹ سروس فعال ہے" else "انٹرنیٹ منقطع ہے",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = conn.networkType,
                            fontSize = 14.sp,
                            color = TurquoiseAccent
                        )
                        if (conn.linkSpeedMbps > 0) {
                            Text(
                                text = "لنک اسپیڈ: ~${conn.linkSpeedMbps} Mbps",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }
            }
        }

        // Wi-Fi vs Cellular cards
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
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
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("وائی فائی", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(if (conn.isWifiConnected) "منسلک" else "غیر فعال", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

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
                            Icon(Icons.Default.SignalCellularAlt, contentDescription = null, tint = TurquoiseAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("سیلولر ڈیٹا", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(if (conn.isCellularConnected) "منسلک" else "غیر فعال", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Urdu Network Advice
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "سگنل اور کنیکٹیویٹی کا حل",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                TroubleshootStepItem(
                    stepNum = "1",
                    titleUrdu = "ایروپلین موڈ آن/آف",
                    descUrdu = "سیلولر انٹینا اور موڈیم ری سیٹ کرنے کے لیے فلائٹ موڈ 10 سیکنڈ آن رکھ کر بند کریں۔"
                )

                TroubleshootStepItem(
                    stepNum = "2",
                    titleUrdu = "وائی فائی راؤٹر کی بحالی",
                    descUrdu = "اگر وائی فائی جڑا ہے لیکن انٹرنیٹ نہیں چل رہا تو اپنے راؤٹر کا پاور کیبل نکال کر دوبارہ لگائیں۔"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val msg = "موبائل کے نیٹ ورک میں مسئلہ ہو سکتا ہے۔ سگنل، سم اور نیٹ ورک سیٹنگز چیک کریں۔"
                        viewModel.speakUrduMessage(msg)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("صوتی نیٹ ورک رپورٹ سنیں", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
