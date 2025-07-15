package com.emman.android.medialarm.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarm.data.repository.PrivacyPolicyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val privacyPolicyRepository: PrivacyPolicyRepository,
) : ViewModel() {

    var acceptedUser: MutableLiveData<Boolean> = MutableLiveData()

    init {
        hasUserAcceptedPolicy()
    }

    private fun hasUserAcceptedPolicy() {
        viewModelScope.launch {
            acceptedUser.value = privacyPolicyRepository.hasUserAcceptedPolicy()
        }
    }


}
