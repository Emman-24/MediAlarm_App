package com.emman.android.medialarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emman.android.medialarm.common.adapters.SharedPreferencesHelper

class MainViewModel(private val sharedPreferencesHelper: SharedPreferencesHelper) : ViewModel() {

    private val _startDestination = MutableLiveData<Boolean>()
    val startDestination: LiveData<Boolean> get() = _startDestination


    fun checkStartDestination() {
        _startDestination.value = sharedPreferencesHelper.getStartDestination()
    }

    fun setStartDestination(isTreatment: Boolean) {
        sharedPreferencesHelper.setStartDestination(isTreatment)
    }
}