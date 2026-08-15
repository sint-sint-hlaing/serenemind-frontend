package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class DownloadUrlResponse(
    @SerializedName("downloadUrl")
    val downloadUrl: String
)
