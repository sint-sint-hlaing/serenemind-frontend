package com.serenemind.model.response
import com.google.gson.annotations.SerializedName
data class MeditationDashboardResponse (
    @SerializedName("categories")
    val categories: List<MeditationCategoryResponse>? = null,

    @SerializedName("times")
    val times: List<MeditationTimeResponse>? = null,

    @SerializedName("featured")
    val featured: List<MeditationResponse>? = null,

    @SerializedName("popular")
    val popular: List<MeditationResponse>? = null,

    @SerializedName("recent")
    val recent: List<MeditationResponse>? = null,

    @SerializedName("statistics")
    val statistics: MeditationStatistics? = null
)
