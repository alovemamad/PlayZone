package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playzone.football.data.model.Prediction
import com.playzone.football.data.model.PredictionStatus
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun PredictionsScreen(
    viewModel: MainViewModel,
    predictions: List<Prediction>,
    onExploreMatchesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<PredictionStatus?>(null) }

    val filtered = remember(predictions, selectedFilter) {
        if (selectedFilter == null) predictions
        else predictions.filter { it.status == selectedFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("predictions_screen")
    ) {
        // Top Filter Bar
        Surface(color = NavyDarkest) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("همه (${predictions.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenAccent,
                        selectedLabelColor = NavyDarkest,
                        containerColor = NavyCard,
                        labelColor = TextWhite
                    )
                )
                FilterChip(
                    selected = selectedFilter == PredictionStatus.PENDING,
                    onClick = { selectedFilter = PredictionStatus.PENDING },
                    label = { Text("در جریان (${predictions.count { it.status == PredictionStatus.PENDING }})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenAccent,
                        selectedLabelColor = NavyDarkest,
                        containerColor = NavyCard,
                        labelColor = TextWhite
                    )
                )
                FilterChip(
                    selected = selectedFilter == PredictionStatus.WON,
                    onClick = { selectedFilter = PredictionStatus.WON },
                    label = { Text("برنده (${predictions.count { it.status == PredictionStatus.WON }})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenAccent,
                        selectedLabelColor = NavyDarkest,
                        containerColor = NavyCard,
                        labelColor = TextWhite
                    )
                )
                FilterChip(
                    selected = selectedFilter == PredictionStatus.LOST,
                    onClick = { selectedFilter = PredictionStatus.LOST },
                    label = { Text("ناموفق (${predictions.count { it.status == PredictionStatus.LOST }})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenAccent,
                        selectedLabelColor = NavyDarkest,
                        containerColor = NavyCard,
                        labelColor = TextWhite
                    )
                )
            }
        }

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = TextGrayMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "پیش‌بینی ثبت‌شده‌ای یافت نشد",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "یک مسابقه فوتبال انتخاب کرده و پیش‌بینی مجازی خود را آغاز نمایید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onExploreMatchesClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = NavyDarkest)
                    ) {
                        Text("مشاهده مسابقات فوتبال", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { prediction ->
                    PredictionItemCard(
                        prediction = prediction,
                        onSimulateResolution = { didWin ->
                            viewModel.resolvePrediction(prediction.id, didWin)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PredictionItemCard(
    prediction: Prediction,
    onSimulateResolution: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Match & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prediction.matchTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (prediction.status) {
                        PredictionStatus.WON -> GreenContainer
                        PredictionStatus.LOST -> LiveRedContainer
                        PredictionStatus.PENDING -> NavyCardElevated
                        PredictionStatus.CANCELLED -> NavyDarkest
                    }
                ) {
                    Text(
                        text = when (prediction.status) {
                            PredictionStatus.WON -> "✓ برنده شده"
                            PredictionStatus.LOST -> "✗ ناموفق"
                            PredictionStatus.PENDING -> "در جریان"
                            PredictionStatus.CANCELLED -> "لغو شده"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = when (prediction.status) {
                            PredictionStatus.WON -> GreenAccent
                            PredictionStatus.LOST -> LiveRed
                            PredictionStatus.PENDING -> GoldAccent
                            PredictionStatus.CANCELLED -> TextGrayMuted
                        },
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prediction Outcome details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("نتیجه انتخابی:", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                    Text(
                        text = prediction.outcomeLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("اعتبار مجازی تخصیص‌یافته:", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                    Text(
                        text = "%,d اعتبار".format(prediction.stakeCredits),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (prediction.status == PredictionStatus.WON) "اعتبار دریافتی:" else "بازگشت فرضی:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrayLight
                )
                Text(
                    text = "%,d اعتبار مجازی".format(prediction.potentialReturn),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (prediction.status == PredictionStatus.WON) GreenAccent else GoldAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            // If pending, allow simulated educational resolution
            if (prediction.status == PredictionStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "شبیه‌سازی نتیجه:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted
                    )
                    OutlinedButton(
                        onClick = { onSimulateResolution(true) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("ثبت برد", style = MaterialTheme.typography.labelSmall, color = GreenAccent)
                    }
                    OutlinedButton(
                        onClick = { onSimulateResolution(false) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("ثبت باخت", style = MaterialTheme.typography.labelSmall, color = LiveRed)
                    }
                }
            }
        }
    }
}
