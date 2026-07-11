package com.serenemind.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.serenemind.model.response.ReminderResponse
import java.util.*

object ReminderScheduler {
    fun scheduleReminder(context: Context, reminder: ReminderResponse) {
        if (!reminder.enabled) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("id", reminder.id)
            putExtra("title", reminder.title)
            putExtra("note", reminder.note)
            putExtra("repeatType", reminder.repeatType)
            putExtra("time", reminder.reminderTime)
            putExtra("tone", reminder.reminderTone)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Parse time "HH:mm:ss"
        val timeParts = reminder.reminderTime.split(":")
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            set(Calendar.MINUTE, timeParts[1].toInt())
            set(Calendar.SECOND, 0)
            
            // If time is in the past, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        // Use setAlarmClock for the most "real" alarm behavior
        // This shows the alarm icon in status bar and system clock
        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            calendar.timeInMillis,
            pendingIntent
        )
        
        try {
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        } catch (e: SecurityException) {
            // Fallback if permission is missing
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    fun cancelReminder(context: Context, reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
