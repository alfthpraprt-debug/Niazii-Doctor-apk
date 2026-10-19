package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertCritical
import com.example.ui.theme.AlertWarning
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MedicalGreen
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TurquoiseAccent

@Composable
fun UrduAlertBanner(
    titleUrdu: String,
    messageUrdu: String,
    severityUrdu: String,
    isSpeaking: Boolean,
    onSpeakClick: () -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        titleUrdu.contains("خطرہ") || severityUrdu.contains("شدید") -> AlertCritical
        titleUrdu.contains("وارننگ") || severityUrdu.contains("اہم") -> AlertWarning
        else -> CyanPrimary
    }

    Box(
        modifier = modifier
            .testTag("urdu_alert_banner")
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(NavySurfaceVariant, NavySurface)
                )
            )
            .border(1.2.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .clickable { onDetailClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(borderColor.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = "Voice Doctor",
                        tint = borderColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "نیازی ڈاکٹر کی صوتی تجویز",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = borderColor
                )

                Spacer(modifier = Modifier.weight(1f))

                // Play / Stop voice button
                IconButton(
                    onClick = onSpeakClick,
                    modifier = Modifier
                        .testTag("btn_speak_urdu")
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyanPrimary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = if (isSpeaking) "Stop voice" else "Speak aloud",
                        tint = CyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = messageUrdu,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "مکمل تشخیصی حل اور تفصیل دیکھیں",
                    fontSize = 12.sp,
                    color = TurquoiseAccent,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open Details",
                    tint = TurquoiseAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
