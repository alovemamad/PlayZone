package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchEvent
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.ui.components.LiveStatusBadge
import com.playzone.football.ui.components.OutcomeButton
import com.playzone.football.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    match: Match?,
    onBackClick: () -> Unit,
    onPredictClick: (Match, PredictionOutcomeType) -> Unit,
    modifier: Modifier = Modifier
) {
    if (match == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NavyBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("اطلاعات مسابقه یافت نشد", color = TextWhite)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${match.homeTeamPersian} - ${match.awayTeamPersian}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("btn_back_match_detail")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        containerColor = NavyBackground,
        modifier = modifier.testTag("match_detail_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Match Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(listOf(NavyBorder, Color(0xFF131D30)))
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = match.leaguePersianName,
                                style = MaterialTheme.typography.labelMedium,
                                color = CyanAccent
                            )
                            when (match.status) {
                                MatchStatus.LIVE -> LiveStatusBadge(minute = match.minute)
                                MatchStatus.UPCOMING -> Text(
                                    text = "${match.matchDate} ${match.matchTime}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextGrayLight
                                )
                                MatchStatus.FINISHED -> Text(
                                    text = "پایان بازی",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextGrayMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Teams & Big Score
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Home Team
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                AsyncImage(
                                    model = match.homeTeamLogo,
                                    contentDescription = match.homeTeamPersian,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(NavySurface)
                                        .padding(6.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = match.homeTeamPersian,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Score / Kickoff
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (match.status != MatchStatus.UPCOMING) {
                                    Text(
                                        text = "${match.homeScore} : ${match.awayScore}",
                                        style = MaterialTheme.typography.displayLarge,
                                        color = if (match.status == MatchStatus.LIVE) GreenAccent else TextWhite,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                } else {
                                    Text(
                                        text = match.matchTime,
                                        style = MaterialTheme.typography.displayMedium,
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Away Team
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                AsyncImage(
                                    model = match.awayTeamLogo,
                                    contentDescription = match.awayTeamPersian,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(NavySurface)
                                        .padding(6.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = match.awayTeamPersian,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Match Metadata (Stadium, Referee)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ورزشگاه: ${match.stadium}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight)
                            Text("داور: ${match.referee}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight)
                        }
                    }
                }
            }

            // Simulated Probabilities Educational Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تحلیل آماری شبیه‌ساز (احتمال وقوع)",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Probability bars
                        ProbabilityBarItem(
                            label = "برد ${match.homeTeamPersian}",
                            percent = match.simHomeProb,
                            color = GreenAccent,
                            multiplier = match.homeProbMultiplier
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProbabilityBarItem(
                            label = "مساوی",
                            percent = match.simDrawProb,
                            color = GoldAccent,
                            multiplier = match.drawProbMultiplier
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ProbabilityBarItem(
                            label = "برد ${match.awayTeamPersian}",
                            percent = match.simAwayProb,
                            color = CyanAccent,
                            multiplier = match.awayProbMultiplier
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = NavyBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Over / Under 2.5
                        Text(
                            text = "پیش‌بینی تعداد گل‌ها:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGrayLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutcomeButton(
                                label = "بیشتر از ۲.۵ گل",
                                prob = "${match.simOverProb}%",
                                mult = "x${"%.2f".format(100.0 / match.simOverProb)}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.OVER_2_5) },
                                modifier = Modifier.weight(1f)
                            )
                            OutcomeButton(
                                label = "کمتر از ۲.۵ گل",
                                prob = "${match.simUnderProb}%",
                                mult = "x${"%.2f".format(100.0 / match.simUnderProb)}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.UNDER_2_5) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "نکته آموزشی: این احتمالات بر اساس مدل ریاضی بدون حاشیه سود بنگاه‌های شرط‌بندی تهیه شده است.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Quick Virtual Prediction Panel
            item {
                Text(
                    text = "ثبت پیش‌بینی بر روی این مسابقه:",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutcomeButton(
                        label = "۱ (میزبان)",
                        prob = "${match.simHomeProb}%",
                        mult = "x${match.homeProbMultiplier}",
                        onClick = { onPredictClick(match, PredictionOutcomeType.HOME_WIN) },
                        modifier = Modifier.weight(1f)
                    )
                    OutcomeButton(
                        label = "X (مساوی)",
                        prob = "${match.simDrawProb}%",
                        mult = "x${match.drawProbMultiplier}",
                        onClick = { onPredictClick(match, PredictionOutcomeType.DRAW) },
                        modifier = Modifier.weight(1f)
                    )
                    OutcomeButton(
                        label = "۲ (میهمان)",
                        prob = "${match.simAwayProb}%",
                        mult = "x${match.awayProbMultiplier}",
                        onClick = { onPredictClick(match, PredictionOutcomeType.AWAY_WIN) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Match Events Timeline
            item {
                Text(
                    text = "رویدادها و وقایع مسابقه (${match.events.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            if (match.events.isEmpty()) {
                item {
                    Text(
                        text = "هنوز رویدادی برای این مسابقه ثبت نشده است.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted
                    )
                }
            } else {
                items(match.events) { event ->
                    EventItemCard(event = event)
                }
            }
        }
    }
}

@Composable
private fun ProbabilityBarItem(
    label: String,
    percent: Int,
    color: Color,
    multiplier: Double
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = TextWhite)
            Text(
                text = "$percent% (ضریب آماری x$multiplier)",
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = NavyCardElevated
        )
    }
}

@Composable
private fun EventItemCard(event: MatchEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (event.type) {
                    "GOAL" -> GreenContainer
                    "CARD_YELLOW" -> Color(0xFF4A3E00)
                    "CARD_RED" -> LiveRedContainer
                    else -> NavyCardElevated
                }
            ) {
                Text(
                    text = "${event.minute}'",
                    style = MaterialTheme.typography.labelMedium,
                    color = when (event.type) {
                        "GOAL" -> GreenAccent
                        "CARD_YELLOW" -> GoldAccent
                        "CARD_RED" -> LiveRed
                        else -> TextWhite
                    },
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${event.player} (${event.teamName})",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrayLight
                )
            }
        }
    }
}
