package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.network.ApiService
import com.serenemind.model.response.DashboardResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class DashboardRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    // API မှ Dashboard အချက်အလက်များကို Flow အနေဖြင့် ရယူခြင်း
    fun getDashboardData(): Flow<Response<DashboardResponse>> = flow {
        try {
            val token = tokenManager.getToken() ?: ""
            val response = apiService.getDashboardData("Bearer $token")
            emit(response)
        } catch (e: Exception) {
            // Rethrow so ViewModel can catch it
            throw e
        }
    }
}
