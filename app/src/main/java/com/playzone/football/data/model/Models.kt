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
    val events: List<MatchEvent> = emptyList(),
    // Real bookmaker odds (0.0 = not available). Only used for API matches (id starts with "api_").
    val statsLoaded: Boolean = true,
    val statusShort: String = "",
    val oddHome: Double = 0.0,
    val oddDraw: Double = 0.0,
    val oddAway: Double = 0.0,
    val oddDc1x: Double = 0.0,
    val oddDc12: Double = 0.0,
    val oddDcx2: Double = 0.0,
    val oddOver25: Double = 0.0,
    val oddUnder25: Double = 0.0,
    val oddOver15: Double = 0.0,
    val oddUnder15: Double = 0.0,
    val oddBttsYes: Double = 0.0,
    val oddBttsNo: Double = 0.0
) {
    val isReal: Boolean get() = id.startsWith("api_")

    // Professional odds multipliers (Format: 1.85, 3.40, etc.)
    val homeProbMultiplier: Double get() = if (isReal) oddHome else (100.0 / simHomeProb.coerceAtLeast(5) * 100).toInt() / 100.0
    val drawProbMultiplier: Double get() = if (isReal) oddDraw else (100.0 / simDrawProb.coerceAtLeast(5) * 100).toInt() / 100.0
    val awayProbMultiplier: Double get() = if (isReal) oddAway else (100.0 / simAwayProb.coerceAtLeast(5) * 100).toInt() / 100.0

    // Double chance
    val dc1XMultiplier: Double get() = if (isReal) oddDc1x else ((100.0 / (simHomeProb + simDrawProb).coerceAtLeast(10)) * 100).toInt() / 100.0
    val dc12Multiplier: Double get() = if (isReal) oddDc12 else ((100.0 / (simHomeProb + simAwayProb).coerceAtLeast(10)) * 100).toInt() / 100.0
    val dcX2Multiplier: Double get() = if (isReal) oddDcx2 else ((100.0 / (simDrawProb + simAwayProb).coerceAtLeast(10)) * 100).toInt() / 100.0

    // Totals
    val over25Multiplier: Double get() = if (isReal) oddOver25 else (100.0 / simOverProb.coerceAtLeast(10) * 100).toInt() / 100.0
    val under25Multiplier: Double get() = if (isReal) oddUnder25 else (100.0 / simUnderProb.coerceAtLeast(10) * 100).toInt() / 100.0

    // Both Teams to Score (GG / NG)
    val bttsYesMultiplier: Double get() = if (isReal) oddBttsYes else 1.82
    val bttsNoMultiplier: Double get() = if (isReal) oddBttsNo else 1.98

    val over15Multiplier: Double get() = if (isReal) oddOver15 else 1.35
    val under15Multiplier: Double get() = if (isReal) oddUnder15 else 3.10

    /** Odds for an outcome; 0.0 means the market is not available. */
    fun oddFor(type: PredictionOutcomeType): Double = when (type) {
        PredictionOutcomeType.HOME_WIN -> homeProbMultiplier
        PredictionOutcomeType.DRAW -> drawProbMultiplier
        PredictionOutcomeType.AWAY_WIN -> awayProbMultiplier
        PredictionOutcomeType.DOUBLE_CHANCE_1X -> dc1XMultiplier
        PredictionOutcomeType.DOUBLE_CHANCE_12 -> dc12Multiplier
        PredictionOutcomeType.DOUBLE_CHANCE_X2 -> dcX2Multiplier
        PredictionOutcomeType.OVER_2_5 -> over25Multiplier
        PredictionOutcomeType.UNDER_2_5 -> under25Multiplier
        PredictionOutcomeType.OVER_1_5 -> over15Multiplier
        PredictionOutcomeType.UNDER_1_5 -> under15Multiplier
        PredictionOutcomeType.BOTH_TEAMS_SCORE_YES -> bttsYesMultiplier
        PredictionOutcomeType.BOTH_TEAMS_SCORE_NO -> bttsNoMultiplier
    }
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
