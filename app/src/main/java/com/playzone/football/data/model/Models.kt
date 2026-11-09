package com.playzone.football.data.model

import com.squareup.moshi.JsonClass

enum class MatchStatus {
    LIVE,
    UPCOMING,
    FINISHED
}

enum class PredictionOutcomeType {
    HOME_WIN,              // 1
    DRAW,                  // X
    AWAY_WIN,              // 2
    DOUBLE_CHANCE_1X,      // 1X
    DOUBLE_CHANCE_12,      // 12
    DOUBLE_CHANCE_X2,      // X2
    OVER_2_5,              // Total Over 2.5
    UNDER_2_5,             // Total Under 2.5
    OVER_1_5,              // Total Over 1.5
    UNDER_1_5,             // Total Under 1.5
    BOTH_TEAMS_SCORE_YES,  // GG
    BOTH_TEAMS_SCORE_NO    // NG
}

enum class PredictionStatus {
    PENDING,
    WON,
    LOST,
    CANCELLED
}

@JsonClass(generateAdapter = true)
data class MatchEvent(
    val minute: Int,
    val type: String, // GOAL, CARD_YELLOW, CARD_RED, SUB, VAR
    val teamName: String,
    val player: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class MatchStats(
    val possessionHome: Int = 52,
    val possessionAway: Int = 48,
    val shotsOnTargetHome: Int = 6,
    val shotsOnTargetAway: Int = 4,
    val cornersHome: Int = 7,
    val cornersAway: Int = 4,
    val foulsHome: Int = 10,
    val foulsAway: Int = 13,
    val yellowCardsHome: Int = 1,
    val yellowCardsAway: Int = 2
)

data class Match(
    val id: String,
    val leagueId: String,
    val leagueName: String,
    val leaguePersianName: String,
    val homeTeamId: String,
    val homeTeamName: String,
    val homeTeamPersian: String,
    val homeTeamLogo: String,
    val awayTeamId: String,
    val awayTeamName: String,
    val awayTeamPersian: String,
    val awayTeamLogo: String,
    val status: MatchStatus,
    val minute: Int,
    val homeScore: Int,
    val awayScore: Int,
    val matchTime: String,
    val matchDate: String,
    val stadium: String,
    val referee: String,
    // Educational simulated probabilities (Sum to 100%)
    val simHomeProb: Int,
    val simDrawProb: Int,
    val simAwayProb: Int,
    val simOverProb: Int = 55,
    val simUnderProb: Int = 45,
    val stats: MatchStats = MatchStats(),
    val events: List<MatchEvent> = emptyList()
) {
    // Professional odds multipliers (Format: 1.85, 3.40, etc.)
    val homeProbMultiplier: Double get() = (100.0 / simHomeProb.coerceAtLeast(5) * 100).toInt() / 100.0
    val drawProbMultiplier: Double get() = (100.0 / simDrawProb.coerceAtLeast(5) * 100).toInt() / 100.0
    val awayProbMultiplier: Double get() = (100.0 / simAwayProb.coerceAtLeast(5) * 100).toInt() / 100.0

    // Double chance
    val dc1XMultiplier: Double get() = ((100.0 / (simHomeProb + simDrawProb).coerceAtLeast(10)) * 100).toInt() / 100.0
    val dc12Multiplier: Double get() = ((100.0 / (simHomeProb + simAwayProb).coerceAtLeast(10)) * 100).toInt() / 100.0
    val dcX2Multiplier: Double get() = ((100.0 / (simDrawProb + simAwayProb).coerceAtLeast(10)) * 100).toInt() / 100.0

    // Totals
    val over25Multiplier: Double get() = (100.0 / simOverProb.coerceAtLeast(10) * 100).toInt() / 100.0
    val under25Multiplier: Double get() = (100.0 / simUnderProb.coerceAtLeast(10) * 100).toInt() / 100.0

    // Both Teams to Score (GG / NG)
    val bttsYesMultiplier: Double get() = 1.82
    val bttsNoMultiplier: Double get() = 1.98
}

data class Team(
    val id: String,
    val name: String,
    val persianName: String,
    val leagueId: String,
    val logoUrl: String,
    val rank: Int,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val points: Int,
    val form: List<String> // e.g. ["W", "W", "D", "L", "W"]
)

data class League(
    val id: String,
    val name: String,
    val persianName: String,
    val country: String,
    val flagEmoji: String,
    val logoUrl: String,
    val isHot: Boolean = false
)

data class Prediction(
    val id: Long = 0,
    val matchId: String,
    val matchTitle: String,
    val outcomeType: PredictionOutcomeType,
    val outcomeLabel: String,
    val simProbability: Int,
    val oddsMultiplier: Double = 1.85,
    val stakeCredits: Long,
    val potentialReturn: Long,
    val status: PredictionStatus = PredictionStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)

data class Wallet(
    val balance: Long = 100_000,
    val initialBalance: Long = 100_000,
    val totalStaked: Long = 0,
    val totalWon: Long = 0,
    val predictionsCount: Int = 0,
    val wonCount: Int = 0,
    val lostCount: Int = 0
) {
    val winRate: Float
        get() = if (predictionsCount > 0) (wonCount.toFloat() / predictionsCount) * 100f else 0f

    val netProfit: Long
        get() = totalWon - totalStaked
}

data class ResponsibleSettings(
    val sessionTimerMinutes: Int = 0,
    val dailyLimitMinutes: Int = 60,
    val isPaused: Boolean = false,
    val pauseUntil: Long = 0L,
    val totalSessionMinutesToday: Int = 12
)
