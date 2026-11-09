package com.playzone.football.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.ui.theme.*

@Composable
fun VirtualPredictionDialog(
    match: Match,
    outcome: PredictionOutcomeType,
    userBalance: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var stakeText by remember { mutableStateOf("10000") }
    val quickStakes = listOf(5_000L, 10_000L, 50_000L, 100_000L, 500_000L)

    val multiplier = when (outcome) {
        PredictionOutcomeType.HOME_WIN -> match.homeProbMultiplier
        PredictionOutcomeType.DRAW -> match.drawProbMultiplier
        PredictionOutcomeType.AWAY_WIN -> match.awayProbMultiplier
        PredictionOutcomeType.DOUBLE_CHANCE_1X -> match.dc1XMultiplier
        PredictionOutcomeType.DOUBLE_CHANCE_12 -> match.dc12Multiplier
        PredictionOutcomeType.DOUBLE_CHANCE_X2 -> match.dcX2Multiplier
        PredictionOutcomeType.OVER_2_5 -> match.over25Multiplier
        PredictionOutcomeType.UNDER_2_5 -> match.under25Multiplier
        PredictionOutcomeType.OVER_1_5 -> 1.35
        PredictionOutcomeType.UNDER_1_5 -> 3.10
        PredictionOutcomeType.BOTH_TEAMS_SCORE_YES -> match.bttsYesMultiplier
        PredictionOutcomeType.BOTH_TEAMS_SCORE_NO -> match.bttsNoMultiplier
    }

    val outcomeTitle = when (outcome) {
        PredictionOutcomeType.HOME_WIN -> "برد ${match.homeTeamPersian} (۱)"
        PredictionOutcomeType.DRAW -> "تساوی دو تیم (X)"
        PredictionOutcomeType.AWAY_WIN -> "برد ${match.awayTeamPersian} (۲)"
        PredictionOutcomeType.DOUBLE_CHANCE_1X -> "برد ${match.homeTeamPersian} یا مساوی (1X)"
        PredictionOutcomeType.DOUBLE_CHANCE_12 -> "برد یکی از دو تیم (12)"
        PredictionOutcomeType.DOUBLE_CHANCE_X2 -> "مساوی یا برد ${match.awayTeamPersian} (X2)"
        PredictionOutcomeType.OVER_2_5 -> "مجموع گل‌ها بیشتر از ۲.۵ (+2.5)"
        PredictionOutcomeType.UNDER_2_5 -> "مجموع گل‌ها کمتر از ۲.۵ (-2.5)"
        PredictionOutcomeType.OVER_1_5 -> "مجموع گل‌ها بیشتر از ۱.۵ (+1.5)"
        PredictionOutcomeType.UNDER_1_5 -> "مجموع گل‌ها کمتر از ۱.۵ (-1.5)"
        PredictionOutcomeType.BOTH_TEAMS_SCORE_YES -> "هر دو تیم گل می‌زنند: بله (GG)"
        PredictionOutcomeType.BOTH_TEAMS_SCORE_NO -> "هر دو تیم گل می‌زنند: خیر (NG)"
    }

    val numericStake = stakeText.toLongOrNull() ?: 0L
    val potentialReturn = (numericStake * multiplier).toLong()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("virtual_prediction_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ثبت برگه پیش‌بینی شبیه‌ساز",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${match.homeTeamPersian} - ${match.awayTeamPersian}",
                    style = MaterialTheme.typography.titleSmall,
                    color = CyanAccent,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Outcome & Odds Summary
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("انتخاب شما:", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            Text(outcomeTitle, style = MaterialTheme.typography.titleSmall, color = GreenAccent, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ضریب شبیه‌ساز:", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            Text("x$multiplier", style = MaterialTheme.typography.titleMedium, color = GoldAccent, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("موجودی شما:", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            Text("%,d اعتبار".format(userBalance), style = MaterialTheme.typography.bodySmall, color = TextWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Stake Input
                Text(
                    text = "مبلغ دلخواه برای پیش‌بینی (اعتبار مجازی):",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextWhite,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = stakeText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            stakeText = input
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("مثال: 50000") },
                    trailingIcon = { Text("اعتبار", color = TextGrayMuted, modifier = Modifier.padding(end = 12.dp), fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenAccent,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Stakes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    quickStakes.forEach { stake ->
                        FilterChip(
                            selected = numericStake == stake,
                            onClick = { stakeText = stake.toString() },
                            label = { Text("%,d".format(stake), fontSize = 10.sp) },
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

                Spacer(modifier = Modifier.height(14.dp))

                // Potential return
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF003816),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "برد احتمالی مجازی:",
                            style = MaterialTheme.typography.bodySmall,
                            color = GreenOnContainer
                        )
                        Text(
                            text = "%,d اعتبار".format(potentialReturn),
                            style = MaterialTheme.typography.titleLarge,
                            color = GreenAccent,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = TextGrayMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "این اعتبار شبیه‌سازی‌شده است و وجه واقعی پرداخت نمی‌شود.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("انصراف", color = TextGrayLight)
                    }

                    Button(
                        onClick = { onConfirm(numericStake) },
                        enabled = numericStake > 0 && userBalance >= numericStake,
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenAccent,
                            contentColor = NavyDarkest
                        )
                    ) {
                        Text(
                            text = if (userBalance < numericStake) "موجودی ناکافی" else "تأیید پیش‌بینی",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
