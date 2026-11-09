package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.Wallet
import com.playzone.football.ui.components.LeagueFilterBar
import com.playzone.football.ui.components.LiveStatusBadge
import com.playzone.football.ui.components.MatchCard
import com.playzone.football.ui.components.PermanentNoticeBanner
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    wallet: Wallet,
    matches: List<Match>,
    selectedLeagueId: String?,
    onNavigateToMatch: (String) -> Unit,
    onNavigateToMatchesList: (MatchStatus?) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToLeagues: () -> Unit,
    onPredictClick: (Match, PredictionOutcomeType) -> Unit,
    modifier: Modifier = Modifier
) {
    val liveMatches = matches.filter { it.status == MatchStatus.LIVE }
    val upcomingMatches = matches.filter { it.status == MatchStatus.UPCOMING }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Permanent Educational Notice Banner
        item {
            PermanentNoticeBanner(modifier = Modifier.padding(16.dp))
        }

        // Virtual Balance & Quick Refill Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_balance_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(GreenAccent.copy(alpha = 0.5f), CyanAccent.copy(alpha = 0.3f)))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "موجودی مجازی شما",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "%,d".format(wallet.balance),
                                style = MaterialTheme.typography.displayMedium,
                                color = GreenAccent,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "اعتبار",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextGrayLight
                            )
                        }
                        Text(
                            text = "نرخ برد: ${"%.1f".format(wallet.winRate)}% (${wallet.wonCount} از ${wallet.predictionsCount})",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = onNavigateToWallet,
                            colors = ButtonDefaults.buttonColors(containerColor = NavySurface),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مدیریت",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextWhite
                            )
                        }
                    }
                }
            }
        }

        // Live Matches Carousel
        if (liveMatches.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LiveRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مسابقات در حال برگزاری (${liveMatches.size})",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = { onNavigateToMatchesList(MatchStatus.LIVE) }) {
                        Text("مشاهده همه", color = CyanAccent, style = MaterialTheme.typography.labelMedium)
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(liveMatches) { liveMatch ->
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .clickable { onNavigateToMatch(liveMatch.id) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.verticalGradient(listOf(LiveRed.copy(alpha = 0.5f), NavyBorder))
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = liveMatch.leaguePersianName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextGrayMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    LiveStatusBadge(minute = liveMatch.minute)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AsyncImage(
                                            model = liveMatch.homeTeamLogo,
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = liveMatch.homeTeamPersian,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "${liveMatch.homeScore} - ${liveMatch.awayScore}",
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = GreenAccent,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AsyncImage(
                                            model = liveMatch.awayTeamLogo,
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = liveMatch.awayTeamPersian,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { onPredictClick(liveMatch, PredictionOutcomeType.HOME_WIN) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyCardElevated),
                                        contentPadding = PaddingValues(2.dp)
                                    ) {
                                        Text("۱ (${liveMatch.homeProbMultiplier})", style = MaterialTheme.typography.labelSmall, color = TextWhite)
                                    }
                                    Button(
                                        onClick = { onPredictClick(liveMatch, PredictionOutcomeType.DRAW) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyCardElevated),
                                        contentPadding = PaddingValues(2.dp)
                                    ) {
                                        Text("X (${liveMatch.drawProbMultiplier})", style = MaterialTheme.typography.labelSmall, color = TextWhite)
                                    }
                                    Button(
                                        onClick = { onPredictClick(liveMatch, PredictionOutcomeType.AWAY_WIN) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyCardElevated),
                                        contentPadding = PaddingValues(2.dp)
                                    ) {
                                        Text("۲ (${liveMatch.awayProbMultiplier})", style = MaterialTheme.typography.labelSmall, color = TextWhite)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Leagues Filter Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "لیگ‌های تحت پوشش",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToLeagues) {
                    Text("جدول لیگ‌ها", color = CyanAccent, style = MaterialTheme.typography.labelMedium)
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                }
            }
            LeagueFilterBar(
                leagues = viewModel.leagues,
                selectedLeagueId = selectedLeagueId,
                onLeagueSelect = { viewModel.selectLeagueFilter(it) }
            )
        }

        // Upcoming Matches List
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مسابقات برجسته پیش‌رو",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigateToMatchesList(MatchStatus.UPCOMING) }) {
                    Text("همه مسابقات", color = CyanAccent, style = MaterialTheme.typography.labelMedium)
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                }
            }
        }

        items(upcomingMatches.take(4)) { match ->
            MatchCard(
                match = match,
                onMatchClick = { onNavigateToMatch(match.id) },
                onPredictClick = { outcome -> onPredictClick(match, outcome) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}
