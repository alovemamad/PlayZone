package com.playzone.football

import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.repository.isPredictionWinner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettlementTest {

    private fun realMatch(h: Double, d: Double, a: Double) = Match(
        id = "api_1", leagueId = "api_league_1", leagueName = "L", leaguePersianName = "L",
        homeTeamId = "t1", homeTeamName = "A", homeTeamPersian = "A", homeTeamLogo = "",
        awayTeamId = "t2", awayTeamName = "B", awayTeamPersian = "B", awayTeamLogo = "",
        status = MatchStatus.UPCOMING, minute = 0, homeScore = 0, awayScore = 0,
        matchTime = "20:00", matchDate = "2026-11-09", stadium = "", referee = "",
        simHomeProb = 40, simDrawProb = 30, simAwayProb = 30,
        oddHome = h, oddDraw = d, oddAway = a
    )

    @Test
    fun `1x2 results`() {
        assertTrue(isPredictionWinner(PredictionOutcomeType.HOME_WIN, 2, 1))
        assertFalse(isPredictionWinner(PredictionOutcomeType.HOME_WIN, 1, 1))
        assertTrue(isPredictionWinner(PredictionOutcomeType.DRAW, 0, 0))
        assertTrue(isPredictionWinner(PredictionOutcomeType.AWAY_WIN, 0, 3))
    }

    @Test
    fun `double chance results`() {
        assertTrue(isPredictionWinner(PredictionOutcomeType.DOUBLE_CHANCE_1X, 1, 1))
        assertFalse(isPredictionWinner(PredictionOutcomeType.DOUBLE_CHANCE_1X, 0, 1))
        assertTrue(isPredictionWinner(PredictionOutcomeType.DOUBLE_CHANCE_12, 2, 0))
        assertFalse(isPredictionWinner(PredictionOutcomeType.DOUBLE_CHANCE_12, 1, 1))
        assertTrue(isPredictionWinner(PredictionOutcomeType.DOUBLE_CHANCE_X2, 1, 1))
    }

    @Test
    fun `totals and both teams to score`() {
        assertTrue(isPredictionWinner(PredictionOutcomeType.OVER_2_5, 2, 1))
        assertFalse(isPredictionWinner(PredictionOutcomeType.OVER_2_5, 1, 1))
        assertTrue(isPredictionWinner(PredictionOutcomeType.UNDER_2_5, 1, 1))
        assertTrue(isPredictionWinner(PredictionOutcomeType.OVER_1_5, 1, 1))
        assertTrue(isPredictionWinner(PredictionOutcomeType.UNDER_1_5, 1, 0))
        assertTrue(isPredictionWinner(PredictionOutcomeType.BOTH_TEAMS_SCORE_YES, 1, 1))
        assertTrue(isPredictionWinner(PredictionOutcomeType.BOTH_TEAMS_SCORE_NO, 2, 0))
    }

    @Test
    fun `real matches use bookmaker odds and never fake missing ones`() {
        val m = realMatch(1.85, 3.4, 4.2)
        assertEquals(1.85, m.oddFor(PredictionOutcomeType.HOME_WIN), 0.0001)
        assertEquals(0.0, m.oddFor(PredictionOutcomeType.OVER_2_5), 0.0001)
        assertEquals(0.0, m.oddFor(PredictionOutcomeType.DOUBLE_CHANCE_1X), 0.0001)
    }
}
