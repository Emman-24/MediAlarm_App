package com.emman.android.medialarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarm.data.repository.PrivacyPolicyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor(
    private val privacyPolicyRepository: PrivacyPolicyRepository,
) : ViewModel() {

    fun saveAcceptance(accepted: Boolean) {
        viewModelScope.launch {
            privacyPolicyRepository.saveAcceptance(accepted)
        }
    }

}