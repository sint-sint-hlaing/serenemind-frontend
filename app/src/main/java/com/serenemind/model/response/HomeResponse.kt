package com.serenemind.model.response
import com.google.gson.annotations.SerializedName

data class HomeResponse (
    @SerializedName("goals")
    val goals: List<GoalResponse>? = null,

    @SerializedName("totalGoals")
    val totalGoals: Int? = null,

    @SerializedName("activeGoals")
    val activeGoals: Int? = null,

    @SerializedName("completedGoals")
    val completedGoals: Int? = null,

    @SerializedName("totalStreak")
    val totalStreak: Int? = null,

    @SerializedName("currentStreak")
    val currentStreak: Int? = null
)
