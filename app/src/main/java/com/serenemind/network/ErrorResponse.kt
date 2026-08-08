package com.serenemind.network

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("errors") val errors: Map<String, List<String>>? = null
)
