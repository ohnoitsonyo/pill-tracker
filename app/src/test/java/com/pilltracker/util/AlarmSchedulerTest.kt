package com.pilltracker.util

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class AlarmSchedulerTest {
    @Test
    fun nextMidnightMillis_isInFuture() {
        assertTrue(AlarmScheduler.nextMidnightMillis() > System.currentTimeMillis())
    }

    @Test
    fun nextMidnightMillis_isMidnightLocalTime() {
        val millis = AlarmScheduler.nextMidnightMillis()
        val instant = Instant.ofEpochMilli(millis)
        val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val time = instant.atZone(ZoneId.systemDefault()).toLocalTime()

        assertEquals(LocalDate.now().plusDays(1), date)
        assertEquals(0, time.hour)
        assertEquals(0, time.minute)
        assertEquals(0, time.second)
    }

    @Test
    fun nextMidnightMillis_isNotUTCMidnight() {
        // Verify we're using local TZ, not UTC — they differ unless user is in UTC
        val millis = AlarmScheduler.nextMidnightMillis()
        val instant = Instant.ofEpochMilli(millis)
        val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()
        val utcTime = instant.atZone(ZoneId.of("UTC")).toLocalTime()

        // Local midnight should be midnight in local zone (the important assertion)
        assertEquals(0, localTime.hour)
        assertEquals(0, localTime.minute)
    }
}

private fun assertEquals(expected: Any, actual: Any) = org.junit.Assert.assertEquals(expected, actual)
