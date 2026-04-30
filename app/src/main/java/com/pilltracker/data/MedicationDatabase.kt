package com.pilltracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MedicationRecord::class], version = 1, exportSchema = false)
abstract class MedicationDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao

    companion object {
        @Volatile private var instance: MedicationDatabase? = null

        fun getInstance(context: Context): MedicationDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MedicationDatabase::class.java,
                    "pill_tracker.db",
                ).build().also { instance = it }
            }
    }
}
