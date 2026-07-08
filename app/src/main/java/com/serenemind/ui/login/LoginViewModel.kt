package com.serenemind.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serenemind.datastore.TokenManager
import com.serenemind.model.request.LoginRequest
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

    fun login(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Empty fields")
            return
        }

        viewModelScope.launch {

            _uiState.value = LoginUiState.Loading

            val result = repository.login(
                LoginRequest(email, password)
            )

            result.onSuccess { response ->

                tokenManager.saveToken(response.accessToken)

                _uiState.value = LoginUiState.Success
            }

            result.onFailure {
                _uiState.value = LoginUiState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun reset() {
        _uiState.value = LoginUiState.Idle
    }
}