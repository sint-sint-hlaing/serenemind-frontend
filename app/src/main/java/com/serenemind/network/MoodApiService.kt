package com.serenemind.network

import com.serenemind.model.request.MoodRequest
import com.serenemind.model.response.DailyMoodResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MoodApiService {
    @POST("api/mood/save")
    suspend fun saveMood(@Body request: MoodRequest): Response<Unit>

    @GET("api/mood/summary")
    suspend fun getMoodSummary(): Map<String, Double>

    @GET("api/mood/history/{year}/{month}")
    suspend fun getMoodHistory(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): Response<List<DailyMoodResponse>>
}

