package com.pilltracker.receiver

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.pilltracker.data.MedicationDao
import com.pilltracker.data.MedicationRecord
import com.pilltracker.util.PillPrefs
import com.pilltracker.util.ResetHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MidnightResetReceiverTest {
    private lateinit var dao: MedicationDao
    private lateinit var prefs: PillPrefs

    @Before
    fun setup() {
        dao = mockk()
        prefs = PillPrefs(ApplicationProvider.getApplicationContext<Context>())
    }

    @Test
    fun performReset_logsYesterdayAsNotTaken_whenPillNotTaken() = runTest {
        prefs.takenToday = false
        coEvery { dao.getByDate("2026-04-28") } returns null
        coEvery { dao.upsert(any()) } returns Unit

        ResetHelper.performReset(dao, prefs, yesterday = "2026-04-28", today = "2026-04-29")

        coVerify { dao.upsert(MedicationRecord(date = "2026-04-28", taken = false)) }
    }

    @Test
    fun performReset_logsYesterdayAsTaken_whenPillWasTaken() = runTest {
        prefs.takenToday = true
        coEvery { dao.getByDate("2026-04-28") } returns null
        coEvery { dao.upsert(any()) } returns Unit

        ResetHelper.performReset(dao, prefs, yesterday = "2026-04-28", today = "2026-04-29")

        coVerify { dao.upsert(MedicationRecord(date = "2026-04-28", taken = true)) }
    }

    @Test
    fun performReset_doesNotOverwrite_whenRecordAlreadyExists() = runTest {
        prefs.takenToday = false
        coEvery { dao.getByDate("2026-04-28") } returns MedicationRecord("2026-04-28", true)

        ResetHelper.performReset(dao, prefs, yesterday = "2026-04-28", today = "2026-04-29")

        coVerify(exactly = 0) { dao.upsert(any()) }
    }

    @Test
    fun performReset_resetsTakenTodayToFalse() = runTest {
        prefs.takenToday = true
        coEvery { dao.getByDate(any()) } returns null
        coEvery { dao.upsert(any()) } returns Unit

        ResetHelper.performReset(dao, prefs, yesterday = "2026-04-28", today = "2026-04-29")

        assertFalse(prefs.takenToday)
    }

    @Test
    fun performReset_updatesLastResetDate() = runTest {
        coEvery { dao.getByDate(any()) } returns null
        coEvery { dao.upsert(any()) } returns Unit

        ResetHelper.performReset(dao, prefs, yesterday = "2026-04-28", today = "2026-04-29")

        assertEquals("2026-04-29", prefs.lastResetDate)
    }
}
