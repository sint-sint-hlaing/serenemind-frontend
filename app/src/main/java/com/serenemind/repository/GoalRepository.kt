package com.serenemind.repository

import com.serenemind.model.request.GoalRequest
import com.serenemind.model.response.UserGoal
import com.serenemind.network.GoalApiService
import retrofit2.Response

class GoalRepository(private val apiService: GoalApiService) {
    suspend fun createGoal(request: GoalRequest): Response<UserGoal> {
        return try {
            apiService.createGoal(request)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun getAllGoals(): Response<List<UserGoal>> {
        return try {
            apiService.getAllGoals()
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }
    
    suspend fun updateProgress(id: Long): Response<UserGoal> {
        return try {
            apiService.updateProgress(id)
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

    suspend fun deleteGoal(id: Long): Response<Void> {
        return try {
            apiService.deleteGoal(id)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }
}
