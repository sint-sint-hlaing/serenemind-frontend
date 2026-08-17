package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class UserActivityResponse(
    @SerializedName("totalJournals")
    val totalJournals: Int,
    
    @SerializedName("goalsCompleted")
    val goalsCompleted: Int,
    
    @SerializedName("totalPosts")
    val totalPosts: Int,
    
    @SerializedName("completedMeditationsCount")
    val completedMeditationsCount: Int,
    
    @SerializedName("favoriteMeditationsCount")
    val favoriteMeditationsCount: Int
)
