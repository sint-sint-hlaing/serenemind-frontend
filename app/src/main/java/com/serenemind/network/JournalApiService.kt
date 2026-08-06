package com.serenemind.network

import com.serenemind.model.request.JournalRequest
import com.serenemind.model.response.JournalAnalysisResponse
import com.serenemind.model.response.JournalResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface JournalApiService {

    @POST("api/journals")
    suspend fun createJournal(
        @Body request: JournalRequest
    ): Response<JournalResponse>

    @Multipart
    @POST("api/journals/{id}/photo")
    suspend fun uploadPhoto(
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part
    ): Response<Unit>

    @GET("api/journals")
    suspend fun listJournals(
        @Query("filter") filter: String = "all"
    ): Response<List<JournalResponse>>

    @GET("api/journals/search")
    suspend fun searchJournals(
        @Query("q") query: String
    ): Response<List<JournalResponse>>

    @GET("api/journals/{id}")
    suspend fun getJournal(
        @Path("id") id: Int
    ): Response<JournalResponse>

    @PUT("api/journals/{id}")
    suspend fun updateJournal(
        @Path("id") id: Int,
        @Body request: JournalRequest
    ): Response<JournalResponse>

    @DELETE("api/journals/{id}")
    suspend fun deleteJournal(
        @Path("id") id: Int
    ): Response<Unit>

    @PATCH("api/journals/{id}/favorite")
    suspend fun toggleFavorite(
        @Path("id") id: Int
    ): Response<Unit>

    @PATCH("api/journals/{id}/private")
    suspend fun togglePrivate(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("api/journals/{id}/analysis")
    suspend fun triggerAnalysis(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("api/journals/{id}/analysis")
    suspend fun getAnalysis(
        @Path("id") id: Int
    ): Response<JournalAnalysisResponse>

    @DELETE("api/journals/{id}/photo")
    suspend fun deletePhoto(
        @Path("id") id: Int
    ): Response<Unit>
}
