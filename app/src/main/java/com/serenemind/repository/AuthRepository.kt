package com.serenemind.repository

import com.serenemind.model.request.ForgotPasswordRequest
import com.serenemind.model.request.LoginRequest
import com.serenemind.model.request.RegisterRequest
import com.serenemind.model.request.ResetPasswordRequest
import com.serenemind.model.response.ForgotPasswordResponse
import com.serenemind.model.response.LoginResponse
import com.serenemind.network.ApiService
import com.serenemind.network.NetworkResult
import com.serenemind.network.SafeApiCall
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val apiService: ApiService
) : SafeApiCall() {

    fun login(request: LoginRequest): Flow<NetworkResult<LoginResponse>> = safeApiCall {
        apiService.login(request)
    }

    fun register(request: RegisterRequest): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.register(request)
    }

    fun forgotPassword(request: ForgotPasswordRequest): Flow<NetworkResult<ForgotPasswordResponse>> = safeApiCall {
        apiService.forgotPassword(request)
    }

    fun resetPassword(request: ResetPasswordRequest): Flow<NetworkResult<Unit>> = safeApiCall {
        apiService.resetPassword(request)
    }
}
