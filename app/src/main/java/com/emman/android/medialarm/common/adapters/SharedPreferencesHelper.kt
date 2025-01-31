package com.emman.android.medialarm.common.adapters

import android.content.Context

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun setStartDestination(isTreatment: Boolean) {
        sharedPreferences.edit().putBoolean("start_destination", isTreatment).apply()
    }

    fun getStartDestination(): Boolean {
        return sharedPreferences.getBoolean("start_destination", false)
    }
}