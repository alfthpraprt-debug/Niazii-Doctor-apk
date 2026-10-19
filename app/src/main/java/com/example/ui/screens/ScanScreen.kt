package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.DiagnosticSeverity
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
fun ScanScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onViewDiagnosis: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isScanning by viewModel.isScanning.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val stageText by viewModel.scanStageText.collectAsState()
    val health by viewModel.deviceHealth.collectAsState()

    val animatedProgress by animateFloatAsState(
        targetValue = scanProgress,
        animationSpec = tween(300, easing = LinearEasing),
        label = "scan_progress"
    )

    LaunchedEffect(Unit) {
        if (!isScanning) {
            viewModel.startFullCheckup()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("scan_screen")
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                        text = "خودکار فون اسکین",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Automated Hardware Diagnostics",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Circular Scanning HUD
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(NavySurfaceVariant, NavySurface)
                        )
                    )
                    .border(2.dp, CyanPrimary.copy(alpha = 0.5f), CircleShape)
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        strokeWidth = 6.dp,
                        color = CyanPrimary,
                        trackColor = NavyCardBorder,
                        modifier = Modifier.size(170.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanPrimary
                    )
                    Text(
                        text = if (isScanning) "اسکیننگ..." else "مکمل",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TurquoiseAccent
                    )
                }
            }
        }

        // Stage progress bar and Urdu text
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = CyanPrimary,
                    trackColor = NavySurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stageText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        // Inspected Checkpoints
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
                    text = "تشخیصی جانچ کی فہرست",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                DiagnosticCheckRow(
                    labelUrdu = "بیٹری اور چارجنگ ٹمپریچر",
                    labelEnglish = "Battery Temp & Voltage (${health.battery.temperatureCelsius}°C)",
                    isDone = animatedProgress >= 0.25f,
                    isWarn = health.battery.temperatureCelsius >= 40f
                )

                DiagnosticCheckRow(
                    labelUrdu = "ریم (RAM) اور انٹرنل اسٹوریج",
                    labelEnglish = "Memory & Storage Capacity (${health.performance.usedStoragePercentage}% used)",
                    isDone = animatedProgress >= 0.50f,
                    isWarn = health.performance.isLowStorage
                )

                DiagnosticCheckRow(
                    labelUrdu = "نیٹ ورک، وائی فائی اور انٹرنیٹ",
                    labelEnglish = "Internet Connection (${health.connectivity.networkType})",
                    isDone = animatedProgress >= 0.75f,
                    isWarn = !health.connectivity.isInternetConnected
                )

                DiagnosticCheckRow(
                    labelUrdu = "ہارڈویئر سنسرز اور کیمرہ",
                    labelEnglish = "Sensors & Camera Components (${health.sensors.totalSensors} active)",
                    isDone = animatedProgress >= 0.90f,
                    isWarn = false
                )
            }
        }

        // Action Buttons upon completion
        item {
            AnimatedVisibility(visible = !isScanning) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onViewDiagnosis,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "AI تشخیصی رپورٹ اور حل دیکھیں",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.startFullCheckup() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "دوبارہ اسکین چلائیں (Re-scan)",
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticCheckRow(
    labelUrdu: String,
    labelEnglish: String,
    isDone: Boolean,
    isWarn: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        val icon = when {
            !isDone -> Icons.Default.PlayArrow
            isWarn -> Icons.Default.Warning
            else -> Icons.Default.CheckCircle
        }

        val iconColor = when {
            !isDone -> TextMutedDark
            isWarn -> AlertWarning
            else -> MedicalGreen
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = labelUrdu,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDone) Color.White else TextMutedDark
            )
            Text(
                text = labelEnglish,
                fontSize = 11.sp,
                color = TextSecondaryDark
            )
        }

        Text(
            text = when {
                !isDone -> "انتظار..."
                isWarn -> "انتباہ"
                else -> "پاس"
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = iconColor
        )
    }
}
