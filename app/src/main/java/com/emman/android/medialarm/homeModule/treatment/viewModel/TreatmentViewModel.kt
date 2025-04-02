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


enum class TreatmentStatus { LOADING, EMPTY, DONE, ERROR }

@HiltViewModel
class TreatmentViewModel @Inject constructor(
    private val getMedicinesForTreatment: GetMedicinesForTreatment
) : ViewModel() {


    private val _status = MutableLiveData<TreatmentStatus>()
    val status: LiveData<TreatmentStatus> = _status

    private val _medicines = MutableLiveData<List<Medicine>>()
    val medicines: LiveData<List<Medicine>> get() = _medicines


    init {
        loadMedicinesForDayOrder(LocalDate.now())
    }

    fun loadMedicinesForDayOrder(date: LocalDate) {
        viewModelScope.launch {
            _status.value = TreatmentStatus.LOADING
            try {
                _medicines.value = getMedicinesForTreatment.invoke(date).sortedBy { medicine ->
                    when (val schedule = medicine.schedule) {
                        is MedicineSchedule.Interval -> schedule.times.first()
                        is MedicineSchedule.Cyclic -> schedule.times.first()
                        is MedicineSchedule.SpecificDaysOfWeek -> schedule.times.first()
                        is MedicineSchedule.MultipleTimesDaily -> schedule.times.first()
                    }
                }
                if (_medicines.value.isNullOrEmpty()) {
                    _status.value = TreatmentStatus.EMPTY
                } else {
                    _status.value = TreatmentStatus.DONE
                }

            } catch (e: Exception) {
                _status.value = TreatmentStatus.ERROR
            }
        }
    }



}