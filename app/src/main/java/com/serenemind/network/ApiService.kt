package com.serenemind.network

import com.serenemind.model.request.*
import com.serenemind.model.response.*
import com.serenemind.model.entity.enums.GoalStatus
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<LoginResponse>

    // --- USER PROFILE (HEAD priority) ---
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


    // --- JOURNAL ---
    @POST("api/journals")
    suspend fun createJournal(@Body request: JournalRequest): Response<JournalResponse>

    @GET("api/journals")
    suspend fun getAllJournals(@Query("filter") filter: String): Response<List<JournalResponse>>

    @GET("api/journals/search")
    suspend fun searchJournals(@Query("q") query: String): Response<List<JournalResponse>>

    @GET("api/journals/{id}")
    suspend fun getJournalById(@Path("id") id: Long): Response<JournalResponse>

    @PUT("api/journals/{id}")
    suspend fun updateJournal(@Path("id") id: Long, @Body request: JournalRequest): Response<JournalResponse>

    @DELETE("api/journals/{id}")
    suspend fun deleteJournal(@Path("id") id: Long): Response<Unit>

    @PATCH("api/journals/{id}/favorite")
    suspend fun toggleFavoriteJournal(@Path("id") id: Long): Response<JournalResponse>

    @PATCH("api/journals/{id}/private")
    suspend fun togglePrivateJournal(@Path("id") id: Long): Response<JournalResponse>

    @Multipart
    @POST("api/journals/{id}/photo")
    suspend fun uploadJournalPhoto(
        @Path("id") id: Long,
        @Part photo: MultipartBody.Part
    ): Response<JournalPhotoResponse>

    @DELETE("api/journals/{id}/photo")
    suspend fun deleteJournalPhoto(@Path("id") id: Long): Response<JournalPhotoResponse>

    @GET("api/journals/{id}/analysis")
    suspend fun getJournalAnalysis(@Path("id") id: Long): Response<JournalAnalysisResponse>

    @POST("api/journals/{id}/analysis")
    suspend fun triggerJournalAnalysis(@Path("id") id: Long): Response<JournalAnalysisResponse>

    // --- COMMUNITY (HEAD priority) ---
    @GET("api/posts")
    suspend fun getPosts(@Query("filter") filter: String? = null): Response<List<PostResponse>>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") postId: Long): Response<PostResponse>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") postId: Long): Response<Unit>

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

    @Multipart
    @POST("api/posts")
    suspend fun createPost(
        @Part("post") post: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<PostResponse>

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
}
