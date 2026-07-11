package com.serenemind.repository

import com.serenemind.model.response.UserGoal
import com.serenemind.network.GoalApiService
import retrofit2.Response

class GoalRepository(private val apiService: GoalApiService) {
    suspend fun getAllGoals(): Response<List<UserGoal>> = apiService.getAllGoals()
    
    suspend fun updateProgress(id: Long): Response<UserGoal> = apiService.updateProgress(id)

    suspend fun completeGoal(id: Long): Response<UserGoal> = apiService.completeGoal(id)

    suspend fun deleteGoal(id: Long): Response<Void> = apiService.deleteGoal(id)
}
