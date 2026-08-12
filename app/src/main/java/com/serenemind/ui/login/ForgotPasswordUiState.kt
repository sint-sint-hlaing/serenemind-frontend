package com.serenemind.ui.login

sealed class ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    object ForgotPasswordSuccess : ForgotPasswordUiState()
    object ResetPasswordSuccess : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
}
