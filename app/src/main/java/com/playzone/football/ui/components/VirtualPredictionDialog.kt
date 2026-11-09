package com.playzone.football.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
    val quickStakes = listOf(1_000L, 2_500L, 5_000L, 10_000L, 25_000L)
    var selectedStake by remember { mutableStateOf(2_500L) }

    val prob = when (outcome) {
        PredictionOutcomeType.HOME_WIN -> match.simHomeProb
        PredictionOutcomeType.DRAW -> match.simDrawProb
        PredictionOutcomeType.AWAY_WIN -> match.simAwayProb
        PredictionOutcomeType.OVER_2_5 -> match.simOverProb
        PredictionOutcomeType.UNDER_2_5 -> match.simUnderProb
    }

    val outcomeTitle = when (outcome) {
        PredictionOutcomeType.HOME_WIN -> "برد ${match.homeTeamPersian}"
        PredictionOutcomeType.DRAW -> "تساوی دو تیم"
        PredictionOutcomeType.AWAY_WIN -> "برد ${match.awayTeamPersian}"
        PredictionOutcomeType.OVER_2_5 -> "مجموع گل‌ها بیشتر از ۲.۵"
        PredictionOutcomeType.UNDER_2_5 -> "مجموع گل‌ها کمتر از ۲.۵"
    }

    val multiplier = (100.0 / prob.coerceAtLeast(1) * 100).toInt() / 100.0
    val potentialReturn = (selectedStake * multiplier).toLong()

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
                    text = "ثبت پیش‌بینی مجازی",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${match.homeTeamPersian} - ${match.awayTeamPersian}",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyanAccent,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Outcome & Probability summary box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("نتیجه انتخابی:", style = MaterialTheme.typography.bodyMedium, color = TextGrayMuted)
                            Text(outcomeTitle, style = MaterialTheme.typography.titleMedium, color = GreenAccent, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("احتمال آماری شبیه‌ساز:", style = MaterialTheme.typography.bodyMedium, color = TextGrayMuted)
                            Text("$prob% (ضریب آماری x$multiplier)", style = MaterialTheme.typography.bodyMedium, color = GoldAccent, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stake Selection
                Text(
                    text = "میزان اعتبار مجازی برای این پیش‌بینی:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextWhite,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickStakes.forEach { stake ->
                        FilterChip(
                            selected = selectedStake == stake,
                            onClick = { selectedStake = stake },
                            label = { Text("%,d".format(stake), fontSize = 11.sp) },
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
                            text = "بازگشت فرضی در صورت درستی:",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Educational notice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextGrayMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "این اعتبار مجازی است و در صورت برد، پولی پرداخت نخواهد شد.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_cancel_prediction"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGrayLight)
                    ) {
                        Text("انصراف")
                    }

                    Button(
                        onClick = { onConfirm(selectedStake) },
                        enabled = userBalance >= selectedStake,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("btn_confirm_prediction"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenAccent,
                            contentColor = NavyDarkest
                        )
                    ) {
                        Text(
                            text = if (userBalance >= selectedStake) "تأیید و ثبت" else "موجودی ناکافی",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
