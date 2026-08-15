package com.serenemind.network

import com.serenemind.model.request.GoalRequest
import com.serenemind.model.response.GoalResponse
import com.serenemind.model.response.GoalStatistics
import com.serenemind.model.entity.enums.GoalStatus
import retrofit2.Response
import retrofit2.http.*
import com.serenemind.model.request.GoalNoteRequest
import com.serenemind.model.request.NoteUpdateRequest
import com.serenemind.model.response.GoalNoteResponse
import com.serenemind.model.response.HomeResponse
interface
GoalApiService {
    // ===== CREATE & UPDATE =====
    @POST("api/goals")
    suspend fun createGoal(
        @Body request: GoalRequest
    ): Response<GoalResponse>

    @PATCH("api/goals/{id}")
    suspend fun updateGoal(
        @Path("id") id: Long,
        @Body request: GoalRequest
    ): Response<GoalResponse>

    // ===== GET ALL GOALS =====
    @GET("api/goals")
    suspend fun getAllGoals(): Response<List<GoalResponse>>

    // ===== GET ACTIVE GOALS =====
    @GET("api/goals/active")
    suspend fun getActiveGoals(): Response<List<GoalResponse>>

    // ===== GET COMPLETED GOALS =====
    @GET("api/goals/completed")
    suspend fun getCompletedGoals(): Response<List<GoalResponse>>

    // ===== GET GOALS BY STATUS =====
    @GET("api/goals/status/{status}")
    suspend fun getGoalsByStatus(
        @Path("status") status: String
    ): Response<List<GoalResponse>>

    // ===== GET GOAL BY ID =====
    @GET("api/goals/{id}")
    suspend fun getGoalById(
        @Path("id") id: Long
    ): Response<GoalResponse>

    // ===== GET STATISTICS =====
    @GET("api/goals/statistics")
    suspend fun getGoalStatistics(): Response<GoalStatistics>

    // ===== UPDATE PROGRESS =====
    @PATCH("api/goals/{id}/progress")
    suspend fun updateProgress(
        @Path("id") id: Long,
        @Body request: com.serenemind.model.request.GoalProgressRequest
    ): Response<GoalResponse>

    // ===== COMPLETE GOAL =====
    @PUT("api/goals/{id}/complete")
    suspend fun completeGoal(
        @Path("id") id: Long
    ): Response<GoalResponse>

    // ===== PAUSE GOAL =====
    @PUT("api/goals/{id}/pause")
    suspend fun pauseGoal(
        @Path("id") id: Long
    ): Response<GoalResponse>

    // ===== RESUME GOAL =====
    @PATCH("api/goals/{id}/resume")
    suspend fun resumeGoal(
        @Path("id") id: Long
    ): Response<GoalResponse>

    // ===== DELETE (Soft Delete) =====
    @DELETE("api/goals/{id}")
    suspend fun deleteGoal(
        @Path("id") id: Long
    ): Response<Unit>

    // ===== HARD DELETE =====
    @DELETE("api/goals/{id}/hard")
    suspend fun hardDeleteGoal(
        @Path("id") id: Long
    ): Response<Unit>

    // ===== NOTE MANAGEMENT =====
    @POST("api/goals/{id}/notes")
    suspend fun addNote(
        @Path("id") id: Long,
        @Body request: GoalNoteRequest
    ): Response<GoalNoteResponse>

    @GET("api/goals/{id}/notes")
    suspend fun getNotes(
        @Path("id") id: Long
    ): Response<List<GoalNoteResponse>>

    @PUT("api/goals/notes/{noteId}")
    suspend fun updateNote(
        @Path("noteId") noteId: Long,
        @Body request: NoteUpdateRequest
    ): Response<GoalNoteResponse>

    @DELETE("api/goals/notes/{noteId}")
    suspend fun deleteNote(
        @Path("noteId") noteId: Long
    ): Response<Unit>

    // ===== UI SPECIFIC =====
    @GET("api/goals/dashboard")
    suspend fun getDashboardGoals(): Response<List<GoalResponse>>

    @GET("api/goals/home")
    suspend fun getHomeData(): Response<HomeResponse>


}