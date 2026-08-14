package com.serenemind.model.response
import com.google.gson.annotations.SerializedName
data class MeditationCategoryResponse (
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("displayName")
    val displayName: String? = null,

    @SerializedName("emoji")
    val emoji: String? = null,

    @SerializedName("description")
    val description: String? = null
)
