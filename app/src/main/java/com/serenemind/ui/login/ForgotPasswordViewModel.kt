package com.serenemind.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.model.request.ForgotPasswordRequest
import com.serenemind.model.request.ResetPasswordRequest
import com.serenemind.network.NetworkResult
import com.serenemind.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _generatedToken = MutableStateFlow<String?>(null)
    val generatedToken = _generatedToken.asStateFlow()

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = ForgotPasswordUiState.Error("Email is required")
            return
        }

        viewModelScope.launch {
            repository.forgotPassword(ForgotPasswordRequest(email.trim())).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = ForgotPasswordUiState.Loading
                    is NetworkResult.Success -> {
                        _generatedToken.value = result.data.token
                        _uiState.value = ForgotPasswordUiState.ForgotPasswordSuccess
                    }
                    is NetworkResult.Error -> _uiState.value = ForgotPasswordUiState.Error(result.message)
                }
            }
        }
    }

    fun resetPassword(newPassword: String) {
        val token = _generatedToken.value
        if (token.isNullOrBlank() || newPassword.isBlank()) {
            _uiState.value = ForgotPasswordUiState.Error("Session expired or invalid password")
            return
        }

        viewModelScope.launch {
            repository.resetPassword(ResetPasswordRequest(token, newPassword)).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = ForgotPasswordUiState.Loading
                    is NetworkResult.Success -> {
                        _uiState.value = ForgotPasswordUiState.ResetPasswordSuccess
                        _generatedToken.value = null // Clear token after success
                    }
                    is NetworkResult.Error -> _uiState.value = ForgotPasswordUiState.Error(result.message)
                }
            }
        }
    }

    fun reset() {
        _uiState.value = ForgotPasswordUiState.Idle
    }
}
