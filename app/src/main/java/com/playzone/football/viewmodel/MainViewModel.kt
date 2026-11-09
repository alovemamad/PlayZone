package com.playzone.football.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.playzone.football.PlayZoneApp
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

    private val _selectedLeagueFilter = MutableStateFlow<String?>(null)
    val selectedLeagueFilter: StateFlow<String?> = _selectedLeagueFilter.asStateFlow()

    private val _matchStatusFilter = MutableStateFlow<MatchStatus?>(null)
    val matchStatusFilter: StateFlow<MatchStatus?> = _matchStatusFilter.asStateFlow()

    val rawMatches: StateFlow<List<Match>> = footballRepository.getAllMatchesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredMatches: StateFlow<List<Match>> = combine(
        rawMatches,
        _selectedLeagueFilter,
        _matchStatusFilter
    ) { matches, leagueId, status ->
        matches.filter { match ->
            (leagueId == null || match.leagueId == leagueId) &&
            (status == null || match.status == status)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wallet: StateFlow<Wallet> = predictionRepository.getWalletFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Wallet())

    val predictions: StateFlow<List<Prediction>> = predictionRepository.getPredictionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val responsibleSettings: StateFlow<ResponsibleSettings> = responsibleGamingRepository.getSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResponsibleSettings())

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Prediction Modal State
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
                _userMessage.emit("پیش‌بینی مجازی شما با موفقیت ثبت گردید.")
                closePredictionModal()
            }.onFailure { ex ->
                _userMessage.emit(ex.message ?: "خطا در ثبت پیش‌بینی")
            }
        }
    }

    fun resolvePrediction(predictionId: Long, didWin: Boolean) {
        viewModelScope.launch {
            predictionRepository.resolvePrediction(predictionId, didWin)
            _userMessage.emit(if (didWin) "پیش‌بینی برنده شد و اعتبار مجازی اضافه شد!" else "نتیجه پیش‌بینی به پایان رسید.")
        }
    }

    fun refillCredits() {
        viewModelScope.launch {
            predictionRepository.resetVirtualBalance()
            _userMessage.emit("۱۰۰,۰۰۰ اعتبار مجازی رایگان با موفقیت به کیف پول افزوده شد.")
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
