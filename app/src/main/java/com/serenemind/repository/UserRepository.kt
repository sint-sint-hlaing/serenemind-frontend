package com.serenemind.repository

import com.serenemind.datastore.TokenManager
import com.serenemind.model.response.UserProfileResponse
import com.serenemind.network.ApiService
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class UserRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    fun getUserProfile() = flow {
        val token = tokenManager.getToken()
        if (token != null) {
            try {
                val response = apiService.getUserProfile("Bearer $token")
                emit(response)
            } catch (e: Exception) {
                emit(Response.error<UserProfileResponse>(500, okhttp3.ResponseBody.create(null, "")))
            }
        } else {
            emit(Response.error<UserProfileResponse>(401, okhttp3.ResponseBody.create(null, "")))
        }
    }
}