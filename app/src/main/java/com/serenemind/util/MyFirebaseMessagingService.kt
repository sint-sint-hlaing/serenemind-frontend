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
        const val REMINDER_CHANNEL_ID = "serenemind_reminders_v3"
        const val GENERAL_CHANNEL_ID = "serenemind_general_v3"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received: ${remoteMessage.data}")

        val data = remoteMessage.data
        val targetType = data["targetType"] ?: "GENERAL"
        val title = data["title"] ?: remoteMessage.notification?.title ?: "SereneMind"
        val body = data["body"] ?: remoteMessage.notification?.body ?: ""
        
        val id = data["id"]?.toLongOrNull() 
            ?: data["targetId"]?.toLongOrNull() 
            ?: data["reminderId"]?.toLongOrNull() 
            ?: System.currentTimeMillis()

        if (targetType == "REMINDER") {
            handleReminderNotification(id, title, body)
            com.serenemind.util.RefreshSignals.signalRefreshReminders()
        } else {
            showGeneralNotification(id, title, body, targetType)
        }
    }

    private fun handleReminderNotification(id: Long, title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createChannels(notificationManager)

        val cleanTitle = title.replace("Reminder: ", "", ignoreCase = true).trim()
        val requestCode = (id % Int.MAX_VALUE).toInt()

        val fullScreenIntent = Intent(this, ReminderAlarmActivity::class.java).apply {
            putExtra("id", id)
            putExtra("title", "It's time to " + cleanTitle)
            putExtra("note", "SereneMind Reminder")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, requestCode + 100, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(this, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_DISMISS
            putExtra("id", id)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            this, requestCode + 200, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val notificationBuilder = NotificationCompat.Builder(this, REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setColor(0xFF6750A4.toInt())
                .setContentTitle("Mental Health Reminder")
                .setContentText(cleanTitle)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_ALL) // Trigger sound, vibration and pop-up
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(0, "Dismiss", dismissPendingIntent)

            if (Build.VERSION.SDK_INT >= 34) {
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                if (alarmManager.canScheduleExactAlarms()) {
                    notificationBuilder.setFullScreenIntent(fullScreenPendingIntent, true)
                }
            } else {
                notificationBuilder.setFullScreenIntent(fullScreenPendingIntent, true)
            }

            notificationManager.notify(requestCode, notificationBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Notification error: ${e.message}")
        }
    }

    private fun showGeneralNotification(id: Long, title: String, body: String, type: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createChannels(notificationManager)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("targetId", id)
            putExtra("targetType", type)
        }
        val pendingIntent = PendingIntent.getActivity(this, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, GENERAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setColor(0xFF6750A4.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set High for Heads-up pop-up
            .setDefaults(Notification.DEFAULT_ALL)        // Enable sound and vibration
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(id.toInt(), notification)
    }

    private fun createChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Reminder Channel
            if (notificationManager.getNotificationChannel(REMINDER_CHANNEL_ID) == null) {
                val reminderChannel = NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Mental Health Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "High priority wellness alerts"
                    enableVibration(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(reminderChannel)
            }

            // General Channel
            if (notificationManager.getNotificationChannel(GENERAL_CHANNEL_ID) == null) {
                val generalChannel = NotificationChannel(
                    GENERAL_CHANNEL_ID,
                    "General Updates",
                    NotificationManager.IMPORTANCE_HIGH // Change from DEFAULT to HIGH
                ).apply {
                    description = "Likes, Comments and Community"
                    enableVibration(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(generalChannel)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token: $token")
    }
}
