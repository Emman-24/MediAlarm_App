package com.emman.android.medialarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarm.domain.models.MedicineScheduleState
import com.emman.android.medialarm.domain.usecases.GetScheduleForDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val getScheduleForDateUseCase: GetScheduleForDateUseCase
) : ViewModel() {


    private val _uiState = MutableStateFlow(MedicineUiState())
    val uiState: StateFlow<MedicineUiState> = _uiState.asStateFlow()

    fun getMedicineSchedules(date: LocalDate) {
        viewModelScope.launch {
            try {
                getScheduleForDateUseCase(date).collect { schedules ->
                    _uiState.value = MedicineUiState(
                        schedules = schedules,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = MedicineUiState(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false
                )
            }
        }
    }


    data class MedicineUiState(
        val schedules: List<MedicineScheduleState> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )
}
