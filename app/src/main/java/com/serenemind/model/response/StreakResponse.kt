package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class StreakResponse(
    @SerializedName("currentStreak") val currentStreak: Int,
    @SerializedName("longestStreak") val longestStreak: Int,
    @SerializedName("totalCompletedDays") val totalCompletedDays: Int,
    @SerializedName("streakFreezeCount") val streakFreezeCount: Int,
    @SerializedName("isNewBest") val isNewBest: Boolean,
    @SerializedName("weeklyOverview") val weeklyOverview: List<Boolean>
)
