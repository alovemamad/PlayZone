package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playzone.football.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سیاست حفظ حریم خصوصی", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_privacy")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        containerColor = NavyBackground,
        modifier = modifier.testTag("privacy_policy_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(26.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حریم خصوصی و شفافیت در PlayZone",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "۱. عدم جمع‌آوری اطلاعات شخصی:\n" +
                                   "برنامه PlayZone هیچ‌گونه اطلاعات هویتی، نام، ایمیل، شماره تماس، لیست مخاطبین، موقعیت مکانی (GPS)، پیامک یا گزارش تماس‌های کاربر را جمع‌آوری یا ذخیره نمی‌کند.\n\n" +
                                   "۲. عدم تبادل مالی یا قمار:\n" +
                                   "این نرم‌افزار به صورت ۱۰۰٪ شبیه‌ساز مجازی و آموزشی طراحی شده است. هیچ مکانیزمی برای واریز وجه، برداشت، خرید درون‌برنامه‌ای قمار، اتصال به درگاه‌های بانکی، کارت‌به‌کارت یا ارز دیجیتال در برنامه تعبیه نشده است.\n\n" +
                                   "۳. ذخیره‌سازی محلی داده‌ها (Room Database):\n" +
                                   "تمام پیش‌بینی‌های مجازی، تاریخچه‌ها و تنظیمات کاربر صرفاً در حافظه محلی دستگاه شما با استفاده از پایگاه داده استاندارد Room ذخیره می‌شوند و به هیچ سرور شخص ثالثی ارسال نمی‌گردند. کاربر در هر زمان می‌تواند از منوی پروفایل کلیه داده‌های محلی خود را پاک کند.\n\n" +
                                   "۴. مجوزهای دستگاه:\n" +
                                   "برنامه تنها از مجوز دسترسی به اینترنت جهت دریافت احتمالی اطلاعات مسابقات فوتبال استفاده می‌نماید و هیچ مجوز حساسی ندارد.\n\n" +
                                   "۵. انطباق با قوانین Google Play:\n" +
                                   "این اپلیکیشن کاملاً مطابق با خط‌مشی‌های گوگل پلی برای برنامه‌های شبیه‌سازی ورزشی و سرگرمی تدوین شده است.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}
