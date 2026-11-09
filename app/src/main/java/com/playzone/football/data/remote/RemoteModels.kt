package com.playzone.football.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiFootballResponse<T>(
    @field:Json(name = "get") val get: String? = null,
    @field:Json(name = "results") val results: Int = 0,
    @field:Json(name = "errors") val errors: Any? = null,
    @field:Json(name = "paging") val paging: ApiPaging? = null,
    @field:Json(name = "response") val response: List<T> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ApiPaging(
    @field:Json(name = "current") val current: Int? = null,
    @field:Json(name = "total") val total: Int? = null
)

@JsonClass(generateAdapter = true)
data class ApiFixtureItem(
    @field:Json(name = "fixture") val fixture: ApiFixtureDetails,
    @field:Json(name = "league") val league: ApiLeagueDetails,
    @field:Json(name = "teams") val teams: ApiTeamsDetails,
    @field:Json(name = "goals") val goals: ApiGoalsDetails,
    @field:Json(name = "score") val score: ApiScoreDetails? = null,
    @field:Json(name = "events") val events: List<ApiEvent>? = null,
    @field:Json(name = "statistics") val statistics: List<ApiTeamStats>? = null
)

@JsonClass(generateAdapter = true)
data class ApiScoreDetails(
    @field:Json(name = "fulltime") val fulltime: ApiGoalsDetails? = null
)

@JsonClass(generateAdapter = true)
data class ApiNamed(
    @field:Json(name = "id") val id: Long? = null,
    @field:Json(name = "name") val name: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiEventTime(
    @field:Json(name = "elapsed") val elapsed: Int? = null,
    @field:Json(name = "extra") val extra: Int? = null
)

@JsonClass(generateAdapter = true)
data class ApiEvent(
    @field:Json(name = "time") val time: ApiEventTime? = null,
    @field:Json(name = "team") val team: ApiNamed? = null,
    @field:Json(name = "player") val player: ApiNamed? = null,
    @field:Json(name = "type") val type: String? = null,
    @field:Json(name = "detail") val detail: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiStatItem(
    @field:Json(name = "type") val type: String? = null,
    @field:Json(name = "value") val value: Any? = null
)

@JsonClass(generateAdapter = true)
data class ApiTeamStats(
    @field:Json(name = "team") val team: ApiNamed? = null,
    @field:Json(name = "statistics") val statistics: List<ApiStatItem>? = null
)

// ---- Odds (pre-match: /odds, live: /odds/live) ----

@JsonClass(generateAdapter = true)
data class ApiOddsFixture(
    @field:Json(name = "id") val id: Long
)

@JsonClass(generateAdapter = true)
data class ApiBetValue(
    @field:Json(name = "value") val value: String? = null,
    @field:Json(name = "odd") val odd: String? = null,
    @field:Json(name = "handicap") val handicap: String? = null,
    @field:Json(name = "suspended") val suspended: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class ApiBet(
    @field:Json(name = "id") val id: Int? = null,
    @field:Json(name = "name") val name: String? = null,
    @field:Json(name = "values") val values: List<ApiBetValue>? = null
)

@JsonClass(generateAdapter = true)
data class ApiBookmaker(
    @field:Json(name = "id") val id: Int? = null,
    @field:Json(name = "name") val name: String? = null,
    @field:Json(name = "bets") val bets: List<ApiBet>? = null
)

@JsonClass(generateAdapter = true)
data class ApiOddsItem(
    @field:Json(name = "fixture") val fixture: ApiOddsFixture,
    @field:Json(name = "bookmakers") val bookmakers: List<ApiBookmaker>? = null
)

@JsonClass(generateAdapter = true)
data class ApiLiveOddsItem(
    @field:Json(name = "fixture") val fixture: ApiOddsFixture,
    @field:Json(name = "odds") val odds: List<ApiBet>? = null
)

@JsonClass(generateAdapter = true)
data class ApiFixtureDetails(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "referee") val referee: String? = null,
    @field:Json(name = "timezone") val timezone: String? = null,
    @field:Json(name = "date") val date: String,
    @field:Json(name = "venue") val venue: ApiNamed? = null,
    @field:Json(name = "status") val status: ApiStatusDetails
)

@JsonClass(generateAdapter = true)
data class ApiStatusDetails(
    @field:Json(name = "long") val longStatus: String? = null,
    @field:Json(name = "short") val shortStatus: String? = null,
    @field:Json(name = "elapsed") val elapsed: Int? = null
)

@JsonClass(generateAdapter = true)
data class ApiLeagueDetails(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "country") val country: String? = null,
    @field:Json(name = "logo") val logo: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiTeamsDetails(
    @field:Json(name = "home") val home: ApiTeamDetails,
    @field:Json(name = "away") val away: ApiTeamDetails
)

@JsonClass(generateAdapter = true)
data class ApiTeamDetails(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "logo") val logo: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiGoalsDetails(
    @field:Json(name = "home") val home: Int? = null,
    @field:Json(name = "away") val away: Int? = null
)

@JsonClass(generateAdapter = true)
data class ApiStatusResponse(
    @field:Json(name = "response") val response: ApiStatusData? = null
)

@JsonClass(generateAdapter = true)
data class ApiStatusData(
    @field:Json(name = "requests") val requests: ApiRequestsInfo? = null,
    @field:Json(name = "subscription") val subscription: ApiSubscriptionInfo? = null
)

@JsonClass(generateAdapter = true)
data class ApiRequestsInfo(
    @field:Json(name = "current") val current: Int? = 0,
    @field:Json(name = "limit_day") val limitDay: Int? = 100
)

@JsonClass(generateAdapter = true)
data class ApiSubscriptionInfo(
    @field:Json(name = "plan") val plan: String? = "Free",
    @field:Json(name = "active") val active: Boolean? = true
)
