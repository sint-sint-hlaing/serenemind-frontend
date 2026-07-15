package com.serenemind.repository

import com.serenemind.model.request.MoodRequest
import com.serenemind.network.ApiService
import retrofit2.Response

class MoodRepository(private val apiService: ApiService) {
    suspend fun saveMood(request: MoodRequest): Response<Unit> {
        return apiService.saveMood(request)
    }

    suspend fun getMoodSummary(): Map<String, Double> {
        val response = apiService.getMoodSummary()
        return if (response.isSuccessful) {
            response.body() ?: emptyMap()
        } else {
            emptyMap()
        }
    }

    suspend fun getMoodHistory(year: Int, month: Int) = apiService.getMoodHistory(year, month)
}