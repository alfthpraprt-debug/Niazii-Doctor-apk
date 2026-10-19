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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.data.model.DiagnosticFinding
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
fun AiDiagnosisScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val health by viewModel.deviceHealth.collectAsState()
    val findings = health.findings

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_diagnosis_screen")
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
                        text = "AI تشخیصی رپورٹ و تجزیہ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "AI Clinical Phone Diagnosis & Safe Repair Guide",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Overview Score
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(NavySurfaceVariant, NavySurface)
                        )
                    )
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = health.statusBadgeUrdu,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "مجموعی صحت کا اسکور: ${health.healthScore}/100",
                            fontSize = 13.sp,
                            color = TurquoiseAccent
                        )
                    }
                }
            }
        }

        if (findings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(NavySurfaceVariant)
                        .border(1.dp, MedicalGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MedicalGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "مبارک ہو! کوئی مسئلہ نہیں ملا",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "نیازی ڈاکٹر نے تمام دستیاب ہارڈویئر پیمائشیں چیک کر لی ہیں۔ بیٹری، میموری، اسٹوریج اور سنسرز بہترین حالت میں کام کر رہے ہیں۔",
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                viewModel.speakUrduMessage("نیازی ڈاکٹر کہہ رہا ہے، ابھی تک دستیاب معلومات میں کوئی خاص مسئلہ نظر نہیں آیا۔ سب ٹھیک چل رہا ہے۔")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("صوتی توثیق سنیں", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            item {
                Text(
                    text = "شناخت شدہ مسائل اور حل (${findings.size} امور)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            items(findings) { finding ->
                DiagnosticFindingCard(finding = finding, onSpeak = {
                    val fullSpeech = "${finding.titleUrdu}۔ ${finding.evidenceUrdu} گھر پر حل: ${finding.homeSafeActionUrdu}"
                    viewModel.speakUrduMessage(fullSpeech, isCritical = finding.severity == DiagnosticSeverity.CRITICAL)
                })
            }
        }
    }
}

@Composable
fun DiagnosticFindingCard(
    finding: DiagnosticFinding,
    onSpeak: () -> Unit
) {
    val borderColor = when (finding.severity) {
        DiagnosticSeverity.CRITICAL -> AlertCritical
        DiagnosticSeverity.WARNING -> AlertWarning
        else -> CyanPrimary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(NavySurfaceVariant)
            .border(1.2.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = when (finding.severity) {
                        DiagnosticSeverity.CRITICAL -> Icons.Default.Warning
                        DiagnosticSeverity.WARNING -> Icons.Default.Warning
                        else -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = borderColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = finding.titleUrdu,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${finding.titleEnglish} • ${finding.priorityUrdu}",
                        fontSize = 11.sp,
                        color = borderColor
                    )
                }

                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyanPrimary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speak",
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FindingDetailRow(title = "دستیاب ثبوت:", value = finding.evidenceUrdu)
            FindingDetailRow(title = "ممکنہ وجہ:", value = finding.possibleCauseUrdu)
            FindingDetailRow(title = "اعتماد کی سطح:", value = finding.confidenceUrdu)

            Spacer(modifier = Modifier.height(6.dp))

            // Home Safe Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NavySurface)
                    .padding(10.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("گھر پر محفوظ طریقہ:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MedicalGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(finding.homeSafeActionUrdu, fontSize = 12.sp, lineHeight = 18.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Technician Need
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NavySurface)
                    .padding(10.dp)
            ) {
                Icon(Icons.Default.Build, contentDescription = null, tint = AlertWarning, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("کب ٹیکنیشن کی ضرورت ہوگی:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AlertWarning)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(finding.technicianNeedUrdu, fontSize = 12.sp, lineHeight = 18.sp, color = TextSecondaryDark)
                }
            }
        }
    }
}

@Composable
fun FindingDetailRow(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TurquoiseAccent)
        Text(text = value, fontSize = 12.sp, lineHeight = 17.sp, color = TextSecondaryDark)
    }
}
