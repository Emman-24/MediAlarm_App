package com.emman.android.medialarm.privacyModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emman.android.medialarm.domain.SetStartDestinationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrivacyViewModel @Inject constructor(
    private val setStartDestinationUseCase: SetStartDestinationUseCase
) : ViewModel() {

    private val _navigationEvent = MutableLiveData<Boolean>()
    val navigationEvent: LiveData<Boolean> = _navigationEvent


    val termsAccepted = MutableLiveData(false)
    val isAcceptButtonEnabled: LiveData<Boolean> = termsAccepted


    fun onAcceptClicked() {
        setStartDestinationUseCase(true)
        _navigationEvent.value = true
    }

    fun onTermsAcceptedChanged(isChecked: Boolean) {
        termsAccepted.value = isChecked
    }

}