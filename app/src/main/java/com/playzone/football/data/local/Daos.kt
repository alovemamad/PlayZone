package com.playzone.football.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE status = :status")
    fun getMatchesByStatus(status: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    suspend fun getMatchById(id: String): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Query("SELECT * FROM matches WHERE id IN (:ids)")
    suspend fun getMatchesByIds(ids: List<String>): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE status = 'LIVE' AND id LIKE 'api_%'")
    suspend fun getLiveApiMatches(): List<MatchEntity>

    @Query("SELECT MAX(oddsUpdatedAt) FROM matches")
    suspend fun maxOddsUpdatedAt(): Long?

    @Query("DELETE FROM matches WHERE id NOT LIKE 'api_%'")
    suspend fun deleteDemoMatches()

    // Old finished/unreferenced API matches (kept if a prediction points at them)
    @Query("DELETE FROM matches WHERE id LIKE 'api_%' AND matchDate < :minDate AND id NOT IN (SELECT matchId FROM predictions)")
    suspend fun deleteOldApiMatches(minDate: String)

    @Query("DELETE FROM matches")
    suspend fun clearAll()
}

@Dao
interface PredictionDao {
    @Query("SELECT * FROM predictions ORDER BY createdAt DESC")
    fun getAllPredictions(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE status = 'PENDING'")
    fun getActivePredictions(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE status = 'PENDING'")
    suspend fun getPendingPredictions(): List<PredictionEntity>

    @Query("SELECT * FROM predictions WHERE id = :id LIMIT 1")
    suspend fun getPredictionById(id: Long): PredictionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: PredictionEntity): Long

    @Update
    suspend fun updatePrediction(prediction: PredictionEntity)

    @Query("DELETE FROM predictions")
    suspend fun clearAll()
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet WHERE id = 1 LIMIT 1")
    fun getWalletFlow(): Flow<WalletEntity?>

    @Query("SELECT * FROM wallet WHERE id = 1 LIMIT 1")
    suspend fun getWallet(): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWallet(wallet: WalletEntity)

    @Query("UPDATE wallet SET balance = :newBalance WHERE id = 1")
    suspend fun updateBalance(newBalance: Long)

    @Query("DELETE FROM wallet")
    suspend fun clearWallet()
}

@Dao
interface ResponsibleSettingsDao {
    @Query("SELECT * FROM responsible_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<ResponsibleSettingsEntity?>

    @Query("SELECT * FROM responsible_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): ResponsibleSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: ResponsibleSettingsEntity)
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettingsEntity)
}
