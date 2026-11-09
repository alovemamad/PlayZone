# PlayZone Android App - Full Source Code Dump

This is a complete text dump of the source files from the PlayZone project (Kotlin/Android).
Generated for uploading to Google AI Studio (which cannot process ZIP archives well).

**IMPORTANT:** Real API keys and binary assets (icons, logos, fonts, keystore) were excluded for security and size.
Use the Secrets panel in AI Studio for keys. See .env.example.

---


================================================================================
FILE: .env.example
================================================================================

```
# GEMINI_API_KEY: Required for Gemini AI API calls.
# This is a placeholder key.
# AI Studio automatically injects this at runtime from user secrets.
# Users configure this via the Secrets panel in the AI Studio UI.
# IMPORTANT: Uncomment the line below if your app uses the Gemini API.
# If left commented out, the key will NOT be packaged in the APK.
# GEMINI_API_KEY=MY_GEMINI_API_KEY

# API-Football key (dashboard.api-football.com -> Account -> My Access).
# With a real key the app shows real fixtures, bookmaker odds and settles bets from final scores.
# With the demo value below it runs in offline demo mode.
API_FOOTBALL_KEY=demo_football_key
THESPORTSDB_KEY=demo_sportsdb_key
```


================================================================================
FILE: .github/workflows/android.yml
================================================================================

```yaml
name: Android CI

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout Code
      uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Setup Gradle
      uses: gradle/actions/setup-gradle@v3
      with:
        gradle-version: 9.3.1

    - name: Generate Debug Keystore
      run: |
        keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"

    - name: Build Debug APK
      run: gradle assembleDebug --stacktrace

    - name: Upload APK
      uses: actions/upload-artifact@v4
      with:
        name: PlayZone-Debug-APK
        path: app/build/outputs/apk/debug/app-debug.apk
```


================================================================================
FILE: .gitignore
================================================================================

```
*.iml
.gradle
.kotlin
/local.properties
/.idea/caches
/.idea/libraries
/.idea/modules.xml
/.idea/workspace.xml
/.idea/navEditor.xml
/.idea/assetWizardSettings.xml
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties
.env
debug.keystore
```


================================================================================
FILE: README.md
================================================================================

```markdown
# PlayZone – virtual-money football betting simulator

Real fixtures, real bookmaker odds (API-Football, bookmaker 8), automatic settlement from the
official final score. Only the money is virtual.

## Run
1. Put your key in `.env` (see `.env.example`): `API_FOOTBALL_KEY=...`
2. Open in Android Studio, run.

## How it works
- Fixtures today + tomorrow, pre-match odds (1X2, double chance, over/under 1.5 & 2.5, BTTS),
  live scores, live odds (best effort), events and statistics on the match page.
- Missing odds are shown as "—" and cannot be bet on. No made-up prices in real mode.
- Pending bets are settled automatically on the 90-minute result; postponed/cancelled matches refund.
- Wallet: any balance, recharge any time.
- Free plan = 100 requests/day: live refresh every 10 minutes. Paid plan: every minute.
```


================================================================================
FILE: app/build.gradle.kts
================================================================================

```kotlin
import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.playzone.football"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.playzone.football"
    minSdk = 26
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  // Do not remove or modify these signingConfigs. They are necessary for building, installing,
  // and updating Android apps in AI Studio.
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD") ?: "playzone_secure_store_2026"
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD") ?: "playzone_secure_store_2026"
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isDebuggable = false
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)

  // Uncomment ALL FOUR of the following dependencies together to use Firebase Auth and Google
  // Sign-In via Credential Manager:
  // implementation(libs.firebase.auth)
  // implementation(libs.androidx.credentials)
  // implementation(libs.androidx.credentials.play.services)
  // implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
```


================================================================================
FILE: app/proguard-rules.pro
================================================================================

```
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
```


================================================================================
FILE: app/src/androidTest/java/com/playzone/football/PlayZoneInstrumentedTest.kt
================================================================================

```kotlin
package com.playzone.football

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayZoneInstrumentedTest {
  @Test
  fun useAppContext() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.playzone.football", appContext.packageName)
  }
}
```


================================================================================
FILE: app/src/main/AndroidManifest.xml
================================================================================

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:name=".PlayZoneApp"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.PlayZone">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.PlayZone"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```


================================================================================
FILE: app/src/main/java/com/playzone/football/MainActivity.kt
================================================================================

```kotlin
package com.playzone.football

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.playzone.football.ui.navigation.MainScreen
import com.playzone.football.ui.theme.PlayZoneTheme
import com.playzone.football.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlayZoneTheme {
                val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/PlayZoneApp.kt
================================================================================

```kotlin
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
```


================================================================================
FILE: app/src/main/java/com/playzone/football/data/local/AppDatabase.kt
================================================================================

```kotlin
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
```


================================================================================
FILE: app/src/main/java/com/playzone/football/data/local/Converters.kt
================================================================================

```kotlin
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
```


================================================================================
FILE: app/src/main/java/com/playzone/football/data/local/Daos.kt
================================================================================

```kotlin
package com.playzone.football.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE status = :status")
    fun getMatchesByStatus(status: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    suspend fun getMatchById(id: String): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Query("SELECT * FROM matches WHERE id IN (:ids)")
    suspend fun getMatchesByIds(ids: List<String>): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE status = 'LIVE' AND id LIKE 'api_%'")
    suspend fun getLiveApiMatches(): List<MatchEntity>

    @Query("SELECT MAX(oddsUpdatedAt) FROM matches")
    suspend fun maxOddsUpdatedAt(): Long?

    @Query("DELETE FROM matches WHERE id NOT LIKE 'api_%'")
    suspend fun deleteDemoMatches()

    // Old finished/unreferenced API matches (kept if a prediction points at them)
    @Query("DELETE FROM matches WHERE id LIKE 'api_%' AND matchDate < :minDate AND id NOT IN (SELECT matchId FROM predictions)")
    suspend fun deleteOldApiMatches(minDate: String)

    @Query("DELETE FROM matches")
    suspend fun clearAll()
}

@Dao
interface PredictionDao {
    @Query("SELECT * FROM predictions ORDER BY createdAt DESC")
    fun getAllPredictions(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE status = 'PENDING'")
    fun getActivePredictions(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE status = 'PENDING'")
    suspend fun getPendingPredictions(): List<PredictionEntity>

    @Query("SELECT * FROM predictions WHERE id = :id LIMIT 1")
    suspend fun getPredictionById(id: Long): PredictionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: PredictionEntity): Long

    @Update
    suspend fun updatePrediction(prediction: PredictionEntity)

    @Query("DELETE FROM predictions")
    suspend fun clearAll()
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet WHERE id = 1 LIMIT 1")
    fun getWalletFlow(): Flow<WalletEntity?>

    @Query("SELECT * FROM wallet WHERE id = 1 LIMIT 1")
    suspend fun getWallet(): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWallet(wallet: WalletEntity)

    @Query("UPDATE wallet SET balance = :newBalance WHERE id = 1")
    suspend fun updateBalance(newBalance: Long)

    @Query("DELETE FROM wallet")
    suspend fun clearWallet()
}

@Dao
interface ResponsibleSettingsDao {
    @Query("SELECT * FROM responsible_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<ResponsibleSettingsEntity?>

    @Query("SELECT * FROM responsible_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): ResponsibleSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: ResponsibleSettingsEntity)
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettingsEntity)
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/data/local/Entities.kt
================================================================================

```kotlin
package com.playzone.football.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.PredictionStatus

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
    val leagueId: String,
    val leagueName: String,
    val leaguePersianName: String,
    val homeTeamId: String,
    val homeTeamName: String,
    val homeTeamPersian: String,
    val homeTeamLogo: String,
    val awayTeamId: String,
    val awayTeamName: String,
    val awayTeamPersian: String,
    val awayTeamLogo: String,
    val status: MatchStatus,
    val minute: Int,
    val homeScore: Int,
    val awayScore: Int,
    val matchTime: String,
    val matchDate: String,
    val stadium: String,
    val referee: String,
    val simHomeProb: Int,
    val simDrawProb: Int,
    val simAwayProb: Int,
    val simOverProb: Int,
    val simUnderProb: Int,
    val eventsJson: String,
    val statsJson: String = "{}",
    val statusShort: String = "",
    val oddsUpdatedAt: Long = 0L,
    val oddHome: Double = 0.0,
    val oddDraw: Double = 0.0,
    val oddAway: Double = 0.0,
    val oddDc1x: Double = 0.0,
    val oddDc12: Double = 0.0,
    val oddDcx2: Double = 0.0,
    val oddOver25: Double = 0.0,
    val oddUnder25: Double = 0.0,
    val oddOver15: Double = 0.0,
    val oddUnder15: Double = 0.0,
    val oddBttsYes: Double = 0.0,
    val oddBttsNo: Double = 0.0
)

@Entity(tableName = "predictions")
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val matchId: String,
    val matchTitle: String,
    val outcomeType: PredictionOutcomeType,
    val outcomeLabel: String,
    val simProbability: Int,
    val oddsMultiplier: Double = 1.85,
    val stakeCredits: Long,
    val potentialReturn: Long,
    val status: PredictionStatus,
    val createdAt: Long,
    val resolvedAt: Long?
)

@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val id: Int = 1,
    val balance: Long = 100_000,
    val initialBalance: Long = 100_000,
    val totalStaked: Long = 0,
    val totalWon: Long = 0,
    val predictionsCount: Int = 0,
    val wonCount: Int = 0,
    val lostCount: Int = 0
)

@Entity(tableName = "responsible_settings")
data class ResponsibleSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val dailyLimitMinutes: Int = 60,
    val totalMinutesToday: Int = 15,
    val isPaused: Boolean = false,
    val pauseUntil: Long = 0L,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val apiFootballKey: String = "",
    val useLiveApi: Boolean = false,
    val customApiConnected: Boolean = false,
    val apiRemainingCalls: Int = 100,
    val lastSyncTime: String = "همگام‌سازی شده"
)
```


================================================================================
FILE: app/src/main/java/com/playzone/football/data/mock/SampleFootballData.kt
================================================================================

```kotlin
package com.playzone.football.data.mock

