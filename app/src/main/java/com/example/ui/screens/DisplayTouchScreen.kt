package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MedicalGreen
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TurquoiseAccent

@Composable
fun DisplayTouchScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val health by viewModel.deviceHealth.collectAsState()
    val display = health.display

    var activeColorIndex by remember { mutableIntStateOf(-1) }
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.White, Color.Black)
    val colorNames = listOf("Red (سرخ)", "Green (سبز)", "Blue (نیلا)", "White (سفید)", "Black (سیاہ)")

    // 24 grid tiles for interactive touch digitizer test
    val totalTiles = 20
    val touchedTiles = remember { mutableStateListOf<Int>() }

    // If activeColorIndex >= 0, show full screen solid color test
    if (activeColorIndex >= 0) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors[activeColorIndex])
                .clickable {
                    if (activeColorIndex < colors.size - 1) {
                        activeColorIndex++
                    } else {
                        activeColorIndex = -1 // Exit full screen color test
                    }
                }
        ) {
            Text(
                text = "اسکرین پر ٹیپ کریں • ${colorNames[activeColorIndex]}",
                color = if (colors[activeColorIndex] == Color.White) Color.Black else Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("display_touch_screen")
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
                        text = "اسکرین اور ٹچ اسکرین ٹیسٹ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Display Digitize & Touch Response",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        // Hardware Specs
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
                        Text("ریزولیوشن", fontSize = 12.sp, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${display.widthPx} x ${display.heightPx}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${display.densityDpi} DPI", fontSize = 11.sp, color = TurquoiseAccent)
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
                        Text("ریفرش ریٹ", fontSize = 12.sp, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${display.refreshRate.toInt()} Hz", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                        Text(if (display.isHdrSupported) "HDR فعال ہے" else "معیاری رنگ", fontSize = 11.sp, color = MedicalGreen)
                    }
                }
            }
        }

        // Dead Pixel Color Tester Section
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
                    Icon(Icons.Default.ColorLens, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ڈیڈ پکسل (Dead Pixel) کلر ٹیسٹ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "مکمل اسکرین پر سرخ، سبز، نیلا، سفید اور سیاہ رنگ دکھا کر اسکرین پر کسی داغ یا خراب پکسل کی جانچ کریں۔",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { activeColorIndex = 0 },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF040814)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("کلر ٹیسٹ شروع کریں (Full Screen)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Interactive Touch Digitizer Canvas Grid
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
                    Icon(Icons.Default.TouchApp, contentDescription = null, tint = TurquoiseAccent, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ٹچ سینسر انٹرایکٹو ٹیسٹ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${touchedTiles.size} / $totalTiles مکمل",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (touchedTiles.size == totalTiles) MedicalGreen else CyanPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "نیچے دی گئی تمام ٹائلز کو انگلی سے چھو کر چیک کریں کہ تمام حصے کام کر رہے ہیں۔",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4 columns x 5 rows grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (row in 0 until 5) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (col in 0 until 4) {
                                val index = row * 4 + col
                                val isTouched = touchedTiles.contains(index)

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isTouched) CyanPrimary.copy(alpha = 0.85f)
                                            else NavySurface
                                        )
                                        .border(
                                            1.dp,
                                            if (isTouched) CyanPrimary else NavyCardBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            if (!touchedTiles.contains(index)) {
                                                touchedTiles.add(index)
                                            }
                                        }
                                ) {
                                    if (isTouched) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Touched",
                                            tint = Color(0xFF040814),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 11.sp,
                                            color = TextMutedDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { touchedTiles.clear() },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ٹچ گرڈ دوبارہ ری سیٹ کریں", fontSize = 12.sp)
                }
            }
        }
    }
}
