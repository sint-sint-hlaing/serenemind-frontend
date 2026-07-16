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
        emit(apiService.getStreak())
    }

    suspend fun useStreakFreeze(): Response<StreakResponse> {
        return apiService.useStreakFreeze()
    }
}
