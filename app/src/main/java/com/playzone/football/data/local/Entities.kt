package com.playzone.football.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.PredictionStatus

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
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
    val simHomeProb: Int,
    val simDrawProb: Int,
    val simAwayProb: Int,
    val simOverProb: Int,
    val simUnderProb: Int,
    val eventsJson: String,
    val statsJson: String = "{}",
    val statusShort: String = "",
    val oddsUpdatedAt: Long = 0L,
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
)

@Entity(tableName = "predictions")
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val matchId: String,
    val matchTitle: String,
    val outcomeType: PredictionOutcomeType,
    val outcomeLabel: String,
    val simProbability: Int,
    val oddsMultiplier: Double = 1.85,
    val stakeCredits: Long,
    val potentialReturn: Long,
    val status: PredictionStatus,
    val createdAt: Long,
    val resolvedAt: Long?
)

@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val id: Int = 1,
    val balance: Long = 100_000,
    val initialBalance: Long = 100_000,
    val totalStaked: Long = 0,
    val totalWon: Long = 0,
    val predictionsCount: Int = 0,
    val wonCount: Int = 0,
    val lostCount: Int = 0
)

@Entity(tableName = "responsible_settings")
data class ResponsibleSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val dailyLimitMinutes: Int = 60,
    val totalMinutesToday: Int = 15,
    val isPaused: Boolean = false,
    val pauseUntil: Long = 0L,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val apiFootballKey: String = "",
    val useLiveApi: Boolean = false,
    val customApiConnected: Boolean = false,
    val apiRemainingCalls: Int = 100,
    val lastSyncTime: String = "همگام‌سازی شده"
)
