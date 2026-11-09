package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.input.KeyboardType
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
    var showCustomBalanceDialog by remember { mutableStateOf(false) }
    var customBalanceInput by remember { mutableStateOf(wallet.balance.toString()) }

    if (showCustomBalanceDialog) {
        val quickAmounts = listOf(100_000L, 500_000L, 1_000_000L, 10_000_000L, 50_000_000L)

        AlertDialog(
            onDismissRequest = { showCustomBalanceDialog = false },
            title = {
                Text(
                    text = "تنظیم موجودی دلخواه کیف پول مجازی",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "می‌توانید هر مبلغ دلخواهی از اعتبارات شبیه‌ساز را برای خود تنظیم کنید:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayLight
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customBalanceInput,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) {
                                customBalanceInput = input
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("مبلغ موجودی (اعتبار)") },
                        trailingIcon = { Text("اعتبار", color = TextGrayMuted, modifier = Modifier.padding(end = 12.dp)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenAccent,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("انتخاب سریع:", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        quickAmounts.take(4).forEach { amount ->
                            FilterChip(
                                selected = customBalanceInput == amount.toString(),
                                onClick = { customBalanceInput = amount.toString() },
                                label = { Text("%,d".format(amount), fontSize = 9.sp) },
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = customBalanceInput.toLongOrNull() ?: 100_000L
                        viewModel.setCustomWalletBalance(amount)
                        showCustomBalanceDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = NavyDarkest)
                ) {
                    Text("ذخیره موجودی", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomBalanceDialog = false }) {
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
                        text = "موجودی کیف پول شبیه‌ساز PlayZone",
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                customBalanceInput = wallet.balance.toString()
                                showCustomBalanceDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_custom_balance"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenAccent,
                                contentColor = NavyDarkest
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "تنظیم موجودی دلخواه", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.refillCredits() },
                            modifier = Modifier.weight(0.8f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyanAccent)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "شارژ ۱۰۰K", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Summary Statistics Grid
        item {
            Text(
                text = "خلاصه گردش اعتبارات شبیه‌ساز",
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
                    title = "کل اعتبارات شرط‌بندی‌شده",
                    value = "%,d".format(wallet.totalStaked),
                    subtitle = "اعتبار",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "کل بردهای دریافت‌شده",
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
                    title = "سود / زیان شبیه‌ساز",
                    value = "${if (wallet.netProfit >= 0) "+" else ""}${"%,d".format(wallet.netProfit)}",
                    subtitle = "اعتبار",
                    valueColor = if (wallet.netProfit >= 0) GreenAccent else LiveRed,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "نرخ موفقیت پیش‌بینی‌ها",
                    value = "${"%.1f".format(wallet.winRate)}%",
                    subtitle = "${wallet.wonCount} برد از ${wallet.predictionsCount} بازی",
                    valueColor = GoldAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Virtual Disclaimer
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
                        Icon(Icons.Default.Security, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ضوابط اعتبارات در شبیه‌ساز PlayZone",
                            style = MaterialTheme.typography.titleMedium,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• موجودی کیف پول صرفاً یک عدد فرضی برای شبیه‌سازی تجربه پیش‌بینی بدون خطر مالی است.\n" +
                               "• کاربر می‌تواند در هر لحظه موجودی خود را آزادانه کم یا زیاد کند.\n" +
                               "• هیچ وجه یا ارزی دریافت یا پرداخت نمی‌شود و سیستم هیچ‌گونه درگاه مالی ندارد.",
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
