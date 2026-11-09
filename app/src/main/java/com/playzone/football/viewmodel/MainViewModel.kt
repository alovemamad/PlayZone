package com.playzone.football.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.playzone.football.PlayZoneApp
import com.playzone.football.data.local.UserSettingsEntity
import com.playzone.football.data.model.League
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.Prediction
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.ResponsibleSettings
import com.playzone.football.data.model.Team
import com.playzone.football.data.model.Wallet
import com.playzone.football.data.repository.FootballRepository
import com.playzone.football.data.repository.PredictionRepository
import com.playzone.football.data.repository.ResponsibleGamingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val footballRepository: FootballRepository,
    private val predictionRepository: PredictionRepository,
    private val responsibleGamingRepository: ResponsibleGamingRepository
) : ViewModel() {

    val leagues: List<League> = footballRepository.leagues
    val teams: List<Team> = footballRepository.teams

    val isRealMode: StateFlow<Boolean> = footballRepository.isRealMode
    val isLiveApiActive: StateFlow<Boolean> = footballRepository.isLiveApiActive
    val apiStatusMessage: StateFlow<String> = footballRepository.apiStatusMessage

    val userSettings: StateFlow<UserSettingsEntity> = footballRepository.getUserSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettingsEntity())

    private val _selectedLeagueFilter = MutableStateFlow<String?>(null)
    val selectedLeagueFilter: StateFlow<String?> = _selectedLeagueFilter.asStateFlow()

    private val _matchStatusFilter = MutableStateFlow<MatchStatus?>(null)
    val matchStatusFilter: StateFlow<MatchStatus?> = _matchStatusFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val rawMatches: StateFlow<List<Match>> = footballRepository.getAllMatchesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredMatches: StateFlow<List<Match>> = combine(
        rawMatches,
        _selectedLeagueFilter,
        _matchStatusFilter,
        _searchQuery
    ) { matches, leagueId, status, query ->
        matches.filter { match ->
            val matchesLeague = (leagueId == null || match.leagueId == leagueId)
            val matchesStatus = (status == null || match.status == status)
            val matchesQuery = query.isBlank() ||
                    match.homeTeamPersian.contains(query, ignoreCase = true) ||
                    match.awayTeamPersian.contains(query, ignoreCase = true) ||
                    match.homeTeamName.contains(query, ignoreCase = true) ||
                    match.awayTeamName.contains(query, ignoreCase = true) ||
                    match.leaguePersianName.contains(query, ignoreCase = true)
            matchesLeague && matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** League chips: sample leagues in demo mode, leagues of real matches otherwise (busiest first). */
    val activeLeagues: StateFlow<List<League>> = combine(rawMatches, isRealMode) { matches, real ->
        if (!real) {
            leagues
        } else {
            matches.groupBy { it.leagueId }
                .entries
                .sortedByDescending { it.value.size }
                .take(25)
                .map { (id, list) ->
                    League(
                        id = id,
                        name = list.first().leagueName,
                        persianName = list.first().leaguePersianName,
                        country = "",
                        flagEmoji = "⚽",
                        logoUrl = ""
                    )
                }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), leagues)

    val wallet: StateFlow<Wallet> = predictionRepository.getWalletFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Wallet())

    val predictions: StateFlow<List<Prediction>> = predictionRepository.getPredictionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val responsibleSettings: StateFlow<ResponsibleSettings> = responsibleGamingRepository.getSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResponsibleSettings())

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Prediction Modal State
    init {
        // Auto-settlement notices from the sync engine
        viewModelScope.launch {
            footballRepository.syncMessages.collect { _userMessage.emit(it) }
        }
    }

    fun refreshNow() = footballRepository.refreshNow()

    fun loadMatchDetail(matchId: String) = footballRepository.loadMatchDetail(matchId)

    private val _activePredictionMatch = MutableStateFlow<Match?>(null)
    val activePredictionMatch: StateFlow<Match?> = _activePredictionMatch.asStateFlow()

    private val _activePredictionOutcome = MutableStateFlow<PredictionOutcomeType?>(null)
    val activePredictionOutcome: StateFlow<PredictionOutcomeType?> = _activePredictionOutcome.asStateFlow()

    fun selectLeagueFilter(leagueId: String?) {
        _selectedLeagueFilter.value = leagueId
    }

    fun selectStatusFilter(status: MatchStatus?) {
        _matchStatusFilter.value = status
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openPredictionModal(match: Match, outcome: PredictionOutcomeType) {
        val settings = responsibleSettings.value
        if (settings.isPaused) {
            viewModelScope.launch {
                _userMessage.emit("شبیه‌ساز در حالت استراحت قرار دارد و پیش‌بینی موقتاً غیرفعال است.")
            }
            return
        }
        _activePredictionMatch.value = match
        _activePredictionOutcome.value = outcome
    }

    fun closePredictionModal() {
        _activePredictionMatch.value = null
        _activePredictionOutcome.value = null
    }

    fun submitPrediction(stake: Long) {
        val match = _activePredictionMatch.value ?: return
        val outcome = _activePredictionOutcome.value ?: return

        viewModelScope.launch {
            val result = predictionRepository.placePrediction(match, outcome, stake)
            result.onSuccess {
                _userMessage.emit("پیش‌بینی مجازی با موفقیت ثبت شد.")
                closePredictionModal()
            }.onFailure { ex ->
                _userMessage.emit(ex.message ?: "خطا در ثبت پیش‌بینی")
            }
        }
    }

    fun resolvePrediction(predictionId: Long, didWin: Boolean) {
        viewModelScope.launch {
            predictionRepository.resolvePrediction(predictionId, didWin)
            _userMessage.emit(if (didWin) "پیش‌بینی برنده شد و اعتبار مجازی افزوده شد!" else "نتیجه بازی ثبت شد.")
        }
    }

    // Custom wallet balance update requested by user
    fun setCustomWalletBalance(amount: Long) {
        viewModelScope.launch {
            predictionRepository.setCustomBalance(amount)
            _userMessage.emit("موجودی کیف پول با موفقیت به %,d اعتبار تغییر یافت.".format(amount))
        }
    }

    fun refillCredits() {
        setCustomWalletBalance(100_000L)
    }

    fun saveApiConfig(apiKey: String, useLive: Boolean) {
        viewModelScope.launch {
            val result = footballRepository.saveApiConfig(apiKey, useLive)
            result.onSuccess { msg ->
                _userMessage.emit(msg)
            }.onFailure { err ->
                _userMessage.emit(err.message ?: "خطا در برقراری ارتباط")
            }
        }
    }

    fun updateDailyLimit(minutes: Int) {
        viewModelScope.launch {
            responsibleGamingRepository.updateDailyLimit(minutes)
            _userMessage.emit("محدودیت زمان روزانه روی $minutes دقیقه تنظیم گردید.")
        }
    }

    fun pauseSimulator(durationHours: Int) {
        viewModelScope.launch {
            responsibleGamingRepository.setPauseSimulator(durationHours)
            _userMessage.emit(
                if (durationHours > 0) "شبیه‌ساز به مدت $durationHours ساعت متوقف شد."
                else "استراحت لغو شد."
            )
        }
    }

    fun resumeSimulator() {
        viewModelScope.launch {
            responsibleGamingRepository.resumeSimulator()
            _userMessage.emit("شبیه‌ساز فعال شد.")
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            predictionRepository.clearAllData()
            _userMessage.emit("اطلاعات محلی با موفقیت بازنشانی شد.")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = PlayZoneApp.instance
                return MainViewModel(
                    app.footballRepository,
                    app.predictionRepository,
                    app.responsibleGamingRepository
                ) as T
            }
        }
    }
}
