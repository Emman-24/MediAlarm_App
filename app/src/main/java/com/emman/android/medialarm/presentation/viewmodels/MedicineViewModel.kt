package com.emman.android.medialarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarm.domain.models.MedicineDetails
import com.emman.android.medialarm.domain.models.MedicineScheduleState
import com.emman.android.medialarm.domain.usecases.GetMedicineUseCase
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
    private val getScheduleForDateUseCase: GetScheduleForDateUseCase,
    private val getMedicineUseCase: GetMedicineUseCase,
) : ViewModel() {


    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val _medicineDetails = MutableStateFlow<MedicineDetails?>(null)
    val medicineDetails: StateFlow<MedicineDetails?> = _medicineDetails.asStateFlow()

    fun getMedicineSchedules(date: LocalDate) {
        viewModelScope.launch {
            try {
                getScheduleForDateUseCase(date).collect { schedules ->
                    _uiState.value = ScheduleUiState(
                        schedules = schedules,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false
                )
            }
        }
    }


    fun getMedicineDetails(medicineId: Long) {
        viewModelScope.launch {
            try {
                getMedicineUseCase(medicineId).collect { medicineDetails ->
                    _medicineDetails.value = medicineDetails
                }
            } catch (e: Exception) {
                _medicineDetails.value = null
            }
        }
    }


    data class ScheduleUiState(
        val schedules: List<MedicineScheduleState> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null,
    )
}
