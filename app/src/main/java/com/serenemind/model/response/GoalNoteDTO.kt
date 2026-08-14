package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class GoalNoteDTO(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null)