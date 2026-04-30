package com.pilltracker.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pilltracker.receiver.MidnightResetReceiver
import java.time.LocalDate
import java.time.ZoneId

object AlarmScheduler {
    fun scheduleNextMidnight(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MidnightResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val midnightMillis = nextMidnightMillis()
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, midnightMillis, pendingIntent)
    }

    fun nextMidnightMillis(): Long =
        LocalDate.now()
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
}
