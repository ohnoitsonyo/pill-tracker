package com.pilltracker.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MedicationDaoTest {
    private lateinit var db: MedicationDatabase
    private lateinit var dao: MedicationDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MedicationDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.medicationDao()
    }

    @After
    fun teardown() = db.close()

    @Test
    fun upsert_insertsRecord() = runTest {
        dao.upsert(MedicationRecord(date = "2026-04-29", taken = true))
        val record = dao.getByDate("2026-04-29")
        assertEquals(true, record?.taken)
    }

    @Test
    fun upsert_isIdempotent() = runTest {
        dao.upsert(MedicationRecord(date = "2026-04-29", taken = true))
        dao.upsert(MedicationRecord(date = "2026-04-29", taken = false))
        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals(false, all[0].taken)
    }

    @Test
    fun getAll_returnsNewestFirst() = runTest {
        dao.upsert(MedicationRecord(date = "2026-04-27", taken = false))
        dao.upsert(MedicationRecord(date = "2026-04-29", taken = true))
        dao.upsert(MedicationRecord(date = "2026-04-28", taken = false))
        val all = dao.getAll().first()
        assertEquals("2026-04-29", all[0].date)
        assertEquals("2026-04-27", all[2].date)
    }

    @Test
    fun getByDate_returnsNullWhenMissing() = runTest {
        assertNull(dao.getByDate("2026-04-29"))
    }

    @Test
    fun getAll_emptyWhenNoRecords() = runTest {
        assertTrue(dao.getAll().first().isEmpty())
    }
}
