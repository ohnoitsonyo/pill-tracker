package com.pilltracker.data

import kotlinx.coroutines.flow.Flow

class MedicationRepository(private val dao: MedicationDao) {
    fun getAll(): Flow<List<MedicationRecord>> = dao.getAll()

    suspend fun upsert(record: MedicationRecord) = dao.upsert(record)

    suspend fun getByDate(date: String): MedicationRecord? = dao.getByDate(date)
}
