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
        @Header("Authorization") token: String,
        @Body request: JournalRequest
    ): Response<JournalResponse>

    @Multipart
    @POST("api/journals/{id}/photo")
    suspend fun uploadPhoto(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part
    ): Response<JournalResponse>

    @GET("api/journals")
    suspend fun listJournals(
        @Header("Authorization") token: String,
        @Query("filter") filter: String = "all"
    ): Response<List<JournalResponse>>

    @GET("api/journals/search")
    suspend fun searchJournals(
        @Header("Authorization") token: String,
        @Query("q") query: String
    ): Response<List<JournalResponse>>

    @GET("api/journals/{id}")
    suspend fun getJournal(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<JournalResponse>

    @PUT("api/journals/{id}")
    suspend fun updateJournal(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: JournalRequest
    ): Response<JournalResponse>

    @DELETE("api/journals/{id}")
    suspend fun deleteJournal(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @PATCH("api/journals/{id}/favorite")
    suspend fun toggleFavorite(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @PATCH("api/journals/{id}/private")
    suspend fun togglePrivate(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @POST("api/journals/{id}/analysis")
    suspend fun triggerAnalysis(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @GET("api/journals/{id}/analysis")
    suspend fun getAnalysis(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<JournalAnalysisResponse>

    @DELETE("api/journals/{id}/photo")
    suspend fun deletePhoto(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}
