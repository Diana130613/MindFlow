package ru.mindflow.app.foundation.remote.api

import retrofit2.http.*
import ru.mindflow.app.foundation.remote.dto.*

interface MindFlowApi {

    // ── Auth ──────────────────────────────────────────────────────────
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refresh(@Header("X-Refresh-Token") token: String): AuthResponse

    // ── Meditations ───────────────────────────────────────────────────
    @GET("meditations")
    suspend fun getMeditations(
        @Query("categoryId") categoryId: Long? = null,
        @Query("search") search: String? = null
    ): List<MeditationDto>

    @GET("meditations/{id}")
    suspend fun getMeditationById(@Path("id") id: Long): MeditationDto

    // ── Mood ──────────────────────────────────────────────────────────
    @POST("mood")
    suspend fun saveMood(@Body request: MoodEntryRequest): MoodEntryDto

    @GET("mood")
    suspend fun getMoodHistory(@Query("days") days: Int = 30): List<MoodEntryDto>

    @GET("mood/today")
    suspend fun getMoodToday(): MoodEntryDto?

    @GET("mood/average")
    suspend fun getMoodAverage(@Query("days") days: Int = 30): Double

    @DELETE("mood/{id}")
    suspend fun deleteMood(@Path("id") id: Long)
}
