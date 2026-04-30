package com.pilltracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_records")
data class MedicationRecord(
    @PrimaryKey val date: String,
    val taken: Boolean,
)
