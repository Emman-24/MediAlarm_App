package com.emman.android.medialarm.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

interface AppPreferences {
    fun setStartDestination(isTreatment: Boolean)
    fun getStartDestination(): Boolean
}


class SharedPreferencesHelper @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AppPreferences {
    override fun setStartDestination(isTreatment: Boolean) {
        sharedPreferences.edit() { putBoolean("start_destination", isTreatment) }
    }

    override fun getStartDestination(): Boolean {
        return sharedPreferences.getBoolean("start_destination", false)
    }
}