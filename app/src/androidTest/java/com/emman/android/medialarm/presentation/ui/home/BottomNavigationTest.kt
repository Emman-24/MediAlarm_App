package com.emman.android.medialarm.presentation.ui.home

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emman.android.medialarm.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomNavigationTest {

    @Before
    fun setup() {
        ActivityScenario.launch(MenuActivity::class.java)
    }

    @Test
    fun testBottomNavigationHomeFragment() {
        onView(withId(R.id.homeFragment)).perform(click())
    }

    @Test
    fun testBottomNavigationScheduleFragment() {
        onView(withId(R.id.scheduleFragment)).perform(click())
    }

    @Test
    fun testBottomNavigationHistoryFragment() {
        onView(withId(R.id.historyFragment)).perform(click())
    }

    @Test
    fun testBottomNavigationSettingsFragment() {
        onView(withId(R.id.settingsFragment)).perform(click())
    }
}
