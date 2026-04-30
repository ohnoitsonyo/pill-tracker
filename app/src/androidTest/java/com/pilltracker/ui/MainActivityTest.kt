package com.pilltracker.ui

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.tabs.TabLayout
import com.pilltracker.R
import com.pilltracker.data.MedicationDatabase
import com.pilltracker.data.MedicationRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    fun historyTab_isFirstTab() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val tabs = activity.findViewById<TabLayout>(R.id.tab_layout)
                assertEquals(2, tabs.tabCount)
                assertEquals("History", tabs.getTabAt(0)?.text)
                assertEquals("Settings", tabs.getTabAt(1)?.text)
            }
        }
    }

    @Test
    fun historyList_isDisplayed() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val fragment = activity.supportFragmentManager.fragments
                    .filterIsInstance<HistoryFragment>().firstOrNull()
                assertNotNull(fragment)
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
                val fragment = activity.supportFragmentManager.fragments
                    .filterIsInstance<HistoryFragment>().firstOrNull()
                val list = fragment?.view?.findViewById<RecyclerView>(R.id.history_list)
                assertEquals(2, list?.adapter?.itemCount)
            }
        }
    }
}
