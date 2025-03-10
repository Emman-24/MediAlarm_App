package com.emman.android.medialarm.privacyModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarm.domain.SetStartDestinationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyViewModel @Inject constructor(
    private val setStartDestinationUseCase: SetStartDestinationUseCase
) : ViewModel() {

    private val _navigationEvent = MutableLiveData<Unit>()
    val navigationEvent: LiveData<Unit> = _navigationEvent


    val termsAccepted = MutableLiveData(false)
    val isAcceptButtonEnabled: LiveData<Boolean> = termsAccepted


    fun onAcceptClicked() {
        if (termsAccepted.value == true) {
            viewModelScope.launch {
                setStartDestinationUseCase(true)
                _navigationEvent.value = Unit
            }
        }
    }

    fun onTermsAcceptedChanged(isChecked: Boolean) {
        termsAccepted.value = isChecked
    }

}