package com.serenemind.model.response
import com.google.gson.annotations.SerializedName

data class GoalStatistics(
    val total: Long,
    val active: Long,
    val paused: Long,
    val completed: Long,
    val expired: Long,
    val cancelled: Long,
    val completionRate: Double,
    val totalProgress: Long,
    @SerializedName("totalStreak")
    val totalStreak: Int? = null,

    @SerializedName("currentStreak")
    val currentStreak: Int? = null
)
