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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BootReceiverTest {
    private lateinit var dao: MedicationDao
    private lateinit var prefs: PillPrefs

    @Before
    fun setup() {
        dao = mockk()
        prefs = PillPrefs(ApplicationProvider.getApplicationContext<Context>())
    }

    @Test
    fun performBootReset_doesNothing_whenLastResetDateEmpty() = runTest {
        prefs.lastResetDate = ""

        ResetHelper.performBootReset(dao, prefs, today = "2026-04-29")

        coVerify(exactly = 0) { dao.upsert(any()) }
    }

    @Test
    fun performBootReset_doesNothing_whenLastResetDateIsToday() = runTest {
        prefs.lastResetDate = "2026-04-29"

        ResetHelper.performBootReset(dao, prefs, today = "2026-04-29")

        coVerify(exactly = 0) { dao.upsert(any()) }
    }

    @Test
    fun performBootReset_backfillsOneMissedDay() = runTest {
        prefs.lastResetDate = "2026-04-28"
        coEvery { dao.getByDate("2026-04-28") } returns null
        coEvery { dao.upsert(any()) } returns Unit

        ResetHelper.performBootReset(dao, prefs, today = "2026-04-29")

        coVerify { dao.upsert(MedicationRecord(date = "2026-04-28", taken = false)) }
    }

    @Test
    fun performBootReset_backfillsMultipleMissedDays() = runTest {
        prefs.lastResetDate = "2026-04-26"
        coEvery { dao.getByDate(any()) } returns null
        coEvery { dao.upsert(any()) } returns Unit

        ResetHelper.performBootReset(dao, prefs, today = "2026-04-29")

        coVerify { dao.upsert(MedicationRecord(date = "2026-04-26", taken = false)) }
        coVerify { dao.upsert(MedicationRecord(date = "2026-04-27", taken = false)) }
        coVerify { dao.upsert(MedicationRecord(date = "2026-04-28", taken = false)) }
    }

    @Test
    fun performBootReset_doesNotDuplicateExistingRecords() = runTest {
        prefs.lastResetDate = "2026-04-28"
        coEvery { dao.getByDate("2026-04-28") } returns MedicationRecord("2026-04-28", true)

        ResetHelper.performBootReset(dao, prefs, today = "2026-04-29")

        coVerify(exactly = 0) { dao.upsert(any()) }
    }

    @Test
    fun performBootReset_resetsTakenTodayAndUpdatesDate() = runTest {
        prefs.lastResetDate = "2026-04-28"
        prefs.takenToday = true
        coEvery { dao.getByDate(any()) } returns null
        coEvery { dao.upsert(any()) } returns Unit

        ResetHelper.performBootReset(dao, prefs, today = "2026-04-29")

        assertFalse(prefs.takenToday)
        assertEquals("2026-04-29", prefs.lastResetDate)
    }
}
