package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.BreathingRequest
import com.serenemind.model.response.BreathingStartResponse
import com.serenemind.model.response.BreathingSummaryResponse
import com.serenemind.network.ApiService
import retrofit2.Response

class BreathingRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun startSession(request: BreathingRequest): Response<BreathingStartResponse> {
        val token = tokenManager.getToken() ?: ""
        return apiService.startBreathingSession("Bearer $token", request)
    }

    suspend fun trackRoundComplete(sessionId: String, roundNumber: Int): Response<Unit> {
        val token = tokenManager.getToken() ?: ""
        return apiService.trackRoundComplete("Bearer $token", sessionId, roundNumber)
    }

    suspend fun completeSession(sessionId: String): Response<BreathingSummaryResponse> {
        val token = tokenManager.getToken() ?: ""
        return apiService.completeBreathingSession("Bearer $token", sessionId)
    }
}
