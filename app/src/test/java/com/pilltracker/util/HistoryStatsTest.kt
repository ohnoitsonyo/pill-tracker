package com.pilltracker.util

import com.pilltracker.data.MedicationRecord
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HistoryStatsTest {
    private val today = LocalDate.now().toString()
    private val yesterday = LocalDate.now().minusDays(1).toString()
    private val twoDaysAgo = LocalDate.now().minusDays(2).toString()
    private val threeDaysAgo = LocalDate.now().minusDays(3).toString()

    // --- streak ---

    @Test
    fun streak_isZero_whenNoRecords() {
        assertEquals(0, HistoryStats.streak(emptyList()))
    }

    @Test
    fun streak_isZero_whenTodayNotTaken() {
        val records = listOf(MedicationRecord(today, taken = false))
        assertEquals(0, HistoryStats.streak(records))
    }

    @Test
    fun streak_isOne_whenOnlyTodayTaken() {
        val records = listOf(MedicationRecord(today, taken = true))
        assertEquals(1, HistoryStats.streak(records))
    }

    @Test
    fun streak_countsFromYesterday_whenTodayNotYetLogged() {
        val records = listOf(
            MedicationRecord(yesterday, taken = true),
            MedicationRecord(twoDaysAgo, taken = true),
        )
        assertEquals(2, HistoryStats.streak(records))
    }

    @Test
    fun streak_breaksOnMissedDay() {
        val records = listOf(
            MedicationRecord(today, taken = true),
            MedicationRecord(yesterday, taken = true),
            MedicationRecord(twoDaysAgo, taken = false),
            MedicationRecord(threeDaysAgo, taken = true),
        )
        assertEquals(2, HistoryStats.streak(records))
    }

    @Test
    fun streak_isZero_whenOnlyOldRecordTaken() {
        val records = listOf(
            MedicationRecord(today, taken = false),
            MedicationRecord(yesterday, taken = false),
            MedicationRecord(twoDaysAgo, taken = true),
        )
        assertEquals(0, HistoryStats.streak(records))
    }

    // --- weekSummary ---

    @Test
    fun weekSummary_isZero_whenNoRecords() {
        assertEquals(0 to 7, HistoryStats.weekSummary(emptyList()))
    }

    @Test
    fun weekSummary_countsOnlyTakenDays() {
        val records = listOf(
            MedicationRecord(today, taken = true),
            MedicationRecord(yesterday, taken = false),
            MedicationRecord(twoDaysAgo, taken = true),
        )
        assertEquals(2 to 7, HistoryStats.weekSummary(records))
    }

    @Test
    fun weekSummary_ignoresRecordsOlderThanSevenDays() {
        val eightDaysAgo = LocalDate.now().minusDays(8).toString()
        val records = listOf(
            MedicationRecord(today, taken = true),
            MedicationRecord(eightDaysAgo, taken = true),
        )
        assertEquals(1 to 7, HistoryStats.weekSummary(records))
    }

    @Test
    fun weekSummary_maxIsSevenWhenAllTaken() {
        val records = (0L..6L).map { i ->
            MedicationRecord(LocalDate.now().minusDays(i).toString(), taken = true)
        }
        assertEquals(7 to 7, HistoryStats.weekSummary(records))
    }
}
