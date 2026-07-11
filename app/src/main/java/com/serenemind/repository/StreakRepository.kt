package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.response.StreakResponse
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class StreakRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    fun getStreak(): Flow<Response<StreakResponse>> = flow {
        val token = tokenManager.getToken() ?: ""
        emit(apiService.getStreak("Bearer $token"))
    }

    suspend fun useStreakFreeze(): Response<StreakResponse> {
        val token = tokenManager.getToken() ?: ""
        return apiService.useStreakFreeze("Bearer $token")
    }
}
