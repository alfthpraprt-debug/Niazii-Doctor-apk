package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CacheCleanerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToApps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val health by viewModel.deviceHealth.collectAsState()
    val perf = health.performance
    val internalCacheBytes by viewModel.internalCacheBytes.collectAsState()
    val isCleaning by viewModel.isCleaning.collectAsState()
    val lastCleanup by viewModel.lastCleanupRecord.collectAsState()
    val cleanupHistory by viewModel.cleanupHistory.collectAsState()

    val freeGb = String.format(Locale.US, "%.1f", perf.freeStorageBytes.toDouble() / (1024.0 * 1024.0 * 1024.0))
    val totalGb = String.format(Locale.US, "%.1f", perf.totalStorageBytes.toDouble() / (1024.0 * 1024.0 * 1024.0))
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cache_cleaner_screen")
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
                        text = "اسمارٹ کیش کلینر AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Smart Storage Optimizer & Safe Housekeeping",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Storage Pressure Overview Card
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
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.SdStorage, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(26.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("انٹرنل اسٹوریج پریشر", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("$freeGb GB خالی بمقابلہ $totalGb GB کل گنجائش", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${perf.usedStoragePercentage}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (perf.isLowStorage) AlertWarning else MedicalGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (perf.isLowStorage) {
                            "وارننگ: اسٹوریج پر شدید دباؤ ہے۔ کیش اور غیر ضروری فائلیں صاف کرنے سے فون کی کارکردگی بہتر ہوگی۔"
                        } else {
                            "اسٹوریج کی حالت تسلی بخش ہے۔ باقاعدہ صفائی سے فون کی رفتار اور عمر برقرار رہتی ہے۔"
                        },
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = if (perf.isLowStorage) AlertWarning else TurquoiseAccent
                    )
                }
            }
        }

        // Mode A — Automatic Safe Cleanup Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.2.dp, CyanPrimary.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "موڈ اے (Mode A) — خودکار محفوظ کیش صفائی",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "یہ موڈ اینڈرائیڈ سیکیورٹی حدود کا 100% احترام کرتے ہوئے نیازی ڈاکٹر کی تمام عارضی فائلیں، میموری کیشے اور فالتو ڈیٹا فوری اور بغیر کسی خطرے کے صاف کرتا ہے۔",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("قابل صفائی کیش سائز:", fontSize = 12.sp, color = TurquoiseAccent, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = viewModel.cacheCleanerEngine.formatBytes(internalCacheBytes),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.runModeACleanup() },
                    enabled = !isCleaning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanPrimary,
                        contentColor = Color(0xFF040814)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    if (isCleaning) {
                        CircularProgressIndicator(color = Color(0xFF040814), strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("صفائی اور تصدیق جاری...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ایک کلک میں کیش صاف کریں (Clean Cache)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (lastCleanup != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = lastCleanup?.notesUrdu ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MedicalGreen
                    )
                }
            }
        }

        // Mode B — Smart Cleanup Assistant (Cross-App Guidance)
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
                    Icon(Icons.Default.Apps, contentDescription = null, tint = TurquoiseAccent, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "موڈ بی (Mode B) — اسمارٹ ایپ کیشے اسسٹنٹ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "اہم قانونی وضاحت: اینڈرائیڈ کے جدید سیکیورٹی اصولوں کے مطابق کوئی بھی عام ایپ صارف کی تصدیق کے بغیر دوسری ایپس کا نجی کیشے خفیہ طور پر ڈیلیٹ نہیں کر سکتی۔ نیازی ڈاکٹر آپ کو ایپس کی لسٹ فراہم کرتا ہے تاکہ آپ سسٹم سیٹنگز میں باآسانی کیش صاف کر سکیں۔",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onNavigateToApps,
                        colors = ButtonDefaults.buttonColors(containerColor = TurquoiseAccent, contentColor = Color(0xFF040814)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ایپس کیشے جائزہ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = viewModel.cacheCleanerEngine.createOpenSystemStorageIntent()
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("سسٹم اسٹوریج سیٹنگز", fontSize = 11.sp)
                    }
                }
            }
        }

        // Mode C — Advanced Device Cleanup
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "موڈ سی (Mode C) — ایڈوانسڈ کلین اپ سٹیٹس",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "یہ ڈیوائس اسٹینڈرڈ اینڈرائیڈ یوزر اسپیس پر کام کر رہی ہے۔ مکمل خودکار کراس ایپ کیش ڈیلیٹ کے لیے انٹرپرائز یا سسٹم ایکسس درکار ہوتا ہے، جس کا نیازی ڈاکٹر کوئی غلط دعویٰ نہیں کرتا۔",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Cleanup History Section
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "صفائی کی تاریخ (Cleanup History)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (cleanupHistory.isEmpty()) {
            item {
                Text(
                    text = "ابھی تک کوئی کلین اپ ریکارڈ نہیں ہوا۔ پہلی بار 'ایک کلک میں کیش صاف کریں' پر ٹیپ کریں۔",
                    fontSize = 12.sp,
                    color = TextMutedDark
                )
            }
        } else {
            items(cleanupHistory) { record ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NavySurface)
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(record.notesUrdu, fontSize = 12.sp, color = Color.White)
                        Text(sdf.format(Date(record.timestamp)), fontSize = 10.sp, color = TextMutedDark)
                    }
                }
            }
        }
    }
}
