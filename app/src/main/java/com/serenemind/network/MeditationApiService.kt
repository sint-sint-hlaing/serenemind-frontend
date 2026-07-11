package com.serenemind.network

import com.serenemind.model.request.MeditationSessionRequest
import com.serenemind.model.response.Meditation
import com.serenemind.model.response.MeditationDashboardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MeditationApiService {
    @GET("api/meditation/dashboard")
    suspend fun getMeditationDashboard(): Response<MeditationDashboardResponse>

    @GET("api/meditation/{id}")
    suspend fun getMeditationById(@Path("id") id: Long): Response<Meditation>

    @POST("api/meditation-sessions")
    suspend fun completeSession(@Body request: MeditationSessionRequest): Response<String>

    @GET("api/meditation-sessions/history")
    suspend fun getHistory(): Response<List<Meditation>> // Assuming history returns list of meditation or similar
}
