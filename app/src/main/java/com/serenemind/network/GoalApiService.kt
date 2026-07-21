package com.serenemind.network

import com.serenemind.model.request.GoalRequest
import com.serenemind.model.response.GoalStatistics
import com.serenemind.model.response.UserGoal
import com.serenemind.model.entity.enums.GoalStatus
import retrofit2.Response
import retrofit2.http.*

interface GoalApiService {

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
    suspend fun updateProgress(@Path("id") id: Long): Response<UserGoal>

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
}
