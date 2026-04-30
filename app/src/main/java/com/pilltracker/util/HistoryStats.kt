package com.pilltracker.util

import com.pilltracker.data.MedicationRecord
import java.time.LocalDate

object HistoryStats {
    fun streak(records: List<MedicationRecord>): Int {
        val recordMap = records.associateBy { it.date }
        val today = LocalDate.now()
        val startDate = if (recordMap[today.toString()]?.taken == true) today else today.minusDays(1)
        var date = startDate
        var count = 0
        while (recordMap[date.toString()]?.taken == true) {
            count++
            date = date.minusDays(1)
        }
        return count
    }

    fun weekSummary(records: List<MedicationRecord>): Pair<Int, Int> {
        val recordMap = records.associateBy { it.date }
        val today = LocalDate.now()
        var taken = 0
        repeat(7) { i ->
            if (recordMap[today.minusDays(i.toLong()).toString()]?.taken == true) taken++
        }
        return taken to 7
    }
}
