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

    suspend fun getRecommendations(): Response<List<Meditation>> = apiService.getRecommendations()

    suspend fun search(keyword: String): Response<List<Meditation>> = apiService.search(keyword)

    suspend fun addFavorite(meditationId: Long): Response<Unit> = apiService.addFavorite(mapOf("meditationId" to meditationId))

    suspend fun getContinueListening(): Response<List<Meditation>> = apiService.getContinueListening()
}
