package com.pilltracker.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MedicationRepositoryTest {
    private lateinit var dao: MedicationDao
    private lateinit var repo: MedicationRepository

    @Before
    fun setup() {
        dao = mockk()
        repo = MedicationRepository(dao)
    }

    @Test
    fun getAll_delegatesToDao() = runTest {
        val records = listOf(MedicationRecord("2026-04-29", true))
        every { dao.getAll() } returns flowOf(records)

        val result = repo.getAll().toList().first()
        assertEquals(records, result)
    }

    @Test
    fun upsert_delegatesToDao() = runTest {
        val record = MedicationRecord("2026-04-29", true)
        coEvery { dao.upsert(record) } returns Unit

        repo.upsert(record)
        coVerify { dao.upsert(record) }
    }

    @Test
    fun getByDate_returnsNullWhenNotFound() = runTest {
        coEvery { dao.getByDate("2026-04-29") } returns null

        assertNull(repo.getByDate("2026-04-29"))
    }

    @Test
    fun getByDate_returnsRecordWhenFound() = runTest {
        val record = MedicationRecord("2026-04-29", true)
        coEvery { dao.getByDate("2026-04-29") } returns record

        assertEquals(record, repo.getByDate("2026-04-29"))
    }
}
