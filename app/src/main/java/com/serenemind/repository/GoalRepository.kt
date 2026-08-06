package com.serenemind.repository

import com.serenemind.model.request.GoalRequest
import com.serenemind.model.response.GoalStatistics
import com.serenemind.model.response.UserGoal
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class GoalRepository(private val apiService: ApiService) {

    fun getAllGoals() = flow {
        try {
            emit(apiService.getAllGoals())
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getActiveGoals() = flow {
        try {
            emit(apiService.getActiveGoals())
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun createGoal(request: GoalRequest) = flow {
        try {
            emit(apiService.createGoal(request))
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun updateProgress(id: Long): Response<UserGoal> {
        return try {
            apiService.updateGoalProgress(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun completeGoal(id: Long): Response<UserGoal> {
        return try {
            apiService.completeGoal(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun deleteGoal(id: Long): Response<Unit> {
        return try {
            apiService.deleteGoal(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    fun getGoalStatistics() = flow {
        try {
            emit(apiService.getGoalStatistics())
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }
}
