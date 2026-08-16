package com.serenemind.model.response
import com.google.gson.annotations.SerializedName
data class MeditationResponse(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("time")
    val time: String? = null,

    @SerializedName("duration")
    val duration: String? = null,

    @SerializedName("durationSeconds")
    val durationSeconds: Int? = null,

    @SerializedName("audioUrl")
    val audioUrl: String? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("difficulty")
    val difficulty: Int? = null,

    @SerializedName("premium")
    val premium: Boolean = false,

    @SerializedName("listenCount")
    val listenCount: Long? = null,

    @SerializedName("favoriteCount")
    val favoriteCount: Long? = null,

    @SerializedName("favorite")
    val favorite: Boolean = false
) {
    /**
     * Extracts actual URL if it's in Markdown format like [text](url)
     * Handles relative paths if necessary.
     */
    fun getCleanImageUrl(): String? {
        if (imageUrl == null) return null
        val markdownRegex = "\\[.*?\\]\\((.*?)\\)".toRegex()
        val match = markdownRegex.find(imageUrl)
        val url = match?.groupValues?.get(1) ?: imageUrl
        
        return if (url.startsWith("http")) url 
        else "http://10.0.2.2:8080" + (if (url.startsWith("/")) "" else "/") + url
    }

    /**
     * Extracts actual URL if it's in Markdown format like [text](url)
     * Handles relative paths if necessary.
     */
    fun getCleanAudioUrl(): String? {
        if (audioUrl == null) return null
        val markdownRegex = "\\[.*?\\]\\((.*?)\\)".toRegex()
        val match = markdownRegex.find(audioUrl)
        val url = match?.groupValues?.get(1) ?: audioUrl
        
        return if (url.startsWith("http")) url 
        else "http://10.0.2.2:8080" + (if (url.startsWith("/")) "" else "/") + url
    }
}


