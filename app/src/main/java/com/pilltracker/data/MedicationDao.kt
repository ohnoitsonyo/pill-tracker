package com.pilltracker.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Upsert
    suspend fun upsert(record: MedicationRecord)

    @Query("SELECT * FROM medication_records ORDER BY date DESC")
    fun getAll(): Flow<List<MedicationRecord>>

    @Query("SELECT * FROM medication_records WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): MedicationRecord?
}
