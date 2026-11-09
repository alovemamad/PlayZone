package com.playzone.football.data.repository

import com.playzone.football.data.local.AppDatabase
import com.playzone.football.data.local.MatchEntity
import com.playzone.football.data.local.PredictionEntity
import com.playzone.football.data.local.WalletEntity
import com.playzone.football.data.model.MatchEvent
import com.playzone.football.data.model.MatchStats
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.PredictionStatus
import com.playzone.football.data.remote.ApiBet
import com.playzone.football.data.remote.ApiFixtureItem
import com.playzone.football.data.remote.RetrofitClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import kotlin.math.roundToInt

/** Pure settlement rule on the 90-minute score. */
fun isPredictionWinner(type: PredictionOutcomeType, h: Int, a: Int): Boolean {
    val total = h + a
    return when (type) {
        PredictionOutcomeType.HOME_WIN -> h > a
        PredictionOutcomeType.DRAW -> h == a
        PredictionOutcomeType.AWAY_WIN -> a > h
        PredictionOutcomeType.DOUBLE_CHANCE_1X -> h >= a
        PredictionOutcomeType.DOUBLE_CHANCE_12 -> h != a
        PredictionOutcomeType.DOUBLE_CHANCE_X2 -> a >= h
        PredictionOutcomeType.OVER_2_5 -> total >= 3
        PredictionOutcomeType.UNDER_2_5 -> total <= 2
        PredictionOutcomeType.OVER_1_5 -> total >= 2
        PredictionOutcomeType.UNDER_1_5 -> total <= 1
        PredictionOutcomeType.BOTH_TEAMS_SCORE_YES -> h > 0 && a > 0
        PredictionOutcomeType.BOTH_TEAMS_SCORE_NO -> h == 0 || a == 0
    }
}

/** Shared lock so wallet read-modify-write operations never interleave. */
object WalletLock {
    val mutex = Mutex()
}

/**
 * Talks to API-Football and keeps the local database up to date.
 * Every function returns an error message (null = success) and never throws.
 *
 * Request budget (free plan = 100/day): /status is free and tells us what is left.
 */
class ApiSyncEngine(private val database: AppDatabase) {

    companion object {
        const val BOOKMAKER_ID = 8            // Bet365 in API-Football
        const val MAX_ODDS_PAGES = 6          // 10 fixtures per page
        const val RESERVE_CALLS = 12          // always keep some calls for settlement
        const val ODDS_TTL_MS = 3 * 60 * 60 * 1000L

        private val LIVE_CODES = setOf("1H", "HT", "2H", "ET", "BT", "P", "INT", "LIVE", "SUSP")
        private val DONE_CODES = setOf("FT", "AET", "PEN")
        val CANCEL_CODES = setOf("PST", "CANC", "ABD", "AWD", "WO")
    }

    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val tzName: String = TimeZone.getDefault().id
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val eventListAdapter = moshi.adapter<List<MatchEvent>>(
        Types.newParameterizedType(List::class.java, MatchEvent::class.java)
    )
    private val statsAdapter = moshi.adapter(MatchStats::class.java)

    private val api get() = RetrofitClient.apiService

    private var lastFixturesSync = 0L
    var remainingCalls: Int = 100
        private set
    var dailyLimit: Int = 100
        private set

    /** Refresh interval for live data: free plans must be gentle. */
    val liveIntervalMs: Long get() = if (dailyLimit <= 100) 10 * 60 * 1000L else 60 * 1000L

    // ---------------------------------------------------------------- budget

    /** /status does not count against the quota. Returns an error message or null. */
    suspend fun refreshBudget(key: String): String? {
        return try {
            val st = api.getApiStatus(key)
            val req = st.response?.requests
            if (req != null) {
                dailyLimit = req.limitDay ?: 100
                remainingCalls = dailyLimit - (req.current ?: 0)
            }
            if (st.response?.subscription?.active == false) "اشتراک API-Football فعال نیست" else null
        } catch (e: Exception) {
            "ارتباط با API-Football برقرار نشد: ${e.localizedMessage ?: "خطای شبکه"}"
        }
    }

    private fun spend() {
        remainingCalls -= 1
    }

    private fun hasBudget(extraReserve: Int = RESERVE_CALLS): Boolean = remainingCalls > extraReserve

    private fun errorText(errors: Any?): String? = when (errors) {
        is Map<*, *> -> if (errors.isEmpty()) null else errors.values.joinToString("؛ ")
        is List<*> -> if (errors.isEmpty()) null else errors.joinToString("؛ ")
        else -> null
    }

