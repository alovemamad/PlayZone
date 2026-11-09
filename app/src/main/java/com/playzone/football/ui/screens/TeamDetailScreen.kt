package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.playzone.football.data.model.Team
import com.playzone.football.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    team: Team?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (team == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NavyBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("تیم مورد نظر یافت نشد", color = TextWhite)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(team.persianName, color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_team_detail")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        containerColor = NavyBackground,
        modifier = modifier.testTag("team_detail_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Team Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = team.logoUrl,
                            contentDescription = team.persianName,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(NavySurface)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = team.persianName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = team.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Form pills (W/D/L)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("فرم ۵ بازی اخیر:", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted)
                            team.form.forEach { f ->
                                Surface(
                                    shape = CircleShape,
                                    color = when (f) {
                                        "W" -> GreenAccent
                                        "D" -> GoldAccent
                                        else -> LiveRed
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = f,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyDarkest
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Stats grid
            item {
                Text("آمار و رتبه در لیگ", style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailStatBox(title = "رتبه در جدول", value = "#${team.rank}", modifier = Modifier.weight(1f))
                    DetailStatBox(title = "امتیاز کسب شده", value = "${team.points}", modifier = Modifier.weight(1f), valueColor = GreenAccent)
                    DetailStatBox(title = "تعداد بازی‌ها", value = "${team.played}", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailStatBox(title = "تعداد بردها", value = "${team.wins}", modifier = Modifier.weight(1f), valueColor = GreenAccent)
                    DetailStatBox(title = "تعداد تساوی‌ها", value = "${team.draws}", modifier = Modifier.weight(1f), valueColor = GoldAccent)
                    DetailStatBox(title = "تعداد باخت‌ها", value = "${team.losses}", modifier = Modifier.weight(1f), valueColor = LiveRed)
                }
            }
        }
    }
}

@Composable
private fun DetailStatBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextWhite
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = TextGrayMuted, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = valueColor, fontWeight = FontWeight.Bold)
        }
    }
}
