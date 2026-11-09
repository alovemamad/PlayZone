package com.playzone.football

import android.app.Application
import com.playzone.football.data.local.AppDatabase
import com.playzone.football.data.repository.FootballRepository
import com.playzone.football.data.repository.PredictionRepository
import com.playzone.football.data.repository.ResponsibleGamingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class PlayZoneApp : Application() {
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getInstance(this) }
    val footballRepository by lazy { FootballRepository(database, appScope) }
    val predictionRepository by lazy { PredictionRepository(database) }
    val responsibleGamingRepository by lazy { ResponsibleGamingRepository(database) }

    companion object {
        lateinit var instance: PlayZoneApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
