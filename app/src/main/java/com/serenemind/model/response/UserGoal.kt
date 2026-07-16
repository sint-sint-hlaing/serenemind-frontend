package com.serenemind.model.response

import com.serenemind.model.entity.enums.GoalStatus

data class UserGoal(
    val id: Long,
    val title: String,
    val description: String?,
    val targetDays: Int,
    val progress: Int,
    val status: GoalStatus
)
