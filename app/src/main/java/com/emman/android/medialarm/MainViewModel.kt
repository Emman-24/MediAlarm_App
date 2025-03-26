package com.emman.android.medialarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emman.android.medialarm.common.utils.NavDestination
import com.emman.android.medialarm.domain.GetStartDestinationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getStartDestinationUseCase: GetStartDestinationUseCase
) : ViewModel() {

    private val _navigationEvent = MutableLiveData<NavDestination>()
    val navigationEvent: LiveData<NavDestination> = _navigationEvent

    fun checkStartDestination() {
        val shouldNavigateToHome = getStartDestinationUseCase()
        if (shouldNavigateToHome){
            _navigationEvent.value = NavDestination.ToHomeModule
        }else{
            _navigationEvent.value = NavDestination.ToWelcomeModule
        }
    }





}