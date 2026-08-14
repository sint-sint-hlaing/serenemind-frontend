package com.serenemind.model.response
import com.google.gson.annotations.SerializedName
data class FavoriteResponse(
    @SerializedName("favorite")
    val favorite: Boolean = false,

    @SerializedName("message")
    val message: String? = null
)
