package com.serenemind.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.LoginRequest
import com.serenemind.model.request.RegisterRequest
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

    fun login(email: String, password: String, fcmToken: String = "") {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Empty fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            
            // Backend requires a non-blank FCM token. 
            // If token is missing (e.g. Google Play Services not ready), send a dummy identifier.
            val finalFcmToken = if (fcmToken.isBlank()) "android_dummy_token_${System.currentTimeMillis()}" else fcmToken
            
            val result = repository.login(LoginRequest(email.trim(), password, finalFcmToken))
            result.onSuccess { response ->
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
                _uiState.value = LoginUiState.Success
            }
            result.onFailure {
                _uiState.value = LoginUiState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Empty fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = repository.register(RegisterRequest(username, email, password))
            result.onSuccess {
                _uiState.value = LoginUiState.RegisterSuccess
            }
            result.onFailure {
                _uiState.value = LoginUiState.Error(it.message ?: "Registration failed")
            }
        }
    }

    fun reset() {
        _uiState.value = LoginUiState.Idle
    }
}
