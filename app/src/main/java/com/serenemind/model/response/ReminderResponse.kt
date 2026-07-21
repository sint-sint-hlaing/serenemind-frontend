package com.serenemind.model.response

data class ReminderResponse(
    val id: Long,
    val title: String,
    val repeatType: String,
    val reminderTime: String,
    val startDate: String,
    val reminderTone: String,
    val note: String?,
    val enabled: Boolean,
    val repeatDays: String? = null
)
