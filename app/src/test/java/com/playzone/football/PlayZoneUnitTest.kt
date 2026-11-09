package com.playzone.football

import com.playzone.football.data.mock.SampleFootballData
import com.playzone.football.data.model.MatchEvent
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.Wallet
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayZoneUnitTest {

  @Test
  fun `verify initial wallet state and credits`() {
    val wallet = Wallet()
    assertEquals(100_000L, wallet.balance)
    assertEquals(0L, wallet.totalStaked)
    assertEquals(0L, wallet.totalWon)
    assertEquals(0f, wallet.winRate, 0.001f)
  }

  @Test
  fun `verify odds multiplier calculation`() {
    val match = SampleFootballData.sampleMatches.first()
    val expectedMultiplier = (100.0 / match.simHomeProb * 100).toInt() / 100.0
    assertEquals(expectedMultiplier, match.homeProbMultiplier, 0.01)
    assertTrue("Multiplier should be positive", match.homeProbMultiplier > 1.0)
  }

  @Test
  fun `verify sample leagues and teams loaded`() {
    assertTrue("Leagues should not be empty", SampleFootballData.sampleLeagues.isNotEmpty())
    assertTrue("Teams should not be empty", SampleFootballData.sampleTeams.isNotEmpty())
    val ucl = SampleFootballData.sampleLeagues.find { it.id == "league_ucl" }
    assertEquals("لیگ قهرمانان اروپا", ucl?.persianName)
  }

  @Test
  fun `verify wallet profit and win rate`() {
    val wallet = Wallet(
      balance = 120_000,
      totalStaked = 50_000,
      totalWon = 70_000,
      predictionsCount = 10,
      wonCount = 6,
      lostCount = 4
    )
    assertEquals(20_000L, wallet.netProfit)
    assertEquals(60.0f, wallet.winRate, 0.01f)
  }

  @Test
  fun `verify Moshi serialization and deserialization of MatchEvent`() {
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val eventListType = Types.newParameterizedType(List::class.java, MatchEvent::class.java)
    val adapter = moshi.adapter<List<MatchEvent>>(eventListType)
    val events = listOf(
      MatchEvent(12, "GOAL", "منچسترسیتی", "ارلینگ هالند", "شوت از داخل محوطه جریمه")
    )
    val json = adapter.toJson(events)
    assertTrue("JSON should contain player name", json.contains("هالند"))
    val deserialized = adapter.fromJson(json)
    assertEquals(1, deserialized?.size)
    assertEquals("GOAL", deserialized?.first()?.type)
    assertEquals("ارلینگ هالند", deserialized?.first()?.player)
  }
}
