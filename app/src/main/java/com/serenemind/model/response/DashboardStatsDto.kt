package com.serenemind.model.response

data class DashboardStatsDto(
    val totalEntries: Int,
    val averageIntensity: Double,
    val topMood: String?,
    val streak: Int
)
