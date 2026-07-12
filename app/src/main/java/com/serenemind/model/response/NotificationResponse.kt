package com.serenemind.model.response

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    val id: Long,
    val title: String? = null,
    val message: String? = null,
    val type: String? = null,
    val createdAt: String? = null,
    // Backend က read သို့မဟုတ် isRead နာမည်နှစ်မျိုးလုံးနဲ့ ပို့နိုင်လို့ alternate ထည့်ထားပါတယ်
    @SerializedName("isRead", alternate = ["read"]) val isRead: Boolean = false,
    val targetId: Long? = null,
    val targetType: String? = null // POST, COMMENT, REMINDER, SYSTEM
)
