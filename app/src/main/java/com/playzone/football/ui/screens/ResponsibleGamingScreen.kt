package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playzone.football.data.model.ResponsibleSettings
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponsibleGamingScreen(
    viewModel: MainViewModel,
    settings: ResponsibleSettings,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("استفاده مسئولانه و مدیریت زمان", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_responsible")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        containerColor = NavyBackground,
        modifier = modifier.testTag("responsible_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simulator Pause Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (settings.isPaused) Color(0xFF351215) else NavyCardElevated
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (settings.isPaused) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = if (settings.isPaused) LiveRed else GreenAccent,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (settings.isPaused) "شبیه‌ساز در حالت استراحت است" else "شبیه‌ساز فعال است",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (settings.isPaused) "امکان ثبت پیش‌بینی تا پایان زمان استراحت متوقف شده است."
                                           else "می‌توانید برای مدیریت زمان، شبیه‌ساز را موقتاً متوقف کنید.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrayLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (settings.isPaused) {
                            Button(
                                onClick = { viewModel.resumeSimulator() },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = NavyDarkest),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("پایان استراحت و فعال‌سازی مجدد", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.pauseSimulator(1) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavySurface)
                                ) {
                                    Text("۱ ساعت توقف", style = MaterialTheme.typography.labelSmall, color = TextWhite)
                                }
                                Button(
                                    onClick = { viewModel.pauseSimulator(24) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavySurface)
                                ) {
                                    Text("۲۴ ساعت", style = MaterialTheme.typography.labelSmall, color = TextWhite)
                                }
                                Button(
                                    onClick = { viewModel.pauseSimulator(168) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavySurface)
                                ) {
                                    Text("۷ روز", style = MaterialTheme.typography.labelSmall, color = TextWhite)
                                }
                            }
                        }
                    }
                }
            }

            // Daily Limit Slider
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "تنظیم سقف زمان استفاده روزانه",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "سقف روزانه تعیین شده: ${settings.dailyLimitMinutes} دقیقه",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GreenAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(15, 30, 60, 120).forEach { mins ->
                                FilterChip(
                                    selected = settings.dailyLimitMinutes == mins,
                                    onClick = { viewModel.updateDailyLimit(mins) },
                                    label = { Text("$mins دقیقه") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GreenAccent,
                                        selectedLabelColor = NavyDarkest,
                                        containerColor = NavyCardElevated,
                                        labelColor = TextWhite
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Awareness of Gambling Harms
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "آگاهی‌بخشی درباره آسیب‌های قمار و شرط‌بندی واقعی",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "این برنامه صرفاً یک شبیه‌ساز تحلیلی برای یادگیری احتمالات ریاضی فوتبال است. قمار واقعی با پول، یکی از عوامل ایجاد بحران‌های شدید مالی، اضطراب روانی و اختلالات رفتاری است.\n\n" +
                                   "• هیچ استراتژی یا سیستمی در شرط‌بندی واقعی تضمین‌کننده برد نیست.\n" +
                                   "• جبران باخت‌های قبلی (Chase Loss) نشانه اصلی رفتارهای اعتیادآور است.\n" +
                                   "• هرگز زمان اختصاص داده شده به خانواده و کار را صرف فعالیت‌های پرخطر نکنید.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Helplines & Support
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مراکز مشاوره و دریافت راهنمایی",
                                style = MaterialTheme.typography.titleMedium,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "اگر احساس می‌کنید تمایل به قمار یا ریسک‌های مالی سلامت روان شما را تحت تأثیر قرار داده است، با متخصصین مشورت کنید:\n\n" +
                                   "• سامانه ملی مشاوره تلفنی (ایران): ۱۴۸۰ (رایگان و ناشناس)\n" +
                                   "• اورژانس اجتماعی: ۱۲۳\n" +
                                   "• منابع بین‌المللی: www.gamblersanonymous.org",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
