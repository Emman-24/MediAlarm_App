package com.emman.android.medialarm.privacyModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PrivacyViewModel() : ViewModel() {

    private val _navigateToHomeFragment = MutableLiveData<Boolean>()
    val navigateToHomeFragment: LiveData<Boolean> = _navigateToHomeFragment


    fun onAcceptClicked() {
        _navigateToHomeFragment.value = true
    }

    fun onNavigateToHomeFragmentComplete() {
        _navigateToHomeFragment.value = false
    }
}