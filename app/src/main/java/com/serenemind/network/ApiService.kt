package com.serenemind.network

import com.serenemind.model.request.*
import com.serenemind.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    // Streak API
    @GET("api/streaks/me")
    suspend fun getStreak(
        @Header("Authorization") token: String
    ): Response<StreakResponse>

    @POST("api/streaks/use-freeze")
    suspend fun useStreakFreeze(
        @Header("Authorization") token: String
    ): Response<StreakResponse>

    @Multipart
    @POST("api/posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Part("post") post: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<PostResponse>

    @GET("api/reminders")
    suspend fun getReminders(
        @Header("Authorization") token: String
    ): Response<List<ReminderResponse>>

    @POST("api/reminders")
    suspend fun createReminder(
        @Header("Authorization") token: String,
        @Body request: ReminderRequest
    ): Response<ReminderResponse>

    @DELETE("api/reminders/{id}")
    suspend fun deleteReminder(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>

    @PATCH("api/reminders/{id}/toggle")
    suspend fun toggleReminder(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<ReminderResponse>

    // Breathing API
    @POST("api/breathing/session/start")
    suspend fun startBreathingSession(
        @Header("Authorization") token: String,
        @Body request: BreathingRequest
    ): Response<BreathingStartResponse>

    @POST("api/breathing/session/{sessionId}/round-complete")
    suspend fun trackRoundComplete(
        @Header("Authorization") token: String,
        @Path("sessionId") sessionId: String,
        @Query("roundNumber") roundNumber: Int
    ): Response<Unit>

    @POST("api/breathing/session/{sessionId}/complete")
    suspend fun completeBreathingSession(
        @Header("Authorization") token: String,
        @Path("sessionId") sessionId: String
    ): Response<BreathingSummaryResponse>
}
