package com.serenemind.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.serenemind.MainActivity
import com.serenemind.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Received message from: ${remoteMessage.from}")

        // 👈 Noti ၂ စောင်မပွားစေရန် အရေးကြီးသော Logic
        // App က Background မှာဆိုရင် System က notification block ကို သူ့အလိုလို ပြပေးမှာဖြစ်လို့
        // ဒီ function ထဲကနေ ထပ်မပြအောင် return ပြန်ထုတ်လိုက်ပါတယ်။
        if (remoteMessage.notification != null) {
            Log.d("FCM", "Notification block found. Letting Android OS handle it.")
            return
        }

        try {
            val data = remoteMessage.data
            // Data-only message ဖြစ်ခဲ့ရင် (ဥပမာ- App ပွင့်နေချိန် သို့မဟုတ် data သီးသန့်ပို့ချိန်) မှသာ ပြပေးပါမယ်
            if (data.isNotEmpty()) {
                val targetId = data["targetId"]?.toLongOrNull() ?: System.currentTimeMillis()
                val title = data["title"] ?: "SereneMind"
                val body = data["body"] ?: ""
                
                if (title.isNotBlank() || body.isNotBlank()) {
                    showNotification(title, body, targetId)
                }
            }
        } catch (e: Exception) {
            Log.e("FCM", "Error processing message", e)
        }
    }

    private fun showNotification(title: String, body: String, id: Long) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel_v2"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mental Health Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Gentle reminders for your wellness"
                enableVibration(true)
                val defaultSoundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = android.media.AudioAttributes.Builder()
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(defaultSoundUri, audioAttributes)
                setShowBadge(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setColor(0xFF6750A4.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id.toInt(), notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
    }
}
