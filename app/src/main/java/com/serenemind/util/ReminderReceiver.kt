package com.serenemind.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderReceiver"
        const val ACTION_DISMISS = "com.serenemind.ACTION_DISMISS"
        const val ACTION_SNOOZE = "com.serenemind.ACTION_SNOOZE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("id", -1L)
        val title = intent.getStringExtra("title") ?: "Reminder"
        val note = intent.getStringExtra("note") ?: ""

        if (id == -1L) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            ACTION_DISMISS -> {
                Log.d(TAG, "Dismissing notification $id")
                notificationManager.cancel(id.toInt())
            }
            ACTION_SNOOZE -> {
                Log.d(TAG, "Snoozing notification $id")
                snoozeReminder(context, id, title, note)
                notificationManager.cancel(id.toInt())
            }
        }
    }

    private fun snoozeReminder(context: Context, id: Long, title: String, note: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("id", id)
            putExtra("title", title)
            putExtra("note", note)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, id.toInt(), snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // 10 minutes later
        val triggerTime = System.currentTimeMillis() + (10 * 60 * 1000)
        
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }
}
