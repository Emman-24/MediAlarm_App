package com.emman.android.medialarm.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddMedineViewModel : ViewModel() {

    /**
     * LiveData for the medicine name
     */

    private val _medicineName = MutableLiveData<String>()
    val medicineName: MutableLiveData<String> = _medicineName

    fun setMedicineName(name: String) {
        _medicineName.value = name
    }

    /**
     * LiveData for cyclic schedule
     */

    private val _intakeDays = MutableLiveData<Int>()
    val intakeDays: MutableLiveData<Int> = _intakeDays

    private val _pauseDays = MutableLiveData<Int>()
    val pauseDays: MutableLiveData<Int> = _pauseDays


    fun setIntakeDays(days: Int) {
        _intakeDays.value = days
    }

    fun setPauseDays(days: Int) {
        _pauseDays.value = days
    }


    /**
     * LiveData for multimple times schedule
     */

    private val _multipleTimesSchedule = MutableLiveData<Int>()
    val multipleTimesSchedule: MutableLiveData<Int> = _multipleTimesSchedule

    fun setMultipleTimesSchedule(times: Int) {
        _multipleTimesSchedule.value = times
    }


}