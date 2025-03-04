package com.emman.android.medialarm.welcomeModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor() : ViewModel() {

    private val _navigateToPrivacyFragment = MutableLiveData<Boolean>()
    val navigateToPrivacyFragment: LiveData<Boolean> = _navigateToPrivacyFragment


    fun onNavigateToPrivacyFragment() {
        _navigateToPrivacyFragment.value = true
    }

    fun onNavigateToPrivacyFragmentComplete() {
        _navigateToPrivacyFragment.value = false
    }


}
