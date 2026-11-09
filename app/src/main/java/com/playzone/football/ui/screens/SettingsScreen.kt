package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var apiKeyInput by remember { mutableStateOf("") }
    var selectedProvider by remember { mutableStateOf("demo") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات برنامه", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_settings")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        containerColor = NavyBackground,
        modifier = modifier.testTag("settings_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Data Provider Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "منبع داده‌های فوتبال (Data Provider)",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "PlayZone از دو حالت پشتیبانی می‌کند: شبیه‌ساز کامل آفلاین با مسابقات واقعی لیگ‌های معتبر و پشتیبانی از اتصال به API-Football / TheSportsDB.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedProvider == "demo",
                                onClick = { selectedProvider = "demo" },
                                colors = RadioButtonDefaults.colors(selectedColor = GreenAccent)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("شبیه‌ساز هوشمند داخلی (فعال - بدون نیاز به اینترنت)", style = MaterialTheme.typography.bodyMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                                Text("مسابقات واقعی UCL، انگلیس، لالیگا و لیگ برتر ایران با رویدادهای زنده", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedProvider == "api",
                                onClick = { selectedProvider = "api" },
                                colors = RadioButtonDefaults.colors(selectedColor = GreenAccent)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("اتصال به سرور API-Football", style = MaterialTheme.typography.bodyMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                                Text("استفاده از کلید اشتراک اختصاصی", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            }
                        }

                        if (selectedProvider == "api") {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = apiKeyInput,
                                onValueChange = { apiKeyInput = it },
                                label = { Text("کلید API-Football (اختیاری)") },
                                placeholder = { Text("مثال: 98a76b5c4d...") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenAccent,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )
                        }
                    }
                }
            }

            // Language & Direction
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "زبان و چیدمان رابط کاربری",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("زبان: فارسی (Iran)", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                                Text("فونت: وزیرمتن (Vazirmatn) • جهت: راست‌به‌چپ (RTL)", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            }
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = GreenAccent)
                        }
                    }
                }
            }

            // App Identity Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("مشخصات نسخه برنامه", style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("نام برنامه: PlayZone", style = MaterialTheme.typography.bodySmall, color = TextGrayLight)
                        Text("پکیج: com.playzone.football", style = MaterialTheme.typography.bodySmall, color = TextGrayLight)
                        Text("نوع نسخه: Release", style = MaterialTheme.typography.bodySmall, color = GreenAccent)
                        Text("پایگاه داده: SQLite / Room Database", style = MaterialTheme.typography.bodySmall, color = TextGrayLight)
                    }
                }
            }
        }
    }
}
