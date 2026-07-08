package com.serenemind.model.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)