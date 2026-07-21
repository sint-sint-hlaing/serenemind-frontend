package com.serenemind.model.request

data class GoalRequest(
    val title: String,
    val description: String? = null,
    val target: Int,
    val unit: String? = null,
    val frequency: String? = "DAILY",
    val color: String? = null,
    val reminderTime: String? = null,
    val startDate: String? = null
)
