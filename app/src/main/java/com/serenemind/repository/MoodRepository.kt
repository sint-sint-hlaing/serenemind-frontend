package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.MoodRequest
import com.serenemind.model.response.DailyMoodResponse
import com.serenemind.model.response.WeeklyMoodResponse
import com.serenemind.network.ApiService
import retrofit2.Response

class MoodRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun saveMood(request: MoodRequest): Response<Unit> {
        return apiService.saveMood(request)
    }

    suspend fun getMoodSummary(): Map<String, Double> {
        return try {
            val response = apiService.getMoodSummary()
            if (response.isSuccessful) {
                response.body() ?: emptyMap()
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun getMoodHistory(year: Int, month: Int) = apiService.getMoodHistory(year, month)

    suspend fun getMoodByDate(date: String) = apiService.getMoodByDate(date)

    suspend fun getWeeklyMood() = apiService.getWeeklyMood()

    suspend fun getMonthlyMood() = apiService.getMonthlyMood()

    suspend fun getWeeklySummary(): Response<WeeklyMoodResponse> = apiService.getWeeklySummary()

    suspend fun deleteMood(id: Long) = apiService.deleteMood(id)
}