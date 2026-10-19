package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Psychology
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
import com.example.data.model.RepairAssistantMessage
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MedicalGreen
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TurquoiseAccent

@Composable
fun AiRepairAssistantScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.repairMessages.collectAsState()

    val quickQuestions = listOf(
        "میرا موبائل سست کیوں چل رہا ہے؟",
        "میری بیٹری جلدی کیوں ختم ہوتی ہے؟",
        "موبائل زیادہ گرم کیوں ہو رہا ہے؟",
        "میرے موبائل کی اسٹوریج کیوں بھر گئی ہے؟",
        "وائی فائی کنیکٹ کیوں نہیں ہو رہا؟",
        "موبائل چارج کیوں نہیں ہو رہا؟"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_repair_assistant_screen")
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
                        text = "اسمارٹ اے آئی ریپیر اسسٹنٹ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Interactive Conversational AI Troubleshooting",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Quick Questions Carousel
        item {
            Column {
                Text(
                    text = "عام مسائل جن کے بارے میں آپ پوچھ سکتے ہیں:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TurquoiseAccent
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickQuestions) { q ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(NavySurfaceVariant)
                                .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .clickable { viewModel.askRepairAssistant(q) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = q,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Message Thread
        items(messages) { msg ->
            RepairMessageCard(
                message = msg,
                onSpeak = {
                    viewModel.speakUrduMessage("${msg.answerUrdu} ${msg.recommendedActionUrdu}")
                },
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
fun RepairMessageCard(
    message: RepairAssistantMessage,
    onSpeak: () -> Unit,
    onNavigate: (String) -> Unit
) {
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
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyanPrimary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = message.questionUrdu,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "اعتماد: ${message.confidenceUrdu}",
                        fontSize = 11.sp,
                        color = TurquoiseAccent
                    )
                }

                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier
                        .size(34.dp)
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = message.answerUrdu,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Evidence Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NavySurface)
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text("دستیاب ہارڈویئر ثبوت:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TurquoiseAccent)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(message.evidenceUrdu, fontSize = 12.sp, color = TextSecondaryDark)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Safe Action Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NavySurface)
                    .border(1.dp, MedicalGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text("گھر پر تجویز کردہ محفوظ اقدام:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MedicalGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(message.recommendedActionUrdu, fontSize = 12.sp, lineHeight = 18.sp, color = Color.White)
                }
            }

            if (message.recommendedRoute != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onNavigate(message.recommendedRoute) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("متعلقہ ٹیسٹ یا فیچر کھولیں", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
