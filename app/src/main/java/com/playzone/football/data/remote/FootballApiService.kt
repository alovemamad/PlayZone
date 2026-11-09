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
    // آدرس‌ها اصلاح شدند (v3 اضافه حذف شد)
    @GET("fixtures")
    suspend fun getLiveFixtures(
        @Header("x-apisports-key") apiKey: String,
        @Query("live") live: String = "all"
    ): ApiFootballResponse<ApiFixtureItem>

    @GET("fixtures")
    suspend fun getFixturesByDate(
        @Header("x-apisports-key") apiKey: String,
        @Query("date") date: String,
        @Query("season") season: Int = 2024, // حل مشکل 403 (فصل)
        @Query("timezone") timezone: String? = null
    ): ApiFootballResponse<ApiFixtureItem>

    @GET("fixtures")
    suspend fun getFixturesByIds(
        @Header("x-apisports-key") apiKey: String,
        @Query("ids") ids: String,
        @Query("timezone") timezone: String? = null
    ): ApiFootballResponse<ApiFixtureItem>

    @GET("fixtures")
    suspend fun getFixtureDetail(
        @Header("x-apisports-key") apiKey: String,
        @Query("id") id: Long,
        @Query("timezone") timezone: String? = null
    ): ApiFootballResponse<ApiFixtureItem>

    @GET("odds")
    suspend fun getOddsByDate(
        @Header("x-apisports-key") apiKey: String,
        @Query("date") date: String,
        @Query("bookmaker") bookmaker: Int,
        @Query("page") page: Int,
        @Query("timezone") timezone: String? = null
    ): ApiFootballResponse<ApiOddsItem>

    @GET("odds/live")
    suspend fun getLiveOdds(
        @Header("x-apisports-key") apiKey: String
    ): ApiFootballResponse<ApiLiveOddsItem>

    @GET("status")
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
        // اینترسپتور کلید حذف شد تا تداخل پیش نیاید
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
