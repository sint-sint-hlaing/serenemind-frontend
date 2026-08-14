package com.serenemind.model.response
import com.google.gson.annotations.SerializedName
data class MeditationHistoryResponse(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("duration")
    val duration: Int? = null,

    @SerializedName("completed")
    val completed: Boolean = false,

    @SerializedName("progressPercentage")
    val progressPercentage: Int? = null,

    @SerializedName("completedAt")
    val completedAt: String? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null
)
