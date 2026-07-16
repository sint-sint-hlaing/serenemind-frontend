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

    fun getDashboardData(): Flow<Response<DashboardResponse>> = flow {
        try {
            val response = apiService.getDashboardData()
            emit(response)
        } catch (e: Exception) {
            emit(Response.error(500, okhttp3.ResponseBody.create(null, "Network Error")))
        }
    }
}
