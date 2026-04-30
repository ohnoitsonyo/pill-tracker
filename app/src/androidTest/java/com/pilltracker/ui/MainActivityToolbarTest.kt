package com.pilltracker.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pilltracker.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityToolbarTest {

    private fun ActivityScenario<MainActivity>.pushSettings() {
        onActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
            activity.supportFragmentManager.executePendingTransactions()
        }
        Thread.sleep(200)
    }

    @Test
    fun toolbar_showsSettingsTitle_whenSettingsOpened() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.pushSettings()
            scenario.onActivity { activity ->
                assertEquals(
                    activity.getString(R.string.settings_title),
                    activity.supportActionBar?.title?.toString(),
                )
                assertEquals(1, activity.supportFragmentManager.backStackEntryCount)
            }
        }
    }

    @Test
    fun toolbar_restoresRootState_afterSystemBackGesture() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.pushSettings()

            scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            Thread.sleep(300)

            scenario.onActivity { activity ->
                assertEquals(0, activity.supportFragmentManager.backStackEntryCount)
                assertEquals(
                    activity.getString(R.string.app_name),
                    activity.supportActionBar?.title?.toString(),
                )
            }
        }
    }

    @Test
    fun toolbar_restoresRootState_afterUpButtonTap() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.pushSettings()

            scenario.onActivity { it.onSupportNavigateUp() }
            Thread.sleep(300)

            scenario.onActivity { activity ->
                assertEquals(0, activity.supportFragmentManager.backStackEntryCount)
                assertEquals(
                    activity.getString(R.string.app_name),
                    activity.supportActionBar?.title?.toString(),
                )
            }
        }
    }

    @Test
    fun toolbar_showsRootState_onFreshLaunch() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertEquals(0, activity.supportFragmentManager.backStackEntryCount)
                assertEquals(
                    activity.getString(R.string.app_name),
                    activity.supportActionBar?.title?.toString(),
                )
            }
        }
    }
}
