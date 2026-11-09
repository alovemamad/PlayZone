package com.playzone.football.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiFootballResponse<T>(
    @field:Json(name = "get") val get: String? = null,
    @field:Json(name = "results") val results: Int = 0,
    @field:Json(name = "errors") val errors: List<String>? = null,
    @field:Json(name = "response") val response: List<T> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ApiFixtureItem(
    @field:Json(name = "fixture") val fixture: ApiFixtureDetails,
    @field:Json(name = "league") val league: ApiLeagueDetails,
    @field:Json(name = "teams") val teams: ApiTeamsDetails,
    @field:Json(name = "goals") val goals: ApiGoalsDetails
)

@JsonClass(generateAdapter = true)
data class ApiFixtureDetails(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "referee") val referee: String? = null,
    @field:Json(name = "timezone") val timezone: String? = null,
    @field:Json(name = "date") val date: String,
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
