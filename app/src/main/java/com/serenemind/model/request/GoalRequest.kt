package com.serenemind.model.request

data class GoalRequest(

    val title: String,

    val description: String?,

    val targetDays: Int

)