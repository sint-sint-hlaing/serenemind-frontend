package com.serenemind.network

import com.serenemind.model.request.GoalRequest
import com.serenemind.model.response.UserGoal
import retrofit2.Response
import retrofit2.http.*


interface GoalApiService {


    @POST("api/goals")
    suspend fun createGoal(
        @Body request: GoalRequest
    ): Response<UserGoal>



    @GET("api/goals")
    suspend fun getAllGoals():
            Response<List<UserGoal>>



    @PUT("api/goals/{id}")
    suspend fun updateProgress(
        @Path("id") id: Long
    ): Response<UserGoal>



    @POST("api/goals/{id}/complete")
    suspend fun completeGoal(
        @Path("id") id: Long
    ): Response<UserGoal>



    @DELETE("api/goals/{id}")
    suspend fun deleteGoal(
        @Path("id") id: Long
    ): Response<Void>

}