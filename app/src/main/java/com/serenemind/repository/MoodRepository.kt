package com.serenemind.repository

import com.serenemind.model.request.MoodRequest
import com.serenemind.network.ApiClient.apiService
import retrofit2.Response

class MoodRepository {
    suspend fun saveMood(request: MoodRequest): Response<Unit> {
        return apiService.saveMood(request)
    }

    suspend fun getMoodSummary(): Map<String, Double> {
        // Make sure this returns the data, not just performs an action
        return apiService.getMoodSummary()
    }
}