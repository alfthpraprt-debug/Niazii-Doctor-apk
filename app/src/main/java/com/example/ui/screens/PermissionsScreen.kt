package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun PermissionsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasNotificationPerm by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    var hasMicPerm by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasCameraPerm by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasNotificationPerm = granted
    }

    val micLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasMicPerm = granted
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPerm = granted
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("permissions_screen")
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
                        text = "اجازتیں اور رازداری (Permissions)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Zero-Cloud Local Privacy & System Access",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Privacy Guarantee Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavySurfaceVariant)
                    .border(1.dp, MedicalGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MedicalGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("100% پرائیویٹ اور لوکل پروسیسنگ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "نیازی ڈاکٹر آپ کا ذاتی ڈیٹا، تصاویر، یا پیغامات ہرگز جمع نہیں کرتا۔ تمام تشخیصی حساب کتاب فون کے اندر ہی ہوتا ہے۔",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Notifications Permission Card
        item {
            PermissionCard(
                titleUrdu = "نوٹیفکیشن الرٹس (Notifications)",
                descUrdu = "پسِ منظر میں بیٹری زیادہ گرم ہونے یا چارجنگ میں خرابی پر بروقت انتباہ دینے کے لیے ضروری ہے۔",
                isGranted = hasNotificationPerm,
                onRequest = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            )
        }

        // Audio Permission Card
        item {
            PermissionCard(
                titleUrdu = "مائیکروفون (Audio Loopback)",
                descUrdu = "مائیک کے ہارڈویئر کی آواز پکڑنے کی صلاحیت کو 3 سیکنڈ ریکارڈ کر کے ٹیسٹ کرنے کے لیے درکار ہے۔",
                isGranted = hasMicPerm,
                onRequest = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) }
            )
        }

        // Camera Permission Card
        item {
            PermissionCard(
                titleUrdu = "کیمرہ اور فلیش لائٹ (Camera / Flash)",
                descUrdu = "کیمرہ سینسر کی موجودگی اور فلیش لائٹ ایل ای ڈی ٹارچ ٹیسٹ کے لیے درکار ہے۔",
                isGranted = hasCameraPerm,
                onRequest = { cameraLauncher.launch(Manifest.permission.CAMERA) }
            )
        }
    }
}

@Composable
fun PermissionCard(
    titleUrdu: String,
    descUrdu: String,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NavySurfaceVariant)
            .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Security,
                    contentDescription = null,
                    tint = if (isGranted) MedicalGreen else AlertWarning,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = titleUrdu,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (isGranted) "اجازت دی گئی ہے" else "درکار ہے",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGranted) MedicalGreen else AlertWarning
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = descUrdu,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = TextSecondaryDark
            )

            if (!isGranted) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onRequest,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("اجازت دیں (Grant Permission)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
