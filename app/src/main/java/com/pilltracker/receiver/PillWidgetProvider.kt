package com.pilltracker.receiver

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.pilltracker.R
import com.pilltracker.data.MedicationDatabase
import com.pilltracker.data.MedicationRecord
import com.pilltracker.util.AlarmScheduler
import com.pilltracker.util.PillPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class PillWidgetProvider : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        val prefs = PillPrefs(context)
        if (prefs.lastResetDate.isEmpty()) {
            prefs.lastResetDate = LocalDate.now().toString()
        }
        AlarmScheduler.scheduleNextMidnight(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val taken = PillPrefs(context).takenToday
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id, taken)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE) {
            val prefs = PillPrefs(context)
            val newState = !prefs.takenToday
            prefs.takenToday = newState

            CoroutineScope(Dispatchers.IO).launch {
                val dao = MedicationDatabase.getInstance(context).medicationDao()
                dao.upsert(MedicationRecord(date = LocalDate.now().toString(), taken = newState))
            }

            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, PillWidgetProvider::class.java))
            ids.forEach { id -> updateWidget(context, manager, id, newState) }
        }
    }

    companion object {
        const val ACTION_TOGGLE = "com.pilltracker.ACTION_TOGGLE"
        const val COLOR_TAKEN = 0xFF4CAF50.toInt()
        const val COLOR_UNTAKEN = 0xFFFFFFFF.toInt()

        fun updateWidget(
            context: Context,
            manager: AppWidgetManager,
            widgetId: Int,
            taken: Boolean,
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_pill)
            val color = if (taken) COLOR_TAKEN else COLOR_UNTAKEN
            views.setInt(R.id.pill_button, "setColorFilter", color)

            val toggleIntent = Intent(context, PillWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE
            }
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                0,
                toggleIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE,
            )
            views.setOnClickPendingIntent(R.id.pill_button, pendingIntent)
            manager.updateAppWidget(widgetId, views)
        }
    }
}
