package com.serenemind.repository

import com.serenemind.model.request.MoodRequest
import com.serenemind.model.response.DailyMoodResponse
import com.serenemind.model.response.WeeklyMoodResponse
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class MoodRepository(private val apiService: ApiService) {

    suspend fun saveMood(request: MoodRequest): Response<Unit> {
        return try {
            apiService.saveMood(request)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    fun getMoodHistory(year: Int, month: Int) = flow {
        try {
            emit(apiService.getMoodHistory(year, month))
        } catch (e: Exception) {
            emit(Response.error<List<DailyMoodResponse>>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getWeeklySummary() = flow {
        try {
            emit(apiService.getWeeklySummary())
        } catch (e: Exception) {
            emit(Response.error<WeeklyMoodResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getWeeklyMood() = flow {
        try {
            emit(apiService.getWeeklyMood())
        } catch (e: Exception) {
            emit(Response.error<List<WeeklyMoodResponse>>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getMoodSummary() = flow {
        try {
            emit(apiService.getMoodSummary())
        } catch (e: Exception) {
            emit(Response.error<Map<String, Double>>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }
}
