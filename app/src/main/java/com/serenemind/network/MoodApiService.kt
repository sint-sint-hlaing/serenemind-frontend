package com.serenemind.network

import com.serenemind.model.request.MoodRequest
import com.serenemind.model.response.DailyMoodResponse
import com.serenemind.model.response.DashboardResponse
import com.serenemind.model.response.WeeklyMoodResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MoodApiService {
    @GET("api/dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>

    @POST("api/mood/save")
    suspend fun saveMood(@Body request: MoodRequest): Response<Unit>

    @GET("api/mood/summary")
    suspend fun getMoodSummary(): Response<Map<String, Double>>

    @GET("api/mood/history")
    suspend fun getMonthlyHistory(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<List<DailyMoodResponse>>

    // ===== GET MOOD BY DATE =====
    @GET("api/mood/date/{date}")
    suspend fun getMoodByDate(
        @Path("date") date: String
    ): Response<DailyMoodResponse>
    @GET("api/mood/weekly")
    suspend fun getWeeklyMood(): Response<List<WeeklyMoodResponse>>

    // ===== GET MONTHLY MOOD =====
    @GET("api/mood/monthly")
    suspend fun getMonthlyMood(): Response<List<DailyMoodResponse>>

    // ===== GET WEEKLY SUMMARY =====
    @GET("api/mood/summary/week")
    suspend fun getWeeklySummary(): Response<WeeklyMoodResponse>

    // ===== DELETE MOOD =====
    @DELETE("api/mood/delete/{id}")
    suspend fun deleteMood(
        @Path("id") id: Long
    ): Response<Unit>


}

