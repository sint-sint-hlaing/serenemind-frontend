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

    @GET("api/users/personal-info")
    suspend fun getPersonalInfo(): Response<PersonalInfoResponse>

    @Multipart
    @PUT("api/users/me")
    suspend fun updateUserProfile(
        @Part("fullname") fullname: RequestBody,
        @Part("username") username: RequestBody,
        @Part("birthday") birthday: RequestBody,
        @Part("bio") bio: RequestBody,
        @Part avatar: MultipartBody.Part? = null
    ): Response<UserProfileResponse>

    @GET("api/users/activity")
    suspend fun getUserActivity(): Response<UserActivityResponse>

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

    @GET("api/mood/date/{date}")
    suspend fun getMoodByDate(@Path("date") date: String): Response<DailyMoodResponse>

    @GET("api/mood/weekly")
    suspend fun getWeeklyMood(): Response<List<DailyMoodResponse>>

    @GET("api/mood/monthly")
    suspend fun getMonthlyMood(): Response<List<DailyMoodResponse>>

    @GET("api/mood/summary/week")
    suspend fun getWeeklySummary(): Response<WeeklyMoodResponse>

    @DELETE("api/mood/delete/{id}")
    suspend fun deleteMood(@Path("id") id: Long): Response<Unit>

    @GET("api/posts")
    suspend fun getPosts(): Response<List<PostResponse>>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") postId: Long): Response<PostResponse>

    @GET("api/posts/{id}/comments")
    suspend fun getComments(@Path("id") postId: Long): Response<List<CommentResponse>>

    @POST("api/posts/{id}/like")
    suspend fun likePost(@Path("id") postId: Long): Response<Unit>

    @POST("api/posts/{id}/save")
    suspend fun toggleSavePost(@Path("id") postId: Long): Response<Unit>

    @GET("api/posts/saved")
    suspend fun getSavedPosts(): Response<List<PostResponse>>

    @POST("api/posts/{id}/comments")
    suspend fun addComment(
        @Path("id") postId: Long,
        @Body request: CommentRequest
    ): Response<CommentResponse>

    // Notification API
    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("filter") filter: String? = null
    ): Response<List<NotificationResponse>>

    @POST("api/notifications/{id}/read")
    suspend fun markAsRead(
        @Path("id") id: Long
    ): Response<Unit>

    @POST("api/notifications/{id}/click")
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
}
