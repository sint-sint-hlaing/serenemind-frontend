package com.serenemind.model.response

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.serenemind.model.entity.enums.MoodType
import com.serenemind.network.LocalDateAdapter
import java.time.LocalDate

data class DashboardResponse(
    val username: String? = null,
    val greeting: String? = null,
    @JsonAdapter(LocalDateAdapter::class)
    val date: LocalDate? = null,
    val todayMood: TodayMoodResponse? = null,
    val weeklyOverview: List<WeeklyMoodResponse>? = null,
    val quickActions: List<QuickActionResponse>? = null,
    val currentStreak: Int? = null,
    val isNewBest: Boolean? = null,
    val unreadNotificationCount: Long = 0
)

data class TodayMoodResponse(
    val mood: MoodType? = null,
    val percentage: Int? = null,
    val message: String? = null
)

data class QuickActionResponse(
    @SerializedName("name") val title: String? = "",
    val route: String? = "",
    val icon: String? = ""
)
