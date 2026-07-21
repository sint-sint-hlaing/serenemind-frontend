package com.serenemind.model.request

data class ReminderRequest(
    val title: String,
    val repeatType: String,
    val reminderTime: String,
    val startDate: String,
    val reminderTone: String,
    val note: String?,
    val enabled: Boolean,
    val repeatDays: String? = null
)
