package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("birthday") val birthday: String? = null,
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null,
    @SerializedName("avatarId") val avatarId: Long? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    
    // Legacy fields for backward compatibility
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("profileCompletionPercentage") val profileCompletionPercentage: Int? = 0
)
