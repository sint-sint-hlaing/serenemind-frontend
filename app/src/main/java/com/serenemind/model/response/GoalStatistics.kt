package com.serenemind.model.response

data class GoalStatistics(
    val totalGoals: Int,
    val activeGoals: Int,
    val completedGoals: Int,
    val overallProgress: Int
)
