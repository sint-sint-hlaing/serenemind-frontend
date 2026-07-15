package com.serenemind.model.response

data class DashboardResponse(
    val username: String,
    val greeting: String,
    val date: String,
    val todayMood: TodayMoodResponse,
    val weeklyOverview: List<WeeklyMoodResponse>,
    val quickActions: List<QuickActionResponse>
)

data class TodayMoodResponse(
    val mood: String,
    val percentage: Int,
    val message: String
)

data class WeeklyMoodResponse(
    val day: String,
    val mood: String,
    val percentage: Int
)

data class QuickActionResponse(
    val title: String,
    val route: String,
    val icon: String
)
