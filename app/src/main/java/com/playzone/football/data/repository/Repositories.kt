package com.playzone.football.data.repository

import android.content.Context
import com.playzone.football.data.local.AppDatabase
import com.playzone.football.data.local.MatchEntity
import com.playzone.football.data.local.PredictionEntity
import com.playzone.football.data.local.ResponsibleSettingsEntity
import com.playzone.football.data.local.WalletEntity
import com.playzone.football.data.mock.SampleFootballData
import com.playzone.football.data.model.League
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchEvent
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.Prediction
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.PredictionStatus
import com.playzone.football.data.model.ResponsibleSettings
import com.playzone.football.data.model.Team
import com.playzone.football.data.model.Wallet
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FootballRepository(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val eventListType = Types.newParameterizedType(List::class.java, MatchEvent::class.java)
    private val eventAdapter = moshi.adapter<List<MatchEvent>>(eventListType)

    private val _isDemoMode = MutableStateFlow(true)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    private val _apiStatusMessage = MutableStateFlow("حالت شبیه‌ساز آفلاین فعال است")
    val apiStatusMessage: StateFlow<String> = _apiStatusMessage.asStateFlow()

    val leagues: List<League> = SampleFootballData.sampleLeagues
    val teams: List<Team> = SampleFootballData.sampleTeams

    init {
        scope.launch {
            initSampleDataIfNeeded()
            startLiveTickerSimulator()
        }
    }

    private suspend fun initSampleDataIfNeeded() = withContext(Dispatchers.IO) {
        val existing = database.matchDao().getMatchById(SampleFootballData.sampleMatches.first().id)
        if (existing == null) {
            val entities = SampleFootballData.sampleMatches.map { matchToEntity(it) }
            database.matchDao().insertMatches(entities)
        }
    }

    fun getAllMatchesFlow(): Flow<List<Match>> {
        return database.matchDao().getAllMatches().map { entities ->
            if (entities.isEmpty()) {
                SampleFootballData.sampleMatches
            } else {
                entities.map { entityToMatch(it) }
            }
        }
    }

    suspend fun getMatchById(id: String): Match? = withContext(Dispatchers.IO) {
        val entity = database.matchDao().getMatchById(id)
        entity?.let { entityToMatch(it) } ?: SampleFootballData.sampleMatches.find { it.id == id }
    }

    fun getTeamById(id: String): Team? {
        return teams.find { it.id == id }
    }

    fun getLeagueById(id: String): League? {
        return leagues.find { it.id == id }
    }

    fun getTeamsForLeague(leagueId: String): List<Team> {
        return teams.filter { it.leagueId == leagueId }.sortedBy { it.rank }
    }

    // Ticker simulator that periodically advances live match minutes and generates educational events
    private fun startLiveTickerSimulator() {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(12_000) // update every 12 seconds
                try {
                    val matches = database.matchDao().getMatchById("match_live_1")
                    if (matches != null) {
                        val currentMin = if (matches.minute < 90) matches.minute + 1 else 90
                        val updated = matches.copy(minute = currentMin)
                        database.matchDao().updateMatch(updated)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun matchToEntity(m: Match): MatchEntity {
        val eventsJson = try {
            eventAdapter.toJson(m.events)
        } catch (_: Exception) {
            "[]"
        }
        return MatchEntity(
            id = m.id,
            leagueId = m.leagueId,
            leagueName = m.leagueName,
            leaguePersianName = m.leaguePersianName,
            homeTeamId = m.homeTeamId,
            homeTeamName = m.homeTeamName,
            homeTeamPersian = m.homeTeamPersian,
            homeTeamLogo = m.homeTeamLogo,
            awayTeamId = m.awayTeamId,
            awayTeamName = m.awayTeamName,
            awayTeamPersian = m.awayTeamPersian,
            awayTeamLogo = m.awayTeamLogo,
            status = m.status,
            minute = m.minute,
            homeScore = m.homeScore,
            awayScore = m.awayScore,
            matchTime = m.matchTime,
            matchDate = m.matchDate,
            stadium = m.stadium,
            referee = m.referee,
            simHomeProb = m.simHomeProb,
            simDrawProb = m.simDrawProb,
            simAwayProb = m.simAwayProb,
            simOverProb = m.simOverProb,
            simUnderProb = m.simUnderProb,
            eventsJson = eventsJson
        )
    }

    private fun entityToMatch(e: MatchEntity): Match {
        val events = try {
            eventAdapter.fromJson(e.eventsJson) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
        return Match(
            id = e.id,
            leagueId = e.leagueId,
            leagueName = e.leagueName,
            leaguePersianName = e.leaguePersianName,
            homeTeamId = e.homeTeamId,
            homeTeamName = e.homeTeamName,
            homeTeamPersian = e.homeTeamPersian,
            homeTeamLogo = e.homeTeamLogo,
            awayTeamId = e.awayTeamId,
            awayTeamName = e.awayTeamName,
            awayTeamPersian = e.awayTeamPersian,
            awayTeamLogo = e.awayTeamLogo,
            status = e.status,
            minute = e.minute,
            homeScore = e.homeScore,
            awayScore = e.awayScore,
            matchTime = e.matchTime,
            matchDate = e.matchDate,
            stadium = e.stadium,
            referee = e.referee,
            simHomeProb = e.simHomeProb,
            simDrawProb = e.simDrawProb,
            simAwayProb = e.simAwayProb,
            simOverProb = e.simOverProb,
            simUnderProb = e.simUnderProb,
            events = events
        )
    }
}

class PredictionRepository(
    private val database: AppDatabase
) {
    suspend fun getWallet(): Wallet = withContext(Dispatchers.IO) {
        val entity = database.walletDao().getWallet()
        if (entity == null) {
            val initial = WalletEntity()
            database.walletDao().saveWallet(initial)
            Wallet()
        } else {
            Wallet(
                balance = entity.balance,
                initialBalance = entity.initialBalance,
                totalStaked = entity.totalStaked,
                totalWon = entity.totalWon,
                predictionsCount = entity.predictionsCount,
                wonCount = entity.wonCount,
                lostCount = entity.lostCount
            )
        }
    }

    fun getWalletFlow(): Flow<Wallet> {
        return database.walletDao().getWalletFlow().map { entity ->
            if (entity == null) {
                Wallet()
            } else {
                Wallet(
                    balance = entity.balance,
                    initialBalance = entity.initialBalance,
                    totalStaked = entity.totalStaked,
                    totalWon = entity.totalWon,
                    predictionsCount = entity.predictionsCount,
                    wonCount = entity.wonCount,
                    lostCount = entity.lostCount
                )
            }
        }
    }

    fun getPredictionsFlow(): Flow<List<Prediction>> {
        return database.predictionDao().getAllPredictions().map { list ->
            list.map {
                Prediction(
                    id = it.id,
                    matchId = it.matchId,
                    matchTitle = it.matchTitle,
                    outcomeType = it.outcomeType,
                    outcomeLabel = it.outcomeLabel,
                    simProbability = it.simProbability,
                    stakeCredits = it.stakeCredits,
                    potentialReturn = it.potentialReturn,
                    status = it.status,
                    createdAt = it.createdAt,
                    resolvedAt = it.resolvedAt
                )
            }
        }
    }

    suspend fun placePrediction(
        match: Match,
        outcomeType: PredictionOutcomeType,
        stakeCredits: Long
    ): Result<Long> = withContext(Dispatchers.IO) {
        val wallet = database.walletDao().getWallet() ?: WalletEntity()
        if (wallet.balance < stakeCredits) {
            return@withContext Result.failure(Exception("موجودی اعتبار مجازی کافی نیست."))
        }

        val prob = when (outcomeType) {
            PredictionOutcomeType.HOME_WIN -> match.simHomeProb
            PredictionOutcomeType.DRAW -> match.simDrawProb
            PredictionOutcomeType.AWAY_WIN -> match.simAwayProb
            PredictionOutcomeType.OVER_2_5 -> match.simOverProb
            PredictionOutcomeType.UNDER_2_5 -> match.simUnderProb
        }

        val label = when (outcomeType) {
            PredictionOutcomeType.HOME_WIN -> "برد ${match.homeTeamPersian}"
            PredictionOutcomeType.DRAW -> "مساوی"
            PredictionOutcomeType.AWAY_WIN -> "برد ${match.awayTeamPersian}"
            PredictionOutcomeType.OVER_2_5 -> "بیشتر از ۲.۵ گل"
            PredictionOutcomeType.UNDER_2_5 -> "کمتر از ۲.۵ گل"
        }

        val potentialReturn = (stakeCredits * (100.0 / prob.coerceAtLeast(1))).toLong()

        // Deduct stake
        val updatedWallet = wallet.copy(
            balance = wallet.balance - stakeCredits,
            totalStaked = wallet.totalStaked + stakeCredits,
            predictionsCount = wallet.predictionsCount + 1
        )
        database.walletDao().saveWallet(updatedWallet)

        val entity = PredictionEntity(
            matchId = match.id,
            matchTitle = "${match.homeTeamPersian} - ${match.awayTeamPersian}",
            outcomeType = outcomeType,
            outcomeLabel = label,
            simProbability = prob,
            stakeCredits = stakeCredits,
            potentialReturn = potentialReturn,
            status = PredictionStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            resolvedAt = null
        )
        val id = database.predictionDao().insertPrediction(entity)
        Result.success(id)
    }

    suspend fun resolvePrediction(
        predictionId: Long,
        didWin: Boolean
    ) = withContext(Dispatchers.IO) {
        val pred = database.predictionDao().getPredictionById(predictionId) ?: return@withContext
        if (pred.status != PredictionStatus.PENDING) return@withContext

        val newStatus = if (didWin) PredictionStatus.WON else PredictionStatus.LOST
        database.predictionDao().updatePrediction(
            pred.copy(status = newStatus, resolvedAt = System.currentTimeMillis())
        )

        val wallet = database.walletDao().getWallet() ?: WalletEntity()
        if (didWin) {
            val updated = wallet.copy(
                balance = wallet.balance + pred.potentialReturn,
                totalWon = wallet.totalWon + pred.potentialReturn,
                wonCount = wallet.wonCount + 1
            )
            database.walletDao().saveWallet(updated)
        } else {
            val updated = wallet.copy(
                lostCount = wallet.lostCount + 1
            )
            database.walletDao().saveWallet(updated)
        }
    }

    suspend fun resetVirtualBalance() = withContext(Dispatchers.IO) {
        val existing = database.walletDao().getWallet() ?: WalletEntity()
        val reset = existing.copy(balance = 100_000)
        database.walletDao().saveWallet(reset)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        database.predictionDao().clearAll()
        database.walletDao().clearWallet()
        database.walletDao().saveWallet(WalletEntity())
    }
}

class ResponsibleGamingRepository(
    private val database: AppDatabase
) {
    fun getSettingsFlow(): Flow<ResponsibleSettings> {
        return database.responsibleSettingsDao().getSettingsFlow().map { entity ->
            if (entity == null) {
                ResponsibleSettings()
            } else {
                val now = System.currentTimeMillis()
                val isCurrentlyPaused = entity.isPaused && (entity.pauseUntil == 0L || entity.pauseUntil > now)
                ResponsibleSettings(
                    dailyLimitMinutes = entity.dailyLimitMinutes,
                    totalSessionMinutesToday = entity.totalMinutesToday,
                    isPaused = isCurrentlyPaused,
                    pauseUntil = entity.pauseUntil
                )
            }
        }
    }

    suspend fun updateDailyLimit(minutes: Int) = withContext(Dispatchers.IO) {
        val current = database.responsibleSettingsDao().getSettings() ?: ResponsibleSettingsEntity()
        database.responsibleSettingsDao().saveSettings(current.copy(dailyLimitMinutes = minutes))
    }

    suspend fun setPauseSimulator(durationHours: Int) = withContext(Dispatchers.IO) {
        val current = database.responsibleSettingsDao().getSettings() ?: ResponsibleSettingsEntity()
        val pauseUntil = if (durationHours > 0) {
            System.currentTimeMillis() + (durationHours * 3600 * 1000L)
        } else {
            0L
        }
        database.responsibleSettingsDao().saveSettings(
            current.copy(
                isPaused = durationHours > 0,
                pauseUntil = pauseUntil
            )
        )
    }

    suspend fun resumeSimulator() = withContext(Dispatchers.IO) {
        val current = database.responsibleSettingsDao().getSettings() ?: ResponsibleSettingsEntity()
        database.responsibleSettingsDao().saveSettings(
            current.copy(isPaused = false, pauseUntil = 0L)
        )
    }
}
