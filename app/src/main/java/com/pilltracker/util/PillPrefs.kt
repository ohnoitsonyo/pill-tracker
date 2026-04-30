package com.pilltracker.util

import android.content.Context
import android.content.SharedPreferences

class PillPrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pill_prefs", Context.MODE_PRIVATE)

    var takenToday: Boolean
        get() = prefs.getBoolean(KEY_TAKEN_TODAY, false)
        set(value) = prefs.edit().putBoolean(KEY_TAKEN_TODAY, value).apply()

    var lastResetDate: String
        get() = prefs.getString(KEY_LAST_RESET_DATE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_RESET_DATE, value).apply()

    companion object {
        private const val KEY_TAKEN_TODAY = "taken_today"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"
    }
}
