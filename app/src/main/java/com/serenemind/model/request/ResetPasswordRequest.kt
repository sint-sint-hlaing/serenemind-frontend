package com.serenemind.model.request

data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)
