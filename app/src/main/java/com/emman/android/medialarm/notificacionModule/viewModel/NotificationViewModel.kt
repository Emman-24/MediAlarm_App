package com.emman.android.medialarm.notificacionModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emman.android.medialarm.domain.PermissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val permissionRepository: PermissionRepository
) : ViewModel(

) {
    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Idle)
    val permissionState: StateFlow<PermissionState> = _permissionState

    private val _ToHomeModuleSkip = MutableLiveData<Boolean>()
    val ToHomeModuleSkip: LiveData<Boolean> = _ToHomeModuleSkip

    fun checkPermission(
        permission: String
    ) {
        if (permissionRepository.hasNotificationPermission(permission)) {
            _permissionState.value = PermissionState.Granted
        } else {
            _permissionState.value = PermissionState.Request(permission)
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _permissionState.value = if (granted) PermissionState.Granted else PermissionState.Denied
    }


}

sealed class PermissionState {
    data object Idle : PermissionState()
    data object Granted : PermissionState()
    data object Denied : PermissionState()
    data class Request(val permission: String) : PermissionState()
}

