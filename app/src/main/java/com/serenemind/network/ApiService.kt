package com.serenemind.network

import com.serenemind.model.request.*
import com.serenemind.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<LoginResponse>

    @GET("api/users/me")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @GET("api/dashboard")
    suspend fun getDashboardData(): Response<DashboardResponse>

    @POST("api/mood/save")
    suspend fun saveMood(@Body request: MoodRequest): Response<Unit>

    @GET("api/mood/summary")
    suspend fun getMoodSummary(): Response<Map<String, Double>>

    @GET("api/posts")
    suspend fun getPosts(): Response<List<PostResponse>>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") postId: Long): Response<PostResponse>

    @GET("api/posts/{id}/comments")
    suspend fun getComments(@Path("id") postId: Long): Response<List<CommentResponse>>

    @POST("api/posts/{id}/like")
    suspend fun likePost(@Path("id") postId: Long): Response<Unit>

    @POST("api/posts/{id}/comments")
    suspend fun addComment(
        @Path("id") postId: Long,
        @Body request: CommentRequest
    ): Response<CommentResponse>

    // Streak API
    @GET("api/streaks/me")
    suspend fun getStreak(): Response<StreakResponse>

    @POST("api/streaks/use-freeze")
    suspend fun useStreakFreeze(): Response<StreakResponse>

    // Notification API
    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("filter") filter: String? = null
    ): Response<List<NotificationResponse>>

    @PATCH("api/notifications/{id}/read")
    suspend fun markAsRead(
        @Path("id") id: Long
    ): Response<Unit>

    @GET("api/notifications/{id}/click")
    suspend fun clickNotification(
        @Path("id") id: Long
    ): Response<NotificationResponse>

    @POST("api/notifications/read-all")
    suspend fun markAllAsRead(): Response<Unit>

    @Multipart
    @POST("api/posts")
    suspend fun createPost(
        @Part("post") post: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<PostResponse>

    @GET("api/reminders")
    suspend fun getReminders(): Response<List<ReminderResponse>>

    @POST("api/reminders")
    suspend fun createReminder(@Body request: ReminderRequest): Response<ReminderResponse>

    @DELETE("api/reminders/{id}")
    suspend fun deleteReminder(@Path("id") id: Long): Response<Unit>

    @PATCH("api/reminders/{id}/toggle")
    suspend fun toggleReminder(@Path("id") id: Long): Response<ReminderResponse>

    @POST("api/breathing/session/start")
    suspend fun startBreathingSession(@Body request: BreathingRequest): Response<BreathingStartResponse>

    @POST("api/breathing/session/{sessionId}/round-complete")
    suspend fun trackRoundComplete(
        @Path("sessionId") sessionId: String,
        @Query("roundNumber") roundNumber: Int
    ): Response<Unit>

    @POST("api/breathing/session/{sessionId}/complete")
    suspend fun completeBreathingSession(@Path("sessionId") sessionId: String): Response<BreathingSummaryResponse>

    @GET("api/mood/history/{year}/{month}")
    suspend fun getMoodHistory(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): Response<List<DailyMoodResponse>>
}
