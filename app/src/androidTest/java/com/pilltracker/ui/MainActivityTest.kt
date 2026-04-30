package com.pilltracker.ui

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pilltracker.R
import com.pilltracker.data.MedicationDatabase
import com.pilltracker.data.MedicationRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private val db by lazy {
        MedicationDatabase.getInstance(ApplicationProvider.getApplicationContext<Context>())
    }

    @Before
    fun clearDatabase() {
        runBlocking(Dispatchers.IO) { db.clearAllTables() }
    }

    @Test
    fun historyList_isDisplayed() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val list = activity.findViewById<RecyclerView>(R.id.history_list)
                assertEquals(RecyclerView.VISIBLE, list.visibility)
            }
        }
    }

    @Test
    fun historyList_showsInsertedRecords() {
        runBlocking(Dispatchers.IO) {
            db.medicationDao().upsert(MedicationRecord(date = "2026-04-28", taken = true))
            db.medicationDao().upsert(MedicationRecord(date = "2026-04-27", taken = false))
        }

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            Thread.sleep(500)
            scenario.onActivity { activity ->
                val list = activity.findViewById<RecyclerView>(R.id.history_list)
                assertEquals(2, list.adapter?.itemCount)
            }
        }
    }
}
