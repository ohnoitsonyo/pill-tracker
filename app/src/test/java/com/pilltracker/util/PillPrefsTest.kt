package com.pilltracker.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PillPrefsTest {
    private lateinit var prefs: PillPrefs

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        prefs = PillPrefs(context)
    }

    @Test
    fun takenToday_defaultsFalse() {
        assertFalse(prefs.takenToday)
    }

    @Test
    fun takenToday_roundTrips() {
        prefs.takenToday = true
        assertEquals(true, prefs.takenToday)
        prefs.takenToday = false
        assertEquals(false, prefs.takenToday)
    }

    @Test
    fun lastResetDate_defaultsEmpty() {
        assertEquals("", prefs.lastResetDate)
    }

    @Test
    fun lastResetDate_roundTrips() {
        prefs.lastResetDate = "2026-04-29"
        assertEquals("2026-04-29", prefs.lastResetDate)
    }
}
