package com.serenemind.network

import com.serenemind.model.response.UserGoal
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface GoalApiService {
        @GET("api/goals")
        suspend fun getAllGoals(): List<UserGoal>

        @PUT("api/goals/{id}")
        suspend fun updateProgress(@Path("id") id: Long): UserGoal
    }
