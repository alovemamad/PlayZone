package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.ui.components.LeagueFilterBar
import com.playzone.football.ui.components.MatchCard
import com.playzone.football.ui.components.SearchBarField
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun MatchesScreen(
    viewModel: MainViewModel,
    matches: List<Match>,
    selectedStatus: MatchStatus?,
    selectedLeagueId: String?,
    onNavigateToMatch: (String) -> Unit,
    onPredictClick: (Match, PredictionOutcomeType) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val tabs = listOf(
        null to "همه مسابقات",
        MatchStatus.LIVE to "زنده",
        MatchStatus.UPCOMING to "پیش‌رو",
        MatchStatus.FINISHED to "پایان‌یافته"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("matches_screen")
    ) {
        // Tab Row for Match Statuses
        TabRow(
            selectedTabIndex = tabs.indexOfFirst { it.first == selectedStatus }.coerceAtLeast(0),
            containerColor = NavyDarkest,
            contentColor = TextWhite,
            indicator = { tabPositions ->
                val index = tabs.indexOfFirst { it.first == selectedStatus }.coerceAtLeast(0)
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                    color = GreenAccent,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEach { (status, title) ->
                val selected = selectedStatus == status
                Tab(
                    selected = selected,
                    onClick = { viewModel.selectStatusFilter(status) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selected) GreenAccent else TextGrayLight,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Search Bar
        SearchBarField(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // League Filters
        LeagueFilterBar(
            leagues = viewModel.leagues,
            selectedLeagueId = selectedLeagueId,
            onLeagueSelect = { viewModel.selectLeagueFilter(it) }
        )

        // Matches List
        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = TextGrayMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "هیچ مسابقه‌ای یافت نشد",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextGrayLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "عبارت جستجو یا فیلتر لیگ را تغییر دهید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(matches, key = { it.id }) { match ->
                    MatchCard(
                        match = match,
                        onMatchClick = { onNavigateToMatch(match.id) },
                        onPredictClick = { outcome -> onPredictClick(match, outcome) }
                    )
                }
            }
        }
    }
}
