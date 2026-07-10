package com.serenemind.network

import com.serenemind.model.request.CommentRequest
import com.serenemind.model.request.LoginRequest
import com.serenemind.model.request.MoodRequest
import com.serenemind.model.request.RegisterRequest
import com.serenemind.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Unit>


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

    @GET("api/posts")
    suspend fun getPosts(
        @Header("Authorization") token: String? = null
    ): Response<List<PostResponse>>

    @GET("api/posts/{id}")
    suspend fun getPostById(
        @Path("id") postId: Long,
        @Header("Authorization") token: String
    ): Response<PostResponse>

    @GET("api/posts/{id}/comments")
    suspend fun getComments(
        @Path("id") postId: Long,
        @Header("Authorization") token: String
    ): Response<List<CommentResponse>>

    @POST("api/posts/{id}/like")
    suspend fun likePost(
        @Path("id") postId: Long,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("api/posts/{id}/comments")
    suspend fun addComment(
        @Path("id") postId: Long,
        @Header("Authorization") token: String,
        @Body request: CommentRequest
    ): Response<CommentResponse>
}
