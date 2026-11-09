package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playzone.football.data.model.Wallet
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    wallet: Wallet,
    onNavigateToSettings: () -> Unit,
    onNavigateToResponsible: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("بازنشانی اطلاعات برنامه", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "آیا مطمئن هستید که می‌خواهید تمام تاریخچه پیش‌بینی‌ها و موجودی را به حالت اولیه بازنشانی کنید؟ این عمل غیرقابل بازگشت است.",
                    color = TextGrayLight,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LiveRed)
                ) {
                    Text("حذف و بازنشانی", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("انصراف", color = TextGrayLight)
                }
            },
            containerColor = NavySurface
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(NavySurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = GreenAccent,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "کاربر شبیه‌ساز PlayZone",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GreenContainer,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (wallet.predictionsCount > 10) "کارشناس پیش‌بینی فوتبال" else "پیش‌بین تازه‌کار",
                                style = MaterialTheme.typography.labelSmall,
                                color = GreenAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Stats summary row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatPill(title = "کل پیش‌بینی‌ها", value = "${wallet.predictionsCount}", modifier = Modifier.weight(1f))
                ProfileStatPill(title = "نرخ موفقیت", value = "${"%.1f".format(wallet.winRate)}%", color = GoldAccent, modifier = Modifier.weight(1f))
                ProfileStatPill(title = "اعتبار فعلی", value = "%,d".format(wallet.balance), color = GreenAccent, modifier = Modifier.weight(1f))
            }
        }

        // Navigation Links
        item {
            Text(
                text = "تنظیمات و دسترسی‌ها",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column {
                    ProfileMenuRow(
                        icon = Icons.Default.Timer,
                        title = "استفاده مسئولانه و مدیریت زمان",
                        subtitle = "سقف روزانه و توقف شبیه‌ساز",
                        onClick = onNavigateToResponsible
                    )
                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
                    ProfileMenuRow(
                        icon = Icons.Default.Settings,
                        title = "تنظیمات برنامه",
                        subtitle = "پیکربندی API، زبان و تم",
                        onClick = onNavigateToSettings
                    )
                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
                    ProfileMenuRow(
                        icon = Icons.Default.Shield,
                        title = "سیاست حفظ حریم خصوصی",
                        subtitle = "شفافیت داده‌ها و قوانین امنیتی",
                        onClick = onNavigateToPrivacy
                    )
                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
                    ProfileMenuRow(
                        icon = Icons.Default.DeleteSweep,
                        title = "بازنشانی داده‌های محلی",
                        subtitle = "حذف پیش‌بینی‌ها و شارژ مجدد کیف پول",
                        iconTint = LiveRed,
                        onClick = { showResetDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatPill(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = TextWhite
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: Color = CyanAccent
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = TextWhite, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextGrayLight)
        }
        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = TextGrayMuted)
    }
}
