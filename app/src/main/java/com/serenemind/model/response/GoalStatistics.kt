package com.serenemind.model.response

data class GoalStatistics(
    val total: Long,
    val active: Long,
    val paused: Long,
    val completed: Long,
    val expired: Long,
    val cancelled: Long,
    val completionRate: Double,
    val totalProgress: Long
)
