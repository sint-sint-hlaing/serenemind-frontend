package com.serenemind.network

import com.serenemind.model.request.LoginRequest
import com.serenemind.model.request.MoodRequest
import com.serenemind.model.response.DashboardResponse
import com.serenemind.model.response.LoginResponse
import com.serenemind.model.response.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    @GET("api/users/me")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>

    @GET("api/dashboard")
    suspend fun getDashboardData(
        @Header("Authorization") token: String
    ): Response<DashboardResponse>

    // network/ApiService.kt
    @POST("api/mood/save")
    suspend fun saveMood(@Body request: MoodRequest): Response<Unit>

    @GET("api/mood/summary")
    suspend fun getMoodSummary(): Map<String, Double>
}
