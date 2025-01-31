package com.emman.android.medialarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emman.android.medialarm.common.adapters.SharedPreferencesHelper

class MainViewModelFactory(private val sharedPreferencesHelper: SharedPreferencesHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(sharedPreferencesHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}