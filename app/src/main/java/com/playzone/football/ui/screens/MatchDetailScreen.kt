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
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SportsSoccer
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
import coil.compose.AsyncImage
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchEvent
import com.playzone.football.data.model.MatchStats
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.ui.components.BetOddButton
import com.playzone.football.ui.components.LiveStatusBadge
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

    var selectedMarketTab by remember { mutableStateOf(0) }
    val marketTabs = listOf("همه بازارها", "نتیجه بازی", "شانس دوبرابر", "مجموع گل‌ها", "هر دو تیم گل")

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
            // Match-Center Header (1xBet Style Pitch Visualizer)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(listOf(Color(0xFF1E3A5F), NavyBorder))
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
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
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

                        // Teams & Score Center
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
                                        .size(64.dp)
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
                                Text(
                                    text = match.homeTeamName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrayMuted,
                                    fontSize = 11.sp
                                )
                            }

                            // Score / Kickoff Center
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
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NavySurface,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = if (match.status == MatchStatus.LIVE) "نیمه دوم" else "زمان رسمی",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextGrayLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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
                                        .size(64.dp)
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
                                Text(
                                    text = match.awayTeamName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrayMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

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

            // Live statistics (real data only; loaded from the API when the page opens)
            if (!match.isReal || match.statsLoaded) item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QueryStats, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "آمار زنده مسابقه (Match Center)",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        DualStatBar(
                            title = "درصد مالکیت توپ",
                            homeVal = "${match.stats.possessionHome}%",
                            awayVal = "${match.stats.possessionAway}%",
                            homeRatio = match.stats.possessionHome / 100f
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DualStatBar(
                            title = "شوت‌های داخل چارچوب",
                            homeVal = "${match.stats.shotsOnTargetHome}",
                            awayVal = "${match.stats.shotsOnTargetAway}",
                            homeRatio = match.stats.shotsOnTargetHome.toFloat() / (match.stats.shotsOnTargetHome + match.stats.shotsOnTargetAway).coerceAtLeast(1)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DualStatBar(
                            title = "تعداد کرنرها",
                            homeVal = "${match.stats.cornersHome}",
                            awayVal = "${match.stats.cornersAway}",
                            homeRatio = match.stats.cornersHome.toFloat() / (match.stats.cornersHome + match.stats.cornersAway).coerceAtLeast(1)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DualStatBar(
                            title = "تعداد خطاها",
                            homeVal = "${match.stats.foulsHome}",
                            awayVal = "${match.stats.foulsAway}",
                            homeRatio = match.stats.foulsHome.toFloat() / (match.stats.foulsHome + match.stats.foulsAway).coerceAtLeast(1)
                        )
                    }
                }
            }

            // 1xBet Full Betting Markets
            item {
                Text(
                    text = "بازارهای پیش‌بینی مسابقه (ضرایب 1xBet شبیه‌ساز)",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Market Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedMarketTab,
                    containerColor = NavyDarkest,
                    contentColor = TextWhite,
                    edgePadding = 0.dp
                ) {
                    marketTabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedMarketTab == index,
                            onClick = { selectedMarketTab = index },
                            text = { Text(tabTitle, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }

            // Market 1: 1 - X - 2
            if (selectedMarketTab == 0 || selectedMarketTab == 1) {
                item {
                    MarketSectionCard(title = "نتیجه مسابقه (1X2)") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BetOddButton(
                                market = "۱ (برد ${match.homeTeamPersian})",
                                odd = "${match.homeProbMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.HOME_WIN) },
                                modifier = Modifier.weight(1f)
                            )
                            BetOddButton(
                                market = "X (تساوی)",
                                odd = "${match.drawProbMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.DRAW) },
                                modifier = Modifier.weight(1f)
                            )
                            BetOddButton(
                                market = "۲ (برد ${match.awayTeamPersian})",
                                odd = "${match.awayProbMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.AWAY_WIN) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Market 2: Double Chance (1X, 12, X2)
            if (selectedMarketTab == 0 || selectedMarketTab == 2) {
                item {
                    MarketSectionCard(title = "شانس دوبرابر (Double Chance)") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BetOddButton(
                                market = "1X (میزبان یا مساوی)",
                                odd = "${match.dc1XMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.DOUBLE_CHANCE_1X) },
                                modifier = Modifier.weight(1f),
                                color = CyanAccent
                            )
                            BetOddButton(
                                market = "12 (برد یکی از دو تیم)",
                                odd = "${match.dc12Multiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.DOUBLE_CHANCE_12) },
                                modifier = Modifier.weight(1f),
                                color = CyanAccent
                            )
                            BetOddButton(
                                market = "X2 (مساوی یا میهمان)",
                                odd = "${match.dcX2Multiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.DOUBLE_CHANCE_X2) },
                                modifier = Modifier.weight(1f),
                                color = CyanAccent
                            )
                        }
                    }
                }
            }

            // Market 3: Totals (Over / Under)
            if (selectedMarketTab == 0 || selectedMarketTab == 3) {
                item {
                    MarketSectionCard(title = "مجموع گل‌ها (Totals Over/Under)") {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BetOddButton(
                                    market = "بیشتر از ۲.۵ گل (+2.5)",
                                    odd = "${match.over25Multiplier}",
                                    onClick = { onPredictClick(match, PredictionOutcomeType.OVER_2_5) },
                                    modifier = Modifier.weight(1f),
                                    color = GoldAccent
                                )
                                BetOddButton(
                                    market = "کمتر از ۲.۵ گل (-2.5)",
                                    odd = "${match.under25Multiplier}",
                                    onClick = { onPredictClick(match, PredictionOutcomeType.UNDER_2_5) },
                                    modifier = Modifier.weight(1f),
                                    color = GoldAccent
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BetOddButton(
                                    market = "بیشتر از ۱.۵ گل (+1.5)",
                                    odd = "1.35",
                                    onClick = { onPredictClick(match, PredictionOutcomeType.OVER_1_5) },
                                    modifier = Modifier.weight(1f)
                                )
                                BetOddButton(
                                    market = "کمتر از ۱.۵ گل (-1.5)",
                                    odd = "3.10",
                                    onClick = { onPredictClick(match, PredictionOutcomeType.UNDER_1_5) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Market 4: Both Teams to Score (GG / NG)
            if (selectedMarketTab == 0 || selectedMarketTab == 4) {
                item {
                    MarketSectionCard(title = "هر دو تیم گل می‌زنند (Both Teams to Score)") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BetOddButton(
                                market = "بله - گلزنی هر دو تیم (GG)",
                                odd = "${match.bttsYesMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.BOTH_TEAMS_SCORE_YES) },
                                modifier = Modifier.weight(1f),
                                color = GreenAccent
                            )
                            BetOddButton(
                                market = "خیر - حداقل یک کلین‌شیت (NG)",
                                odd = "${match.bttsNoMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.BOTH_TEAMS_SCORE_NO) },
                                modifier = Modifier.weight(1f),
                                color = LiveRed
                            )
                        }
                    }
                }
            }

            // Match Events Timeline
            item {
                Text(
                    text = "گزارش لحظه‌به‌لحظه و رویدادهای بازی (${match.events.size})",
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
private fun MarketSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextGrayLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun DualStatBar(
    title: String,
    homeVal: String,
    awayVal: String,
    homeRatio: Float
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(homeVal, style = MaterialTheme.typography.labelSmall, color = GreenAccent, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextGrayLight)
            Text(awayVal, style = MaterialTheme.typography.labelSmall, color = CyanAccent, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(NavyCardElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(homeRatio.coerceIn(0.05f, 0.95f))
                    .background(GreenAccent)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((1f - homeRatio).coerceIn(0.05f, 0.95f))
                    .background(CyanAccent)
            )
        }
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
