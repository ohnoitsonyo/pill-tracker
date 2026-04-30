package com.pilltracker.util

import com.pilltracker.data.MedicationDao
import com.pilltracker.data.MedicationRecord
import java.time.LocalDate

object ResetHelper {
    suspend fun performReset(dao: MedicationDao, prefs: PillPrefs, yesterday: String, today: String) {
        if (dao.getByDate(yesterday) == null) {
            dao.upsert(MedicationRecord(date = yesterday, taken = prefs.takenToday))
        }
        prefs.takenToday = false
        prefs.lastResetDate = today
    }

    suspend fun performBootReset(dao: MedicationDao, prefs: PillPrefs, today: String) {
        val lastReset = prefs.lastResetDate
        if (lastReset.isEmpty()) return

        val lastResetDate = LocalDate.parse(lastReset)
        val todayDate = LocalDate.parse(today)
        if (!lastResetDate.isBefore(todayDate)) return

        var date = lastResetDate
        while (date.isBefore(todayDate)) {
            if (dao.getByDate(date.toString()) == null) {
                dao.upsert(MedicationRecord(date = date.toString(), taken = false))
            }
            date = date.plusDays(1)
        }

        prefs.takenToday = false
        prefs.lastResetDate = today
    }
}
