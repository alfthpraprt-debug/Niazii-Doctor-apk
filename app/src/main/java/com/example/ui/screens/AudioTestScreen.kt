package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.MainViewModel
import com.example.diagnostic.AudioTester
import com.example.ui.theme.AlertWarning
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MedicalGreen
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TurquoiseAccent
import kotlinx.coroutines.launch

@Composable
fun AudioTestScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val audioTester = remember { AudioTester() }

    var isPlayingTone by remember { mutableStateOf(false) }
    var isTestingMic by remember { mutableStateOf(false) }
    var micStatusMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                isTestingMic = true
                micStatusMessage = "بولیں: 'ہیلو نیازی ڈاکٹر'... (3 سیکنڈ ریکارڈنگ)"
                val success = audioTester.recordAndPlaybackSample(3)
                isTestingMic = false
                micStatusMessage = if (success) "مائیک اور اسپیکر کا لوپ بیک ٹیسٹ کامیاب رہا!" else "ٹیسٹ مکمل نہ ہو سکا۔"
            }
        } else {
            micStatusMessage = "مائیکروفون ٹیسٹ کے لیے اجازت درکار ہے۔"
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audioTester.stopTone()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("audio_test_screen")
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
                        text = "اسپیکر اور مائیکروفون ٹیسٹ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Hardware Acoustic Loopback Diagnostics",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Speaker Diagnostics Section
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
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "اسپیکر ٹون جنریٹر (440Hz / 880Hz)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "یہ ٹیسٹ موبائل کے لاؤڈ اسپیکر سے ایک واضح تشخیصی فریکوئنسی ٹون بجاتا ہے تاکہ آواز میں کسی خرابی یا مسخ پن کی جانچ ہو سکے۔",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (isPlayingTone) {
                                audioTester.stopTone()
                                isPlayingTone = false
                            } else {
                                audioTester.playDiagnosticTone(frequency = 440.0, durationSeconds = 3.0)
                                isPlayingTone = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlayingTone) AlertWarning else CyanPrimary,
                            contentColor = Color(0xFF040814)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isPlayingTone) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isPlayingTone) "ٹون روکیں" else "440Hz ٹون بجائیں", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            audioTester.playDiagnosticTone(frequency = 880.0, durationSeconds = 3.0)
                            isPlayingTone = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("ہائی فریکوئنسی (880Hz)", fontSize = 11.sp)
                    }
                }
            }
        }

        // Microphone Diagnostic Section
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
                    Icon(Icons.Default.Mic, contentDescription = null, tint = TurquoiseAccent, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "مائیکروفون ریکارڈنگ اور پلے بیک",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "3 سیکنڈ کے لیے آپ کی آواز ریکارڈ کی جائے گی اور فوراً اسپیکر پر سنائی جائے گی، تاکہ مائیک کی آواز کی صفائی کی تصدیق ہو سکے۔",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            scope.launch {
                                isTestingMic = true
                                micStatusMessage = "بولیں: 'ہیلو نیازی ڈاکٹر'... (3 سیکنڈ)"
                                val success = audioTester.recordAndPlaybackSample(3)
                                isTestingMic = false
                                micStatusMessage = if (success) "مائیکروفون اور اسپیکر دونوں فعال ہیں!" else "ٹیسٹ مکمل نہیں ہو سکا۔"
                            }
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TurquoiseAccent,
                        contentColor = Color(0xFF040814)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isTestingMic,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTestingMic) "ریکارڈنگ اور پلے بیک جاری..." else "مائیکروفون ٹیسٹ شروع کریں",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (micStatusMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = micStatusMessage ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MedicalGreen
                    )
                }
            }
        }
    }
}
