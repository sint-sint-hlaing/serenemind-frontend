package com.serenemind.repository

import com.serenemind.model.request.MeditationSessionRequest
import com.serenemind.model.response.Meditation
import com.serenemind.model.response.MeditationDashboardResponse
import com.serenemind.network.MeditationApiService
import retrofit2.Response

class MeditationRepository(private val apiService: MeditationApiService) {
    suspend fun getMeditationDashboard(): Response<MeditationDashboardResponse> = apiService.getMeditationDashboard()

    suspend fun getMeditationById(id: Long): Response<Meditation> = apiService.getMeditationById(id)

    suspend fun completeSession(request: MeditationSessionRequest): Response<String> = apiService.completeSession(request)

    suspend fun getHistory(): Response<List<Meditation>> = apiService.getHistory()
}