    // ------------------------------------------------------------- fixtures

    /** Today + tomorrow fixtures. Cheap (2 calls). */
    suspend fun syncFixtures(key: String, force: Boolean): String? {
        val now = System.currentTimeMillis()
        val ttl = if (dailyLimit <= 100) 2 * 60 * 60 * 1000L else 15 * 60 * 1000L
        if (!force && now - lastFixturesSync < ttl) return null
        if (!hasBudget()) return "سهمیه امروز API رو به پایان است"

        val today = LocalDate.now(zoneId)
        var firstError: String? = null
        for (day in listOf(today, today.plusDays(1))) {
            try {
                spend()
                val res = api.getFixturesByDate(key, day.toString(), tzName)
                val err = errorText(res.errors)
                if (err != null) {
                    firstError = firstError ?: err
                    continue
                }
                upsertFixtures(res.response, liveOddsById = null)
            } catch (e: Exception) {
                firstError = firstError ?: "خطا در دریافت بازی‌ها: ${e.localizedMessage ?: "شبکه"}"
            }
        }
        if (firstError == null) lastFixturesSync = now
        try {
            database.matchDao().deleteOldApiMatches(today.minusDays(2).toString())
        } catch (_: Exception) {
        }
        return firstError
    }

    // ----------------------------------------------------------- pre-match odds

    suspend fun oddsAreStale(): Boolean {
        val last = try {
            database.matchDao().maxOddsUpdatedAt() ?: 0L
        } catch (_: Exception) {
            0L
        }
        return System.currentTimeMillis() - last > ODDS_TTL_MS
    }

    /** Pre-match odds for today and tomorrow, a few pages per day. */
    suspend fun syncOdds(key: String, force: Boolean): String? {
        if (!force && !oddsAreStale()) return null
        if (!hasBudget(RESERVE_CALLS + 4)) return "سهمیه امروز API برای دریافت ضرایب کافی نیست"

        val today = LocalDate.now(zoneId)
        var firstError: String? = null
        var pagesUsed = 0
        for (day in listOf(today, today.plusDays(1))) {
            var page = 1
            var totalPages = 1
            while (page <= totalPages && pagesUsed < MAX_ODDS_PAGES && hasBudget()) {
                try {
                    spend()
                    pagesUsed++
                    val res = api.getOddsByDate(key, day.toString(), BOOKMAKER_ID, page, tzName)
                    val err = errorText(res.errors)
                    if (err != null) {
                        firstError = firstError ?: err
                        break
                    }
                    totalPages = res.paging?.total ?: 1
                    applyPreMatchOdds(res.response)
                } catch (e: Exception) {
                    firstError = firstError ?: "خطا در دریافت ضرایب: ${e.localizedMessage ?: "شبکه"}"
                    break
                }
                page++
                delay(1500) // free plan: 10 requests / minute
            }
        }
        return firstError
    }

    private suspend fun applyPreMatchOdds(items: List<com.playzone.football.data.remote.ApiOddsItem>) {
        val now = System.currentTimeMillis()
        for (item in items) {
            val id = "api_${item.fixture.id}"
            val old = database.matchDao().getMatchById(id) ?: continue
            if (old.status != MatchStatus.UPCOMING) continue
            val bets = item.bookmakers?.firstOrNull()?.bets ?: continue
            val pack = readBets(bets, live = false)
            if (pack.home <= 0 || pack.draw <= 0 || pack.away <= 0) continue
            database.matchDao().updateMatch(applyPack(old, pack).copy(oddsUpdatedAt = now))
        }
    }

    // -------------------------------------------------------------------- live

    /**
     * Live scores (1 call) + live odds (1 call) + refresh of matches that just
     * finished or carry open predictions (1 call), then settlement.
     * Returns number of predictions settled via [settledOut] and an error message.
     */
    suspend fun syncLive(key: String, settledOut: (Int) -> Unit): String? {
        if (!hasBudget(RESERVE_CALLS / 2)) return "سهمیه امروز API رو به پایان است"
        var error: String? = null
        try {
            spend()
            val live = api.getLiveFixtures(key)
            val err = errorText(live.errors)
            if (err != null) {
                error = err
            } else {
                var liveOdds: Map<Long, OddsPack> = emptyMap()
                if (live.response.isNotEmpty() && hasBudget(RESERVE_CALLS / 2)) {
                    liveOdds = fetchLiveOdds(key)
                }
                upsertFixtures(live.response, liveOdds)
            }
        } catch (e: Exception) {
            error = "خطا در دریافت بازی‌های زنده: ${e.localizedMessage ?: "شبکه"}"
        }
        val refreshErr = refreshTrackedMatches(key)
        val settled = settleAll()
        if (settled > 0) settledOut(settled)
        return error ?: refreshErr
    }

