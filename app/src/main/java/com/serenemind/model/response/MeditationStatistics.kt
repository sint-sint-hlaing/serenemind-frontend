package com.serenemind.model.response
import com.google.gson.annotations.SerializedName
data class MeditationStatistics(
    @SerializedName("totalMeditations")
    val totalMeditations: Long? = null,

    @SerializedName("totalSessions")
    val totalSessions: Long? = null,

    @SerializedName("totalMinutes")
    val totalMinutes: Long? = null,

    @SerializedName("currentStreak")
    val currentStreak: Long? = null,

    @SerializedName("longestStreak")
    val longestStreak: Long? = null
)
