package com.serenemind.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class MeditationAlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(meditationId: Long, endTimeMillis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmScheduler", "Cannot schedule exact alarms. Permission missing.")
            return
        }

        val intent = Intent(context, MeditationAlarmReceiver::class.java).apply {
            putExtra("MEDITATION_ID", meditationId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            meditationId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                endTimeMillis,
                pendingIntent
            )
            Log.d("AlarmScheduler", "Alarm scheduled for meditation $meditationId at $endTimeMillis")
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error scheduling exact alarm", e)
        }
    }

    fun cancel(meditationId: Long) {
        val intent = Intent(context, MeditationAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            meditationId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("AlarmScheduler", "Alarm cancelled for meditation $meditationId")
        }
    }
}
