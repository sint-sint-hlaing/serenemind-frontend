package com.serenemind.model.request

data class UpdateProfileRequest(
    val fullname: String?,
    val email: String?,
    val bio: String?,
    val location: String?
)
