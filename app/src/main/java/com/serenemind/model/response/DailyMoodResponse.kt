package com.serenemind.model.response

data class DailyMoodResponse(
    val date: String,
    val mood: String,
    val intensity: Int,
    val note: String?
) {

}