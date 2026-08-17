// MeditationApiService.kt
package com.serenemind.network

import com.serenemind.model.request.MeditationSessionRequest
import com.serenemind.model.request.TimerRequest
import com.serenemind.model.response.FavoriteResponse
import com.serenemind.model.response.MeditationDashboardResponse
import com.serenemind.model.response.MeditationHistoryResponse
import com.serenemind.model.response.MeditationList
import com.serenemind.model.response.MeditationResponse
import com.serenemind.model.response.ShareResponse
import com.serenemind.model.response.TimerResponse
import retrofit2.Response
import retrofit2.http.*

interface MeditationApiService {

    @GET("api/meditations/dashboard")
    suspend fun getDashboard(): Response<MeditationDashboardResponse>


    @GET("api/meditations/{id}")
    suspend fun getMeditationById(
        @Path("id") id: Long
    ): Response<MeditationResponse>

    @POST("api/meditations/complete")
    suspend fun completeSession(
        @Body request: MeditationSessionRequest
    ): Response<Unit>

    @GET("api/meditations/history")
    suspend fun getHistory(): Response<List<MeditationHistoryResponse>>

    @POST("api/meditations/{id}/favorite")
    suspend fun toggleFavorite(
        @Path("id") id: Long
    ): Response<FavoriteResponse>

    @POST("api/meditations/{id}/timer")
    suspend fun saveTimer(
        @Path("id") id: Long,
        @Body request: TimerRequest
    ): Response<TimerResponse>

    @GET("api/meditations/{id}/share")
    suspend fun getShareLink(
        @Path("id") id: Long
    ): Response<ShareResponse>


    @GET("api/meditations")
    suspend fun searchMeditations(
        @Query("query") query: String? = null,
        @Query("category") category: String? = null,
        @Query("time") time: String? = null
    ): Response<List<MeditationResponse>>

    @GET("api/users/me/recommendations")
    suspend fun getRecommendations(): Response<List<MeditationResponse>>

    @GET("api/users/me/continue-listening")
    suspend fun getContinueListening(): Response<List<MeditationResponse>>

    @POST("api/users/favorites")
    suspend fun addFavorite(
        @Body request: com.serenemind.model.request.FavoriteRequest
    ): Response<Unit>

    @GET("api/meditations/{id}/previous")
    suspend fun getPrevious(
        @Path("id") id: Long
    ): Response<MeditationResponse>

    // ===== NEXT =====
    @GET("api/meditations/{id}/next")
    suspend fun getNext(
        @Path("id") id: Long
    ): Response<MeditationList>

    @GET("api/meditations/{id}/download-url")
    suspend fun getDownloadUrl(
        @Path("id") id: Long
    ): Response<com.serenemind.model.response.DownloadUrlResponse>

    @GET("api/meditations/{id}/stream")
    suspend fun streamAudio(
        @Path("id") id: Long
    ): Response<okhttp3.ResponseBody>

    // ===== DOWNLOAD =====
    @GET("api/meditations/{id}/download")
    suspend fun downloadAudio(
        @Path("id") id: Long
    ): Response<okhttp3.ResponseBody>
}