package com.serenemind.model.response
import com.google.gson.annotations.SerializedName
data class MeditationList(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("thumbnail")
    val thumbnail: String? = null,

    @SerializedName("duration")
    val duration: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("time")
    val time: String? = null
)
