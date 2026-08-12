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

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>

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

    // --- DASHBOARD (Incoming priority) ---
    @GET("api/dashboard")
    suspend fun getDashboardData(): Response<DashboardResponse>

    // --- MOOD TRACKING (Incoming priority) ---
    @POST("api/mood/save")
    suspend fun saveMood(@Body request: MoodRequest): Response<Unit>

    @GET("api/mood/summary")
    suspend fun getMoodSummary(): Response<Map<String, Double>>

    @GET("api/mood/history")
    suspend fun getMoodHistory(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<List<DailyMoodResponse>>

    @GET("api/mood/date/{date}")
    suspend fun getMoodByDate(@Path("date") date: String): Response<DailyMoodResponse>

    @GET("api/mood/weekly")
    suspend fun getWeeklyMood(): Response<List<WeeklyMoodResponse>>

    @GET("api/mood/monthly")
    suspend fun getMonthlyMood(): Response<List<DailyMoodResponse>>

    @GET("api/mood/summary/week")
    suspend fun getWeeklySummary(): Response<WeeklyMoodResponse>

    @DELETE("api/mood/delete/{id}")
    suspend fun deleteMood(@Path("id") id: Long): Response<Unit>

    // --- GOALS (Incoming priority) ---
    @POST("api/goals")
    suspend fun createGoal(@Body request: GoalRequest): Response<UserGoal>

    @GET("api/goals")
    suspend fun getAllGoals(): Response<List<UserGoal>>

    @GET("api/goals/active")
    suspend fun getActiveGoals(): Response<List<UserGoal>>

    @GET("api/goals/completed")
    suspend fun getCompletedGoals(): Response<List<UserGoal>>

    @GET("api/goals/status/{status}")
    suspend fun getGoalsByStatus(@Path("status") status: GoalStatus): Response<List<UserGoal>>

    @GET("api/goals/statistics")
    suspend fun getGoalStatistics(): Response<GoalStatistics>

    @PATCH("api/goals/{id}/progress")
    suspend fun updateGoalProgress(@Path("id") id: Long): Response<UserGoal>

    @PATCH("api/goals/{id}/complete")
    suspend fun completeGoal(@Path("id") id: Long): Response<UserGoal>

    @PATCH("api/goals/{id}/pause")
    suspend fun pauseGoal(@Path("id") id: Long): Response<UserGoal>

    @PATCH("api/goals/{id}/resume")
    suspend fun resumeGoal(@Path("id") id: Long): Response<UserGoal>

    @DELETE("api/goals/{id}")
    suspend fun deleteGoal(@Path("id") id: Long): Response<Unit>

    @DELETE("api/goals/{id}/hard")
    suspend fun hardDeleteGoal(@Path("id") id: Long): Response<Unit>

    // --- MEDITATION (Incoming priority) ---
    @GET("api/meditations/dashboard")
    suspend fun getMeditationDashboard(): Response<MeditationDashboardResponse>

    @GET("api/meditations/{id}")
    suspend fun getMeditationById(@Path("id") id: Long): Response<MeditationResponse>

    @POST("api/meditations/complete")
    suspend fun completeMeditationSession(@Body request: MeditationSessionRequest): Response<Unit>

    @GET("api/meditations/history")
    suspend fun getMeditationHistory(): Response<List<MeditationResponse>>

    @GET("api/meditations/{id}/download")
    suspend fun downloadMeditationAudio(@Path("id") id: Long): Response<okhttp3.ResponseBody>

    @POST("api/meditations/{id}/favorite")
    suspend fun toggleMeditationFavorite(@Path("id") id: Long): Response<FavoriteResponse>

    @POST("api/meditations/{id}/timer")
    suspend fun saveMeditationTimer(
        @Path("id") id: Long,
        @Body request: TimerRequest
    ): Response<TimerResponse>

    @GET("api/meditations/{id}/share")
    suspend fun shareMeditation(@Path("id") id: Long): Response<ShareResponse>

    @GET("api/meditations/{id}/previous")
    suspend fun getPreviousMeditation(@Path("id") id: Long): Response<MeditationResponse>

    @GET("api/meditations/{id}/next")
    suspend fun getNextMeditation(@Path("id") id: Long): Response<MeditationList>

    @GET("api/meditations/{id}/stream")
    suspend fun streamMeditationAudio(@Path("id") id: Long): Response<okhttp3.ResponseBody>

    // --- USER MEDITATION ---
    @GET("api/users/me/recommendations")
    suspend fun getMeditationRecommendations(): Response<List<MeditationResponse>>

    @GET("api/users/search")
    suspend fun searchMeditation(@Query("keyword") keyword: String): Response<List<MeditationResponse>>

    @POST("api/users/favorites")
    suspend fun addFavoriteMeditation(@Body request: FavoriteRequest): Response<Unit>

    @GET("api/users/me/continue-listening")
    suspend fun getContinueListening(): Response<List<MeditationResponse>>

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
