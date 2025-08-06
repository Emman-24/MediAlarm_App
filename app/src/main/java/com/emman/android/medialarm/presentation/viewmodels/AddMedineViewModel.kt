package com.emman.android.medialarm.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddMedineViewModel : ViewModel() {

    private val _medicineName = MutableLiveData<String>()
    val medicineName: MutableLiveData<String> = _medicineName

    private val _multipleTimesSchedule = MutableLiveData<Int>()
    val multipleTimesSchedule: MutableLiveData<Int> = _multipleTimesSchedule

    fun setMultipleTimesSchedule(times: Int) {
        _multipleTimesSchedule.value = times
    }

    fun setMedicineName(name: String) {
        _medicineName.value = name
    }


}