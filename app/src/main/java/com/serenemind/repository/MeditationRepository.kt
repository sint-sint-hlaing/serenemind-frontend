package com.serenemind.repository

import com.serenemind.model.request.MeditationSessionRequest
import com.serenemind.model.response.MeditationDashboardResponse
import com.serenemind.model.response.MeditationResponse
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class MeditationRepository(private val apiService: ApiService) {

    fun getDashboard() = flow {
        try {
            emit(apiService.getMeditationDashboard())
        } catch (e: Exception) {
            emit(Response.error<MeditationDashboardResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    fun getMeditationById(id: Long) = flow {
        try {
            emit(apiService.getMeditationById(id))
        } catch (e: Exception) {
            emit(Response.error<MeditationResponse>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }

    suspend fun completeSession(request: MeditationSessionRequest): Response<Unit> {
        return try {
            apiService.completeMeditationSession(request)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Network Error"))
        }
    }

    fun getHistory() = flow {
        try {
            emit(apiService.getMeditationHistory())
        } catch (e: Exception) {
            emit(Response.error<List<MeditationResponse>>(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }
}
