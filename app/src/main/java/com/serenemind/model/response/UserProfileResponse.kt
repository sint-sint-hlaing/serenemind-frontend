package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("fullname") val fullname: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("profileCompletionPercentage") val profileCompletionPercentage: Int?
)
