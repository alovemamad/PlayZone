package com.playzone.football.data.local

import androidx.room.TypeConverter
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.PredictionStatus

class Converters {
    @TypeConverter
    fun fromMatchStatus(value: MatchStatus): String = value.name

    @TypeConverter
    fun toMatchStatus(value: String): MatchStatus = try {
        MatchStatus.valueOf(value)
    } catch (_: Exception) {
        MatchStatus.UPCOMING
    }

    @TypeConverter
    fun fromPredictionOutcomeType(value: PredictionOutcomeType): String = value.name

    @TypeConverter
    fun toPredictionOutcomeType(value: String): PredictionOutcomeType = try {
        PredictionOutcomeType.valueOf(value)
    } catch (_: Exception) {
        PredictionOutcomeType.HOME_WIN
    }

    @TypeConverter
    fun fromPredictionStatus(value: PredictionStatus): String = value.name

    @TypeConverter
    fun toPredictionStatus(value: String): PredictionStatus = try {
        PredictionStatus.valueOf(value)
    } catch (_: Exception) {
        PredictionStatus.PENDING
    }
}
