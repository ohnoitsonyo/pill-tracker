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
    fun historyFragment_isShownOnLaunch() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val fragment = activity.supportFragmentManager.fragments
                    .filterIsInstance<HistoryFragment>().firstOrNull()
                assertNotNull(fragment)
            }
        }
    }

    @Test
    fun settingsFragment_isShownAfterGearTap() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.onOptionsItemSelected(
                    activity.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
                        .menu.findItem(R.id.action_settings) ?: return@onActivity
                )
            }
            Thread.sleep(200)
            scenario.onActivity { activity ->
                val fragment = activity.supportFragmentManager.fragments
                    .filterIsInstance<SettingsFragment>().firstOrNull()
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
