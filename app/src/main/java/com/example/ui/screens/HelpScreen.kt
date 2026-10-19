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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TurquoiseAccent

@Composable
fun HelpScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("help_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        text = "مدد اور رہنمائی (Help & FAQs)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Phone Care & Safe Troubleshooting Guide",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        item {
            FaqItem(
                questionUrdu = "اگر موبائل بہت گرم ہو جائے تو فوری کیا کریں؟",
                answerUrdu = "فوری طور پر چارجر الگ کریں، گیم یا بھاری ایپس بند کر دیں، موبائل کا کور (Back Cover) اتار دیں اور موبائل کو پنکھے کے آگے یا سایہ دار ٹھنڈی جگہ پر رکھیں۔ موبائل کو کبھی بھی فریج میں نہ رکھیں کیونکہ اس سے اندر نمی پیدا ہو سکتی ہے۔"
            )
        }

        item {
            FaqItem(
                questionUrdu = "چارجنگ بار بار کیوں منقطع ہوتی ہے؟",
                answerUrdu = "عام طور پر 80 فیصد مسائل ناقص یا مڑی ہوئی کیبل اور چارجنگ پورٹ میں دھول جمنے کی وجہ سے ہوتے ہیں۔ نرم لکڑی کی خلال سے پورٹ کو صفائی دیں اور تصدیق شدہ چارجر کیبل استعمال کریں۔ اگر مسئلہ برقرار رہے تو پورٹ ڈھیلا ہو سکتا ہے۔"
            )
        }

        item {
            FaqItem(
                questionUrdu = "موبائل سست (Slow) کیوں ہو جاتا ہے؟",
                answerUrdu = "جب موبائل کی اسٹوریج 90 فیصد سے زیادہ بھر جاتی ہے یا بیک گراؤنڈ میں 20 سے زیادہ ایپس کھلی رہتی ہیں، تو ریم پر دباؤ بڑھ جاتا ہے۔ غیر ضروری ویڈیوز صاف کریں اور فون کو ہفتے میں ایک بار ری اسٹارٹ کریں۔"
            )
        }

        item {
            FaqItem(
                questionUrdu = "کیا نیازی ڈاکٹر انٹرنیٹ کے بغیر کام کرتا ہے؟",
                answerUrdu = "جی ہاں! نیازی ڈاکٹر کا تمام تشخیصی سسٹم مکمل طور پر آف لائن کام کرتا ہے۔ بیٹری، ٹمپریچر، چارجنگ، اسکرین اور اردو صوتی انتباہات بغیر انٹرنیٹ کے آپ کی نگہبانی کرتے ہیں۔"
            )
        }

        item {
            FaqItem(
                questionUrdu = "بیٹری کی صحت کو سالوں تک قائم رکھنے کا سنہری اصول کیا ہے؟",
                answerUrdu = "بیٹری کو 20 فیصد سے کم نہ ہونے دیں اور 85 سے 90 فیصد تک چارج کر کے ہٹا لیں۔ رات بھر چارجنگ پر لگا کر تکیے کے نیچے نہ رکھیں، اور چارجنگ کے دوران ویڈیو گیمز کھیلنے سے پرہیز کریں۔"
            )
        }
    }
}

@Composable
fun FaqItem(
    questionUrdu: String,
    answerUrdu: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NavySurfaceVariant)
            .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = questionUrdu,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TurquoiseAccent,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (expanded) 180f else 0f)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = answerUrdu,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = TextSecondaryDark
                )
            }
        }
    }
}
