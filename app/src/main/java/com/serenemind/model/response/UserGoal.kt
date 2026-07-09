package com.serenemind.model.response

data class UserGoal(
    val id: Long,
    val title: String,
    val description: String?,
    val targetDays: Int,
    val progress: Int,
    val status: String // Use a String or your GoalStatus enum
)
