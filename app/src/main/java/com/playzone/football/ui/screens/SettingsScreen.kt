package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isLiveApi by viewModel.isLiveApiActive.collectAsStateWithLifecycle()
    val apiStatusMessage by viewModel.apiStatusMessage.collectAsStateWithLifecycle()

    var apiKeyInput by remember(userSettings.apiFootballKey) {
        mutableStateOf(userSettings.apiFootballKey)
    }
    var useLiveApiSwitch by remember(userSettings.useLiveApi) {
        mutableStateOf(userSettings.useLiveApi)
    }
    var isCheckingConnection by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات و اتصال API فوتبال", color = TextWhite, fontWeight = FontWeight.Bold) },
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
            // Live Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLiveApi) Color(0xFF003816) else NavyCardElevated
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLiveApi) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isLiveApi) GreenAccent else CyanAccent,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isLiveApi) "اتصال آنلاین به API-Football فعال است" else "حالت شبیه‌ساز هوشمند فعال است",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = apiStatusMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isLiveApi) GreenOnContainer else TextGrayLight
                            )
                        }
                    }
                }
            }

            // API-Football Configuration Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "پیکربندی کلید API-Football",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "اگر دارای کلید API اختصاصی از سایت api-football.com یا api-sports.io هستید، می‌توانید آن را در کادر زیر وارد کنید تا مسابقات زنده بین‌المللی مستقیماً از سرور دریافت شوند:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            label = { Text("کلید اختصاصی API-Football (اختیاری)") },
                            placeholder = { Text("مثال: 94a8bc371e...") },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = CyanAccent) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenAccent,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedContainerColor = NavySurface,
                                unfocusedContainerColor = NavySurface
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "استفاده از داده‌های آنلاین سرور",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "در صورت خاموش بودن، مسابقات از شبیه‌ساز داخلی با داده‌های واقعی خوانده می‌شوند.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrayMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = useLiveApiSwitch,
                                onCheckedChange = { useLiveApiSwitch = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NavyDarkest,
                                    checkedTrackColor = GreenAccent
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    isCheckingConnection = true
                                    viewModel.saveApiConfig(apiKeyInput.trim(), useLiveApiSwitch)
                                    isCheckingConnection = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenAccent,
                                    contentColor = NavyDarkest
                                )
                            ) {
                                if (isCheckingConnection) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = NavyDarkest, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text("ذخیره و اتصال به سرور", fontWeight = FontWeight.Bold)
                            }

                            if (apiKeyInput.isNotBlank()) {
                                OutlinedButton(
                                    onClick = {
                                        apiKeyInput = ""
                                        useLiveApiSwitch = false
                                        viewModel.saveApiConfig("", false)
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGrayLight)
                                ) {
                                    Text("حذف کلید")
                                }
                            }
                        }
                    }
                }
            }

            // Information on Free API Key
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "نحوه دریافت کلید رایگان API-Football:",
                            style = MaterialTheme.typography.titleSmall,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "۱. به وب‌سایت dashboard.api-football.com مراجعه کرده و ثبت نام کنید.\n" +
                                   "۲. در بخش API Key، کلید اختصاصی رایگان خود را کپی کنید (دارای ۱۰۰ درخواست رایگان در روز).\n" +
                                   "۳. کلید را در کادر بالا وارد کرده و دکمه «ذخیره و اتصال» را فشار دهید.\n" +
                                   "۴. در صورتی که کلید وارد نکنید، شبیه‌ساز پیشرفته PlayZone تمامی مسابقات لیگ‌های برتر ایران و اروپا را به شکل زنده شبیه‌سازی می‌کند.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Language & Direction Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                Text("فونت: وزیرمتن (Vazirmatn) • چیدمان: راست‌به‌چپ (RTL)", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            }
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = GreenAccent)
                        }
                    }
                }
            }
        }
    }
}
