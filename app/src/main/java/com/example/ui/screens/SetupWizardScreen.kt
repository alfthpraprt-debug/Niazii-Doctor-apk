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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MedicalGreen
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TurquoiseAccent

@Composable
fun SetupWizardScreen(
    onFinishSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("setup_wizard_screen")
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF040814), Color(0xFF08122A), Color(0xFF0E1A38))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing AI Pulse Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary.copy(alpha = 0.15f))
                    .border(2.dp, CyanPrimary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "نیازی ڈاکٹر",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CyanPrimary
            )

            Text(
                text = "آپ کے موبائل کا ذہین ڈاکٹر",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TurquoiseAccent
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ultra-Premium AI Mobile Health & Diagnostics System",
                fontSize = 11.sp,
                color = TextSecondaryDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Feature 1
            WizardFeatureRow(
                icon = Icons.Default.HealthAndSafety,
                title = "خودکار ہارڈویئر چیک اپ",
                desc = "بیٹری، درجہ حرارت، چارجنگ، ریم اور سنسرز کی مسلسل حقیقی پیمائش"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Feature 2
            WizardFeatureRow(
                icon = Icons.Default.RecordVoiceOver,
                title = "اردو صوتی انتباہات",
                desc = "کسی بھی مسئلے یا خطرے کی صورت میں آسان اور واضح اردو میں بول کر رہنمائی"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Feature 3
            WizardFeatureRow(
                icon = Icons.Default.Security,
                title = "مستقل پسِ منظر نگہبانی",
                desc = "ایپ بند ہونے کے بعد بھی خودکار نگرانی اور حادثات سے بروقت بچاؤ"
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onFinishSetup,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color(0xFF040814)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .testTag("btn_get_started")
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "موبائل اسکین شروع کریں (Get Started)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WizardFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NavySurfaceVariant.copy(alpha = 0.6f))
            .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CyanPrimary.copy(alpha = 0.15f))
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 12.sp, lineHeight = 16.sp, color = TextSecondaryDark)
        }
    }
}
