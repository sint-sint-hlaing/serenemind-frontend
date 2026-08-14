package com.serenemind.repository

import com.serenemind.model.request.MoodRequest
import com.serenemind.model.response.DailyMoodResponse
import com.serenemind.model.response.WeeklyMoodResponse
import com.serenemind.network.ApiService
import com.serenemind.network.MoodApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow

class MoodRepository(private val apiService: MoodApiService) : SafeApiCall() {

    fun saveMood(request: MoodRequest): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.saveMood(request)
    }

    fun getMoodSummary(): Flow<NetworkResult<Map<String, Double>>> = safeApiCall {
        apiService.getMoodSummary()
    }

    fun getMoodHistory(year: Int, month: Int): Flow<NetworkResult<List<DailyMoodResponse>>> = safeApiCall {
        apiService.getMonthlyHistory(year, month)
    }

    fun getMoodByDate(date: String): Flow<NetworkResult<DailyMoodResponse>> = safeApiCall {
        apiService.getMoodByDate(date)
    }
    fun getWeeklyMood(): Flow<NetworkResult<List<WeeklyMoodResponse>>> = safeApiCall {
        apiService.getWeeklyMood()
    }

    fun getMonthlyMood(): Flow<NetworkResult<List<DailyMoodResponse>>> = safeApiCall {
        apiService.getMonthlyMood()
    }

    fun getWeeklySummary(): Flow<NetworkResult<WeeklyMoodResponse>> = safeApiCall {
        apiService.getWeeklySummary()
    }

    fun deleteMood(id: Long): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.deleteMood(id)
    }
}
