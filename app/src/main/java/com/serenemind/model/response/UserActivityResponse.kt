package com.serenemind.model.response

data class UserActivityResponse(
    val totalJournals: Int,
    val goalsCompleted: Int,
    val totalPosts: Int
)
