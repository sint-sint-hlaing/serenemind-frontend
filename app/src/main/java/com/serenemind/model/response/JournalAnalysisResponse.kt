package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class JournalAnalysisResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("emotion") val emotion: String?,
    @SerializedName("sentiment") val sentiment: String?,
    @SerializedName("stressLevel") val stressLevel: String?,
    @SerializedName("stressScore") val stressScore: Int?,
    @SerializedName("keyThemes") val keyThemes: List<String>?,
    @SerializedName("aiResponse") val aiResponse: String?,
    @SerializedName("aiSuggestion") val aiSuggestion: String?,
    @SerializedName("analysedAt") val analysedAt: String?
)
