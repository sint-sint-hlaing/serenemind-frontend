package com.serenemind.util

import com.serenemind.R
import java.text.SimpleDateFormat
import java.util.*

fun getAvatarResource(avatarName: String?): Int {
    return when (avatarName) {
        "avatar-1", "avatar_1" -> R.drawable.avatar_1
        "avatar-2", "avatar_2" -> R.drawable.avatar_2
        else -> R.drawable.default_avatar
    }
}

fun formatPostDate(dateStr: String?): String {
    if (dateStr == null) return ""
    val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    )

    var parsedDate: Date? = null
    for (format in dateFormats) {
        try {
            parsedDate = format.parse(dateStr)
            if (parsedDate != null) break
        } catch (e: Exception) { }
    }

    return try {
        if (parsedDate != null) {
            val now = Date()
            val diff = now.time - parsedDate.time
            
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            when {
                diff < 0 -> "Just now"
                seconds < 60 -> "Just now"
                minutes < 60 -> "$minutes mins ago"
                hours < 24 -> "$hours hours ago"
                days < 7 -> "$days days ago"
                else -> {
                    val sdfOutput = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    sdfOutput.format(parsedDate)
                }
            }
        } else dateStr
    } catch (e: Exception) {
        dateStr
    }
}
