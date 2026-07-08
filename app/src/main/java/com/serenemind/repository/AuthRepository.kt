package com.serenemind.repository

import com.serenemind.model.request.LoginRequest
import com.serenemind.model.response.LoginResponse
import com.serenemind.network.ApiService

class AuthRepository(
    private val apiService: ApiService
) {

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {

            val response = apiService.login(request)

            if (response.isSuccessful) {

                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response"))

            } else {
                Result.failure(Exception("Invalid credentials"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}