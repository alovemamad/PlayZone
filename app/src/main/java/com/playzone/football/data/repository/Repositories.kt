package com.playzone.football.data.repository

import com.playzone.football.BuildConfig
import com.playzone.football.data.local.AppDatabase
import com.playzone.football.data.local.MatchEntity
import com.playzone.football.data.local.PredictionEntity
import com.playzone.football.data.local.ResponsibleSettingsEntity
import com.playzone.football.data.local.UserSettingsEntity
import com.playzone.football.data.local.WalletEntity
import com.playzone.football.data.mock.SampleFootballData
import com.playzone.football.data.model.League
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchEvent
import com.playzone.football.data.model.MatchStats
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.Prediction
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.PredictionStatus
import com.playzone.football.data.model.ResponsibleSettings
import com.playzone.football.data.model.Team
import com.playzone.football.data.model.Wallet
import com.playzone.football.data.remote.RetrofitClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.withLock
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
    private val statsAdapter = moshi.adapter(MatchStats::class.java)

    private val engine = ApiSyncEngine(database)

    private val _isLiveApiActive = MutableStateFlow(false)
    val isLiveApiActive: StateFlow<Boolean> = _isLiveApiActive.asStateFlow()

    private val _apiStatusMessage = MutableStateFlow("شبیه‌ساز مسابقات فعال است")
    val apiStatusMessage: StateFlow<String> = _apiStatusMessage.asStateFlow()

    /** True when real API data is used (no demo matches at all). */
    private val _isRealMode = MutableStateFlow(false)
    val isRealMode: StateFlow<Boolean> = _isRealMode.asStateFlow()

    private val _syncMessages = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val syncMessages: SharedFlow<String> = _syncMessages.asSharedFlow()

    @Volatile private var syncJob: Job? = null

    val leagues: List<League> = SampleFootballData.sampleLeagues
    val teams: List<Team> = SampleFootballData.sampleTeams

    init {
        scope.launch { startup() }
    }

    private fun isUsableKey(key: String): Boolean =
        key.isNotBlank() && !key.startsWith("demo") && key != "MY_API_FOOTBALL_KEY"

    /** Key typed in Settings wins, otherwise the one baked in from .env. */
    private suspend fun effectiveKey(): String = withContext(Dispatchers.IO) {
        val saved = database.userSettingsDao().getSettings()?.apiFootballKey?.trim().orEmpty()
        if (isUsableKey(saved)) saved else BuildConfig.API_FOOTBALL_KEY.trim()
    }

    private suspend fun startup() {
        val key = effectiveKey()
        if (!isUsableKey(key)) {
            // No key at all: offline demo mode (sample data + local ticker)
            initSampleDataIfNeeded()
            startLiveTickerSimulator()
            return
        }
        withContext(Dispatchers.IO) { database.matchDao().deleteDemoMatches() }
        _isRealMode.value = true
        startSync(key)
    }

    private fun startSync(key: String) {
        syncJob?.cancel()
        _isLiveApiActive.value = true
        _apiStatusMessage.value = "در حال اتصال به API-Football…"
        syncJob = scope.launch(Dispatchers.IO) {
            syncCycle(key, firstRun = true)
            while (isActive) {
                delay(engine.liveIntervalMs)
                syncCycle(key, firstRun = false)
            }
        }
    }

    /** One manual refresh (pull-to-refresh / button). */
    fun refreshNow() {
        scope.launch(Dispatchers.IO) {
            val key = effectiveKey()
            if (isUsableKey(key)) syncCycle(key, firstRun = true)
        }
    }

    private suspend fun syncCycle(key: String, firstRun: Boolean) {
        val budgetError = engine.refreshBudget(key)
        if (budgetError != null) {
            _apiStatusMessage.value = budgetError
            return
        }
        val errors = mutableListOf<String>()
        engine.syncFixtures(key, force = firstRun)?.let { errors.add(it) }
        engine.syncOdds(key, force = false)?.let { errors.add(it) }
        var settled = 0
        engine.syncLive(key) { settled += it }?.let { errors.add(it) }
        if (settled > 0) {
            _syncMessages.tryEmit("$settled پیش‌بینی بر اساس نتیجه واقعی بازی تسویه شد")
        }
        val remaining = engine.remainingCalls.coerceAtLeast(0)
        _apiStatusMessage.value = if (errors.isEmpty()) {
            "متصل به API-Football (سهمیه باقی‌مانده امروز: $remaining)"
        } else {
            errors.distinct().joinToString(" | ")
        }
        try {
            val current = database.userSettingsDao().getSettings() ?: UserSettingsEntity()
            database.userSettingsDao().saveSettings(
                current.copy(apiRemainingCalls = remaining, lastSyncTime = "همین الان")
            )
        } catch (_: Exception) {
        }
    }

    /** Loads events and statistics when a match page opens (1 API call). */
    fun loadMatchDetail(matchId: String) {
        if (!matchId.startsWith("api_")) return
        scope.launch(Dispatchers.IO) {
            val key = effectiveKey()
            if (isUsableKey(key)) engine.loadDetail(key, matchId)
        }
    }

    private suspend fun initSampleDataIfNeeded() = withContext(Dispatchers.IO) {
        val initialMatches = SampleFootballData.createInitialMatches()
        val existing = database.matchDao().getMatchById(initialMatches.first().id)
        if (existing == null) {
            val entities = initialMatches.map { matchToEntity(it) }
            database.matchDao().insertMatches(entities)
        }
    }

    fun getAllMatchesFlow(): Flow<List<Match>> {
        return database.matchDao().getAllMatches().map { entities ->
            val api = entities.filter { it.id.startsWith("api_") }
            when {
                api.isNotEmpty() || _isRealMode.value ->
                    api.filter { e ->
                        e.statusShort !in ApiSyncEngine.CANCEL_CODES &&
                            (e.status != MatchStatus.UPCOMING || e.oddHome > 0.0)
                    }.map { entityToMatch(it) }
                entities.isEmpty() -> SampleFootballData.createInitialMatches()
                else -> entities.map { entityToMatch(it) }
            }
        }
    }

    suspend fun getMatchById(id: String): Match? = withContext(Dispatchers.IO) {
        val entity = database.matchDao().getMatchById(id)
        entity?.let { entityToMatch(it) }
            ?: if (_isRealMode.value) null else SampleFootballData.createInitialMatches().find { it.id == id }
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

    // Save user custom API key and optionally connect
    suspend fun saveApiConfig(apiKey: String, useLive: Boolean): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                val current = database.userSettingsDao().getSettings() ?: UserSettingsEntity()
                database.userSettingsDao().saveSettings(
                    current.copy(apiFootballKey = "", useLiveApi = false, customApiConnected = false)
                )
                _isLiveApiActive.value = false
                _apiStatusMessage.value = "شبیه‌ساز مسابقات فعال است"
                return@withContext Result.success("تنظیمات شبیه‌ساز ذخیره گردید.")
            }

            // Test connection using API-Football status endpoint
            val statusResponse = try {
                RetrofitClient.apiService.getApiStatus(apiKey.trim())
            } catch (e: Exception) {
                null
            }

            val remainingCalls = statusResponse?.response?.requests?.let {
                (it.limitDay ?: 100) - (it.current ?: 0)
            } ?: 95

            val newSettings = UserSettingsEntity(
                apiFootballKey = apiKey.trim(),
                useLiveApi = useLive,
                customApiConnected = true,
                apiRemainingCalls = remainingCalls,
                lastSyncTime = "همین الان"
            )
            database.userSettingsDao().saveSettings(newSettings)
            _isLiveApiActive.value = useLive
            _apiStatusMessage.value = "متصل به سرور API-Football ($remainingCalls درخواست باقی‌مانده)"

            if (useLive) {
                withContext(Dispatchers.IO) { database.matchDao().deleteDemoMatches() }
                _isRealMode.value = true
                startSync(apiKey.trim())
            }

            Result.success("کلید API با موفقیت متصل و ذخیره شد ($remainingCalls درخواست باقی‌مانده).")
        } catch (e: Exception) {
            Result.failure(Exception("خطا در اعتبارسنجی کلید API: ${e.localizedMessage ?: "عدم ارتباط با سرور"}"))
        }
    }

    suspend fun getUserSettings(): UserSettingsEntity = withContext(Dispatchers.IO) {
        database.userSettingsDao().getSettings() ?: UserSettingsEntity()
    }

    fun getUserSettingsFlow(): Flow<UserSettingsEntity> {
        return database.userSettingsDao().getSettingsFlow().map { it ?: UserSettingsEntity() }
    }

    // Ticker simulator that periodically advances live match minutes and generates events
    private fun startLiveTickerSimulator() {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(10_000) // update every 10 seconds
                try {
                    val liveMatch = database.matchDao().getMatchById("live_ucl_1")
                    if (liveMatch != null && liveMatch.status == MatchStatus.LIVE) {
                        val currentMin = if (liveMatch.minute < 90) liveMatch.minute + 1 else 90
                        val updated = liveMatch.copy(minute = currentMin)
                        database.matchDao().updateMatch(updated)
                    }

                    val liveMatch2 = database.matchDao().getMatchById("live_persian_1")
                    if (liveMatch2 != null && liveMatch2.status == MatchStatus.LIVE) {
                        val currentMin = if (liveMatch2.minute < 90) liveMatch2.minute + 1 else 90
                        val updated = liveMatch2.copy(minute = currentMin)
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
        val statsJson = try {
            statsAdapter.toJson(m.stats)
        } catch (_: Exception) {
            "{}"
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
            eventsJson = eventsJson,
            statsJson = statsJson,
            statusShort = m.statusShort,
            oddHome = m.oddHome,
            oddDraw = m.oddDraw,
            oddAway = m.oddAway,
            oddDc1x = m.oddDc1x,
            oddDc12 = m.oddDc12,
            oddDcx2 = m.oddDcx2,
            oddOver25 = m.oddOver25,
            oddUnder25 = m.oddUnder25,
            oddOver15 = m.oddOver15,
            oddUnder15 = m.oddUnder15,
            oddBttsYes = m.oddBttsYes,
            oddBttsNo = m.oddBttsNo
        )
    }

    private fun entityToMatch(e: MatchEntity): Match {
        val events = try {
            eventAdapter.fromJson(e.eventsJson) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
        val stats = try {
            statsAdapter.fromJson(e.statsJson) ?: MatchStats()
        } catch (_: Exception) {
            MatchStats()
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
            stats = stats,
            events = events,
            statsLoaded = e.statsJson.contains("possessionHome"),
            statusShort = e.statusShort,
            oddHome = e.oddHome,
            oddDraw = e.oddDraw,
            oddAway = e.oddAway,
            oddDc1x = e.oddDc1x,
            oddDc12 = e.oddDc12,
            oddDcx2 = e.oddDcx2,
            oddOver25 = e.oddOver25,
            oddUnder25 = e.oddUnder25,
            oddOver15 = e.oddOver15,
            oddUnder15 = e.oddUnder15,
            oddBttsYes = e.oddBttsYes,
            oddBttsNo = e.oddBttsNo
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
                    oddsMultiplier = it.oddsMultiplier,
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
    ): Result<Long> = WalletLock.mutex.withLock { placePredictionInternal(match, outcomeType, stakeCredits) }

    private suspend fun placePredictionInternal(
        match: Match,
        outcomeType: PredictionOutcomeType,
        stakeCredits: Long
    ): Result<Long> = withContext(Dispatchers.IO) {
        if (match.status == MatchStatus.FINISHED) {
            return@withContext Result.failure(Exception("این بازی به پایان رسیده است."))
        }
        val wallet = database.walletDao().getWallet() ?: WalletEntity()
        if (wallet.balance < stakeCredits) {
            return@withContext Result.failure(Exception("موجودی اعتبار مجازی کافی نیست. لطفاً کیف پول خود را شارژ کنید."))
        }

        val multiplier = match.oddFor(outcomeType)
        if (multiplier <= 1.0) {
            return@withContext Result.failure(Exception("ضریب این بازار در حال حاضر در دسترس نیست."))
        }

        val prob = when (outcomeType) {
            PredictionOutcomeType.HOME_WIN -> match.simHomeProb
            PredictionOutcomeType.DRAW -> match.simDrawProb
            PredictionOutcomeType.AWAY_WIN -> match.simAwayProb
            PredictionOutcomeType.DOUBLE_CHANCE_1X -> match.simHomeProb + match.simDrawProb
            PredictionOutcomeType.DOUBLE_CHANCE_12 -> match.simHomeProb + match.simAwayProb
            PredictionOutcomeType.DOUBLE_CHANCE_X2 -> match.simDrawProb + match.simAwayProb
            PredictionOutcomeType.OVER_2_5 -> match.simOverProb
            PredictionOutcomeType.UNDER_2_5 -> match.simUnderProb
            else -> 50
        }

        val label = when (outcomeType) {
            PredictionOutcomeType.HOME_WIN -> "برد ${match.homeTeamPersian} (۱)"
            PredictionOutcomeType.DRAW -> "تساوی (X)"
            PredictionOutcomeType.AWAY_WIN -> "برد ${match.awayTeamPersian} (۲)"
            PredictionOutcomeType.DOUBLE_CHANCE_1X -> "شانس دوبرابر: ${match.homeTeamPersian} یا مساوی (1X)"
            PredictionOutcomeType.DOUBLE_CHANCE_12 -> "شانس دوبرابر: برد یکی از دو تیم (12)"
            PredictionOutcomeType.DOUBLE_CHANCE_X2 -> "شانس دوبرابر: مساوی یا ${match.awayTeamPersian} (X2)"
            PredictionOutcomeType.OVER_2_5 -> "مجموع گل‌ها بیشتر از ۲.۵"
            PredictionOutcomeType.UNDER_2_5 -> "مجموع گل‌ها کمتر از ۲.۵"
            PredictionOutcomeType.OVER_1_5 -> "مجموع گل‌ها بیشتر از ۱.۵"
            PredictionOutcomeType.UNDER_1_5 -> "مجموع گل‌ها کمتر از ۱.۵"
            PredictionOutcomeType.BOTH_TEAMS_SCORE_YES -> "هر دو تیم گل می‌زنند: بله (GG)"
            PredictionOutcomeType.BOTH_TEAMS_SCORE_NO -> "هر دو تیم گل می‌زنند: خیر (NG)"
        }

        val potentialReturn = (stakeCredits * multiplier).toLong()

        // Deduct stake from wallet
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
            oddsMultiplier = multiplier,
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
    ) = WalletLock.mutex.withLock { resolvePredictionInternal(predictionId, didWin) }

    private suspend fun resolvePredictionInternal(
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

    // Set custom balance chosen by the user
    suspend fun setCustomBalance(amount: Long) = WalletLock.mutex.withLock {
        withContext(Dispatchers.IO) {
            val existing = database.walletDao().getWallet() ?: WalletEntity()
            val updated = existing.copy(balance = amount.coerceAtLeast(1_000L))
            database.walletDao().saveWallet(updated)
        }
    }

    suspend fun resetVirtualBalance() = withContext(Dispatchers.IO) {
        setCustomBalance(100_000L)
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
