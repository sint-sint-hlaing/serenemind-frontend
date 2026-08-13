package com.serenemind.ui.login

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data object RegisterSuccess : LoginUiState
    data class Error(
        val message: String,
        val fieldErrors: Map<String, String>? = null
    ) : LoginUiState
}
