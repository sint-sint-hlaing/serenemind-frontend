package com.serenemind.model.response
import com.google.gson.annotations.SerializedName
data class ShareResponse(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("shareUrl")
    val shareUrl: String? = null
)
