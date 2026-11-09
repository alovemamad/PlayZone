package com.playzone.football.data.model

import com.squareup.moshi.JsonClass

enum class MatchStatus {
    LIVE,
    UPCOMING,
    FINISHED
}

enum class PredictionOutcomeType {
    HOME_WIN,
    DRAW,
    AWAY_WIN,
    OVER_2_5,
    UNDER_2_5
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
    val type: String, // GOAL, CARD_YELLOW, CARD_RED, SUB
    val teamName: String,
    val player: String,
    val description: String
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
    val simOverProb: Int,
    val simUnderProb: Int,
    val events: List<MatchEvent> = emptyList()
) {
    // Educational simulated multiplier: calculated as 100 / probability with 0 house margin
    val homeProbMultiplier: Double get() = (100.0 / simHomeProb.coerceAtLeast(1) * 100).toInt() / 100.0
    val drawProbMultiplier: Double get() = (100.0 / simDrawProb.coerceAtLeast(1) * 100).toInt() / 100.0
    val awayProbMultiplier: Double get() = (100.0 / simAwayProb.coerceAtLeast(1) * 100).toInt() / 100.0
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
    val logoUrl: String
)

data class Prediction(
    val id: Long = 0,
    val matchId: String,
    val matchTitle: String,
    val outcomeType: PredictionOutcomeType,
    val outcomeLabel: String,
    val simProbability: Int,
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
