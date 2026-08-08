package com.serenemind.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.LoginRequest
import com.serenemind.model.request.RegisterRequest
import com.serenemind.network.NetworkResult
import com.serenemind.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String, fcmToken: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Email and password are required")
            return
        }

        if (fcmToken.isBlank()) {
            _uiState.value = LoginUiState.Error("FCM Token is required by the server. Please check your internet or Firebase setup.")
            return
        }

        viewModelScope.launch {
            repository.login(LoginRequest(email.trim(), password, fcmToken)).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = LoginUiState.Loading
                    is NetworkResult.Success -> {
                        tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                        _uiState.value = LoginUiState.Success
                    }
                    is NetworkResult.Error -> _uiState.value = LoginUiState.Error(result.message)
                }
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Empty fields")
            return
        }

        viewModelScope.launch {
            repository.register(RegisterRequest(username, email, password)).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.value = LoginUiState.Loading
                    is NetworkResult.Success -> _uiState.value = LoginUiState.RegisterSuccess
                    is NetworkResult.Error -> _uiState.value = LoginUiState.Error(result.message)
                }
            }
        }
    }

    fun reset() {
        _uiState.value = LoginUiState.Idle
    }
}
