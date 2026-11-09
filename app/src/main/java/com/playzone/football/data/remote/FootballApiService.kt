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
    // نکته مهم: کلمه v3/ از ابتدای آدرس‌ها حذف شد
    @GET("fixtures")
    suspend fun getLiveFixtures(
        @Header("x-apisports-key") apiKey: String,
        @Query("live") live: String = "all"
    ): ApiFootballResponse<ApiFixtureItem>

    @GET("fixtures")
    suspend fun getFixturesByDate(
        @Header("x-apisports-key") apiKey: String,
        @Query("date") date: String,
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
        // این بخش جدید است: کلید را به صورت پیش‌فرض به همه درخواست‌ها اضافه می‌کند
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-apisports-key", "4377b2974432543c18543e04d2b3de40")
                .build()
            chain.proceed(request)
        }
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
