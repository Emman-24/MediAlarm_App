package com.emman.android.medialarm.homeModule.treatment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TreatmentViewModel : ViewModel() {

    private val _isNavBottomVisible = MutableLiveData<Boolean>()
    val isNavBottomVisible: LiveData<Boolean> = _isNavBottomVisible

    init {
        _isNavBottomVisible.value = false
    }



}