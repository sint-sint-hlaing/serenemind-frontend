package com.serenemind.model.request
import com.google.gson.annotations.SerializedName
data class MeditationSessionRequest(
    @SerializedName("meditationId")
    val meditationId: Long,

    @SerializedName("durationMinutes")
    val durationMinutes: Int? = null,

    @SerializedName("completed")
    val completed: Boolean = true,

    @SerializedName("progressPercentage")
    val progressPercentage: Int? = null
)
