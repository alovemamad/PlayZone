package com.playzone.football.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        MatchEntity::class,
        PredictionEntity::class,
        WalletEntity::class,
        ResponsibleSettingsEntity::class,
        UserSettingsEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
    abstract fun predictionDao(): PredictionDao
    abstract fun walletDao(): WalletDao
    abstract fun responsibleSettingsDao(): ResponsibleSettingsDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        // Keeps wallet and predictions when real-odds columns are added.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE matches ADD COLUMN statusShort TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE matches ADD COLUMN oddsUpdatedAt INTEGER NOT NULL DEFAULT 0")
                listOf(
                    "oddHome", "oddDraw", "oddAway", "oddDc1x", "oddDc12", "oddDcx2",
                    "oddOver25", "oddUnder25", "oddOver15", "oddUnder15", "oddBttsYes", "oddBttsNo"
                ).forEach { col ->
                    db.execSQL("ALTER TABLE matches ADD COLUMN $col REAL NOT NULL DEFAULT 0.0")
                }
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "playzone_database.db"
                ).addMigrations(MIGRATION_2_3).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
