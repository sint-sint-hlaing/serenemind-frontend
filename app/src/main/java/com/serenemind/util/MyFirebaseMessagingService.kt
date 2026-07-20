package com.serenemind.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.serenemind.MainActivity
import com.serenemind.R
import com.serenemind.ReminderAlarmActivity
import com.serenemind.datastore.TokenManager
import com.serenemind.network.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM_Service"
        const val REMINDER_CHANNEL_ID = "serenemind_gentle_reminders"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received: ${remoteMessage.data}")

        val data = remoteMessage.data
        val targetType = data["targetType"] ?: "GENERAL"
        val title = data["title"] ?: remoteMessage.notification?.title ?: "SereneMind"
        val body = data["body"] ?: remoteMessage.notification?.body ?: ""
        
        // Try multiple potential ID keys from server
        val id = data["id"]?.toLongOrNull() 
            ?: data["targetId"]?.toLongOrNull() 
            ?: data["reminderId"]?.toLongOrNull() 
            ?: System.currentTimeMillis()

        if (targetType == "REMINDER") {
            // Handle Reminder with Full Screen Alert and Buttons
            handleReminderNotification(id, title, body)
            
            // Server က status ကို update လုပ်ပြီးသားဖြစ်လို့ App ဘက်က UI ကိုပဲ refresh လုပ်ခိုင်းရပါမယ်
            CoroutineScope(Dispatchers.Main).launch {
                com.serenemind.util.RefreshSignals.signalRefreshReminders()
            }
        } else {
            // Handle General Notifications
            showGeneralNotification(id, title, body, targetType)
        }
    }

    private fun handleReminderNotification(id: Long, title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createReminderChannel(notificationManager)

        // Clean title if it contains "Reminder: " prefix from server
        val cleanTitle = title.replace("Reminder: ", "", ignoreCase = true).trim()

        // Intent for Full Screen Alarm
        val fullScreenIntent = Intent(this, ReminderAlarmActivity::class.java).apply {
            putExtra("id", id)
            putExtra("title", "It's time to " + cleanTitle)
            putExtra("note", "SereneMind Reminder")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, id.toInt() + 100, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Quick Actions
        val dismissIntent = Intent(this, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_DISMISS
            putExtra("id", id)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            this, id.toInt() + 200, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setColor(0xFF6750A4.toInt())
            .setContentTitle("Mental Health Reminder")
            .setContentText(cleanTitle)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(0, "Dismiss", dismissPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(id.toInt(), notification)
    }

    private fun showGeneralNotification(id: Long, title: String, body: String, type: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "serenemind_general_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "General Updates", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("targetId", id)
            putExtra("targetType", type)
        }
        val pendingIntent = PendingIntent.getActivity(this, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id.toInt(), notification)
    }

    private fun createReminderChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(REMINDER_CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Mental Health Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "High priority wellness alerts"
                    enableVibration(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token: $token")
    }
}
