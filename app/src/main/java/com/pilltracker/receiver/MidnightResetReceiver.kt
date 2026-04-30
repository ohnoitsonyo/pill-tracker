package com.pilltracker.receiver

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.pilltracker.data.MedicationDatabase
import com.pilltracker.util.AlarmScheduler
import com.pilltracker.util.PillPrefs
import com.pilltracker.util.ResetHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MidnightResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val today = LocalDate.now()
                val dao = MedicationDatabase.getInstance(context).medicationDao()
                ResetHelper.performReset(
                    dao = dao,
                    prefs = PillPrefs(context),
                    yesterday = today.minusDays(1).toString(),
                    today = today.toString(),
                )

                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(ComponentName(context, PillWidgetProvider::class.java))
                ids.forEach { id -> PillWidgetProvider.updateWidget(context, manager, id, false) }

                AlarmScheduler.scheduleNextMidnight(context)
            } finally {
                result.finish()
            }
        }
    }
}
