package com.serenemind.model.response

data class ProgressHistoryItem(
    val date: String,
    val completed: Boolean,
    val notes: String?,
    val value: Double
)