    private suspend fun fetchLiveOdds(key: String): Map<Long, OddsPack> {
        return try {
            spend()
            val res = api.getLiveOdds(key)
            if (errorText(res.errors) != null) return emptyMap()
            val map = HashMap<Long, OddsPack>()
            for (item in res.response) {
                val pack = readBets(item.odds ?: emptyList(), live = true)
                if (pack.home > 0 && pack.draw > 0 && pack.away > 0) map[item.fixture.id] = pack
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }

    /**
     * Matches that are LIVE in our DB but no longer in the live feed, plus matches
     * with pending predictions that are not finished: fetch by ids (max 20 per call).
     */
    suspend fun refreshTrackedMatches(key: String): String? {
        val ids = LinkedHashSet<Long>()
        try {
            database.matchDao().getLiveApiMatches().forEach { m ->
                m.id.removePrefix("api_").toLongOrNull()?.let { ids.add(it) }
            }
            val pending = database.predictionDao().getPendingPredictions()
            if (pending.isNotEmpty()) {
                val matches = database.matchDao().getMatchesByIds(pending.map { it.matchId }.distinct())
                matches.filter { it.id.startsWith("api_") && it.status != MatchStatus.FINISHED }
                    .forEach { m -> m.id.removePrefix("api_").toLongOrNull()?.let { ids.add(it) } }
                // pending predictions whose match row is already finished are settled locally
            }
        } catch (_: Exception) {
        }
        if (ids.isEmpty() || !hasBudget(2)) return null
        return try {
            val chunk = ids.take(20).joinToString("-")
            spend()
            val res = api.getFixturesByIds(key, chunk, tzName)
            val err = errorText(res.errors)
            if (err != null) err else {
                upsertFixtures(res.response, liveOddsById = null)
                null
            }
        } catch (e: Exception) {
            "خطا در به‌روزرسانی نتایج: ${e.localizedMessage ?: "شبکه"}"
        }
    }

    // ------------------------------------------------------------ match detail

    /** Events + statistics for one match (1 call). */
    suspend fun loadDetail(key: String, matchId: String): String? {
        val fixtureId = matchId.removePrefix("api_").toLongOrNull() ?: return null
        if (!hasBudget(2)) return "سهمیه امروز API رو به پایان است"
        return try {
            spend()
            val res = api.getFixtureDetail(key, fixtureId, tzName)
            val err = errorText(res.errors)
            if (err != null) return err
            val item = res.response.firstOrNull() ?: return null
            val old = database.matchDao().getMatchById(matchId) ?: return null

            val events = (item.events ?: emptyList()).mapNotNull { e ->
                val kind = when {
                    e.type.equals("Goal", true) -> "GOAL"
                    e.type.equals("Card", true) && (e.detail ?: "").contains("Red", true) -> "CARD_RED"
                    e.type.equals("Card", true) -> "CARD_YELLOW"
                    e.type.equals("subst", true) -> "SUB"
                    e.type.equals("Var", true) -> "VAR"
                    else -> return@mapNotNull null
                }
                MatchEvent(
                    minute = (e.time?.elapsed ?: 0) + (e.time?.extra ?: 0),
                    type = kind,
                    teamName = e.team?.name ?: "",
                    player = e.player?.name ?: "",
                    description = e.detail ?: ""
                )
            }
            var updated = old.copy(eventsJson = eventListAdapter.toJson(events))

            val statsList = item.statistics
            if (!statsList.isNullOrEmpty() && statsList.size >= 2) {
                fun num(t: com.playzone.football.data.remote.ApiTeamStats, name: String): Int {
                    val v = t.statistics?.firstOrNull { it.type.equals(name, true) }?.value
                    return when (v) {
                        is Number -> v.toInt()
                        is String -> v.filter { it.isDigit() }.toIntOrNull() ?: 0
                        else -> 0
                    }
                }
                val h = statsList[0]
                val a = statsList[1]
                val stats = MatchStats(
                    possessionHome = num(h, "Ball Possession"),
                    possessionAway = num(a, "Ball Possession"),
                    shotsOnTargetHome = num(h, "Shots on Goal"),
                    shotsOnTargetAway = num(a, "Shots on Goal"),
                    cornersHome = num(h, "Corner Kicks"),
                    cornersAway = num(a, "Corner Kicks"),
                    foulsHome = num(h, "Fouls"),
                    foulsAway = num(a, "Fouls"),
                    yellowCardsHome = num(h, "Yellow Cards"),
                    yellowCardsAway = num(a, "Yellow Cards")
                )
                updated = updated.copy(statsJson = statsAdapter.toJson(stats))
            }
            database.matchDao().updateMatch(updated)
            null
        } catch (e: Exception) {
            "خطا در دریافت جزئیات بازی: ${e.localizedMessage ?: "شبکه"}"
        }
    }

    // ------------------------------------------------------------- settlement

    /** Settles every pending prediction whose match is finished or cancelled. */
    suspend fun settleAll(): Int {
        var settled = 0
        try {
            val pending = database.predictionDao().getPendingPredictions()
            if (pending.isEmpty()) return 0
            val matches = database.matchDao().getMatchesByIds(pending.map { it.matchId }.distinct())
                .associateBy { it.id }
            for (p in pending) {
                val m = matches[p.matchId] ?: continue
                if (!m.id.startsWith("api_")) continue
                when {
                    m.statusShort in CANCEL_CODES -> {
                        refund(p)
                        settled++
                    }
                    m.status == MatchStatus.FINISHED && m.statusShort in DONE_CODES -> {
                        val won = isWinner(p.outcomeType, m.homeScore, m.awayScore)
                        finish(p, won)
                        settled++
                    }
                }
            }
        } catch (_: Exception) {
        }
        return settled
    }

    private fun isWinner(type: PredictionOutcomeType, h: Int, a: Int): Boolean =
        isPredictionWinner(type, h, a)

    private suspend fun finish(p: PredictionEntity, won: Boolean) = WalletLock.mutex.withLock {
        val fresh = database.predictionDao().getPredictionById(p.id) ?: return@withLock
        if (fresh.status != PredictionStatus.PENDING) return@withLock
        database.predictionDao().updatePrediction(
            fresh.copy(
                status = if (won) PredictionStatus.WON else PredictionStatus.LOST,
                resolvedAt = System.currentTimeMillis()
            )
        )
        val w = database.walletDao().getWallet() ?: WalletEntity()
        database.walletDao().saveWallet(
            if (won) w.copy(
                balance = w.balance + fresh.potentialReturn,
                totalWon = w.totalWon + fresh.potentialReturn,
                wonCount = w.wonCount + 1
            ) else w.copy(lostCount = w.lostCount + 1)
        )
    }

    private suspend fun refund(p: PredictionEntity) = WalletLock.mutex.withLock {
        val fresh = database.predictionDao().getPredictionById(p.id) ?: return@withLock
        if (fresh.status != PredictionStatus.PENDING) return@withLock
        database.predictionDao().updatePrediction(
            fresh.copy(status = PredictionStatus.CANCELLED, resolvedAt = System.currentTimeMillis())
        )
        val w = database.walletDao().getWallet() ?: WalletEntity()
        database.walletDao().saveWallet(
            w.copy(
                balance = w.balance + fresh.stakeCredits,
                totalStaked = (w.totalStaked - fresh.stakeCredits).coerceAtLeast(0),
                predictionsCount = (w.predictionsCount - 1).coerceAtLeast(0)
            )
        )
    }

    // ---------------------------------------------------------------- mapping

    private suspend fun upsertFixtures(items: List<ApiFixtureItem>, liveOddsById: Map<Long, OddsPack>?) {
        if (items.isEmpty()) return
        val ids = items.map { "api_${it.fixture.id}" }
        val existing = database.matchDao().getMatchesByIds(ids).associateBy { it.id }
        val out = items.map { item ->
            val id = "api_${item.fixture.id}"
            val merged = mergeFixture(existing[id], item)
            when {
                merged.status == MatchStatus.LIVE && liveOddsById != null -> {
                    val pack = liveOddsById[item.fixture.id]
                    if (pack != null) applyPack(zeroOdds(merged), pack) else zeroOdds(merged)
                }
                merged.status == MatchStatus.LIVE -> {
                    // A match that just kicked off must not keep its pre-match prices.
                    val prev = existing[id]
                    if (prev == null || prev.status != MatchStatus.LIVE) zeroOdds(merged) else merged
                }
                merged.status == MatchStatus.FINISHED -> zeroOdds(merged)
                else -> merged
            }
        }
        database.matchDao().insertMatches(out)
    }

    private fun mergeFixture(old: MatchEntity?, item: ApiFixtureItem): MatchEntity {
        val short = item.fixture.status.shortStatus ?: "NS"
        val status = when {
            short in LIVE_CODES -> MatchStatus.LIVE
            short in DONE_CODES || short in CANCEL_CODES -> MatchStatus.FINISHED
            else -> MatchStatus.UPCOMING
        }
        val time = try {
            OffsetDateTime.parse(item.fixture.date).atZoneSameInstant(zoneId)
        } catch (_: Exception) {
            null
        }
        val timeText = time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--"
        val dateText = time?.toLocalDate()?.toString() ?: item.fixture.date.take(10)

        // 90-minute result for settlement; fall back to the running score
        val ft = item.score?.fulltime
        val home = if (status == MatchStatus.FINISHED) (ft?.home ?: item.goals.home ?: 0) else (item.goals.home ?: 0)
        val away = if (status == MatchStatus.FINISHED) (ft?.away ?: item.goals.away ?: 0) else (item.goals.away ?: 0)

        val base = old ?: MatchEntity(
            id = "api_${item.fixture.id}",
            leagueId = "api_league_${item.league.id}",
            leagueName = item.league.name,
            leaguePersianName = item.league.name,
            homeTeamId = "api_team_${item.teams.home.id}",
            homeTeamName = item.teams.home.name,
            homeTeamPersian = item.teams.home.name,
            homeTeamLogo = item.teams.home.logo ?: "",
            awayTeamId = "api_team_${item.teams.away.id}",
            awayTeamName = item.teams.away.name,
            awayTeamPersian = item.teams.away.name,
            awayTeamLogo = item.teams.away.logo ?: "",
            status = status,
            minute = 0,
            homeScore = 0,
            awayScore = 0,
            matchTime = timeText,
            matchDate = dateText,
            stadium = "",
            referee = "",
            simHomeProb = 34,
            simDrawProb = 33,
            simAwayProb = 33,
            simOverProb = 50,
            simUnderProb = 50,
            eventsJson = "[]",
            statsJson = "{}"
        )
        return base.copy(
            leagueName = item.league.name,
            leaguePersianName = item.league.name,
            status = status,
            statusShort = short,
            minute = item.fixture.status.elapsed ?: base.minute,
            homeScore = home,
            awayScore = away,
            matchTime = if (status == MatchStatus.LIVE) "زنده" else timeText,
            matchDate = dateText,
            stadium = item.fixture.venue?.name ?: base.stadium,
            referee = item.fixture.referee ?: base.referee
        )
    }

    // ------------------------------------------------------------------- odds

    class OddsPack {
        var home = 0.0
        var draw = 0.0
        var away = 0.0
        var dc1x = 0.0
        var dc12 = 0.0
        var dcx2 = 0.0
        var over25 = 0.0
        var under25 = 0.0
        var over15 = 0.0
        var under15 = 0.0
        var bttsYes = 0.0
        var bttsNo = 0.0
    }

    private fun odd(v: String?): Double = v?.trim()?.toDoubleOrNull()?.takeIf { it > 1.0 } ?: 0.0

    /**
     * Pre-match ids (API-Football): 1 = Match Winner, 5 = Goals Over/Under,
     * 8 = Both Teams Score, 12 = Double Chance. Live feed uses other ids, so it is matched by name.
     * Anything not recognised stays 0.0 and the button is disabled (never a made-up price).
     */
    private fun readBets(bets: List<ApiBet>, live: Boolean): OddsPack {
        val p = OddsPack()
        for (bet in bets) {
            val name = (bet.name ?: "").trim()
            val values = bet.values ?: continue
            if (!live) {
                when {
                    bet.id == 1 || name.equals("Match Winner", true) -> values.forEach {
                        when (it.value?.trim()) {
                            "Home" -> p.home = odd(it.odd)
                            "Draw" -> p.draw = odd(it.odd)
                            "Away" -> p.away = odd(it.odd)
                        }
                    }
                    bet.id == 5 || name.equals("Goals Over/Under", true) -> values.forEach {
                        when (it.value?.trim()) {
                            "Over 2.5" -> p.over25 = odd(it.odd)
                            "Under 2.5" -> p.under25 = odd(it.odd)
                            "Over 1.5" -> p.over15 = odd(it.odd)
                            "Under 1.5" -> p.under15 = odd(it.odd)
                        }
                    }
                    bet.id == 8 || name.equals("Both Teams Score", true) -> values.forEach {
                        when (it.value?.trim()) {
                            "Yes" -> p.bttsYes = odd(it.odd)
                            "No" -> p.bttsNo = odd(it.odd)
                        }
                    }
                    bet.id == 12 || name.equals("Double Chance", true) -> values.forEach {
                        when (it.value?.trim()) {
                            "Home/Draw" -> p.dc1x = odd(it.odd)
                            "Home/Away" -> p.dc12 = odd(it.odd)
                            "Draw/Away" -> p.dcx2 = odd(it.odd)
                        }
                    }
                }
            } else {
                val lower = name.lowercase()
                val excluded = listOf("1st", "2nd", "half", "team", "corner", "card", "next", "asian")
                    .any { lower.contains(it) }
                when {
                    lower == "fulltime result" || lower == "match winner" -> values.forEach {
                        if (it.suspended == true) return@forEach
                        when (it.value?.trim()) {
                            "Home" -> p.home = odd(it.odd)
                            "Draw" -> p.draw = odd(it.odd)
                            "Away" -> p.away = odd(it.odd)
                        }
                    }
                    lower == "double chance" -> values.forEach {
                        if (it.suspended == true) return@forEach
                        when (it.value?.trim()) {
                            "Home/Draw" -> p.dc1x = odd(it.odd)
                            "Home/Away" -> p.dc12 = odd(it.odd)
                            "Draw/Away" -> p.dcx2 = odd(it.odd)
                        }
                    }
                    !excluded && (lower.contains("over/under") || lower == "match goals") -> values.forEach {
                        if (it.suspended == true) return@forEach
                        val line = it.handicap?.trim()
                        val side = it.value?.trim()
                        if (line == "2.5" && side == "Over") p.over25 = odd(it.odd)
                        if (line == "2.5" && side == "Under") p.under25 = odd(it.odd)
                        if (line == "1.5" && side == "Over") p.over15 = odd(it.odd)
                        if (line == "1.5" && side == "Under") p.under15 = odd(it.odd)
                    }
                    !excluded && lower.contains("both teams to score") -> values.forEach {
                        if (it.suspended == true) return@forEach
                        when (it.value?.trim()) {
                            "Yes" -> p.bttsYes = odd(it.odd)
                            "No" -> p.bttsNo = odd(it.odd)
                        }
                    }
                }
            }
        }
        return p
    }

    private fun zeroOdds(m: MatchEntity) = m.copy(
        oddHome = 0.0, oddDraw = 0.0, oddAway = 0.0, oddDc1x = 0.0, oddDc12 = 0.0, oddDcx2 = 0.0,
        oddOver25 = 0.0, oddUnder25 = 0.0, oddOver15 = 0.0, oddUnder15 = 0.0,
        oddBttsYes = 0.0, oddBttsNo = 0.0
    )

    private fun applyPack(m: MatchEntity, p: OddsPack): MatchEntity {
        // implied probabilities (normalised) for the info bars
        val inv = listOf(1 / p.home, 1 / p.draw, 1 / p.away)
        val sum = inv.sum()
        val h = (100 * inv[0] / sum).roundToInt()
        val d = (100 * inv[1] / sum).roundToInt()
        val a = (100 - h - d).coerceAtLeast(0)
        var over = m.simOverProb
        var under = m.simUnderProb
        if (p.over25 > 0 && p.under25 > 0) {
            val o = 1 / p.over25
            val u = 1 / p.under25
            over = (100 * o / (o + u)).roundToInt()
            under = 100 - over
        }
        return m.copy(
            oddHome = p.home, oddDraw = p.draw, oddAway = p.away,
            oddDc1x = p.dc1x, oddDc12 = p.dc12, oddDcx2 = p.dcx2,
            oddOver25 = p.over25, oddUnder25 = p.under25,
            oddOver15 = p.over15, oddUnder15 = p.under15,
            oddBttsYes = p.bttsYes, oddBttsNo = p.bttsNo,
            simHomeProb = h, simDrawProb = d, simAwayProb = a,
            simOverProb = over, simUnderProb = under
        )
    }
}
