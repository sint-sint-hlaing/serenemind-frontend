package com.serenemind.network

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("status") val status: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("errors") val errors: Map<String, String>? = null,
    @SerializedName("timestamp") val timestamp: String? = null
) {
    /**
     * Helper function to join validation errors into a single string
     * or fallback to the main message.
     */
    fun getDisplayMessage(): String {
        return if (!errors.isNullOrEmpty()) {
            errors.values.joinToString("\n")
        } else {
            message ?: "An unexpected error occurred"
        }
    }
}
