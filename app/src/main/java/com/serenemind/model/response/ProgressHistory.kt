package com.serenemind.model.response
import com.google.gson.annotations.SerializedName

data class ProgressHistory(
    @SerializedName("date")
    val date: String? = null,

    @SerializedName("completed")
    val completed: Boolean? = null,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("value")
    val value: Double? = null
)
