package com.playzone.football.ui.components

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00B4D8).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "قوانین شبیه‌ساز",
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "شبیه‌ساز تخصصی مسابقات فوتبال PlayZone",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "بازی‌ها و ضرایب از منبع واقعی (API-Football) گرفته می‌شوند؛ ولی اعتبارات کاملاً مجازی‌اند و ارزش مادی یا امکان برداشت و واریز ندارند.",
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
    isLiveApi: Boolean,
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
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
                        .background(Color(0xFF00E676).copy(alpha = 0.15f))
                        .border(1.dp, GreenAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PlayZone",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextWhite,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // API / Simulator Status Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isLiveApi) GreenContainer else NavyCardElevated
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isLiveApi) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = if (isLiveApi) GreenAccent else CyanAccent,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isLiveApi) "API آنلاین" else "شبیه‌ساز",
                                    fontSize = 9.sp,
                                    color = if (isLiveApi) GreenAccent else CyanAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = "شبیه‌ساز حرفه‌ای مسابقات فوتبال",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGrayMuted
                    )
                }
            }

            // Right side: Balance badge and Safety button
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onResponsibleClick,
                    modifier = Modifier
                        .testTag("btn_responsible_topbar")
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "مدیریت زمان",
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
fun SearchBarField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("جستجوی تیم (پرسپولیس، رئال مادرید، آرسنال...) یا لیگ", fontSize = 12.sp, color = TextGrayMuted) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "پاک کردن", tint = TextGrayMuted, modifier = Modifier.size(18.dp))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenAccent,
            unfocusedBorderColor = NavyBorder,
            focusedContainerColor = NavyCard,
            unfocusedContainerColor = NavyCard,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("search_bar_field")
    )
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
            .padding(horizontal = 16.dp, vertical = 6.dp),
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

// 1xBet Style Match Card
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = match.leaguePersianName,
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

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

            Spacer(modifier = Modifier.height(12.dp))

            // Teams & Score (1xBet style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Home Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = match.homeTeamLogo,
                        contentDescription = match.homeTeamPersian,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NavySurface)
                            .padding(3.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = match.homeTeamPersian,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = match.homeTeamName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayMuted,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }

                // Score / VS Center
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (match.status != MatchStatus.UPCOMING) {
                        Text(
                            text = "${match.homeScore} - ${match.awayScore}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (match.status == MatchStatus.LIVE) GreenAccent else TextWhite,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavySurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = match.matchTime,
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Away Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = match.awayTeamPersian,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = match.awayTeamName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayMuted,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = match.awayTeamLogo,
                        contentDescription = match.awayTeamPersian,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NavySurface)
                            .padding(3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1xBet Betting Markets Grid: Row 1 (1 - X - 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BetOddButton(
                    market = "۱ (میزبان)",
                    odd = "${match.homeProbMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.HOME_WIN) },
                    modifier = Modifier.weight(1f)
                )
                BetOddButton(
                    market = "X (مساوی)",
                    odd = "${match.drawProbMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.DRAW) },
                    modifier = Modifier.weight(1f)
                )
                BetOddButton(
                    market = "۲ (میهمان)",
                    odd = "${match.awayProbMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.AWAY_WIN) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Secondary markets (1X, 12, X2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BetOddButton(
                    market = "1X (شانس ۲)",
                    odd = "${match.dc1XMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.DOUBLE_CHANCE_1X) },
                    modifier = Modifier.weight(1f),
                    color = CyanAccent
                )
                BetOddButton(
                    market = "12 (شانس ۲)",
                    odd = "${match.dc12Multiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.DOUBLE_CHANCE_12) },
                    modifier = Modifier.weight(1f),
                    color = CyanAccent
                )
                BetOddButton(
                    market = "X2 (شانس ۲)",
                    odd = "${match.dcX2Multiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.DOUBLE_CHANCE_X2) },
                    modifier = Modifier.weight(1f),
                    color = CyanAccent
                )
            }
        }
    }
}

@Composable
fun BetOddButton(
    market: String,
    odd: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = GreenAccent
) {
    val oddValue = odd.toDoubleOrNull()
    val available = oddValue == null || oddValue > 1.0
    val oddText = if (available) odd else "—"
    Surface(
        onClick = { if (available) onClick() },
        enabled = available,
        shape = RoundedCornerShape(8.dp),
        color = NavyCardElevated,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(listOf(NavyBorder, Color(0xFF1B2C4B)))
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = market,
                style = MaterialTheme.typography.labelSmall,
                color = TextGrayLight,
                fontSize = 10.sp,
                maxLines = 1
            )
            Text(
                text = oddText,
                style = MaterialTheme.typography.labelLarge,
                color = if (available) color else TextGrayMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
