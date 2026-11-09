package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.playzone.football.data.model.League
import com.playzone.football.data.model.Team
import com.playzone.football.ui.components.LeagueFilterBar
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun LeaguesScreen(
    viewModel: MainViewModel,
    onTeamClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val realMode by viewModel.isRealMode.collectAsStateWithLifecycle()
    if (realMode) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NavyBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "جدول رده‌بندی در حالت داده‌های واقعی هنوز اضافه نشده است.",
                color = TextGrayLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp)
            )
        }
        return
    }
    var selectedLeagueId by remember { mutableStateOf(viewModel.leagues.first().id) }

    val currentLeague = viewModel.leagues.find { it.id == selectedLeagueId } ?: viewModel.leagues.first()
    val teams = remember(selectedLeagueId) {
        viewModel.teams.filter { it.leagueId == selectedLeagueId }.sortedBy { it.rank }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("leagues_screen")
    ) {
        // League selector tabs
        LeagueFilterBar(
            leagues = viewModel.leagues,
            selectedLeagueId = selectedLeagueId,
            onLeagueSelect = { id -> id?.let { selectedLeagueId = it } }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // League Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = currentLeague.logoUrl,
                            contentDescription = currentLeague.persianName,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NavySurface)
                                .padding(6.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = currentLeague.persianName,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentLeague.name} • ${currentLeague.country} ${currentLeague.flagEmoji}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGrayLight
                            )
                        }
                    }
                }
            }

            // Standings Table Header
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NavyDarkest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("#", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.width(24.dp))
                        Text("تیم", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.weight(1f))
                        Text("بازی", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                        Text("برد", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                        Text("مساوی", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                        Text("باخت", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                        Text("امتیاز", style = MaterialTheme.typography.labelSmall, color = GreenAccent, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Table Rows
            items(teams) { team ->
                TeamTableRow(
                    team = team,
                    onClick = { onTeamClick(team.id) }
                )
            }
        }
    }
}

@Composable
private fun TeamTableRow(
    team: Team,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("team_row_${team.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${team.rank}",
                style = MaterialTheme.typography.titleSmall,
                color = if (team.rank <= 3) GreenAccent else TextGrayLight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(24.dp)
            )

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = team.logoUrl,
                    contentDescription = team.persianName,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = team.persianName,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text("${team.played}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
            Text("${team.wins}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
            Text("${team.draws}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
            Text("${team.losses}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
            Text(
                text = "${team.points}",
                style = MaterialTheme.typography.titleMedium,
                color = GreenAccent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
