package com.serenemind.model.response

import com.serenemind.model.entity.enums.GoalStatus

data class UserGoal(
    val id: Long,
    val username: String? = null,
    val email: String? = null,
    val title: String,
    val description: String? = null,
    val targetDays: Int,
    val targetDate: String? = null,
    val progress: Int,
    val status: GoalStatus,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val completedAt: String? = null,
    
    // UI helpers (legacy or additional)
    val streak: Int = 0,
    val frequency: String = "DAILY",
    val color: String? = null,
    val reminderTime: String? = null,
    val startDate: String? = null,
    val history: List<GoalHistoryDto>? = null
)

data class GoalHistoryDto(
    val date: String,
    val completed: Boolean
)
