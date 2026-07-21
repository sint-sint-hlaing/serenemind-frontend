package com.serenemind.model.response

import com.serenemind.model.entity.enums.GoalStatus

data class UserGoal(
    val id: Long,
    val title: String,
    val description: String?,
    val target: Int,
    val unit: String?,
    val progress: Int,
    val streak: Int,
    val frequency: String,
    val status: GoalStatus,
    val color: String?,
    val reminderTime: String?,
    val startDate: String?,
    val history: List<GoalHistoryDto>? = null
)

data class GoalHistoryDto(
    val date: String,
    val completed: Boolean
)
