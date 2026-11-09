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
    val eventsJson: String
)

@Entity(tableName = "predictions")
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val matchId: String,
    val matchTitle: String,
    val outcomeType: PredictionOutcomeType,
    val outcomeLabel: String,
    val simProbability: Int,
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
