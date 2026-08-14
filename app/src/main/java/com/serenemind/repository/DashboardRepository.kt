package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.network.ApiService
import com.serenemind.model.response.DashboardResponse
import com.serenemind.network.MoodApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow

class DashboardRepository(
    private val apiService: MoodApiService,
    private val tokenManager: TokenManager
) : SafeApiCall() {
    fun getDashboardData(): Flow<NetworkResult<DashboardResponse>> = safeApiCall {
        apiService.getDashboard()
    }

}
