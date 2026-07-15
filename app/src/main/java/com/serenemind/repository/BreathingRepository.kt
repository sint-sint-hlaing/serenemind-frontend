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
        return try {
            apiService.startBreathingSession(request)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun trackRoundComplete(sessionId: String, roundNumber: Int): Response<Unit> {
        return try {
            apiService.trackRoundComplete(sessionId, roundNumber)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    suspend fun completeSession(sessionId: String): Response<BreathingSummaryResponse> {
        return try {
            apiService.completeBreathingSession(sessionId)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }
}
