package com.serenemind.network

import com.serenemind.model.request.MeditationSessionRequest
import com.serenemind.model.response.Meditation
import com.serenemind.model.response.MeditationDashboardResponse
import retrofit2.Response
import retrofit2.http.*

interface MeditationApiService {
    @GET("api/meditations/dashboard")
    suspend fun getMeditationDashboard(): Response<MeditationDashboardResponse>

    @GET("api/meditations/{id}")
    suspend fun getMeditationById(
        @Path("id") id: Long
    ): Response<Meditation>
    @POST("api/meditations/complete")
    suspend fun completeSession(@Body request: MeditationSessionRequest): Response<String>

    @GET("api/meditations/history")
    suspend fun getHistory(): Response<List<Meditation>>

    @GET("api/users/me/recommendations")
    suspend fun getRecommendations(): Response<List<Meditation>>

    @GET("api/users/search")
    suspend fun search(@Query("keyword") keyword: String): Response<List<Meditation>>

    @POST("api/users/favorites")
    suspend fun addFavorite(@Body request: Map<String, Long>): Response<Unit>

    @GET("api/users/me/continue-listening")
    suspend fun getContinueListening(): Response<List<Meditation>>
}
