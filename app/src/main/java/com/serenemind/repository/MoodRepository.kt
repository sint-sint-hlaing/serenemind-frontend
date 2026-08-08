package com.serenemind.repository

import com.serenemind.model.request.MoodRequest
import com.serenemind.model.response.DailyMoodResponse
import com.serenemind.model.response.WeeklyMoodResponse
import com.serenemind.network.ApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow

class MoodRepository(private val apiService: ApiService) : SafeApiCall() {

    fun saveMood(request: MoodRequest): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.saveMood(request)
    }

    fun getMoodHistory(year: Int, month: Int): Flow<NetworkResult<List<DailyMoodResponse>>> = safeApiCall {
        apiService.getMoodHistory(year, month)
    }

    fun getWeeklySummary(): Flow<NetworkResult<WeeklyMoodResponse>> = safeApiCall {
        apiService.getWeeklySummary()
    }

    fun getWeeklyMood(): Flow<NetworkResult<List<WeeklyMoodResponse>>> = safeApiCall {
        apiService.getWeeklyMood()
    }

    fun getMoodSummary(): Flow<NetworkResult<Map<String, Double>>> = safeApiCall {
        apiService.getMoodSummary()
    }
}
