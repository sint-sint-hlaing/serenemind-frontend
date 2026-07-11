package com.serenemind.network

import com.serenemind.model.request.LoginRequest
import com.serenemind.model.request.MoodRequest
import com.serenemind.model.response.DailyMoodResponse
import com.serenemind.model.response.DashboardResponse
import com.serenemind.model.response.LoginResponse
import com.serenemind.model.response.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    @GET("api/users/me")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @GET("api/dashboard")
    suspend fun getDashboardData(): Response<DashboardResponse>

    @POST("api/mood/save")
    suspend fun saveMood(@Body request: MoodRequest): Response<Unit>

    @GET("api/mood/summary")
    suspend fun getMoodSummary(): Response<Map<String, Double>>

    @GET("api/mood/history/{year}/{month}")
    suspend fun getMoodHistory(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): Response<List<DailyMoodResponse>>
}

    // network/ApiService.kt

