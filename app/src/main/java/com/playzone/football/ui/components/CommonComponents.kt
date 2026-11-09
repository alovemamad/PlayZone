package com.playzone.football.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.playzone.football.R
import com.playzone.football.data.model.League
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.ui.theme.*

@Composable
fun PermanentNoticeBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("permanent_notice_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132238)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(Color(0xFF243B55), Color(0xFF141E30)))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00B4D8).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "قوانین مجازی",
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "شبیه‌ساز آموزشی پیش‌بینی فوتبال",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "کلیه اعتبارات و پیش‌بینی‌ها کاملاً مجازی هستند و فاقد هرگونه ارزش مادی یا امکان برداشت یا تبدیل می‌باشند.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrayLight,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun PlayZoneTopBar(
    virtualBalance: Long,
    onWalletClick: () -> Unit,
    onResponsibleClick: () -> Unit
) {
    Surface(
        color = NavyDarkest,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("app_logo_title")
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF00E676).copy(alpha = 0.12f))
                        .border(1.dp, GreenAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "PlayZone",
                        tint = GreenAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "PlayZone",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "شبیه‌ساز پیش‌بینی فوتبال",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGrayMuted
                    )
                }
            }

            // Right side: Balance badge and Safety button
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Responsible use icon button
                IconButton(
                    onClick = onResponsibleClick,
                    modifier = Modifier
                        .testTag("btn_responsible_topbar")
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "مدیریت زمان و استفاده مسئولانه",
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))

                // Virtual Credit Pill
                Surface(
                    onClick = onWalletClick,
                    shape = RoundedCornerShape(20.dp),
                    color = NavyCardElevated,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(GreenAccent, CyanAccent))
                    ),
                    modifier = Modifier.testTag("btn_wallet_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "%,d".format(virtualBalance),
                            style = MaterialTheme.typography.labelLarge,
                            color = GreenAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "اعتبار",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextGrayLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveStatusBadge(
    minute: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = LiveRedContainer,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(LiveRed, LiveRed.copy(alpha = 0.6f)))
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(LiveRed)
                    .alpha(alphaAnim)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "زنده $minute'",
                style = MaterialTheme.typography.labelMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LeagueFilterBar(
    leagues: List<League>,
    selectedLeagueId: String?,
    onLeagueSelect: (String?) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedLeagueId == null,
            onClick = { onLeagueSelect(null) },
            label = { Text("همه لیگ‌ها", style = MaterialTheme.typography.labelMedium) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GreenAccent,
                selectedLabelColor = NavyDarkest,
                containerColor = NavyCard,
                labelColor = TextWhite
            ),
            modifier = Modifier.testTag("filter_all_leagues")
        )
        leagues.forEach { league ->
            FilterChip(
                selected = selectedLeagueId == league.id,
                onClick = { onLeagueSelect(league.id) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = league.flagEmoji, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = league.persianName,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GreenAccent,
                    selectedLabelColor = NavyDarkest,
                    containerColor = NavyCard,
                    labelColor = TextWhite
                ),
                modifier = Modifier.testTag("filter_league_${league.id}")
            )
        }
    }
}

@Composable
fun MatchCard(
    match: Match,
    onMatchClick: () -> Unit,
    onPredictClick: (PredictionOutcomeType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMatchClick() }
            .testTag("match_card_${match.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(listOf(NavyBorder, NavySurface))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: League name & status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.leaguePersianName,
                    style = MaterialTheme.typography.labelMedium,
                    color = CyanAccent,
                    fontWeight = FontWeight.Medium
                )

                when (match.status) {
                    MatchStatus.LIVE -> LiveStatusBadge(minute = match.minute)
                    MatchStatus.UPCOMING -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavyCardElevated
                        ) {
                            Text(
                                text = "${match.matchDate} ${match.matchTime}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextGrayLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    MatchStatus.FINISHED -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavyDarkest
                        ) {
                            Text(
                                text = "پایان بازی",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextGrayMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Teams & Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Home Team
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = match.homeTeamLogo,
                        contentDescription = match.homeTeamPersian,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NavySurface)
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = match.homeTeamPersian,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Score / VS Center
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (match.status != MatchStatus.UPCOMING) {
                        Text(
                            text = "${match.homeScore} - ${match.awayScore}",
                            style = MaterialTheme.typography.displayMedium,
                            color = if (match.status == MatchStatus.LIVE) GreenAccent else TextWhite,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NavySurface,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "VS",
                                style = MaterialTheme.typography.titleLarge,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Text(
                        text = match.stadium,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGrayMuted,
                        maxLines = 1
                    )
                }

                // Away Team
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = match.awayTeamLogo,
                        contentDescription = match.awayTeamPersian,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NavySurface)
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = match.awayTeamPersian,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simulated Probabilities Prediction Row
            Text(
                text = "احتمال شبیه‌سازی‌شده (آموزشی):",
                style = MaterialTheme.typography.labelSmall,
                color = TextGrayMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1: Home Win
                OutcomeButton(
                    label = "۱ (میزبان)",
                    prob = "${match.simHomeProb}%",
                    mult = "x${match.homeProbMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.HOME_WIN) },
                    modifier = Modifier.weight(1f)
                )

                // X: Draw
                OutcomeButton(
                    label = "X (مساوی)",
                    prob = "${match.simDrawProb}%",
                    mult = "x${match.drawProbMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.DRAW) },
                    modifier = Modifier.weight(1f)
                )

                // 2: Away Win
                OutcomeButton(
                    label = "۲ (میهمان)",
                    prob = "${match.simAwayProb}%",
                    mult = "x${match.awayProbMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.AWAY_WIN) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun OutcomeButton(
    label: String,
    prob: String,
    mult: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = NavyCardElevated,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(listOf(NavyBorder, Color(0xFF1B2C4B)))
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextGrayLight,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = mult,
                style = MaterialTheme.typography.labelLarge,
                color = GreenAccent,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = prob,
                style = MaterialTheme.typography.labelSmall,
                color = TextGrayMuted,
                fontSize = 9.sp
            )
        }
    }
}