import com.playzone.football.data.model.League
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchEvent
import com.playzone.football.data.model.MatchStats
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.Team
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object SampleFootballData {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayDate(): String = dateFormat.format(Date())
    fun getTomorrowDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        return dateFormat.format(cal.time)
    }
    fun getYesterdayDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(cal.time)
    }

    val sampleLeagues = listOf(
        League(
            id = "league_ucl",
            name = "UEFA Champions League",
            persianName = "لیگ قهرمانان اروپا",
            country = "اروپا",
            flagEmoji = "🇪🇺",
            logoUrl = "https://media.api-sports.io/football/leagues/2.png",
            isHot = true
        ),
        League(
            id = "league_epl",
            name = "Premier League",
            persianName = "لیگ برتر انگلیس",
            country = "انگلستان",
            flagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
            logoUrl = "https://media.api-sports.io/football/leagues/39.png",
            isHot = true
        ),
        League(
            id = "league_laliga",
            name = "La Liga",
            persianName = "لالیگا اسپانیا",
            country = "اسپانیا",
            flagEmoji = "🇪🇸",
            logoUrl = "https://media.api-sports.io/football/leagues/140.png",
            isHot = true
        ),
        League(
            id = "league_persian",
            name = "Persian Gulf Pro League",
            persianName = "لیگ برتر خلیج فارس",
            country = "ایران",
            flagEmoji = "🇮🇷",
            logoUrl = "https://media.api-sports.io/football/leagues/291.png",
            isHot = true
        ),
        League(
            id = "league_seriea",
            name = "Serie A",
            persianName = "سری آ ایتالیا",
            country = "ایتالیا",
            flagEmoji = "🇮🇹",
            logoUrl = "https://media.api-sports.io/football/leagues/135.png"
        ),
        League(
            id = "league_bundesliga",
            name = "Bundesliga",
            persianName = "بوندس‌لیگا آلمان",
            country = "آلمان",
            flagEmoji = "🇩🇪",
            logoUrl = "https://media.api-sports.io/football/leagues/78.png"
        ),
        League(
            id = "league_ligue1",
            name = "Ligue 1",
            persianName = "لیگ ۱ فرانسه",
            country = "فرانسه",
            flagEmoji = "🇫🇷",
            logoUrl = "https://media.api-sports.io/football/leagues/61.png"
        )
    )

    val sampleTeams = listOf(
        // EPL
        Team("team_mancity", "Manchester City", "منچسترسیتی", "league_epl", "https://media.api-sports.io/football/teams/50.png", 1, 28, 20, 5, 3, 67, 24, 65, listOf("W", "W", "W", "D", "W")),
        Team("team_liverpool", "Liverpool", "لیورپول", "league_epl", "https://media.api-sports.io/football/teams/40.png", 2, 28, 19, 6, 3, 64, 25, 63, listOf("W", "D", "W", "W", "W")),
        Team("team_arsenal", "Arsenal", "آرسنال", "league_epl", "https://media.api-sports.io/football/teams/42.png", 3, 28, 18, 7, 3, 60, 22, 61, listOf("W", "W", "D", "W", "D")),
        Team("team_chelsea", "Chelsea", "چلسی", "league_epl", "https://media.api-sports.io/football/teams/49.png", 4, 28, 15, 6, 7, 51, 35, 51, listOf("W", "L", "W", "W", "D")),
        Team("team_tottenham", "Tottenham Hotspur", "تاتنهام", "league_epl", "https://media.api-sports.io/football/teams/47.png", 5, 28, 14, 5, 9, 53, 40, 47, listOf("L", "W", "D", "W", "L")),
        Team("team_manunited", "Manchester United", "منچستریونایتد", "league_epl", "https://media.api-sports.io/football/teams/33.png", 6, 28, 13, 5, 10, 42, 39, 44, listOf("W", "D", "L", "W", "W")),

        // La Liga
        Team("team_realmadrid", "Real Madrid", "رئال مادرید", "league_laliga", "https://media.api-sports.io/football/teams/541.png", 1, 27, 21, 4, 2, 62, 18, 67, listOf("W", "W", "W", "W", "D")),
        Team("team_barcelona", "Barcelona", "بارسلونا", "league_laliga", "https://media.api-sports.io/football/teams/529.png", 2, 27, 19, 5, 3, 65, 28, 62, listOf("W", "W", "W", "L", "W")),
        Team("team_atletico", "Atletico Madrid", "اتلتیکو مادرید", "league_laliga", "https://media.api-sports.io/football/teams/530.png", 3, 27, 16, 6, 5, 48, 26, 54, listOf("D", "W", "W", "D", "W")),
        Team("team_girona", "Girona", "خیرونا", "league_laliga", "https://media.api-sports.io/football/teams/547.png", 4, 27, 15, 5, 7, 45, 32, 50, listOf("W", "L", "W", "L", "W")),

        // Persian Gulf Pro League
        Team("team_persepolis", "Persepolis FC", "پرسپولیس تهران", "league_persian", "https://media.api-sports.io/football/teams/3522.png", 1, 23, 16, 5, 2, 40, 12, 53, listOf("W", "W", "D", "W", "W")),
        Team("team_esteghlal", "Esteghlal FC", "استقلال تهران", "league_persian", "https://media.api-sports.io/football/teams/3521.png", 2, 23, 15, 6, 2, 36, 14, 51, listOf("W", "D", "W", "W", "D")),
        Team("team_sepahan", "Sepahan SC", "سپاهان اصفهان", "league_persian", "https://media.api-sports.io/football/teams/3523.png", 3, 23, 14, 4, 5, 44, 21, 46, listOf("L", "W", "W", "D", "W")),
        Team("team_tractor", "Tractor SC", "تراکتور تبریز", "league_persian", "https://media.api-sports.io/football/teams/3524.png", 4, 23, 13, 5, 5, 39, 18, 44, listOf("W", "W", "L", "W", "W")),
        Team("team_foolad", "Foolad FC", "فولاد خوزستان", "league_persian", "https://media.api-sports.io/football/teams/3525.png", 5, 23, 10, 7, 6, 28, 22, 37, listOf("D", "W", "D", "L", "W")),
        Team("team_golgohar", "Gol Gohar Sirjan", "گل‌گهر سیرجان", "league_persian", "https://media.api-sports.io/football/teams/3526.png", 6, 23, 9, 8, 6, 25, 20, 35, listOf("D", "D", "W", "D", "L")),
        Team("team_malavan", "Malavan Bandar Anzali", "ملوان انزلی", "league_persian", "https://media.api-sports.io/football/teams/3527.png", 7, 23, 9, 7, 7, 26, 22, 34, listOf("W", "L", "D", "W", "D")),
        Team("team_zobahan", "Zob Ahan Isfahan", "ذوب‌آهن اصفهان", "league_persian", "https://media.api-sports.io/football/teams/3528.png", 8, 23, 8, 9, 6, 24, 23, 33, listOf("D", "W", "L", "D", "D")),

        // Champions League / Europe
        Team("team_bayern", "Bayern Munich", "بایرن مونیخ", "league_bundesliga", "https://media.api-sports.io/football/teams/157.png", 1, 25, 19, 3, 3, 71, 29, 60, listOf("W", "W", "W", "W", "D")),
        Team("team_leverkusen", "Bayer Leverkusen", "بایر لورکوزن", "league_bundesliga", "https://media.api-sports.io/football/teams/168.png", 2, 25, 17, 6, 2, 60, 24, 57, listOf("W", "W", "D", "W", "W")),
        Team("team_dortmund", "Borussia Dortmund", "دورتموند", "league_bundesliga", "https://media.api-sports.io/football/teams/165.png", 3, 25, 15, 5, 5, 52, 31, 50, listOf("W", "L", "W", "W", "D")),
        Team("team_inter", "Inter Milan", "اینتر میلان", "league_seriea", "https://media.api-sports.io/football/teams/505.png", 1, 27, 22, 3, 2, 66, 16, 69, listOf("W", "W", "W", "W", "W")),
        Team("team_juventus", "Juventus", "یوونتوس", "league_seriea", "https://media.api-sports.io/football/teams/496.png", 2, 27, 18, 5, 4, 46, 20, 59, listOf("W", "D", "W", "W", "L")),
        Team("team_milan", "AC Milan", "آث میلان", "league_seriea", "https://media.api-sports.io/football/teams/489.png", 3, 27, 17, 6, 4, 54, 30, 57, listOf("W", "W", "L", "W", "D")),
        Team("team_psg", "Paris Saint-Germain", "پاری‌سن‌ژرمن", "league_ligue1", "https://media.api-sports.io/football/teams/85.png", 1, 25, 18, 5, 2, 63, 21, 59, listOf("W", "W", "W", "D", "W"))
    )

    fun createInitialMatches(): List<Match> {
        val today = getTodayDate()
        val tomorrow = getTomorrowDate()
        val yesterday = getYesterdayDate()

        return listOf(
            // === LIVE MATCHES (در حال برگزاری - شبیه‌ساز زنده با دقایق، رویدادها و آمار کامل) ===
            Match(
                id = "live_ucl_1",
                leagueId = "league_ucl",
                leagueName = "UEFA Champions League",
                leaguePersianName = "لیگ قهرمانان اروپا",
                homeTeamId = "team_realmadrid",
                homeTeamName = "Real Madrid",
                homeTeamPersian = "رئال مادرید",
                homeTeamLogo = "https://media.api-sports.io/football/teams/541.png",
                awayTeamId = "team_mancity",
                awayTeamName = "Manchester City",
                awayTeamPersian = "منچسترسیتی",
                awayTeamLogo = "https://media.api-sports.io/football/teams/50.png",
                status = MatchStatus.LIVE,
                minute = 73,
                homeScore = 2,
                awayScore = 2,
                matchTime = "۲۲:۳۰",
                matchDate = today,
                stadium = "ورزشگاه سانتیاگو برنابئو، مادرید",
                referee = "شیمون مارسینیاک",
                simHomeProb = 36,
                simDrawProb = 33,
                simAwayProb = 31,
                simOverProb = 68,
                simUnderProb = 32,
                stats = MatchStats(51, 49, 7, 6, 8, 5, 12, 10, 2, 1),
                events = listOf(
                    MatchEvent(14, "GOAL", "منچسترسیتی", "ارلینگ هالند", "ضربه سر محکم روی سانتر دی‌بروینه"),
                    MatchEvent(32, "GOAL", "رئال مادرید", "وینیسیوس جونیور", "نفوذ تماشایی از جناح چپ و شوت زاویه بسته"),
                    MatchEvent(41, "CARD_YELLOW", "منچسترسیتی", "رودری", "خطای تاکتیکی در میانه میدان"),
                    MatchEvent(56, "GOAL", "رئال مادرید", "جود بلینگام", "ضربه پای چپ ریباند"),
                    MatchEvent(65, "GOAL", "منچسترسیتی", "کوین دی‌بروینه", "شوت تماشایی از پشت محوطه جریمه")
                )
            ),
            Match(
                id = "live_persian_1",
                leagueId = "league_persian",
                leagueName = "Persian Gulf Pro League",
                leaguePersianName = "لیگ برتر خلیج فارس",
                homeTeamId = "team_persepolis",
                homeTeamName = "Persepolis FC",
                homeTeamPersian = "پرسپولیس تهران",
                homeTeamLogo = "https://media.api-sports.io/football/teams/3522.png",
                awayTeamId = "team_esteghlal",
                awayTeamName = "Esteghlal FC",
                awayTeamPersian = "استقلال تهران",
                awayTeamLogo = "https://media.api-sports.io/football/teams/3521.png",
                status = MatchStatus.LIVE,
                minute = 61,
                homeScore = 1,
                awayScore = 0,
                matchTime = "۱۷:۳۰",
                matchDate = today,
                stadium = "ورزشگاه بزرگ آزادی، تهران",
                referee = "موعود بنیادی‌فر",
                simHomeProb = 48,
                simDrawProb = 32,
                simAwayProb = 20,
                simOverProb = 40,
                simUnderProb = 60,
                stats = MatchStats(56, 44, 5, 3, 6, 3, 14, 16, 2, 3),
                events = listOf(
                    MatchEvent(19, "CARD_YELLOW", "استقلال تهران", "روزبه چشمی", "تکل روی مهاجم حریف"),
                    MatchEvent(38, "GOAL", "پرسپولیس تهران", "اوستون اورونوف", "دریبل زیبا و شوت پای راست به کنج دروازه"),
                    MatchEvent(52, "CARD_YELLOW", "پرسپولیس تهران", "سروش رفیعی", "اعتراض به تصمیم داور")
                )
            ),
            Match(
                id = "live_epl_1",
                leagueId = "league_epl",
                leagueName = "Premier League",
                leaguePersianName = "لیگ برتر انگلیس",
                homeTeamId = "team_arsenal",
                homeTeamName = "Arsenal",
                homeTeamPersian = "آرسنال",
                homeTeamLogo = "https://media.api-sports.io/football/teams/42.png",
                awayTeamId = "team_chelsea",
                awayTeamName = "Chelsea",
                awayTeamPersian = "چلسی",
                awayTeamLogo = "https://media.api-sports.io/football/teams/49.png",
                status = MatchStatus.LIVE,
                minute = 34,
                homeScore = 1,
                awayScore = 1,
                matchTime = "۲۰:۰۰",
                matchDate = today,
                stadium = "ورزشگاه امارات، لندن",
                referee = "آنتونی تیلور",
                simHomeProb = 45,
                simDrawProb = 30,
                simAwayProb = 25,
                simOverProb = 62,
                simUnderProb = 38,
                stats = MatchStats(58, 42, 4, 3, 5, 2, 8, 9, 1, 1),
                events = listOf(
                    MatchEvent(9, "GOAL", "آرسنال", "بوکایو ساکا", "نفوذ سریع و شوت به زاویه مخالف"),
                    MatchEvent(27, "GOAL", "چلسی", "کول پالمر", "ضربه پنالتی دقیق")
                )
            ),
            Match(
                id = "live_seriea_1",
                leagueId = "league_seriea",
                leagueName = "Serie A",
                leaguePersianName = "سری آ ایتالیا",
                homeTeamId = "team_inter",
                homeTeamName = "Inter Milan",
                homeTeamPersian = "اینتر میلان",
                homeTeamLogo = "https://media.api-sports.io/football/teams/505.png",
                awayTeamId = "team_juventus",
                awayTeamName = "Juventus",
                awayTeamPersian = "یوونتوس",
                awayTeamLogo = "https://media.api-sports.io/football/teams/496.png",
                status = MatchStatus.LIVE,
                minute = 82,
                homeScore = 2,
                awayScore = 1,
                matchTime = "۲۱:۱۵",
                matchDate = today,
                stadium = "ورزشگاه سن سیرو، میلان",
                referee = "دنیله اورساتو",
                simHomeProb = 52,
                simDrawProb = 28,
                simAwayProb = 20,
                simOverProb = 50,
                simUnderProb = 50,
                stats = MatchStats(54, 46, 6, 4, 7, 4, 11, 14, 2, 3),
                events = listOf(
                    MatchEvent(21, "GOAL", "اینتر میلان", "لائوتارو مارتینز", "ضربه سر زیبا"),
                    MatchEvent(58, "GOAL", "یوونتوس", "دوشان ولاهوویچ", "شوت از داخل محوطه جریمه"),
                    MatchEvent(71, "GOAL", "اینتر میلان", "مارکوس تورام", "فرار عالی در ضدحمله")
                )
            ),

            // === UPCOMING MATCHES (پیش‌رو برای امروز و فردا با ضرایب کامل 1xBet) ===
            Match(
                id = "upcoming_laliga_1",
                leagueId = "league_laliga",
                leagueName = "La Liga",
                leaguePersianName = "لالیگا اسپانیا",
                homeTeamId = "team_barcelona",
                homeTeamName = "Barcelona",
                homeTeamPersian = "بارسلونا",
                homeTeamLogo = "https://media.api-sports.io/football/teams/529.png",
                awayTeamId = "team_atletico",
                awayTeamName = "Atletico Madrid",
                awayTeamPersian = "اتلتیکو مادرید",
                awayTeamLogo = "https://media.api-sports.io/football/teams/530.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۲۳:۳۰",
                matchDate = today,
                stadium = "ورزشگاه المپیک لوئیس کمپانی",
                referee = "خسوس خیل مانزانو",
                simHomeProb = 48,
                simDrawProb = 27,
                simAwayProb = 25,
                simOverProb = 58,
                simUnderProb = 42
            ),
            Match(
                id = "upcoming_epl_2",
                leagueId = "league_epl",
                leagueName = "Premier League",
                leaguePersianName = "لیگ برتر انگلیس",
                homeTeamId = "team_liverpool",
                homeTeamName = "Liverpool",
                homeTeamPersian = "لیورپول",
                homeTeamLogo = "https://media.api-sports.io/football/teams/40.png",
                awayTeamId = "team_manunited",
                awayTeamName = "Manchester United",
                awayTeamPersian = "منچستریونایتد",
                awayTeamLogo = "https://media.api-sports.io/football/teams/33.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۱۹:۰۰",
                matchDate = tomorrow,
                stadium = "ورزشگاه آنفیلد، لیورپول",
                referee = "مایکل اولیور",
                simHomeProb = 58,
                simDrawProb = 24,
                simAwayProb = 18,
                simOverProb = 64,
                simUnderProb = 36
            ),
            Match(
                id = "upcoming_persian_2",
                leagueId = "league_persian",
                leagueName = "Persian Gulf Pro League",
                leaguePersianName = "لیگ برتر خلیج فارس",
                homeTeamId = "team_sepahan",
                homeTeamName = "Sepahan SC",
                homeTeamPersian = "سپاهان اصفهان",
                homeTeamLogo = "https://media.api-sports.io/football/teams/3523.png",
                awayTeamId = "team_tractor",
                awayTeamName = "Tractor SC",
                awayTeamPersian = "تراکتور تبریز",
                awayTeamLogo = "https://media.api-sports.io/football/teams/3524.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۱۶:۳۰",
                matchDate = tomorrow,
                stadium = "ورزشگاه نقش جهان اصفهان",
                referee = "بیژن حیدری",
                simHomeProb = 42,
                simDrawProb = 33,
                simAwayProb = 25,
                simOverProb = 46,
                simUnderProb = 54
            ),
            Match(
                id = "upcoming_bundesliga_1",
                leagueId = "league_bundesliga",
                leagueName = "Bundesliga",
                leaguePersianName = "بوندس‌لیگا آلمان",
                homeTeamId = "team_bayern",
                homeTeamName = "Bayern Munich",
                homeTeamPersian = "بایرن مونیخ",
                homeTeamLogo = "https://media.api-sports.io/football/teams/157.png",
                awayTeamId = "team_leverkusen",
                awayTeamName = "Bayer Leverkusen",
                awayTeamPersian = "بایر لورکوزن",
                awayTeamLogo = "https://media.api-sports.io/football/teams/168.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۲۱:۰۰",
                matchDate = tomorrow,
                stadium = "ورزشگاه آلیانتس آرنا، مونیخ",
                referee = "فلیکس زوایر",
                simHomeProb = 51,
                simDrawProb = 26,
                simAwayProb = 23,
                simOverProb = 68,
                simUnderProb = 32
            ),
            Match(
                id = "upcoming_ucl_2",
                leagueId = "league_ucl",
                leagueName = "UEFA Champions League",
                leaguePersianName = "لیگ قهرمانان اروپا",
                homeTeamId = "team_psg",
                homeTeamName = "Paris Saint-Germain",
                homeTeamPersian = "پاری‌سن‌ژرمن",
                homeTeamLogo = "https://media.api-sports.io/football/teams/85.png",
                awayTeamId = "team_dortmund",
                awayTeamName = "Borussia Dortmund",
                awayTeamPersian = "دورتموند",
                awayTeamLogo = "https://media.api-sports.io/football/teams/165.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۲۳:۱۵",
                matchDate = tomorrow,
                stadium = "ورزشگاه پارک د پرنس، پاریس",
                referee = "کلمنت تورپین",
                simHomeProb = 55,
                simDrawProb = 25,
                simAwayProb = 20,
                simOverProb = 65,
                simUnderProb = 35
            ),
            Match(
                id = "upcoming_persian_3",
                leagueId = "league_persian",
                leagueName = "Persian Gulf Pro League",
                leaguePersianName = "لیگ برتر خلیج فارس",
                homeTeamId = "team_foolad",
                homeTeamName = "Foolad FC",
                homeTeamPersian = "فولاد خوزستان",
                homeTeamLogo = "https://media.api-sports.io/football/teams/3525.png",
                awayTeamId = "team_malavan",
                awayTeamName = "Malavan",
                awayTeamPersian = "ملوان انزلی",
                awayTeamLogo = "https://media.api-sports.io/football/teams/3527.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۱۸:۰۰",
                matchDate = tomorrow,
                stadium = "ورزشگاه فولاد آرنا، اهواز",
                referee = "پیام حیدری",
                simHomeProb = 38,
                simDrawProb = 35,
                simAwayProb = 27,
                simOverProb = 35,
                simUnderProb = 65
            ),

            // === FINISHED MATCHES (پایان‌یافته با نتایج قطعی) ===
            Match(
                id = "fin_epl_1",
                leagueId = "league_epl",
                leagueName = "Premier League",
                leaguePersianName = "لیگ برتر انگلیس",
                homeTeamId = "team_mancity",
                homeTeamName = "Manchester City",
                homeTeamPersian = "منچسترسیتی",
                homeTeamLogo = "https://media.api-sports.io/football/teams/50.png",
                awayTeamId = "team_tottenham",
                awayTeamName = "Tottenham Hotspur",
                awayTeamPersian = "تاتنهام",
                awayTeamLogo = "https://media.api-sports.io/football/teams/47.png",
                status = MatchStatus.FINISHED,
                minute = 90,
                homeScore = 3,
                awayScore = 1,
                matchTime = "پایان",
                matchDate = yesterday,
                stadium = "ورزشگاه اتحاد، منچستر",
                referee = "کریس کاوانا",
                simHomeProb = 56,
                simDrawProb = 24,
                simAwayProb = 20,
                simOverProb = 65,
                simUnderProb = 35,
                stats = MatchStats(62, 38, 9, 3, 8, 2, 7, 12, 1, 2),
                events = listOf(
                    MatchEvent(18, "GOAL", "منچسترسیتی", "فیل فودن", "شوت دقیق زمینی"),
                    MatchEvent(43, "GOAL", "تاتنهام", "سون هیونگ مین", "ضدحمله برق‌آسا"),
                    MatchEvent(67, "GOAL", "منچسترسیتی", "ارلینگ هالند", "ضربه سر چکشی"),
                    MatchEvent(89, "GOAL", "منچسترسیتی", "برناردو سیلوا", "پاس‌کاری تکنیکی در محوطه")
                )
            ),
            Match(
                id = "fin_persian_1",
                leagueId = "league_persian",
                leagueName = "Persian Gulf Pro League",
                leaguePersianName = "لیگ برتر خلیج فارس",
                homeTeamId = "team_tractor",
                homeTeamName = "Tractor SC",
                homeTeamPersian = "تراکتور تبریز",
                homeTeamLogo = "https://media.api-sports.io/football/teams/3524.png",
                awayTeamId = "team_golgohar",
                awayTeamName = "Gol Gohar Sirjan",
                awayTeamPersian = "گل‌گهر سیرجان",
                awayTeamLogo = "https://media.api-sports.io/football/teams/3526.png",
                status = MatchStatus.FINISHED,
                minute = 90,
                homeScore = 2,
                awayScore = 0,
                matchTime = "پایان",
                matchDate = yesterday,
                stadium = "ورزشگاه یادگار امام تبریز",
                referee = "امیر عرب‌براقی",
                simHomeProb = 49,
                simDrawProb = 31,
                simAwayProb = 20,
                simOverProb = 45,
                simUnderProb = 55,
                stats = MatchStats(55, 45, 6, 2, 5, 3, 12, 15, 2, 2),
                events = listOf(
                    MatchEvent(29, "GOAL", "تراکتور تبریز", "امیرحسین حسین‌زاده", "شوت فنی زیبا"),
                    MatchEvent(84, "GOAL", "تراکتور تبریز", "تومیسلاو اشترکالی", "ضربه تمام‌کننده تک به تک")
                )
            )
        )
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/data/model/Models.kt
================================================================================

```kotlin
package com.playzone.football.data.model

import com.squareup.moshi.JsonClass

enum class MatchStatus {
    LIVE,
    UPCOMING,
    FINISHED
}

enum class PredictionOutcomeType {
    HOME_WIN,              // 1
    DRAW,                  // X
    AWAY_WIN,              // 2
    DOUBLE_CHANCE_1X,      // 1X
    DOUBLE_CHANCE_12,      // 12
    DOUBLE_CHANCE_X2,      // X2
    OVER_2_5,              // Total Over 2.5
    UNDER_2_5,             // Total Under 2.5
    OVER_1_5,              // Total Over 1.5
    UNDER_1_5,             // Total Under 1.5
    BOTH_TEAMS_SCORE_YES,  // GG
    BOTH_TEAMS_SCORE_NO    // NG
}

enum class PredictionStatus {
    PENDING,
    WON,
    LOST,
    CANCELLED
}

@JsonClass(generateAdapter = true)
data class MatchEvent(
    val minute: Int,
    val type: String, // GOAL, CARD_YELLOW, CARD_RED, SUB, VAR
    val teamName: String,
    val player: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class MatchStats(
    val possessionHome: Int = 52,
    val possessionAway: Int = 48,
    val shotsOnTargetHome: Int = 6,
    val shotsOnTargetAway: Int = 4,
    val cornersHome: Int = 7,
    val cornersAway: Int = 4,
    val foulsHome: Int = 10,
    val foulsAway: Int = 13,
    val yellowCardsHome: Int = 1,
    val yellowCardsAway: Int = 2
)

data class Match(
    val id: String,
    val leagueId: String,
    val leagueName: String,
    val leaguePersianName: String,
    val homeTeamId: String,
    val homeTeamName: String,
    val homeTeamPersian: String,
    val homeTeamLogo: String,
    val awayTeamId: String,
    val awayTeamName: String,
    val awayTeamPersian: String,
    val awayTeamLogo: String,
    val status: MatchStatus,
    val minute: Int,
    val homeScore: Int,
    val awayScore: Int,
    val matchTime: String,
    val matchDate: String,
    val stadium: String,
    val referee: String,
    // Educational simulated probabilities (Sum to 100%)
    val simHomeProb: Int,
    val simDrawProb: Int,
    val simAwayProb: Int,
    val simOverProb: Int = 55,
    val simUnderProb: Int = 45,
    val stats: MatchStats = MatchStats(),
    val events: List<MatchEvent> = emptyList(),
    // Real bookmaker odds (0.0 = not available). Only used for API matches (id starts with "api_").
    val statsLoaded: Boolean = true,
    val statusShort: String = "",
    val oddHome: Double = 0.0,
    val oddDraw: Double = 0.0,
    val oddAway: Double = 0.0,
    val oddDc1x: Double = 0.0,
    val oddDc12: Double = 0.0,
    val oddDcx2: Double = 0.0,
    val oddOver25: Double = 0.0,
    val oddUnder25: Double = 0.0,
    val oddOver15: Double = 0.0,
    val oddUnder15: Double = 0.0,
    val oddBttsYes: Double = 0.0,
    val oddBttsNo: Double = 0.0
) {
    val isReal: Boolean get() = id.startsWith("api_")

    // Professional odds multipliers (Format: 1.85, 3.40, etc.)
    val homeProbMultiplier: Double get() = if (isReal) oddHome else (100.0 / simHomeProb.coerceAtLeast(5) * 100).toInt() / 100.0
    val drawProbMultiplier: Double get() = if (isReal) oddDraw else (100.0 / simDrawProb.coerceAtLeast(5) * 100).toInt() / 100.0
    val awayProbMultiplier: Double get() = if (isReal) oddAway else (100.0 / simAwayProb.coerceAtLeast(5) * 100).toInt() / 100.0

    // Double chance
    val dc1XMultiplier: Double get() = if (isReal) oddDc1x else ((100.0 / (simHomeProb + simDrawProb).coerceAtLeast(10)) * 100).toInt() / 100.0
    val dc12Multiplier: Double get() = if (isReal) oddDc12 else ((100.0 / (simHomeProb + simAwayProb).coerceAtLeast(10)) * 100).toInt() / 100.0
    val dcX2Multiplier: Double get() = if (isReal) oddDcx2 else ((100.0 / (simDrawProb + simAwayProb).coerceAtLeast(10)) * 100).toInt() / 100.0

    // Totals
    val over25Multiplier: Double get() = if (isReal) oddOver25 else (100.0 / simOverProb.coerceAtLeast(10) * 100).toInt() / 100.0
    val under25Multiplier: Double get() = if (isReal) oddUnder25 else (100.0 / simUnderProb.coerceAtLeast(10) * 100).toInt() / 100.0

    // Both Teams to Score (GG / NG)
    val bttsYesMultiplier: Double get() = if (isReal) oddBttsYes else 1.82
    val bttsNoMultiplier: Double get() = if (isReal) oddBttsNo else 1.98

    val over15Multiplier: Double get() = if (isReal) oddOver15 else 1.35
    val under15Multiplier: Double get() = if (isReal) oddUnder15 else 3.10

    /** Odds for an outcome; 0.0 means the market is not available. */
    fun oddFor(type: PredictionOutcomeType): Double = when (type) {
        PredictionOutcomeType.HOME_WIN -> homeProbMultiplier
        PredictionOutcomeType.DRAW -> drawProbMultiplier
        PredictionOutcomeType.AWAY_WIN -> awayProbMultiplier
        PredictionOutcomeType.DOUBLE_CHANCE_1X -> dc1XMultiplier
        PredictionOutcomeType.DOUBLE_CHANCE_12 -> dc12Multiplier
        PredictionOutcomeType.DOUBLE_CHANCE_X2 -> dcX2Multiplier
        PredictionOutcomeType.OVER_2_5 -> over25Multiplier
        PredictionOutcomeType.UNDER_2_5 -> under25Multiplier
        PredictionOutcomeType.OVER_1_5 -> over15Multiplier
        PredictionOutcomeType.UNDER_1_5 -> under15Multiplier
        PredictionOutcomeType.BOTH_TEAMS_SCORE_YES -> bttsYesMultiplier
        PredictionOutcomeType.BOTH_TEAMS_SCORE_NO -> bttsNoMultiplier
    }
}

data class Team(
    val id: String,
    val name: String,
    val persianName: String,
    val leagueId: String,
    val logoUrl: String,
    val rank: Int,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val points: Int,
    val form: List<String> // e.g. ["W", "W", "D", "L", "W"]
)

data class League(
    val id: String,
    val name: String,
    val persianName: String,
    val country: String,
    val flagEmoji: String,
    val logoUrl: String,
    val isHot: Boolean = false
)

data class Prediction(
    val id: Long = 0,
    val matchId: String,
    val matchTitle: String,
    val outcomeType: PredictionOutcomeType,
    val outcomeLabel: String,
    val simProbability: Int,
    val oddsMultiplier: Double = 1.85,
    val stakeCredits: Long,
    val potentialReturn: Long,
    val status: PredictionStatus = PredictionStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)

data class Wallet(
    val balance: Long = 100_000,
    val initialBalance: Long = 100_000,
    val totalStaked: Long = 0,
    val totalWon: Long = 0,
    val predictionsCount: Int = 0,
    val wonCount: Int = 0,
    val lostCount: Int = 0
) {
    val winRate: Float
        get() = if (predictionsCount > 0) (wonCount.toFloat() / predictionsCount) * 100f else 0f

    val netProfit: Long
        get() = totalWon - totalStaked
}

data class ResponsibleSettings(
    val sessionTimerMinutes: Int = 0,
    val dailyLimitMinutes: Int = 60,
    val isPaused: Boolean = false,
    val pauseUntil: Long = 0L,
    val totalSessionMinutesToday: Int = 12
)
```


================================================================================
FILE: app/src/main/java/com/playzone/football/data/remote/FootballApiService.kt
================================================================================

```kotlin
package com.playzone.football.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface FootballApiService {
    @GET("v3/fixtures")
    suspend fun getLiveFixtures(
        @Header("x-apisports-key") apiKey: String,
        @Query("live") live: String = "all"
    ): ApiFootballResponse<ApiFixtureItem>

    @GET("v3/fixtures")
    suspend fun getFixturesByDate(
        @Header("x-apisports-key") apiKey: String,
        @Query("date") date: String,
        @Query("timezone") timezone: String? = null
    ): ApiFootballResponse<ApiFixtureItem>

    // ids = "123-456-789" (max 20)
    @GET("v3/fixtures")
    suspend fun getFixturesByIds(
        @Header("x-apisports-key") apiKey: String,
        @Query("ids") ids: String,
        @Query("timezone") timezone: String? = null
    ): ApiFootballResponse<ApiFixtureItem>

    // One fixture including events and statistics
    @GET("v3/fixtures")
    suspend fun getFixtureDetail(
        @Header("x-apisports-key") apiKey: String,
        @Query("id") id: Long,
        @Query("timezone") timezone: String? = null
    ): ApiFootballResponse<ApiFixtureItem>

    @GET("v3/odds")
    suspend fun getOddsByDate(
        @Header("x-apisports-key") apiKey: String,
        @Query("date") date: String,
        @Query("bookmaker") bookmaker: Int,
        @Query("page") page: Int,
        @Query("timezone") timezone: String? = null
    ): ApiFootballResponse<ApiOddsItem>

    @GET("v3/odds/live")
    suspend fun getLiveOdds(
        @Header("x-apisports-key") apiKey: String
    ): ApiFootballResponse<ApiLiveOddsItem>

    @GET("v3/status")
    suspend fun getApiStatus(
        @Header("x-apisports-key") apiKey: String
    ): ApiStatusResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://v3.football.api-sports.io/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val apiService: FootballApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(FootballApiService::class.java)
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/data/remote/RemoteModels.kt
================================================================================

```kotlin
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
```


================================================================================
FILE: app/src/main/java/com/playzone/football/data/repository/ApiSyncEngine.kt
================================================================================

```kotlin
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
```


================================================================================
FILE: app/src/main/java/com/playzone/football/data/repository/Repositories.kt
================================================================================

```kotlin
package com.playzone.football.data.repository

import com.playzone.football.BuildConfig
import com.playzone.football.data.local.AppDatabase
import com.playzone.football.data.local.MatchEntity
import com.playzone.football.data.local.PredictionEntity
import com.playzone.football.data.local.ResponsibleSettingsEntity
import com.playzone.football.data.local.UserSettingsEntity
import com.playzone.football.data.local.WalletEntity
import com.playzone.football.data.mock.SampleFootballData
import com.playzone.football.data.model.League
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchEvent
import com.playzone.football.data.model.MatchStats
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.Prediction
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.PredictionStatus
import com.playzone.football.data.model.ResponsibleSettings
import com.playzone.football.data.model.Team
import com.playzone.football.data.model.Wallet
import com.playzone.football.data.remote.RetrofitClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FootballRepository(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val eventListType = Types.newParameterizedType(List::class.java, MatchEvent::class.java)
    private val eventAdapter = moshi.adapter<List<MatchEvent>>(eventListType)
    private val statsAdapter = moshi.adapter(MatchStats::class.java)

    private val engine = ApiSyncEngine(database)

    private val _isLiveApiActive = MutableStateFlow(false)
    val isLiveApiActive: StateFlow<Boolean> = _isLiveApiActive.asStateFlow()

    private val _apiStatusMessage = MutableStateFlow("شبیه‌ساز مسابقات فعال است")
    val apiStatusMessage: StateFlow<String> = _apiStatusMessage.asStateFlow()

    /** True when real API data is used (no demo matches at all). */
    private val _isRealMode = MutableStateFlow(false)
    val isRealMode: StateFlow<Boolean> = _isRealMode.asStateFlow()

    private val _syncMessages = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val syncMessages: SharedFlow<String> = _syncMessages.asSharedFlow()

    @Volatile private var syncJob: Job? = null

    val leagues: List<League> = SampleFootballData.sampleLeagues
    val teams: List<Team> = SampleFootballData.sampleTeams

    init {
        scope.launch { startup() }
    }

    private fun isUsableKey(key: String): Boolean =
        key.isNotBlank() && !key.startsWith("demo") && key != "MY_API_FOOTBALL_KEY"

    /** Key typed in Settings wins, otherwise the one baked in from .env. */
    private suspend fun effectiveKey(): String = withContext(Dispatchers.IO) {
        val saved = database.userSettingsDao().getSettings()?.apiFootballKey?.trim().orEmpty()
        if (isUsableKey(saved)) saved else BuildConfig.API_FOOTBALL_KEY.trim()
    }

    private suspend fun startup() {
        val key = effectiveKey()
        if (!isUsableKey(key)) {
            // No key at all: offline demo mode (sample data + local ticker)
            initSampleDataIfNeeded()
            startLiveTickerSimulator()
            return
        }
        withContext(Dispatchers.IO) { database.matchDao().deleteDemoMatches() }
        _isRealMode.value = true
        startSync(key)
    }

    private fun startSync(key: String) {
        syncJob?.cancel()
        _isLiveApiActive.value = true
        _apiStatusMessage.value = "در حال اتصال به API-Football…"
        syncJob = scope.launch(Dispatchers.IO) {
            syncCycle(key, firstRun = true)
            while (isActive) {
                delay(engine.liveIntervalMs)
                syncCycle(key, firstRun = false)
            }
        }
    }

    /** One manual refresh (pull-to-refresh / button). */
    fun refreshNow() {
        scope.launch(Dispatchers.IO) {
            val key = effectiveKey()
            if (isUsableKey(key)) syncCycle(key, firstRun = true)
        }
    }

    private suspend fun syncCycle(key: String, firstRun: Boolean) {
        val budgetError = engine.refreshBudget(key)
        if (budgetError != null) {
            _apiStatusMessage.value = budgetError
            return
        }
        val errors = mutableListOf<String>()
        engine.syncFixtures(key, force = firstRun)?.let { errors.add(it) }
        engine.syncOdds(key, force = false)?.let { errors.add(it) }
        var settled = 0
        engine.syncLive(key) { settled += it }?.let { errors.add(it) }
        if (settled > 0) {
            _syncMessages.tryEmit("$settled پیش‌بینی بر اساس نتیجه واقعی بازی تسویه شد")
        }
        val remaining = engine.remainingCalls.coerceAtLeast(0)
        _apiStatusMessage.value = if (errors.isEmpty()) {
            "متصل به API-Football (سهمیه باقی‌مانده امروز: $remaining)"
        } else {
            errors.distinct().joinToString(" | ")
        }
        try {
            val current = database.userSettingsDao().getSettings() ?: UserSettingsEntity()
            database.userSettingsDao().saveSettings(
                current.copy(apiRemainingCalls = remaining, lastSyncTime = "همین الان")
            )
        } catch (_: Exception) {
        }
    }

    /** Loads events and statistics when a match page opens (1 API call). */
    fun loadMatchDetail(matchId: String) {
        if (!matchId.startsWith("api_")) return
        scope.launch(Dispatchers.IO) {
            val key = effectiveKey()
            if (isUsableKey(key)) engine.loadDetail(key, matchId)
        }
    }

    private suspend fun initSampleDataIfNeeded() = withContext(Dispatchers.IO) {
        val initialMatches = SampleFootballData.createInitialMatches()
        val existing = database.matchDao().getMatchById(initialMatches.first().id)
        if (existing == null) {
            val entities = initialMatches.map { matchToEntity(it) }
            database.matchDao().insertMatches(entities)
        }
    }

    fun getAllMatchesFlow(): Flow<List<Match>> {
        return database.matchDao().getAllMatches().map { entities ->
            val api = entities.filter { it.id.startsWith("api_") }
            when {
                api.isNotEmpty() || _isRealMode.value ->
                    api.filter { e ->
                        e.statusShort !in ApiSyncEngine.CANCEL_CODES &&
                            (e.status != MatchStatus.UPCOMING || e.oddHome > 0.0)
                    }.map { entityToMatch(it) }
                entities.isEmpty() -> SampleFootballData.createInitialMatches()
                else -> entities.map { entityToMatch(it) }
            }
        }
    }

    suspend fun getMatchById(id: String): Match? = withContext(Dispatchers.IO) {
        val entity = database.matchDao().getMatchById(id)
        entity?.let { entityToMatch(it) }
            ?: if (_isRealMode.value) null else SampleFootballData.createInitialMatches().find { it.id == id }
    }

    fun getTeamById(id: String): Team? {
        return teams.find { it.id == id }
    }

    fun getLeagueById(id: String): League? {
        return leagues.find { it.id == id }
    }

    fun getTeamsForLeague(leagueId: String): List<Team> {
        return teams.filter { it.leagueId == leagueId }.sortedBy { it.rank }
    }

    // Save user custom API key and optionally connect
    suspend fun saveApiConfig(apiKey: String, useLive: Boolean): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                val current = database.userSettingsDao().getSettings() ?: UserSettingsEntity()
                database.userSettingsDao().saveSettings(
                    current.copy(apiFootballKey = "", useLiveApi = false, customApiConnected = false)
                )
                _isLiveApiActive.value = false
                _apiStatusMessage.value = "شبیه‌ساز مسابقات فعال است"
                return@withContext Result.success("تنظیمات شبیه‌ساز ذخیره گردید.")
            }

            // Test connection using API-Football status endpoint
            val statusResponse = try {
                RetrofitClient.apiService.getApiStatus(apiKey.trim())
            } catch (e: Exception) {
                null
            }

            val remainingCalls = statusResponse?.response?.requests?.let {
                (it.limitDay ?: 100) - (it.current ?: 0)
            } ?: 95

            val newSettings = UserSettingsEntity(
                apiFootballKey = apiKey.trim(),
                useLiveApi = useLive,
                customApiConnected = true,
                apiRemainingCalls = remainingCalls,
                lastSyncTime = "همین الان"
            )
            database.userSettingsDao().saveSettings(newSettings)
            _isLiveApiActive.value = useLive
            _apiStatusMessage.value = "متصل به سرور API-Football ($remainingCalls درخواست باقی‌مانده)"

            if (useLive) {
                withContext(Dispatchers.IO) { database.matchDao().deleteDemoMatches() }
                _isRealMode.value = true
                startSync(apiKey.trim())
            }

            Result.success("کلید API با موفقیت متصل و ذخیره شد ($remainingCalls درخواست باقی‌مانده).")
        } catch (e: Exception) {
            Result.failure(Exception("خطا در اعتبارسنجی کلید API: ${e.localizedMessage ?: "عدم ارتباط با سرور"}"))
        }
    }

    suspend fun getUserSettings(): UserSettingsEntity = withContext(Dispatchers.IO) {
        database.userSettingsDao().getSettings() ?: UserSettingsEntity()
    }

    fun getUserSettingsFlow(): Flow<UserSettingsEntity> {
        return database.userSettingsDao().getSettingsFlow().map { it ?: UserSettingsEntity() }
    }

    // Ticker simulator that periodically advances live match minutes and generates events
    private fun startLiveTickerSimulator() {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(10_000) // update every 10 seconds
                try {
                    val liveMatch = database.matchDao().getMatchById("live_ucl_1")
                    if (liveMatch != null && liveMatch.status == MatchStatus.LIVE) {
                        val currentMin = if (liveMatch.minute < 90) liveMatch.minute + 1 else 90
                        val updated = liveMatch.copy(minute = currentMin)
                        database.matchDao().updateMatch(updated)
                    }

                    val liveMatch2 = database.matchDao().getMatchById("live_persian_1")
                    if (liveMatch2 != null && liveMatch2.status == MatchStatus.LIVE) {
                        val currentMin = if (liveMatch2.minute < 90) liveMatch2.minute + 1 else 90
                        val updated = liveMatch2.copy(minute = currentMin)
                        database.matchDao().updateMatch(updated)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun matchToEntity(m: Match): MatchEntity {
        val eventsJson = try {
            eventAdapter.toJson(m.events)
        } catch (_: Exception) {
            "[]"
        }
        val statsJson = try {
            statsAdapter.toJson(m.stats)
        } catch (_: Exception) {
            "{}"
        }
        return MatchEntity(
            id = m.id,
            leagueId = m.leagueId,
            leagueName = m.leagueName,
            leaguePersianName = m.leaguePersianName,
            homeTeamId = m.homeTeamId,
            homeTeamName = m.homeTeamName,
            homeTeamPersian = m.homeTeamPersian,
            homeTeamLogo = m.homeTeamLogo,
            awayTeamId = m.awayTeamId,
            awayTeamName = m.awayTeamName,
            awayTeamPersian = m.awayTeamPersian,
            awayTeamLogo = m.awayTeamLogo,
            status = m.status,
            minute = m.minute,
            homeScore = m.homeScore,
            awayScore = m.awayScore,
            matchTime = m.matchTime,
            matchDate = m.matchDate,
            stadium = m.stadium,
            referee = m.referee,
            simHomeProb = m.simHomeProb,
            simDrawProb = m.simDrawProb,
            simAwayProb = m.simAwayProb,
            simOverProb = m.simOverProb,
            simUnderProb = m.simUnderProb,
            eventsJson = eventsJson,
            statsJson = statsJson
        )
    }

    private fun entityToMatch(e: MatchEntity): Match {
        val events = try {
            eventAdapter.fromJson(e.eventsJson) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
        val stats = try {
            statsAdapter.fromJson(e.statsJson) ?: MatchStats()
        } catch (_: Exception) {
            MatchStats()
        }
        return Match(
            id = e.id,
            leagueId = e.leagueId,
            leagueName = e.leagueName,
            leaguePersianName = e.leaguePersianName,
            homeTeamId = e.homeTeamId,
            homeTeamName = e.homeTeamName,
            homeTeamPersian = e.homeTeamPersian,
            homeTeamLogo = e.homeTeamLogo,
            awayTeamId = e.awayTeamId,
            awayTeamName = e.awayTeamName,
            awayTeamPersian = e.awayTeamPersian,
            awayTeamLogo = e.awayTeamLogo,
            status = e.status,
            minute = e.minute,
            homeScore = e.homeScore,
            awayScore = e.awayScore,
            matchTime = e.matchTime,
            matchDate = e.matchDate,
            stadium = e.stadium,
            referee = e.referee,
            simHomeProb = e.simHomeProb,
            simDrawProb = e.simDrawProb,
            simAwayProb = e.simAwayProb,
            simOverProb = e.simOverProb,
            simUnderProb = e.simUnderProb,
            stats = stats,
            events = events,
            statsLoaded = e.statsJson.contains("possessionHome"),
            statusShort = e.statusShort,
            oddHome = e.oddHome,
            oddDraw = e.oddDraw,
            oddAway = e.oddAway,
            oddDc1x = e.oddDc1x,
            oddDc12 = e.oddDc12,
            oddDcx2 = e.oddDcx2,
            oddOver25 = e.oddOver25,
            oddUnder25 = e.oddUnder25,
            oddOver15 = e.oddOver15,
            oddUnder15 = e.oddUnder15,
            oddBttsYes = e.oddBttsYes,
            oddBttsNo = e.oddBttsNo
        )
    }
}

class PredictionRepository(
    private val database: AppDatabase
) {
    suspend fun getWallet(): Wallet = withContext(Dispatchers.IO) {
        val entity = database.walletDao().getWallet()
        if (entity == null) {
            val initial = WalletEntity()
            database.walletDao().saveWallet(initial)
            Wallet()
        } else {
            Wallet(
                balance = entity.balance,
                initialBalance = entity.initialBalance,
                totalStaked = entity.totalStaked,
                totalWon = entity.totalWon,
                predictionsCount = entity.predictionsCount,
                wonCount = entity.wonCount,
                lostCount = entity.lostCount
            )
        }
    }

    fun getWalletFlow(): Flow<Wallet> {
        return database.walletDao().getWalletFlow().map { entity ->
            if (entity == null) {
                Wallet()
            } else {
                Wallet(
                    balance = entity.balance,
                    initialBalance = entity.initialBalance,
                    totalStaked = entity.totalStaked,
                    totalWon = entity.totalWon,
                    predictionsCount = entity.predictionsCount,
                    wonCount = entity.wonCount,
                    lostCount = entity.lostCount
                )
            }
        }
    }

    fun getPredictionsFlow(): Flow<List<Prediction>> {
        return database.predictionDao().getAllPredictions().map { list ->
            list.map {
                Prediction(
                    id = it.id,
                    matchId = it.matchId,
                    matchTitle = it.matchTitle,
                    outcomeType = it.outcomeType,
                    outcomeLabel = it.outcomeLabel,
                    simProbability = it.simProbability,
                    oddsMultiplier = it.oddsMultiplier,
                    stakeCredits = it.stakeCredits,
                    potentialReturn = it.potentialReturn,
                    status = it.status,
                    createdAt = it.createdAt,
                    resolvedAt = it.resolvedAt
                )
            }
        }
    }

    suspend fun placePrediction(
        match: Match,
        outcomeType: PredictionOutcomeType,
        stakeCredits: Long
    ): Result<Long> = WalletLock.mutex.withLock { placePredictionInternal(match, outcomeType, stakeCredits) }

    private suspend fun placePredictionInternal(
        match: Match,
        outcomeType: PredictionOutcomeType,
        stakeCredits: Long
    ): Result<Long> = withContext(Dispatchers.IO) {
        if (match.status == MatchStatus.FINISHED) {
            return@withContext Result.failure(Exception("این بازی به پایان رسیده است."))
        }
        val wallet = database.walletDao().getWallet() ?: WalletEntity()
        if (wallet.balance < stakeCredits) {
            return@withContext Result.failure(Exception("موجودی اعتبار مجازی کافی نیست. لطفاً کیف پول خود را شارژ کنید."))
        }

        val multiplier = match.oddFor(outcomeType)
        if (multiplier <= 1.0) {
            return@withContext Result.failure(Exception("ضریب این بازار در حال حاضر در دسترس نیست."))
        }

        val prob = when (outcomeType) {
            PredictionOutcomeType.HOME_WIN -> match.simHomeProb
            PredictionOutcomeType.DRAW -> match.simDrawProb
            PredictionOutcomeType.AWAY_WIN -> match.simAwayProb
            PredictionOutcomeType.DOUBLE_CHANCE_1X -> match.simHomeProb + match.simDrawProb
            PredictionOutcomeType.DOUBLE_CHANCE_12 -> match.simHomeProb + match.simAwayProb
            PredictionOutcomeType.DOUBLE_CHANCE_X2 -> match.simDrawProb + match.simAwayProb
            PredictionOutcomeType.OVER_2_5 -> match.simOverProb
            PredictionOutcomeType.UNDER_2_5 -> match.simUnderProb
            else -> 50
        }

        val label = when (outcomeType) {
            PredictionOutcomeType.HOME_WIN -> "برد ${match.homeTeamPersian} (۱)"
            PredictionOutcomeType.DRAW -> "تساوی (X)"
            PredictionOutcomeType.AWAY_WIN -> "برد ${match.awayTeamPersian} (۲)"
            PredictionOutcomeType.DOUBLE_CHANCE_1X -> "شانس دوبرابر: ${match.homeTeamPersian} یا مساوی (1X)"
            PredictionOutcomeType.DOUBLE_CHANCE_12 -> "شانس دوبرابر: برد یکی از دو تیم (12)"
            PredictionOutcomeType.DOUBLE_CHANCE_X2 -> "شانس دوبرابر: مساوی یا ${match.awayTeamPersian} (X2)"
            PredictionOutcomeType.OVER_2_5 -> "مجموع گل‌ها بیشتر از ۲.۵"
            PredictionOutcomeType.UNDER_2_5 -> "مجموع گل‌ها کمتر از ۲.۵"
            PredictionOutcomeType.OVER_1_5 -> "مجموع گل‌ها بیشتر از ۱.۵"
            PredictionOutcomeType.UNDER_1_5 -> "مجموع گل‌ها کمتر از ۱.۵"
            PredictionOutcomeType.BOTH_TEAMS_SCORE_YES -> "هر دو تیم گل می‌زنند: بله (GG)"
            PredictionOutcomeType.BOTH_TEAMS_SCORE_NO -> "هر دو تیم گل می‌زنند: خیر (NG)"
        }

        val potentialReturn = (stakeCredits * multiplier).toLong()

        // Deduct stake from wallet
        val updatedWallet = wallet.copy(
            balance = wallet.balance - stakeCredits,
            totalStaked = wallet.totalStaked + stakeCredits,
            predictionsCount = wallet.predictionsCount + 1
        )
        database.walletDao().saveWallet(updatedWallet)

        val entity = PredictionEntity(
            matchId = match.id,
            matchTitle = "${match.homeTeamPersian} - ${match.awayTeamPersian}",
            outcomeType = outcomeType,
            outcomeLabel = label,
            simProbability = prob,
            oddsMultiplier = multiplier,
            stakeCredits = stakeCredits,
            potentialReturn = potentialReturn,
            status = PredictionStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            resolvedAt = null
        )
        val id = database.predictionDao().insertPrediction(entity)
        Result.success(id)
    }

    suspend fun resolvePrediction(
        predictionId: Long,
        didWin: Boolean
    ) = WalletLock.mutex.withLock { resolvePredictionInternal(predictionId, didWin) }

    private suspend fun resolvePredictionInternal(
        predictionId: Long,
        didWin: Boolean
    ) = withContext(Dispatchers.IO) {
        val pred = database.predictionDao().getPredictionById(predictionId) ?: return@withContext
        if (pred.status != PredictionStatus.PENDING) return@withContext

        val newStatus = if (didWin) PredictionStatus.WON else PredictionStatus.LOST
        database.predictionDao().updatePrediction(
            pred.copy(status = newStatus, resolvedAt = System.currentTimeMillis())
        )

        val wallet = database.walletDao().getWallet() ?: WalletEntity()
        if (didWin) {
            val updated = wallet.copy(
                balance = wallet.balance + pred.potentialReturn,
                totalWon = wallet.totalWon + pred.potentialReturn,
                wonCount = wallet.wonCount + 1
            )
            database.walletDao().saveWallet(updated)
        } else {
            val updated = wallet.copy(
                lostCount = wallet.lostCount + 1
            )
            database.walletDao().saveWallet(updated)
        }
    }

    // Set custom balance chosen by the user
    suspend fun setCustomBalance(amount: Long) = WalletLock.mutex.withLock {
        withContext(Dispatchers.IO) {
        val existing = database.walletDao().getWallet() ?: WalletEntity()
        val updated = existing.copy(balance = amount.coerceAtLeast(1_000L))
        database.walletDao().saveWallet(updated)
        }
    }

    suspend fun resetVirtualBalance() = withContext(Dispatchers.IO) {
        setCustomBalance(100_000L)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        database.predictionDao().clearAll()
        database.walletDao().clearWallet()
        database.walletDao().saveWallet(WalletEntity())
    }
}

class ResponsibleGamingRepository(
    private val database: AppDatabase
) {
    fun getSettingsFlow(): Flow<ResponsibleSettings> {
        return database.responsibleSettingsDao().getSettingsFlow().map { entity ->
            if (entity == null) {
                ResponsibleSettings()
            } else {
                val now = System.currentTimeMillis()
                val isCurrentlyPaused = entity.isPaused && (entity.pauseUntil == 0L || entity.pauseUntil > now)
                ResponsibleSettings(
                    dailyLimitMinutes = entity.dailyLimitMinutes,
                    totalSessionMinutesToday = entity.totalMinutesToday,
                    isPaused = isCurrentlyPaused,
                    pauseUntil = entity.pauseUntil
                )
            }
        }
    }

    suspend fun updateDailyLimit(minutes: Int) = withContext(Dispatchers.IO) {
        val current = database.responsibleSettingsDao().getSettings() ?: ResponsibleSettingsEntity()
        database.responsibleSettingsDao().saveSettings(current.copy(dailyLimitMinutes = minutes))
    }

    suspend fun setPauseSimulator(durationHours: Int) = withContext(Dispatchers.IO) {
        val current = database.responsibleSettingsDao().getSettings() ?: ResponsibleSettingsEntity()
        val pauseUntil = if (durationHours > 0) {
            System.currentTimeMillis() + (durationHours * 3600 * 1000L)
        } else {
            0L
        }
        database.responsibleSettingsDao().saveSettings(
            current.copy(
                isPaused = durationHours > 0,
                pauseUntil = pauseUntil
            )
        )
    }

    suspend fun resumeSimulator() = withContext(Dispatchers.IO) {
        val current = database.responsibleSettingsDao().getSettings() ?: ResponsibleSettingsEntity()
        database.responsibleSettingsDao().saveSettings(
            current.copy(isPaused = false, pauseUntil = 0L)
        )
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/components/CommonComponents.kt
================================================================================

```kotlin
package com.playzone.football.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.playzone.football.data.model.League
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.ui.theme.*

@Composable
fun PermanentNoticeBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("permanent_notice_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132238)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(Color(0xFF243B55), Color(0xFF141E30)))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00B4D8).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "قوانین شبیه‌ساز",
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "شبیه‌ساز تخصصی مسابقات فوتبال PlayZone",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "بازی‌ها و ضرایب از منبع واقعی (API-Football) گرفته می‌شوند؛ ولی اعتبارات کاملاً مجازی‌اند و ارزش مادی یا امکان برداشت و واریز ندارند.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrayLight,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun PlayZoneTopBar(
    virtualBalance: Long,
    isLiveApi: Boolean,
    onWalletClick: () -> Unit,
    onResponsibleClick: () -> Unit
) {
    Surface(
        color = NavyDarkest,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("app_logo_title")
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF00E676).copy(alpha = 0.15f))
                        .border(1.dp, GreenAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "PlayZone",
                        tint = GreenAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PlayZone",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextWhite,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // API / Simulator Status Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isLiveApi) GreenContainer else NavyCardElevated
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isLiveApi) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = if (isLiveApi) GreenAccent else CyanAccent,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isLiveApi) "API آنلاین" else "شبیه‌ساز",
                                    fontSize = 9.sp,
                                    color = if (isLiveApi) GreenAccent else CyanAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = "شبیه‌ساز حرفه‌ای مسابقات فوتبال",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGrayMuted
                    )
                }
            }

            // Right side: Balance badge and Safety button
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onResponsibleClick,
                    modifier = Modifier
                        .testTag("btn_responsible_topbar")
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "مدیریت زمان",
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))

                // Virtual Credit Pill
                Surface(
                    onClick = onWalletClick,
                    shape = RoundedCornerShape(20.dp),
                    color = NavyCardElevated,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(GreenAccent, CyanAccent))
                    ),
                    modifier = Modifier.testTag("btn_wallet_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "%,d".format(virtualBalance),
                            style = MaterialTheme.typography.labelLarge,
                            color = GreenAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "اعتبار",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextGrayLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBarField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("جستجوی تیم (پرسپولیس، رئال مادرید، آرسنال...) یا لیگ", fontSize = 12.sp, color = TextGrayMuted) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "پاک کردن", tint = TextGrayMuted, modifier = Modifier.size(18.dp))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenAccent,
            unfocusedBorderColor = NavyBorder,
            focusedContainerColor = NavyCard,
            unfocusedContainerColor = NavyCard,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("search_bar_field")
    )
}

@Composable
fun LiveStatusBadge(
    minute: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = LiveRedContainer,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(LiveRed, LiveRed.copy(alpha = 0.6f)))
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(LiveRed)
                    .alpha(alphaAnim)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "زنده $minute'",
                style = MaterialTheme.typography.labelMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LeagueFilterBar(
    leagues: List<League>,
    selectedLeagueId: String?,
    onLeagueSelect: (String?) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedLeagueId == null,
            onClick = { onLeagueSelect(null) },
            label = { Text("همه لیگ‌ها", style = MaterialTheme.typography.labelMedium) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GreenAccent,
                selectedLabelColor = NavyDarkest,
                containerColor = NavyCard,
                labelColor = TextWhite
            ),
            modifier = Modifier.testTag("filter_all_leagues")
        )
        leagues.forEach { league ->
            FilterChip(
                selected = selectedLeagueId == league.id,
                onClick = { onLeagueSelect(league.id) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = league.flagEmoji, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = league.persianName,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GreenAccent,
                    selectedLabelColor = NavyDarkest,
                    containerColor = NavyCard,
                    labelColor = TextWhite
                ),
                modifier = Modifier.testTag("filter_league_${league.id}")
            )
        }
    }
}

// 1xBet Style Match Card
@Composable
fun MatchCard(
    match: Match,
    onMatchClick: () -> Unit,
    onPredictClick: (PredictionOutcomeType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMatchClick() }
            .testTag("match_card_${match.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(listOf(NavyBorder, NavySurface))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: League name & status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = match.leaguePersianName,
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                when (match.status) {
                    MatchStatus.LIVE -> LiveStatusBadge(minute = match.minute)
                    MatchStatus.UPCOMING -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavyCardElevated
                        ) {
                            Text(
                                text = "${match.matchDate} ${match.matchTime}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextGrayLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    MatchStatus.FINISHED -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavyDarkest
                        ) {
                            Text(
                                text = "پایان بازی",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextGrayMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Teams & Score (1xBet style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Home Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = match.homeTeamLogo,
                        contentDescription = match.homeTeamPersian,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NavySurface)
                            .padding(3.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = match.homeTeamPersian,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = match.homeTeamName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayMuted,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }

                // Score / VS Center
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (match.status != MatchStatus.UPCOMING) {
                        Text(
                            text = "${match.homeScore} - ${match.awayScore}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (match.status == MatchStatus.LIVE) GreenAccent else TextWhite,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavySurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = match.matchTime,
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Away Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = match.awayTeamPersian,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = match.awayTeamName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayMuted,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = match.awayTeamLogo,
                        contentDescription = match.awayTeamPersian,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NavySurface)
                            .padding(3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1xBet Betting Markets Grid: Row 1 (1 - X - 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BetOddButton(
                    market = "۱ (میزبان)",
                    odd = "${match.homeProbMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.HOME_WIN) },
                    modifier = Modifier.weight(1f)
                )
                BetOddButton(
                    market = "X (مساوی)",
                    odd = "${match.drawProbMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.DRAW) },
                    modifier = Modifier.weight(1f)
                )
                BetOddButton(
                    market = "۲ (میهمان)",
                    odd = "${match.awayProbMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.AWAY_WIN) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Secondary markets (1X, 12, X2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BetOddButton(
                    market = "1X (شانس ۲)",
                    odd = "${match.dc1XMultiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.DOUBLE_CHANCE_1X) },
                    modifier = Modifier.weight(1f),
                    color = CyanAccent
                )
                BetOddButton(
                    market = "12 (شانس ۲)",
                    odd = "${match.dc12Multiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.DOUBLE_CHANCE_12) },
                    modifier = Modifier.weight(1f),
                    color = CyanAccent
                )
                BetOddButton(
                    market = "X2 (شانس ۲)",
                    odd = "${match.dcX2Multiplier}",
                    onClick = { onPredictClick(PredictionOutcomeType.DOUBLE_CHANCE_X2) },
                    modifier = Modifier.weight(1f),
                    color = CyanAccent
                )
            }
        }
    }
}

@Composable
fun BetOddButton(
    market: String,
    odd: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = GreenAccent
) {
    val oddValue = odd.toDoubleOrNull()
    val available = oddValue == null || oddValue > 1.0
    val oddText = if (available) odd else "—"
    Surface(
        onClick = { if (available) onClick() },
        enabled = available,
        shape = RoundedCornerShape(8.dp),
        color = NavyCardElevated,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(listOf(NavyBorder, Color(0xFF1B2C4B)))
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = market,
                style = MaterialTheme.typography.labelSmall,
                color = TextGrayLight,
                fontSize = 10.sp,
                maxLines = 1
            )
            Text(
                text = oddText,
                style = MaterialTheme.typography.labelLarge,
                color = if (available) color else TextGrayMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/components/VirtualPredictionDialog.kt
================================================================================

```kotlin
package com.playzone.football.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.ui.theme.*

@Composable
fun VirtualPredictionDialog(
    match: Match,
    outcome: PredictionOutcomeType,
    userBalance: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var stakeText by remember { mutableStateOf("10000") }
    val quickStakes = listOf(5_000L, 10_000L, 50_000L, 100_000L, 500_000L)

    val multiplier = match.oddFor(outcome)

    val outcomeTitle = when (outcome) {
        PredictionOutcomeType.HOME_WIN -> "برد ${match.homeTeamPersian} (۱)"
        PredictionOutcomeType.DRAW -> "تساوی دو تیم (X)"
        PredictionOutcomeType.AWAY_WIN -> "برد ${match.awayTeamPersian} (۲)"
        PredictionOutcomeType.DOUBLE_CHANCE_1X -> "برد ${match.homeTeamPersian} یا مساوی (1X)"
        PredictionOutcomeType.DOUBLE_CHANCE_12 -> "برد یکی از دو تیم (12)"
        PredictionOutcomeType.DOUBLE_CHANCE_X2 -> "مساوی یا برد ${match.awayTeamPersian} (X2)"
        PredictionOutcomeType.OVER_2_5 -> "مجموع گل‌ها بیشتر از ۲.۵ (+2.5)"
        PredictionOutcomeType.UNDER_2_5 -> "مجموع گل‌ها کمتر از ۲.۵ (-2.5)"
        PredictionOutcomeType.OVER_1_5 -> "مجموع گل‌ها بیشتر از ۱.۵ (+1.5)"
        PredictionOutcomeType.UNDER_1_5 -> "مجموع گل‌ها کمتر از ۱.۵ (-1.5)"
        PredictionOutcomeType.BOTH_TEAMS_SCORE_YES -> "هر دو تیم گل می‌زنند: بله (GG)"
        PredictionOutcomeType.BOTH_TEAMS_SCORE_NO -> "هر دو تیم گل می‌زنند: خیر (NG)"
    }

    val numericStake = stakeText.toLongOrNull() ?: 0L
    val potentialReturn = (numericStake * multiplier).toLong()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("virtual_prediction_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ثبت برگه پیش‌بینی شبیه‌ساز",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${match.homeTeamPersian} - ${match.awayTeamPersian}",
                    style = MaterialTheme.typography.titleSmall,
                    color = CyanAccent,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Outcome & Odds Summary
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("انتخاب شما:", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            Text(outcomeTitle, style = MaterialTheme.typography.titleSmall, color = GreenAccent, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ضریب شبیه‌ساز:", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            Text("x$multiplier", style = MaterialTheme.typography.titleMedium, color = GoldAccent, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("موجودی شما:", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            Text("%,d اعتبار".format(userBalance), style = MaterialTheme.typography.bodySmall, color = TextWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Stake Input
                Text(
                    text = "مبلغ دلخواه برای پیش‌بینی (اعتبار مجازی):",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextWhite,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = stakeText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            stakeText = input
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("مثال: 50000") },
                    trailingIcon = { Text("اعتبار", color = TextGrayMuted, modifier = Modifier.padding(end = 12.dp), fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenAccent,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Stakes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    quickStakes.forEach { stake ->
                        FilterChip(
                            selected = numericStake == stake,
                            onClick = { stakeText = stake.toString() },
                            label = { Text("%,d".format(stake), fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenAccent,
                                selectedLabelColor = NavyDarkest,
                                containerColor = NavyCardElevated,
                                labelColor = TextWhite
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Potential return
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF003816),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "برد احتمالی مجازی:",
                            style = MaterialTheme.typography.bodySmall,
                            color = GreenOnContainer
                        )
                        Text(
                            text = "%,d اعتبار".format(potentialReturn),
                            style = MaterialTheme.typography.titleLarge,
                            color = GreenAccent,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = TextGrayMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "این اعتبار شبیه‌سازی‌شده است و وجه واقعی پرداخت نمی‌شود.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("انصراف", color = TextGrayLight)
                    }

                    Button(
                        onClick = { onConfirm(numericStake) },
                        enabled = numericStake > 0 && userBalance >= numericStake,
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenAccent,
                            contentColor = NavyDarkest
                        )
                    ) {
                        Text(
                            text = if (userBalance < numericStake) "موجودی ناکافی" else "تأیید پیش‌بینی",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/navigation/Navigation.kt
================================================================================

```kotlin
package com.playzone.football.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.ui.components.PlayZoneTopBar
import com.playzone.football.ui.components.VirtualPredictionDialog
import com.playzone.football.ui.screens.*
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home : Screen("home", "خانه", Icons.Default.Home)
    data object Matches : Screen("matches", "مسابقات", Icons.Default.SportsSoccer)
    data object Predictions : Screen("predictions", "پیش‌بینی‌ها", Icons.Default.History)
    data object Wallet : Screen("wallet", "کیف پول", Icons.Default.AccountBalanceWallet)
    data object Profile : Screen("profile", "پروفایل", Icons.Default.Person)

    data object Leagues : Screen("leagues", "لیگ‌ها")
    data object MatchDetail : Screen("match_detail/{matchId}", "جزئیات مسابقه") {
        fun createRoute(matchId: String) = "match_detail/$matchId"
    }
    data object TeamDetail : Screen("team_detail/{teamId}", "جزئیات تیم") {
        fun createRoute(teamId: String) = "team_detail/$teamId"
    }
    data object Responsible : Screen("responsible", "مدیریت زمان")
    data object Settings : Screen("settings", "تنظیمات")
    data object Privacy : Screen("privacy", "حریم خصوصی")
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val matches by viewModel.filteredMatches.collectAsStateWithLifecycle()
    val rawMatches by viewModel.rawMatches.collectAsStateWithLifecycle()
    val selectedLeagueId by viewModel.selectedLeagueFilter.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.matchStatusFilter.collectAsStateWithLifecycle()
    val predictions by viewModel.predictions.collectAsStateWithLifecycle()
    val responsibleSettings by viewModel.responsibleSettings.collectAsStateWithLifecycle()

    val activePredictionMatch by viewModel.activePredictionMatch.collectAsStateWithLifecycle()
    val activePredictionOutcome by viewModel.activePredictionOutcome.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Listen to messages from ViewModel
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val isLiveApi by viewModel.isLiveApiActive.collectAsStateWithLifecycle()

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Matches,
        Screen.Predictions,
        Screen.Wallet,
        Screen.Profile
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        topBar = {
            if (showBottomBar) {
                PlayZoneTopBar(
                    virtualBalance = wallet.balance,
                    isLiveApi = isLiveApi,
                    onWalletClick = { navController.navigate(Screen.Wallet.route) },
                    onResponsibleClick = { navController.navigate(Screen.Responsible.route) }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = NavyDarkest,
                    contentColor = TextWhite
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                item.icon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GreenAccent,
                                selectedTextColor = GreenAccent,
                                unselectedIconColor = TextGrayMuted,
                                unselectedTextColor = TextGrayMuted,
                                indicatorColor = NavyCardElevated
                            ),
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NavyBackground
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    wallet = wallet,
                    matches = matches,
                    selectedLeagueId = selectedLeagueId,
                    onNavigateToMatch = { matchId ->
                        navController.navigate(Screen.MatchDetail.createRoute(matchId))
                    },
                    onNavigateToMatchesList = { status ->
                        viewModel.selectStatusFilter(status)
                        navController.navigate(Screen.Matches.route)
                    },
                    onNavigateToWallet = {
                        navController.navigate(Screen.Wallet.route)
                    },
                    onNavigateToLeagues = {
                        navController.navigate(Screen.Leagues.route)
                    },
                    onPredictClick = { match, outcome ->
                        viewModel.openPredictionModal(match, outcome)
                    }
                )
            }

            composable(Screen.Matches.route) {
                MatchesScreen(
                    viewModel = viewModel,
                    matches = matches,
                    selectedStatus = selectedStatus,
                    selectedLeagueId = selectedLeagueId,
                    onNavigateToMatch = { matchId ->
                        navController.navigate(Screen.MatchDetail.createRoute(matchId))
                    },
                    onPredictClick = { match, outcome ->
                        viewModel.openPredictionModal(match, outcome)
                    }
                )
            }

            composable(Screen.Predictions.route) {
                PredictionsScreen(
                    viewModel = viewModel,
                    predictions = predictions,
                    onExploreMatchesClick = {
                        navController.navigate(Screen.Matches.route)
                    }
                )
            }

            composable(Screen.Wallet.route) {
                WalletScreen(
                    viewModel = viewModel,
                    wallet = wallet
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    wallet = wallet,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToResponsible = { navController.navigate(Screen.Responsible.route) },
                    onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) }
                )
            }

            composable(Screen.Leagues.route) {
                LeaguesScreen(
                    viewModel = viewModel,
                    onTeamClick = { teamId ->
                        navController.navigate(Screen.TeamDetail.createRoute(teamId))
                    }
                )
            }

            composable(
                route = Screen.MatchDetail.route,
                arguments = listOf(navArgument("matchId") { type = NavType.StringType })
            ) { backStackEntry ->
                val matchId = backStackEntry.arguments?.getString("matchId")
                val match = rawMatches.find { it.id == matchId }
                LaunchedEffect(matchId) {
                    matchId?.let { viewModel.loadMatchDetail(it) }
                }
                MatchDetailScreen(
                    match = match,
                    onBackClick = { navController.popBackStack() },
                    onPredictClick = { m, outcome ->
                        viewModel.openPredictionModal(m, outcome)
                    }
                )
            }

            composable(
                route = Screen.TeamDetail.route,
                arguments = listOf(navArgument("teamId") { type = NavType.StringType })
            ) { backStackEntry ->
                val teamId = backStackEntry.arguments?.getString("teamId")
                val team = viewModel.teams.find { it.id == teamId }
                TeamDetailScreen(
                    team = team,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Responsible.route) {
                ResponsibleGamingScreen(
                    viewModel = viewModel,
                    settings = responsibleSettings,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Privacy.route) {
                PrivacyPolicyScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // Active prediction modal dialog
        if (activePredictionMatch != null && activePredictionOutcome != null) {
            VirtualPredictionDialog(
                match = activePredictionMatch!!,
                outcome = activePredictionOutcome!!,
                userBalance = wallet.balance,
                onDismiss = { viewModel.closePredictionModal() },
                onConfirm = { stake -> viewModel.submitPrediction(stake) }
            )
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/HomeScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.data.model.Wallet
import com.playzone.football.ui.components.BetOddButton
import com.playzone.football.ui.components.LeagueFilterBar
import com.playzone.football.ui.components.LiveStatusBadge
import com.playzone.football.ui.components.MatchCard
import com.playzone.football.ui.components.PermanentNoticeBanner
import com.playzone.football.ui.components.SearchBarField
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    wallet: Wallet,
    matches: List<Match>,
    selectedLeagueId: String?,
    onNavigateToMatch: (String) -> Unit,
    onNavigateToMatchesList: (MatchStatus?) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToLeagues: () -> Unit,
    onPredictClick: (Match, PredictionOutcomeType) -> Unit,
    modifier: Modifier = Modifier
) {
    val liveMatches = matches.filter { it.status == MatchStatus.LIVE }
    val upcomingMatches = matches.filter { it.status == MatchStatus.UPCOMING }
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Permanent Notice Banner
        item {
            PermanentNoticeBanner(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp))
        }

        // Search Bar for matches, teams, and leagues
        item {
            SearchBarField(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Virtual Balance & Custom Setting Shortcut Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_balance_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(GreenAccent.copy(alpha = 0.5f), CyanAccent.copy(alpha = 0.3f)))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "موجودی کیف پول شبیه‌ساز",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "%,d".format(wallet.balance),
                                style = MaterialTheme.typography.displayMedium,
                                color = GreenAccent,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "اعتبار",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextGrayLight
                            )
                        }
                        Text(
                            text = "نرخ برد: ${"%.1f".format(wallet.winRate)}% (${wallet.wonCount} برد از ${wallet.predictionsCount} بازی)",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = onNavigateToWallet,
                            colors = ButtonDefaults.buttonColors(containerColor = NavySurface),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تنظیم موجودی",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextWhite
                            )
                        }
                    }
                }
            }
        }

        // Live Matches Carousel (1xBet Style)
        if (liveMatches.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LiveRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مسابقات زنده در حال برگزاری (${liveMatches.size})",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = { onNavigateToMatchesList(MatchStatus.LIVE) }) {
                        Text("مشاهده همه", color = CyanAccent, style = MaterialTheme.typography.labelMedium)
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(liveMatches) { liveMatch ->
                        Card(
                            modifier = Modifier
                                .width(310.dp)
                                .clickable { onNavigateToMatch(liveMatch.id) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.verticalGradient(listOf(LiveRed.copy(alpha = 0.5f), NavyBorder))
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = liveMatch.leaguePersianName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextGrayMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    LiveStatusBadge(minute = liveMatch.minute)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AsyncImage(
                                            model = liveMatch.homeTeamLogo,
                                            contentDescription = null,
                                            modifier = Modifier.size(30.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = liveMatch.homeTeamPersian,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "${liveMatch.homeScore} - ${liveMatch.awayScore}",
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = GreenAccent,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = liveMatch.awayTeamPersian,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        AsyncImage(
                                            model = liveMatch.awayTeamLogo,
                                            contentDescription = null,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                // 1 - X - 2 Odds Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    BetOddButton(
                                        market = "۱",
                                        odd = "${liveMatch.homeProbMultiplier}",
                                        onClick = { onPredictClick(liveMatch, PredictionOutcomeType.HOME_WIN) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    BetOddButton(
                                        market = "X",
                                        odd = "${liveMatch.drawProbMultiplier}",
                                        onClick = { onPredictClick(liveMatch, PredictionOutcomeType.DRAW) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    BetOddButton(
                                        market = "۲",
                                        odd = "${liveMatch.awayProbMultiplier}",
                                        onClick = { onPredictClick(liveMatch, PredictionOutcomeType.AWAY_WIN) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Leagues Filter Header
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "لیگ‌های معتبر جهان و ایران",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToLeagues) {
                    Text("جدول رده‌بندی", color = CyanAccent, style = MaterialTheme.typography.labelMedium)
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                }
            }
            LeagueFilterBar(
                leagues = viewModel.activeLeagues.collectAsStateWithLifecycle().value,
                selectedLeagueId = selectedLeagueId,
                onLeagueSelect = { viewModel.selectLeagueFilter(it) }
            )
        }

        // Upcoming Matches List (1xBet Pro Style)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مسابقات داغ امروز و فردا",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigateToMatchesList(MatchStatus.UPCOMING) }) {
                    Text("لیست کامل", color = CyanAccent, style = MaterialTheme.typography.labelMedium)
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                }
            }
        }

        items(upcomingMatches.take(6)) { match ->
            MatchCard(
                match = match,
                onMatchClick = { onNavigateToMatch(match.id) },
                onPredictClick = { outcome -> onPredictClick(match, outcome) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/LeaguesScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.playzone.football.data.model.League
import com.playzone.football.data.model.Team
import com.playzone.football.ui.components.LeagueFilterBar
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun LeaguesScreen(
    viewModel: MainViewModel,
    onTeamClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val realMode by viewModel.isRealMode.collectAsStateWithLifecycle()
    if (realMode) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NavyBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "جدول رده‌بندی در حالت داده‌های واقعی هنوز اضافه نشده است.",
                color = TextGrayLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp)
            )
        }
        return
    }
    var selectedLeagueId by remember { mutableStateOf(viewModel.leagues.first().id) }

    val currentLeague = viewModel.leagues.find { it.id == selectedLeagueId } ?: viewModel.leagues.first()
    val teams = remember(selectedLeagueId) {
        viewModel.teams.filter { it.leagueId == selectedLeagueId }.sortedBy { it.rank }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("leagues_screen")
    ) {
        // League selector tabs
        LeagueFilterBar(
            leagues = viewModel.leagues,
            selectedLeagueId = selectedLeagueId,
            onLeagueSelect = { id -> id?.let { selectedLeagueId = it } }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // League Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = currentLeague.logoUrl,
                            contentDescription = currentLeague.persianName,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NavySurface)
                                .padding(6.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = currentLeague.persianName,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentLeague.name} • ${currentLeague.country} ${currentLeague.flagEmoji}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGrayLight
                            )
                        }
                    }
                }
            }

            // Standings Table Header
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NavyDarkest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("#", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.width(24.dp))
                        Text("تیم", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.weight(1f))
                        Text("بازی", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                        Text("برد", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                        Text("مساوی", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                        Text("باخت", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                        Text("امتیاز", style = MaterialTheme.typography.labelSmall, color = GreenAccent, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Table Rows
            items(teams) { team ->
                TeamTableRow(
                    team = team,
                    onClick = { onTeamClick(team.id) }
                )
            }
        }
    }
}

@Composable
private fun TeamTableRow(
    team: Team,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("team_row_${team.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${team.rank}",
                style = MaterialTheme.typography.titleSmall,
                color = if (team.rank <= 3) GreenAccent else TextGrayLight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(24.dp)
            )

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = team.logoUrl,
                    contentDescription = team.persianName,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = team.persianName,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text("${team.played}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
            Text("${team.wins}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
            Text("${team.draws}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
            Text("${team.losses}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
            Text(
                text = "${team.points}",
                style = MaterialTheme.typography.titleMedium,
                color = GreenAccent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/MatchDetailScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchEvent
import com.playzone.football.data.model.MatchStats
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.ui.components.BetOddButton
import com.playzone.football.ui.components.LiveStatusBadge
import com.playzone.football.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    match: Match?,
    onBackClick: () -> Unit,
    onPredictClick: (Match, PredictionOutcomeType) -> Unit,
    modifier: Modifier = Modifier
) {
    if (match == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NavyBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("اطلاعات مسابقه یافت نشد", color = TextWhite)
        }
        return
    }

    var selectedMarketTab by remember { mutableStateOf(0) }
    val marketTabs = listOf("همه بازارها", "نتیجه بازی", "شانس دوبرابر", "مجموع گل‌ها", "هر دو تیم گل")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${match.homeTeamPersian} - ${match.awayTeamPersian}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("btn_back_match_detail")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        containerColor = NavyBackground,
        modifier = modifier.testTag("match_detail_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Match-Center Header (1xBet Style Pitch Visualizer)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(listOf(Color(0xFF1E3A5F), NavyBorder))
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = match.leaguePersianName,
                                style = MaterialTheme.typography.labelMedium,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                            when (match.status) {
                                MatchStatus.LIVE -> LiveStatusBadge(minute = match.minute)
                                MatchStatus.UPCOMING -> Text(
                                    text = "${match.matchDate} ${match.matchTime}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextGrayLight
                                )
                                MatchStatus.FINISHED -> Text(
                                    text = "پایان بازی",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextGrayMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Teams & Score Center
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Home Team
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                AsyncImage(
                                    model = match.homeTeamLogo,
                                    contentDescription = match.homeTeamPersian,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(NavySurface)
                                        .padding(6.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = match.homeTeamPersian,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = match.homeTeamName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrayMuted,
                                    fontSize = 11.sp
                                )
                            }

                            // Score / Kickoff Center
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (match.status != MatchStatus.UPCOMING) {
                                    Text(
                                        text = "${match.homeScore} : ${match.awayScore}",
                                        style = MaterialTheme.typography.displayLarge,
                                        color = if (match.status == MatchStatus.LIVE) GreenAccent else TextWhite,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                } else {
                                    Text(
                                        text = match.matchTime,
                                        style = MaterialTheme.typography.displayMedium,
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NavySurface,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = if (match.status == MatchStatus.LIVE) "نیمه دوم" else "زمان رسمی",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextGrayLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Away Team
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                AsyncImage(
                                    model = match.awayTeamLogo,
                                    contentDescription = match.awayTeamPersian,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(NavySurface)
                                        .padding(6.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = match.awayTeamPersian,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = match.awayTeamName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrayMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ورزشگاه: ${match.stadium}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight)
                            Text("داور: ${match.referee}", style = MaterialTheme.typography.bodySmall, color = TextGrayLight)
                        }
                    }
                }
            }

            // Live statistics (real data only; loaded from the API when the page opens)
            if (!match.isReal || match.statsLoaded) item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QueryStats, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "آمار زنده مسابقه (Match Center)",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        DualStatBar(
                            title = "درصد مالکیت توپ",
                            homeVal = "${match.stats.possessionHome}%",
                            awayVal = "${match.stats.possessionAway}%",
                            homeRatio = match.stats.possessionHome / 100f
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DualStatBar(
                            title = "شوت‌های داخل چارچوب",
                            homeVal = "${match.stats.shotsOnTargetHome}",
                            awayVal = "${match.stats.shotsOnTargetAway}",
                            homeRatio = match.stats.shotsOnTargetHome.toFloat() / (match.stats.shotsOnTargetHome + match.stats.shotsOnTargetAway).coerceAtLeast(1)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DualStatBar(
                            title = "تعداد کرنرها",
                            homeVal = "${match.stats.cornersHome}",
                            awayVal = "${match.stats.cornersAway}",
                            homeRatio = match.stats.cornersHome.toFloat() / (match.stats.cornersHome + match.stats.cornersAway).coerceAtLeast(1)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DualStatBar(
                            title = "تعداد خطاها",
                            homeVal = "${match.stats.foulsHome}",
                            awayVal = "${match.stats.foulsAway}",
                            homeRatio = match.stats.foulsHome.toFloat() / (match.stats.foulsHome + match.stats.foulsAway).coerceAtLeast(1)
                        )
                    }
                }
            }

            // 1xBet Full Betting Markets
            item {
                Text(
                    text = "بازارهای پیش‌بینی مسابقه (ضرایب 1xBet شبیه‌ساز)",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Market Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedMarketTab,
                    containerColor = NavyDarkest,
                    contentColor = TextWhite,
                    edgePadding = 0.dp
                ) {
                    marketTabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedMarketTab == index,
                            onClick = { selectedMarketTab = index },
                            text = { Text(tabTitle, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }

            // Market 1: 1 - X - 2
            if (selectedMarketTab == 0 || selectedMarketTab == 1) {
                item {
                    MarketSectionCard(title = "نتیجه مسابقه (1X2)") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BetOddButton(
                                market = "۱ (برد ${match.homeTeamPersian})",
                                odd = "${match.homeProbMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.HOME_WIN) },
                                modifier = Modifier.weight(1f)
                            )
                            BetOddButton(
                                market = "X (تساوی)",
                                odd = "${match.drawProbMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.DRAW) },
                                modifier = Modifier.weight(1f)
                            )
                            BetOddButton(
                                market = "۲ (برد ${match.awayTeamPersian})",
                                odd = "${match.awayProbMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.AWAY_WIN) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Market 2: Double Chance (1X, 12, X2)
            if (selectedMarketTab == 0 || selectedMarketTab == 2) {
                item {
                    MarketSectionCard(title = "شانس دوبرابر (Double Chance)") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BetOddButton(
                                market = "1X (میزبان یا مساوی)",
                                odd = "${match.dc1XMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.DOUBLE_CHANCE_1X) },
                                modifier = Modifier.weight(1f),
                                color = CyanAccent
                            )
                            BetOddButton(
                                market = "12 (برد یکی از دو تیم)",
                                odd = "${match.dc12Multiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.DOUBLE_CHANCE_12) },
                                modifier = Modifier.weight(1f),
                                color = CyanAccent
                            )
                            BetOddButton(
                                market = "X2 (مساوی یا میهمان)",
                                odd = "${match.dcX2Multiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.DOUBLE_CHANCE_X2) },
                                modifier = Modifier.weight(1f),
                                color = CyanAccent
                            )
                        }
                    }
                }
            }

            // Market 3: Totals (Over / Under)
            if (selectedMarketTab == 0 || selectedMarketTab == 3) {
                item {
                    MarketSectionCard(title = "مجموع گل‌ها (Totals Over/Under)") {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BetOddButton(
                                    market = "بیشتر از ۲.۵ گل (+2.5)",
                                    odd = "${match.over25Multiplier}",
                                    onClick = { onPredictClick(match, PredictionOutcomeType.OVER_2_5) },
                                    modifier = Modifier.weight(1f),
                                    color = GoldAccent
                                )
                                BetOddButton(
                                    market = "کمتر از ۲.۵ گل (-2.5)",
                                    odd = "${match.under25Multiplier}",
                                    onClick = { onPredictClick(match, PredictionOutcomeType.UNDER_2_5) },
                                    modifier = Modifier.weight(1f),
                                    color = GoldAccent
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BetOddButton(
                                    market = "بیشتر از ۱.۵ گل (+1.5)",
                                    odd = "1.35",
                                    onClick = { onPredictClick(match, PredictionOutcomeType.OVER_1_5) },
                                    modifier = Modifier.weight(1f)
                                )
                                BetOddButton(
                                    market = "کمتر از ۱.۵ گل (-1.5)",
                                    odd = "3.10",
                                    onClick = { onPredictClick(match, PredictionOutcomeType.UNDER_1_5) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Market 4: Both Teams to Score (GG / NG)
            if (selectedMarketTab == 0 || selectedMarketTab == 4) {
                item {
                    MarketSectionCard(title = "هر دو تیم گل می‌زنند (Both Teams to Score)") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BetOddButton(
                                market = "بله - گلزنی هر دو تیم (GG)",
                                odd = "${match.bttsYesMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.BOTH_TEAMS_SCORE_YES) },
                                modifier = Modifier.weight(1f),
                                color = GreenAccent
                            )
                            BetOddButton(
                                market = "خیر - حداقل یک کلین‌شیت (NG)",
                                odd = "${match.bttsNoMultiplier}",
                                onClick = { onPredictClick(match, PredictionOutcomeType.BOTH_TEAMS_SCORE_NO) },
                                modifier = Modifier.weight(1f),
                                color = LiveRed
                            )
                        }
                    }
                }
            }

            // Match Events Timeline
            item {
                Text(
                    text = "گزارش لحظه‌به‌لحظه و رویدادهای بازی (${match.events.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            if (match.events.isEmpty()) {
                item {
                    Text(
                        text = "هنوز رویدادی برای این مسابقه ثبت نشده است.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted
                    )
                }
            } else {
                items(match.events) { event ->
                    EventItemCard(event = event)
                }
            }
        }
    }
}

@Composable
private fun MarketSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextGrayLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun DualStatBar(
    title: String,
    homeVal: String,
    awayVal: String,
    homeRatio: Float
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(homeVal, style = MaterialTheme.typography.labelSmall, color = GreenAccent, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextGrayLight)
            Text(awayVal, style = MaterialTheme.typography.labelSmall, color = CyanAccent, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(NavyCardElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(homeRatio.coerceIn(0.05f, 0.95f))
                    .background(GreenAccent)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((1f - homeRatio).coerceIn(0.05f, 0.95f))
                    .background(CyanAccent)
            )
        }
    }
}

@Composable
private fun EventItemCard(event: MatchEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (event.type) {
                    "GOAL" -> GreenContainer
                    "CARD_YELLOW" -> Color(0xFF4A3E00)
                    "CARD_RED" -> LiveRedContainer
                    else -> NavyCardElevated
                }
            ) {
                Text(
                    text = "${event.minute}'",
                    style = MaterialTheme.typography.labelMedium,
                    color = when (event.type) {
                        "GOAL" -> GreenAccent
                        "CARD_YELLOW" -> GoldAccent
                        "CARD_RED" -> LiveRed
                        else -> TextWhite
                    },
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${event.player} (${event.teamName})",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrayLight
                )
            }
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/MatchesScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.PredictionOutcomeType
import com.playzone.football.ui.components.LeagueFilterBar
import com.playzone.football.ui.components.MatchCard
import com.playzone.football.ui.components.SearchBarField
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun MatchesScreen(
    viewModel: MainViewModel,
    matches: List<Match>,
    selectedStatus: MatchStatus?,
    selectedLeagueId: String?,
    onNavigateToMatch: (String) -> Unit,
    onPredictClick: (Match, PredictionOutcomeType) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val tabs = listOf(
        null to "همه مسابقات",
        MatchStatus.LIVE to "زنده",
        MatchStatus.UPCOMING to "پیش‌رو",
        MatchStatus.FINISHED to "پایان‌یافته"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("matches_screen")
    ) {
        // Tab Row for Match Statuses
        TabRow(
            selectedTabIndex = tabs.indexOfFirst { it.first == selectedStatus }.coerceAtLeast(0),
            containerColor = NavyDarkest,
            contentColor = TextWhite,
            indicator = { tabPositions ->
                val index = tabs.indexOfFirst { it.first == selectedStatus }.coerceAtLeast(0)
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                    color = GreenAccent,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEach { (status, title) ->
                val selected = selectedStatus == status
                Tab(
                    selected = selected,
                    onClick = { viewModel.selectStatusFilter(status) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selected) GreenAccent else TextGrayLight,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Search Bar
        SearchBarField(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // League Filters
        LeagueFilterBar(
            leagues = viewModel.activeLeagues.collectAsStateWithLifecycle().value,
            selectedLeagueId = selectedLeagueId,
            onLeagueSelect = { viewModel.selectLeagueFilter(it) }
        )

        // Matches List
        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = TextGrayMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "هیچ مسابقه‌ای یافت نشد",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextGrayLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "عبارت جستجو یا فیلتر لیگ را تغییر دهید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted
                    )
                    val realMode by viewModel.isRealMode.collectAsStateWithLifecycle()
                    val apiStatus by viewModel.apiStatusMessage.collectAsStateWithLifecycle()
                    if (realMode) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = apiStatus,
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldAccent,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        TextButton(onClick = { viewModel.refreshNow() }) {
                            Text("به‌روزرسانی مسابقات", color = CyanAccent)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(matches, key = { it.id }) { match ->
                    MatchCard(
                        match = match,
                        onMatchClick = { onNavigateToMatch(match.id) },
                        onPredictClick = { outcome -> onPredictClick(match, outcome) }
                    )
                }
            }
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/PredictionsScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playzone.football.data.model.Prediction
import com.playzone.football.data.model.PredictionStatus
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun PredictionsScreen(
    viewModel: MainViewModel,
    predictions: List<Prediction>,
    onExploreMatchesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<PredictionStatus?>(null) }

    val filtered = remember(predictions, selectedFilter) {
        if (selectedFilter == null) predictions
        else predictions.filter { it.status == selectedFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("predictions_screen")
    ) {
        // Top Filter Bar
        Surface(color = NavyDarkest) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("همه (${predictions.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenAccent,
                        selectedLabelColor = NavyDarkest,
                        containerColor = NavyCard,
                        labelColor = TextWhite
                    )
                )
                FilterChip(
                    selected = selectedFilter == PredictionStatus.PENDING,
                    onClick = { selectedFilter = PredictionStatus.PENDING },
                    label = { Text("در جریان (${predictions.count { it.status == PredictionStatus.PENDING }})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenAccent,
                        selectedLabelColor = NavyDarkest,
                        containerColor = NavyCard,
                        labelColor = TextWhite
                    )
                )
                FilterChip(
                    selected = selectedFilter == PredictionStatus.WON,
                    onClick = { selectedFilter = PredictionStatus.WON },
                    label = { Text("برنده (${predictions.count { it.status == PredictionStatus.WON }})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenAccent,
                        selectedLabelColor = NavyDarkest,
                        containerColor = NavyCard,
                        labelColor = TextWhite
                    )
                )
                FilterChip(
                    selected = selectedFilter == PredictionStatus.LOST,
                    onClick = { selectedFilter = PredictionStatus.LOST },
                    label = { Text("ناموفق (${predictions.count { it.status == PredictionStatus.LOST }})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenAccent,
                        selectedLabelColor = NavyDarkest,
                        containerColor = NavyCard,
                        labelColor = TextWhite
                    )
                )
            }
        }

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = TextGrayMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "پیش‌بینی ثبت‌شده‌ای یافت نشد",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "یک مسابقه فوتبال انتخاب کرده و پیش‌بینی مجازی خود را آغاز نمایید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onExploreMatchesClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = NavyDarkest)
                    ) {
                        Text("مشاهده مسابقات فوتبال", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { prediction ->
                    PredictionItemCard(
                        prediction = prediction,
                        onSimulateResolution = { didWin ->
                            viewModel.resolvePrediction(prediction.id, didWin)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PredictionItemCard(
    prediction: Prediction,
    onSimulateResolution: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Match & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prediction.matchTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (prediction.status) {
                        PredictionStatus.WON -> GreenContainer
                        PredictionStatus.LOST -> LiveRedContainer
                        PredictionStatus.PENDING -> NavyCardElevated
                        PredictionStatus.CANCELLED -> NavyDarkest
                    }
                ) {
                    Text(
                        text = when (prediction.status) {
                            PredictionStatus.WON -> "✓ برنده شده"
                            PredictionStatus.LOST -> "✗ ناموفق"
                            PredictionStatus.PENDING -> "در جریان"
                            PredictionStatus.CANCELLED -> "لغو شده"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = when (prediction.status) {
                            PredictionStatus.WON -> GreenAccent
                            PredictionStatus.LOST -> LiveRed
                            PredictionStatus.PENDING -> GoldAccent
                            PredictionStatus.CANCELLED -> TextGrayMuted
                        },
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prediction Outcome details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("نتیجه انتخابی:", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                    Text(
                        text = prediction.outcomeLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("اعتبار مجازی تخصیص‌یافته:", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                    Text(
                        text = "%,d اعتبار".format(prediction.stakeCredits),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (prediction.status == PredictionStatus.WON) "اعتبار دریافتی:" else "بازگشت فرضی:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrayLight
                )
                Text(
                    text = "%,d اعتبار مجازی".format(prediction.potentialReturn),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (prediction.status == PredictionStatus.WON) GreenAccent else GoldAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            // If pending, allow simulated educational resolution
            if (prediction.status == PredictionStatus.PENDING && prediction.matchId.startsWith("api_")) {
                // Real matches settle automatically from the official final score.
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "در انتظار نتیجه رسمی بازی؛ پس از پایان بازی خودکار تسویه می‌شود.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrayMuted
                )
            } else if (prediction.status == PredictionStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "شبیه‌سازی نتیجه:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayMuted
                    )
                    OutlinedButton(
                        onClick = { onSimulateResolution(true) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("ثبت برد", style = MaterialTheme.typography.labelSmall, color = GreenAccent)
                    }
                    OutlinedButton(
                        onClick = { onSimulateResolution(false) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("ثبت باخت", style = MaterialTheme.typography.labelSmall, color = LiveRed)
                    }
                }
            }
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/PrivacyPolicyScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playzone.football.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سیاست حفظ حریم خصوصی", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_privacy")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        containerColor = NavyBackground,
        modifier = modifier.testTag("privacy_policy_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(26.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حریم خصوصی و شفافیت در PlayZone",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "۱. عدم جمع‌آوری اطلاعات شخصی:\n" +
                                   "برنامه PlayZone هیچ‌گونه اطلاعات هویتی، نام، ایمیل، شماره تماس، لیست مخاطبین، موقعیت مکانی (GPS)، پیامک یا گزارش تماس‌های کاربر را جمع‌آوری یا ذخیره نمی‌کند.\n\n" +
                                   "۲. عدم تبادل مالی یا قمار:\n" +
                                   "این نرم‌افزار به صورت ۱۰۰٪ شبیه‌ساز مجازی و آموزشی طراحی شده است. هیچ مکانیزمی برای واریز وجه، برداشت، خرید درون‌برنامه‌ای قمار، اتصال به درگاه‌های بانکی، کارت‌به‌کارت یا ارز دیجیتال در برنامه تعبیه نشده است.\n\n" +
                                   "۳. ذخیره‌سازی محلی داده‌ها (Room Database):\n" +
                                   "تمام پیش‌بینی‌های مجازی، تاریخچه‌ها و تنظیمات کاربر صرفاً در حافظه محلی دستگاه شما با استفاده از پایگاه داده استاندارد Room ذخیره می‌شوند و به هیچ سرور شخص ثالثی ارسال نمی‌گردند. کاربر در هر زمان می‌تواند از منوی پروفایل کلیه داده‌های محلی خود را پاک کند.\n\n" +
                                   "۴. مجوزهای دستگاه:\n" +
                                   "برنامه تنها از مجوز دسترسی به اینترنت جهت دریافت احتمالی اطلاعات مسابقات فوتبال استفاده می‌نماید و هیچ مجوز حساسی ندارد.\n\n" +
                                   "۵. انطباق با قوانین Google Play:\n" +
                                   "این اپلیکیشن کاملاً مطابق با خط‌مشی‌های گوگل پلی برای برنامه‌های شبیه‌سازی ورزشی و سرگرمی تدوین شده است.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/ProfileScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playzone.football.data.model.Wallet
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    wallet: Wallet,
    onNavigateToSettings: () -> Unit,
    onNavigateToResponsible: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("بازنشانی اطلاعات برنامه", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "آیا مطمئن هستید که می‌خواهید تمام تاریخچه پیش‌بینی‌ها و موجودی را به حالت اولیه بازنشانی کنید؟ این عمل غیرقابل بازگشت است.",
                    color = TextGrayLight,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LiveRed)
                ) {
                    Text("حذف و بازنشانی", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("انصراف", color = TextGrayLight)
                }
            },
            containerColor = NavySurface
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(NavySurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = GreenAccent,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "کاربر شبیه‌ساز PlayZone",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GreenContainer,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (wallet.predictionsCount > 10) "کارشناس پیش‌بینی فوتبال" else "پیش‌بین تازه‌کار",
                                style = MaterialTheme.typography.labelSmall,
                                color = GreenAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Stats summary row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatPill(title = "کل پیش‌بینی‌ها", value = "${wallet.predictionsCount}", modifier = Modifier.weight(1f))
                ProfileStatPill(title = "نرخ موفقیت", value = "${"%.1f".format(wallet.winRate)}%", color = GoldAccent, modifier = Modifier.weight(1f))
                ProfileStatPill(title = "اعتبار فعلی", value = "%,d".format(wallet.balance), color = GreenAccent, modifier = Modifier.weight(1f))
            }
        }

        // Navigation Links
        item {
            Text(
                text = "تنظیمات و دسترسی‌ها",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column {
                    ProfileMenuRow(
                        icon = Icons.Default.Timer,
                        title = "استفاده مسئولانه و مدیریت زمان",
                        subtitle = "سقف روزانه و توقف شبیه‌ساز",
                        onClick = onNavigateToResponsible
                    )
                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
                    ProfileMenuRow(
                        icon = Icons.Default.Settings,
                        title = "تنظیمات برنامه",
                        subtitle = "پیکربندی API، زبان و تم",
                        onClick = onNavigateToSettings
                    )
                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
                    ProfileMenuRow(
                        icon = Icons.Default.Shield,
                        title = "سیاست حفظ حریم خصوصی",
                        subtitle = "شفافیت داده‌ها و قوانین امنیتی",
                        onClick = onNavigateToPrivacy
                    )
                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))
                    ProfileMenuRow(
                        icon = Icons.Default.DeleteSweep,
                        title = "بازنشانی داده‌های محلی",
                        subtitle = "حذف پیش‌بینی‌ها و شارژ مجدد کیف پول",
                        iconTint = LiveRed,
                        onClick = { showResetDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatPill(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = TextWhite
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextGrayMuted, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: Color = CyanAccent
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = TextWhite, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextGrayLight)
        }
        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = TextGrayMuted)
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/ResponsibleGamingScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playzone.football.data.model.ResponsibleSettings
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponsibleGamingScreen(
    viewModel: MainViewModel,
    settings: ResponsibleSettings,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("استفاده مسئولانه و مدیریت زمان", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_responsible")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        containerColor = NavyBackground,
        modifier = modifier.testTag("responsible_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simulator Pause Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (settings.isPaused) Color(0xFF351215) else NavyCardElevated
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (settings.isPaused) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = if (settings.isPaused) LiveRed else GreenAccent,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (settings.isPaused) "شبیه‌ساز در حالت استراحت است" else "شبیه‌ساز فعال است",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (settings.isPaused) "امکان ثبت پیش‌بینی تا پایان زمان استراحت متوقف شده است."
                                           else "می‌توانید برای مدیریت زمان، شبیه‌ساز را موقتاً متوقف کنید.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrayLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (settings.isPaused) {
                            Button(
                                onClick = { viewModel.resumeSimulator() },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = NavyDarkest),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("پایان استراحت و فعال‌سازی مجدد", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.pauseSimulator(1) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavySurface)
                                ) {
                                    Text("۱ ساعت توقف", style = MaterialTheme.typography.labelSmall, color = TextWhite)
                                }
                                Button(
                                    onClick = { viewModel.pauseSimulator(24) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavySurface)
                                ) {
                                    Text("۲۴ ساعت", style = MaterialTheme.typography.labelSmall, color = TextWhite)
                                }
                                Button(
                                    onClick = { viewModel.pauseSimulator(168) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavySurface)
                                ) {
                                    Text("۷ روز", style = MaterialTheme.typography.labelSmall, color = TextWhite)
                                }
                            }
                        }
                    }
                }
            }

            // Daily Limit Slider
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "تنظیم سقف زمان استفاده روزانه",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "سقف روزانه تعیین شده: ${settings.dailyLimitMinutes} دقیقه",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GreenAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(15, 30, 60, 120).forEach { mins ->
                                FilterChip(
                                    selected = settings.dailyLimitMinutes == mins,
                                    onClick = { viewModel.updateDailyLimit(mins) },
                                    label = { Text("$mins دقیقه") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GreenAccent,
                                        selectedLabelColor = NavyDarkest,
                                        containerColor = NavyCardElevated,
                                        labelColor = TextWhite
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Awareness of Gambling Harms
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "آگاهی‌بخشی درباره آسیب‌های قمار و شرط‌بندی واقعی",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "این برنامه صرفاً یک شبیه‌ساز تحلیلی برای یادگیری احتمالات ریاضی فوتبال است. قمار واقعی با پول، یکی از عوامل ایجاد بحران‌های شدید مالی، اضطراب روانی و اختلالات رفتاری است.\n\n" +
                                   "• هیچ استراتژی یا سیستمی در شرط‌بندی واقعی تضمین‌کننده برد نیست.\n" +
                                   "• جبران باخت‌های قبلی (Chase Loss) نشانه اصلی رفتارهای اعتیادآور است.\n" +
                                   "• هرگز زمان اختصاص داده شده به خانواده و کار را صرف فعالیت‌های پرخطر نکنید.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Helplines & Support
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مراکز مشاوره و دریافت راهنمایی",
                                style = MaterialTheme.typography.titleMedium,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "اگر احساس می‌کنید تمایل به قمار یا ریسک‌های مالی سلامت روان شما را تحت تأثیر قرار داده است، با متخصصین مشورت کنید:\n\n" +
                                   "• سامانه ملی مشاوره تلفنی (ایران): ۱۴۸۰ (رایگان و ناشناس)\n" +
                                   "• اورژانس اجتماعی: ۱۲۳\n" +
                                   "• منابع بین‌المللی: www.gamblersanonymous.org",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/SettingsScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isLiveApi by viewModel.isLiveApiActive.collectAsStateWithLifecycle()
    val apiStatusMessage by viewModel.apiStatusMessage.collectAsStateWithLifecycle()

    var apiKeyInput by remember(userSettings.apiFootballKey) {
        mutableStateOf(userSettings.apiFootballKey)
    }
    var useLiveApiSwitch by remember(userSettings.useLiveApi) {
        mutableStateOf(userSettings.useLiveApi)
    }
    var isCheckingConnection by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات و اتصال API فوتبال", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_settings")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        containerColor = NavyBackground,
        modifier = modifier.testTag("settings_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLiveApi) Color(0xFF003816) else NavyCardElevated
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLiveApi) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isLiveApi) GreenAccent else CyanAccent,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isLiveApi) "اتصال آنلاین به API-Football فعال است" else "حالت شبیه‌ساز هوشمند فعال است",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = apiStatusMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isLiveApi) GreenOnContainer else TextGrayLight
                            )
                        }
                    }
                }
            }

            // API-Football Configuration Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "پیکربندی کلید API-Football",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "اگر دارای کلید API اختصاصی از سایت api-football.com یا api-sports.io هستید، می‌توانید آن را در کادر زیر وارد کنید تا مسابقات زنده بین‌المللی مستقیماً از سرور دریافت شوند:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            label = { Text("کلید اختصاصی API-Football (اختیاری)") },
                            placeholder = { Text("مثال: 94a8bc371e...") },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = CyanAccent) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenAccent,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedContainerColor = NavySurface,
                                unfocusedContainerColor = NavySurface
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "استفاده از داده‌های آنلاین سرور",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "در صورت خاموش بودن، مسابقات از شبیه‌ساز داخلی با داده‌های واقعی خوانده می‌شوند.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrayMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = useLiveApiSwitch,
                                onCheckedChange = { useLiveApiSwitch = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NavyDarkest,
                                    checkedTrackColor = GreenAccent
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    isCheckingConnection = true
                                    viewModel.saveApiConfig(apiKeyInput.trim(), useLiveApiSwitch)
                                    isCheckingConnection = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenAccent,
                                    contentColor = NavyDarkest
                                )
                            ) {
                                if (isCheckingConnection) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = NavyDarkest, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text("ذخیره و اتصال به سرور", fontWeight = FontWeight.Bold)
                            }

                            if (apiKeyInput.isNotBlank()) {
                                OutlinedButton(
                                    onClick = {
                                        apiKeyInput = ""
                                        useLiveApiSwitch = false
                                        viewModel.saveApiConfig("", false)
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGrayLight)
                                ) {
                                    Text("حذف کلید")
                                }
                            }
                        }
                    }
                }
            }

            // Information on Free API Key
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "نحوه دریافت کلید رایگان API-Football:",
                            style = MaterialTheme.typography.titleSmall,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "۱. به وب‌سایت dashboard.api-football.com مراجعه کرده و ثبت نام کنید.\n" +
                                   "۲. در بخش API Key، کلید اختصاصی رایگان خود را کپی کنید (دارای ۱۰۰ درخواست رایگان در روز).\n" +
                                   "۳. کلید را در کادر بالا وارد کرده و دکمه «ذخیره و اتصال» را فشار دهید.\n" +
                                   "۴. در صورتی که کلید وارد نکنید، شبیه‌ساز پیشرفته PlayZone تمامی مسابقات لیگ‌های برتر ایران و اروپا را به شکل زنده شبیه‌سازی می‌کند.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Language & Direction Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "زبان و چیدمان رابط کاربری",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("زبان: فارسی (Iran)", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                                Text("فونت: وزیرمتن (Vazirmatn) • چیدمان: راست‌به‌چپ (RTL)", style = MaterialTheme.typography.bodySmall, color = TextGrayMuted)
                            }
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = GreenAccent)
                        }
                    }
                }
            }
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/TeamDetailScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.playzone.football.data.model.Team
import com.playzone.football.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    team: Team?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (team == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NavyBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("تیم مورد نظر یافت نشد", color = TextWhite)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(team.persianName, color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_team_detail")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDarkest)
            )
        },
        containerColor = NavyBackground,
        modifier = modifier.testTag("team_detail_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Team Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCardElevated)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = team.logoUrl,
                            contentDescription = team.persianName,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(NavySurface)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = team.persianName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = team.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrayLight
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Form pills (W/D/L)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("فرم ۵ بازی اخیر:", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted)
                            team.form.forEach { f ->
                                Surface(
                                    shape = CircleShape,
                                    color = when (f) {
                                        "W" -> GreenAccent
                                        "D" -> GoldAccent
                                        else -> LiveRed
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = f,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyDarkest
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Stats grid
            item {
                Text("آمار و رتبه در لیگ", style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailStatBox(title = "رتبه در جدول", value = "#${team.rank}", modifier = Modifier.weight(1f))
                    DetailStatBox(title = "امتیاز کسب شده", value = "${team.points}", modifier = Modifier.weight(1f), valueColor = GreenAccent)
                    DetailStatBox(title = "تعداد بازی‌ها", value = "${team.played}", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailStatBox(title = "تعداد بردها", value = "${team.wins}", modifier = Modifier.weight(1f), valueColor = GreenAccent)
                    DetailStatBox(title = "تعداد تساوی‌ها", value = "${team.draws}", modifier = Modifier.weight(1f), valueColor = GoldAccent)
                    DetailStatBox(title = "تعداد باخت‌ها", value = "${team.losses}", modifier = Modifier.weight(1f), valueColor = LiveRed)
                }
            }
        }
    }
}

@Composable
private fun DetailStatBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextWhite
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = TextGrayMuted, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = valueColor, fontWeight = FontWeight.Bold)
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/screens/WalletScreen.kt
================================================================================

```kotlin
package com.playzone.football.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playzone.football.data.model.Wallet
import com.playzone.football.ui.theme.*
import com.playzone.football.viewmodel.MainViewModel

@Composable
fun WalletScreen(
    viewModel: MainViewModel,
    wallet: Wallet,
    modifier: Modifier = Modifier
) {
    var showCustomBalanceDialog by remember { mutableStateOf(false) }
    var customBalanceInput by remember { mutableStateOf(wallet.balance.toString()) }

    if (showCustomBalanceDialog) {
        val quickAmounts = listOf(100_000L, 500_000L, 1_000_000L, 10_000_000L, 50_000_000L)

        AlertDialog(
            onDismissRequest = { showCustomBalanceDialog = false },
            title = {
                Text(
                    text = "تنظیم موجودی دلخواه کیف پول مجازی",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "می‌توانید هر مبلغ دلخواهی از اعتبارات شبیه‌ساز را برای خود تنظیم کنید:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayLight
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customBalanceInput,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) {
                                customBalanceInput = input
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("مبلغ موجودی (اعتبار)") },
                        trailingIcon = { Text("اعتبار", color = TextGrayMuted, modifier = Modifier.padding(end = 12.dp)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenAccent,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("انتخاب سریع:", style = MaterialTheme.typography.labelSmall, color = TextGrayMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        quickAmounts.take(4).forEach { amount ->
                            FilterChip(
                                selected = customBalanceInput == amount.toString(),
                                onClick = { customBalanceInput = amount.toString() },
                                label = { Text("%,d".format(amount), fontSize = 9.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GreenAccent,
                                    selectedLabelColor = NavyDarkest,
                                    containerColor = NavyCardElevated,
                                    labelColor = TextWhite
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = customBalanceInput.toLongOrNull() ?: 100_000L
                        viewModel.setCustomWalletBalance(amount)
                        showCustomBalanceDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = NavyDarkest)
                ) {
                    Text("ذخیره موجودی", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomBalanceDialog = false }) {
                    Text("انصراف", color = TextGrayLight)
                }
            },
            containerColor = NavySurface
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Balance Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(GreenAccent.copy(alpha = 0.6f), NavyBorder))
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(GreenAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = GreenAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "موجودی کیف پول شبیه‌ساز PlayZone",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGrayLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "%,d".format(wallet.balance),
                            style = MaterialTheme.typography.displayLarge,
                            color = GreenAccent,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "اعتبار",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextGrayMuted,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                customBalanceInput = wallet.balance.toString()
                                showCustomBalanceDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_custom_balance"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenAccent,
                                contentColor = NavyDarkest
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "تنظیم موجودی دلخواه", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.refillCredits() },
                            modifier = Modifier.weight(0.8f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyanAccent)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "شارژ ۱۰۰K", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Summary Statistics Grid
        item {
            Text(
                text = "خلاصه گردش اعتبارات شبیه‌ساز",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "کل اعتبارات شرط‌بندی‌شده",
                    value = "%,d".format(wallet.totalStaked),
                    subtitle = "اعتبار",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "کل بردهای دریافت‌شده",
                    value = "%,d".format(wallet.totalWon),
                    subtitle = "اعتبار",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "سود / زیان شبیه‌ساز",
                    value = "${if (wallet.netProfit >= 0) "+" else ""}${"%,d".format(wallet.netProfit)}",
                    subtitle = "اعتبار",
                    valueColor = if (wallet.netProfit >= 0) GreenAccent else LiveRed,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "نرخ موفقیت پیش‌بینی‌ها",
                    value = "${"%.1f".format(wallet.winRate)}%",
                    subtitle = "${wallet.wonCount} برد از ${wallet.predictionsCount} بازی",
                    valueColor = GoldAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Virtual Disclaimer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(listOf(CyanAccent.copy(alpha = 0.4f), NavyBorder))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ضوابط اعتبارات در شبیه‌ساز PlayZone",
                            style = MaterialTheme.typography.titleMedium,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• موجودی کیف پول صرفاً یک عدد فرضی برای شبیه‌سازی تجربه پیش‌بینی بدون خطر مالی است.\n" +
                               "• کاربر می‌تواند در هر لحظه موجودی خود را آزادانه کم یا زیاد کند.\n" +
                               "• هیچ وجه یا ارزی دریافت یا پرداخت نمی‌شود و سیستم هیچ‌گونه درگاه مالی ندارد.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrayLight,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextWhite
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextGrayMuted, maxLines = 1)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = TextGrayLight)
        }
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/theme/Color.kt
================================================================================

```kotlin
package com.playzone.football.ui.theme

import androidx.compose.ui.graphics.Color

// Navy Blue Backgrounds
val NavyDarkest = Color(0xFF070D1B)
val NavyBackground = Color(0xFF0B132B)
val NavySurface = Color(0xFF111D38)
val NavyCard = Color(0xFF16233B)
val NavyCardElevated = Color(0xFF1C2A4A)
val NavyBorder = Color(0xFF243452)

// Green & Vibrant Accents
val GreenAccent = Color(0xFF00E676)
val GreenAccentDark = Color(0xFF00B050)
val GreenContainer = Color(0xFF003816)
val GreenOnContainer = Color(0xFF69FF9E)

// Cyan & Gold Secondary Accents
val CyanAccent = Color(0xFF00B4D8)
val CyanContainer = Color(0xFF003544)
val GoldAccent = Color(0xFFFFB703)
val LiveRed = Color(0xFFFF3B30)
val LiveRedContainer = Color(0xFF3E1215)

// Text Colors
val TextWhite = Color(0xFFFFFFFF)
val TextGrayLight = Color(0xFFD1D8E0)
val TextGrayMuted = Color(0xFF8896A6)
val TextDark = Color(0xFF070D1B)

// Outcome Probability Colors
val ProbHigh = Color(0xFF00E676)
val ProbMid = Color(0xFFFFB703)
val ProbLow = Color(0xFF8896A6)
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/theme/Theme.kt
================================================================================

```kotlin
package com.playzone.football.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = GreenAccent,
    onPrimary = NavyDarkest,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenOnContainer,
    secondary = CyanAccent,
    onSecondary = NavyDarkest,
    secondaryContainer = CyanContainer,
    onSecondaryContainer = TextWhite,
    tertiary = GoldAccent,
    onTertiary = NavyDarkest,
    background = NavyBackground,
    onBackground = TextWhite,
    surface = NavySurface,
    onSurface = TextWhite,
    surfaceVariant = NavyCardElevated,
    onSurfaceVariant = TextGrayLight,
    outline = NavyBorder,
    error = LiveRed
)

@Composable
fun PlayZoneTheme(
    content: @Composable () -> Unit
) {
    // Provide Iranian/Persian RTL layout direction globally across the app
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
```


================================================================================
FILE: app/src/main/java/com/playzone/football/ui/theme/Type.kt
================================================================================

```kotlin
package com.playzone.football.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.playzone.football.R

val VazirmatnFontFamily = FontFamily(
    Font(R.font.vazirmatn, FontWeight.Normal)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.sp
    )
)
```


================================================================================
FILE: app/src/main/java/com/playzone/football/viewmodel/MainViewModel.kt
================================================================================

```kotlin
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
```


================================================================================
FILE: app/src/main/res/drawable/ic_launcher_background.xml
================================================================================

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#0B132B"
        android:pathData="M0,0h108v108h-108z" />
</vector>
```


================================================================================
FILE: app/src/main/res/drawable/ic_launcher_foreground.xml
================================================================================

```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:width="66dp"
        android:height="66dp"
        android:drawable="@drawable/ic_playzone_logo"
        android:gravity="center" />
</layer-list>
```


================================================================================
FILE: app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
================================================================================

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```


================================================================================
FILE: app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
================================================================================

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```


================================================================================
FILE: app/src/main/res/values/colors.xml
================================================================================

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
```


================================================================================
FILE: app/src/main/res/values/strings.xml
================================================================================

```xml
<resources>
    <string name="app_name">PlayZone</string>
    <string name="nav_home">خانه</string>
    <string name="nav_matches">مسابقات</string>
    <string name="nav_predictions">پیش‌بینی‌ها</string>
    <string name="nav_wallet">کیف پول</string>
    <string name="nav_profile">پروفایل</string>

    <!-- General and Virtual Simulator Notices -->
    <string name="virtual_notice_permanent">کلیه اعتبارات و پیش‌بینی‌ها کاملاً مجازی هستند و فاقد هرگونه ارزش مادی یا پولی می‌باشند و امکان خرید، فروش، برداشت یا تبدیل آن‌ها وجود ندارد.</string>
    <string name="simulated_odds_notice">احتمالات نمایش داده شده صرفاً شبیه‌سازی آماری و آموزشی هستند و ارتباطی با ضرایب سایت‌های شرط‌بندی ندارند.</string>
    <string name="playzone_credit_currency">اعتبار مجازی</string>
    <string name="demo_mode_badge">حالت شبیه‌ساز آفلاین</string>
    <string name="demo_mode_desc">داده‌ها و رویدادها در حالت شبیه‌سازی واقعی بارگذاری شده‌اند.</string>

    <!-- Match Categories -->
    <string name="matches_live">زنده</string>
    <string name="matches_upcoming">پیش‌رو</string>
    <string name="matches_finished">پایان‌یافته</string>
    <string name="matches_all">همه مسابقات</string>

    <!-- Outcomes -->
    <string name="outcome_home">برد میزبان</string>
    <string name="outcome_draw">مساوی</string>
    <string name="outcome_away">برد میهمان</string>

    <!-- Wallet & Credits -->
    <string name="wallet_title">کیف پول مجازی</string>
    <string name="wallet_balance_label">موجودی اعتبار مجازی</string>
    <string name="wallet_reset_credits">شارژ مجدد اعتبار رایگان</string>
    <string name="wallet_reset_dialog_title">شارژ اعتبار هدیه</string>
    <string name="wallet_reset_dialog_desc">موجودی مجازی شما به ۱۰۰,۰۰۰ اعتبار بازنشانی شد. این اعتبارات صرفاً جنبه آموزشی و سرگرمی دارد.</string>

    <!-- Responsible Gaming -->
    <string name="responsible_title">استفاده مسئولانه و مدیریت زمان</string>
    <string name="responsible_session_timer">مدت زمان جلسه جاری</string>
    <string name="responsible_take_break">استراحت و توقف شبیه‌ساز</string>
    <string name="responsible_daily_limit">محدودیت روزانه شبیه‌سازی</string>
    <string name="responsible_helpline_title">آگاهی‌بخشی درباره آسیب‌های قمار واقعی</string>
    <string name="responsible_helpline_desc">این برنامه شبیه‌ساز آموزشی است. اگر احساس می‌کنید قمار یا رفتارهای پرخطر به زندگی شما آسیب می‌زند، با مشاوران تماس بگیرید.</string>

    <!-- Buttons -->
    <string name="btn_submit_prediction">ثبت پیش‌بینی مجازی</string>
    <string name="btn_close">بستن</string>
    <string name="btn_confirm">تأیید</string>
    <string name="btn_cancel">انصراف</string>
    <string name="btn_retry">تلاش مجدد</string>
    <string name="btn_view_details">مشاهده جزئیات</string>
</resources>
```


================================================================================
FILE: app/src/main/res/values/themes.xml
================================================================================

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.PlayZone" parent="android:Theme.DeviceDefault.NoActionBar">
        <item name="android:statusBarColor">#0B132B</item>
        <item name="android:navigationBarColor">#070D1B</item>
        <item name="android:windowLightStatusBar">false</item>
    </style>
    <style name="Theme.MyApplication" parent="Theme.PlayZone" />
</resources>
```


================================================================================
FILE: app/src/main/res/xml/backup_rules.xml
================================================================================

```xml
<?xml version="1.0" encoding="utf-8"?><!--
   Sample backup rules file; uncomment and customize as necessary.
   See https://developer.android.com/guide/topics/data/autobackup
   for details.
   Note: This file is ignored for devices older than API 31
   See https://developer.android.com/about/versions/12/backup-restore
-->
<full-backup-content>
    <!--
   <include domain="sharedpref" path="."/>
   <exclude domain="sharedpref" path="device.xml"/>
-->
</full-backup-content>
```


================================================================================
FILE: app/src/main/res/xml/data_extraction_rules.xml
================================================================================

```xml
<?xml version="1.0" encoding="utf-8"?><!--
   Sample data extraction rules file; uncomment and customize as necessary.
   See https://developer.android.com/about/versions/12/backup-restore#xml-changes
   for details.
-->
<data-extraction-rules>
    <cloud-backup>
        <!-- TODO: Use <include> and <exclude> to control what is backed up.
        <include .../>
        <exclude .../>
        -->
    </cloud-backup>
    <!--
    <device-transfer>
        <include .../>
        <exclude .../>
    </device-transfer>
    -->
</data-extraction-rules>
```


================================================================================
FILE: app/src/test/java/com/playzone/football/PlayZoneUnitTest.kt
================================================================================

```kotlin
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
    val match = SampleFootballData.createInitialMatches().first()
    val expectedMultiplier = (100.0 / match.simHomeProb.coerceAtLeast(5) * 100).toInt() / 100.0
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
```


================================================================================
FILE: app/src/test/java/com/playzone/football/SettlementTest.kt
================================================================================

```kotlin
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
```


================================================================================
FILE: build.gradle.kts
================================================================================

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.secrets) apply false
  alias(libs.plugins.google.services) apply false
}
```


================================================================================
FILE: gradle/libs.versions.toml
================================================================================

```toml
[versions]
agp = "9.1.1"
coreKtx = "1.18.0"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
lifecycleRuntimeKtx = "2.8.7"
lifecycleViewmodelCompose = "2.8.7"
lifecycleRuntimeCompose = "2.8.7"
activityCompose = "1.10.1"
kotlin = "2.2.10"
composeBom = "2024.09.00"
googleDevtoolsKsp = "2.3.5"
navigationCompose = "2.8.9"
roomRuntime = "2.7.0"
roomKtx = "2.7.0"
roomCompiler = "2.7.0"
kotlinxCoroutinesTest = "1.10.2"
core = "1.6.1"
runner = "1.6.2"
coilCompose = "2.7.0"
retrofit = "2.12.0"
converterMoshi = "2.12.0"
kotlinxCoroutinesAndroid = "1.10.2"
kotlinxCoroutinesCore = "1.10.2"
accompanistPermissions = "0.37.3"
playServicesLocation = "21.3.0"
cameraCamera2 = "1.5.0"
cameraLifecycle = "1.5.0"
cameraView = "1.5.0"
cameraCore = "1.5.0"
loggingInterceptor = "4.10.0"
okhttp = "4.10.0"
moshiKotlin = "1.15.2"
moshiKotlinCodegen = "1.15.2"
datastorePreferences = "1.1.7"
robolectric = "4.16.1"
firebaseBom = "34.17.0"
secretsGradlePlugin = "2.0.1"
googleServices = "4.5.0"
credentials = "1.5.0"
googleid = "1.1.1"


[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodelCompose" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeCompose" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "roomRuntime" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "roomKtx" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "roomCompiler" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlinxCoroutinesTest" }
androidx-core = { group = "androidx.test", name = "core", version.ref = "core" }
androidx-runner = { group = "androidx.test", name = "runner", version.ref = "runner" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-core = { group = "androidx.compose.material", name = "material-icons-core" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coilCompose" }
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
converter-moshi = { group = "com.squareup.retrofit2", name = "converter-moshi", version.ref = "converterMoshi" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "kotlinxCoroutinesAndroid" }
kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "kotlinxCoroutinesCore" }
accompanist-permissions = { group = "com.google.accompanist", name = "accompanist-permissions", version.ref = "accompanistPermissions" }
play-services-location = { group = "com.google.android.gms", name = "play-services-location", version.ref = "playServicesLocation" }
androidx-camera-camera2 = { group = "androidx.camera", name = "camera-camera2", version.ref = "cameraCamera2" }
androidx-camera-lifecycle = { group = "androidx.camera", name = "camera-lifecycle", version.ref = "cameraLifecycle" }
androidx-camera-view = { group = "androidx.camera", name = "camera-view", version.ref = "cameraView" }
androidx-camera-core = { group = "androidx.camera", name = "camera-core", version.ref = "cameraCore" }
logging-interceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "loggingInterceptor" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
moshi-kotlin = { group = "com.squareup.moshi", name = "moshi-kotlin", version.ref = "moshiKotlin" }
moshi-kotlin-codegen = { group = "com.squareup.moshi", name = "moshi-kotlin-codegen", version.ref = "moshiKotlinCodegen" }
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastorePreferences" }
robolectric = { group = "org.robolectric", name = "robolectric", version.ref = "robolectric" }
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
firebase-ai = { group = "com.google.firebase", name = "firebase-ai" }
firebase-appcheck-recaptcha = { group = "com.google.firebase", name = "firebase-appcheck-recaptcha" }
firebase-appcheck-debug = { group = "com.google.firebase", name = "firebase-appcheck-debug" }
firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }
androidx-credentials = { group = "androidx.credentials", name = "credentials", version.ref = "credentials" }
androidx-credentials-play-services = { group = "androidx.credentials", name = "credentials-play-services-auth", version.ref = "credentials" }
googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleid" }


[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
google-devtools-ksp = { id = "com.google.devtools.ksp", version.ref = "googleDevtoolsKsp" }
secrets = { id = "com.google.android.libraries.mapsplatform.secrets-gradle-plugin", version.ref = "secretsGradlePlugin" }
google-services = { id = "com.google.gms.google-services", version.ref = "googleServices" }
```


================================================================================
FILE: gradle/wrapper/gradle-wrapper.properties
================================================================================

```
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-9.3.1-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```


================================================================================
FILE: gradle.properties
================================================================================

```
# Project-wide Gradle settings.
# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.
# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html
# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
# When configured, Gradle will run in incubating parallel mode.
# This option should only be used with decoupled projects. For more details, visit
# https://developer.android.com/r/tools/gradle-multi-project-decoupled-projects
org.gradle.parallel=true
# Kotlin code style for this project: "official" or "obsolete":
kotlin.code.style=official
# Enables namespacing of each library's R class so that its R class includes only the
# resources declared in the library itself and none from the library's dependencies,
# thereby reducing the size of the R class for that library
android.nonTransitiveRClass=true
org.gradle.caching=true
org.gradle.configuration-cache=true
# Set the maximum number of workers to 4 to avoid overloading the machine.
org.gradle.workers.max=4
# Set the Kotlin compiler execution strategy to in-process to avoid "Could not
# connect to Kotlin compile daemon" error.
kotlin.compiler.execution.strategy=in-process
# Allow the google-services plugin to work even if the google-services.json is not present.
googleServices.missing.passthrough=true
```


================================================================================
FILE: metadata.json
================================================================================

```json
{
  "name": "PlayZone",
  "description": "Football match prediction simulator with virtual credits, live matches, educational probabilities, and responsible gaming tools.",
  "requestFramePermissions": [],
  "majorCapabilities": ["MAJOR_CAPABILITY_SERVER_SIDE_GEMINI_API"]
}
```


================================================================================
FILE: settings.gradle.kts
================================================================================

```kotlin
pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "PlayZone"

include(":app")
```


================================================================================
NOTE: Binary files (PNG/JPG icons & logos, TTF font, .jks keystore) and the real .env were excluded.
================================================================================
