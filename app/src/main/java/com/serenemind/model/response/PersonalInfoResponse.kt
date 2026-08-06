package com.serenemind.model.response

data class PersonalInfoResponse(
    val fullname: String?,
    val email: String?,
    val username: String?,
    val birthday: String?,
    val accountStatus: String?,
    val role: String?,
    val memberSince: String?
)
