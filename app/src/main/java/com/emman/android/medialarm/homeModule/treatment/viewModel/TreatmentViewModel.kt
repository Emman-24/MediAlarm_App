package com.emman.android.medialarm.homeModule.treatment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarm.data.local.Medicine
import com.emman.android.medialarm.data.local.MedicineSchedule
import com.emman.android.medialarm.domain.GetMedicinesForTreatment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TreatmentViewModel @Inject constructor(
    private val getMedicinesForTreatment: GetMedicinesForTreatment
) : ViewModel() {

    private val _uiState = MutableLiveData<TreatmentUiState>(TreatmentUiState.Loading)
    val uiState: LiveData<TreatmentUiState> = _uiState

    fun loadMedicinesForDayOrder(date: LocalDate) {
        _uiState.value = TreatmentUiState.Loading
        viewModelScope.launch {
            try {
                val medicines = getMedicinesForTreatment.invoke(date).sortedBy { medicine ->
                    when (val schedule = medicine.schedule) {
                        is MedicineSchedule.Interval -> schedule.times.first()
                        is MedicineSchedule.Cyclic -> schedule.times.first()
                        is MedicineSchedule.SpecificDaysOfWeek -> schedule.times.first()
                        is MedicineSchedule.MultipleTimesDaily -> schedule.times.first()
                    }
                }
                _uiState.value = TreatmentUiState.Success(medicines)
            } catch (e: Exception) {
                _uiState.value = TreatmentUiState.Error(e)
            }
        }
    }

    sealed class TreatmentUiState {
        object Loading : TreatmentUiState()
        data class Success(val medicines: List<Medicine>) : TreatmentUiState()
        data class Error(val exception: Throwable) : TreatmentUiState()
    }


}