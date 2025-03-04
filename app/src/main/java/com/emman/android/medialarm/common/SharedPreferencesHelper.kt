package com.emman.android.medialarm.common

import android.content.SharedPreferences
import javax.inject.Inject

interface AppPreferences {
    fun setStartDestination(isTreatment: Boolean)
    fun getStartDestination(): Boolean
}


class SharedPreferencesHelper @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AppPreferences {
    override fun setStartDestination(isTreatment: Boolean) {
        sharedPreferences.edit().putBoolean("start_destination", isTreatment).apply()
    }

    override fun getStartDestination(): Boolean {
        return sharedPreferences.getBoolean("start_destination", false)
    }
}