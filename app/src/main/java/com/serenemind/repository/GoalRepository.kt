package com.serenemind.repository

import com.serenemind.model.request.GoalRequest
import com.serenemind.model.response.GoalStatistics
import com.serenemind.model.response.UserGoal
import com.serenemind.network.ApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val apiService: ApiService) : SafeApiCall() {

    fun getAllGoals(): Flow<NetworkResult<List<UserGoal>>> = safeApiCall {
        apiService.getAllGoals()
    }

    fun getActiveGoals(): Flow<NetworkResult<List<UserGoal>>> = safeApiCall {
        apiService.getActiveGoals()
    }

    fun createGoal(request: GoalRequest): Flow<NetworkResult<UserGoal>> = safeApiCall {
        apiService.createGoal(request)
    }

    fun updateProgress(id: Long): Flow<NetworkResult<UserGoal>> = safeApiCall {
        apiService.updateGoalProgress(id)
    }

    fun completeGoal(id: Long): Flow<NetworkResult<UserGoal>> = safeApiCall {
        apiService.completeGoal(id)
    }

    fun deleteGoal(id: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.deleteGoal(id)
    }

    fun getGoalStatistics(): Flow<NetworkResult<GoalStatistics>> = safeApiCall {
        apiService.getGoalStatistics()
    }
}
