package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("avatar") val avatar: String,
    @SerializedName("birthday") val birthday: String,
    @SerializedName("email") val email: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("profileCompletionPercentage") val profileCompletionPercentage: Int,
    @SerializedName("username") val username: String
)
