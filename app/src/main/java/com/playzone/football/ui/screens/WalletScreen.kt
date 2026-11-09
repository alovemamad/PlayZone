package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playzone.football.data.model.Wallet
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun WalletScreen(
    viewModel: MainViewModel,
    wallet: Wallet,
    modifier: Modifier = Modifier
) {
    var showRefillDialog by remember { mutableStateOf(false) }

    if (showRefillDialog) {
        AlertDialog(
            onDismissRequest = { showRefillDialog = false },
            title = {
                Text(
                    text = "شارژ رایگان اعتبار مجازی",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "موجودی کیف پول مجازی شما به ۱۰۰,۰۰۰ اعتبار بازنشانی خواهد شد. این اعتبارات کاملاً رایگان و مجازی بوده و ارزش پولی ندارند.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrayLight
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.refillCredits()
                        showRefillDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = NavyDarkest)
                ) {
                    Text("شارژ مجدد اعتبار", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRefillDialog = false }) {
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
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Balance Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(GreenAccent.copy(alpha = 0.6f), NavyBorder))
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(GreenAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = GreenAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "موجودی اعتبار مجازی PlayZone",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGrayLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "%,d".format(wallet.balance),
                            style = MaterialTheme.typography.displayLarge,
                            color = GreenAccent,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "اعتبار",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextGrayMuted,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showRefillDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_refill_credits"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenAccent,
                            contentColor = NavyDarkest
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "شارژ رایگان ۱۰۰,۰۰۰ اعتبار هدیه", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Summary Statistics Grid
        item {
            Text(
                text = "خلاصه عملکرد اعتبارات شما",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "کل اعتبارات ثبت‌شده",
                    value = "%,d".format(wallet.totalStaked),
                    subtitle = "اعتبار",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "کل اعتبارات دریافت‌شده",
                    value = "%,d".format(wallet.totalWon),
                    subtitle = "اعتبار",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "سود / زیان فرضی",
                    value = "${if (wallet.netProfit >= 0) "+" else ""}${"%,d".format(wallet.netProfit)}",
                    subtitle = "اعتبار",
                    valueColor = if (wallet.netProfit >= 0) GreenAccent else LiveRed,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "نرخ موفقیت پیش‌بینی‌ها",
                    value = "${"%.1f".format(wallet.winRate)}%",
                    subtitle = "${wallet.wonCount} برد از ${wallet.predictionsCount}",
                    valueColor = GoldAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Strict Virtual Currency Notice
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(CyanAccent.copy(alpha = 0.4f), NavyBorder))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ضوابط اعتبارات مجازی PlayZone",
                            style = MaterialTheme.typography.titleMedium,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• کلیه اعتبارات کیف پول مجازی کاملاً شبیه‌سازی‌شده و فاقد ارزش مادی هستند.\n" +
                               "• امکان واریز پول واقعی، خرید اعتبار، درگاه پرداخت یا رمزارز در این برنامه وجود ندارد.\n" +
                               "• اعتبارات مجازی به هیچ عنوان قابل برداشت، تبدیل به وجه نقد یا انتقال به سایرین نمی‌باشند.\n" +
                               "• این سیستم صرفاً به منظور شبیه‌سازی آموزشی الگوهای احتمالی فوتبال طراحی شده است.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayLight,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextWhite
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextGrayMuted, maxLines = 1)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = TextGrayLight)
        }
    }
}
