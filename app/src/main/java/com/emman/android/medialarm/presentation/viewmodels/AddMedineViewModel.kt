package com.emman.android.medialarm.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emman.android.medialarm.data.local.entities.DosageUnit
import com.emman.android.medialarm.data.local.entities.MedicineEntity
import com.emman.android.medialarm.data.local.entities.MedicineForm
import com.emman.android.medialarm.domain.models.MedicineFormState
import com.emman.android.medialarm.domain.repository.MedicineRepository
import com.emman.android.medialarm.utils.ValidationResult
import com.emman.android.medialarm.utils.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddMedineViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
) : ViewModel() {

    /**
     * LiveData for the medicine name
     */

    private val _medicine = MutableLiveData<MedicineEntity>()
    val medicine: MutableLiveData<MedicineEntity> = _medicine

    private val _uiStateMedicine = MutableStateFlow(MedicineFormState())
    val uiStateMedicine: StateFlow<MedicineFormState> = _uiStateMedicine.asStateFlow()

    private val _medicineNameUiState = MutableLiveData<String>()
    val medicineNameUiState: MutableLiveData<String> = _medicineNameUiState

    fun validateName(name: String): String? {
        val nameResult = Validators.notEmpty(name)
        // Only update the name field in the ViewModel when validation passes
        if (nameResult is ValidationResult.Valid) {
            _uiStateMedicine.update {
                it.copy(medicineName = name)
            }
        }
        return when (nameResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> nameResult.errorMessage
        }
    }

    fun validateDosage(dosage: String): String? {
        val dosageResult = Validators.isDosageDecimal(dosage)
        if (dosageResult is ValidationResult.Valid) {
            _uiStateMedicine.update {
                it.copy(
                    dosage = dosage
                )
            }
        }
        return when (dosageResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> dosageResult.errorMessage
        }
    }

    fun validateUnit(unit: String): String? {
        val unitResult = Validators.isValidateUnit(unit)
        if (unitResult is ValidationResult.Valid) {
            _uiStateMedicine.update {
                it.copy(
                    unit = unit
                )
            }
        }
        return when (unitResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> unitResult.errorMessage
        }
    }

    fun validateFormType(formType: String): String? {
        val formTypeResult = Validators.isValidateFormType(formType)
        if (formTypeResult is ValidationResult.Valid) {
            _uiStateMedicine.update {
                it.copy(
                    formType = formType
                )
            }
        }
        return when (formTypeResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> formTypeResult.errorMessage
        }
    }

    fun validateNotes(notes: String): String? {
        val notesResult = Validators.isValidNote(notes)
        if (notesResult is ValidationResult.Valid) {
            _uiStateMedicine.update {
                it.copy(
                    notes = notes
                )
            }
        }
        return when (notesResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> notesResult.errorMessage
        }
    }

    fun setMedicineName(name: String) {
        _medicineNameUiState.value = name
    }

    fun setIsActive(isActive: Boolean) {
        _uiStateMedicine.value.isActive = isActive
    }

    fun findDosageUnit(unit: String): DosageUnit {
        return when (unit) {
            "mg" -> DosageUnit.MILLIGRAM
            "g" -> DosageUnit.GRAM
            "ml" -> DosageUnit.MILLILITER
            "l" -> DosageUnit.LITER
            "IU" -> DosageUnit.INTERNATIONAL_UNIT
            "mcg" -> DosageUnit.MICROGRAM
            "%" -> DosageUnit.PERCENT
            "drops" -> DosageUnit.DROPS
            "puff" -> DosageUnit.PUFF
            else -> {
                DosageUnit.MILLIGRAM
            }
        }
    }


    fun mapStateToEntity(state: MedicineFormState): MedicineEntity {
        return MedicineEntity(
            name = state.medicineName,
            dosageUnit = findDosageUnit(state.unit),
            dosageAmount = state.dosage.toDouble(),
            formType = MedicineForm.valueOf(state.formType),
            notes = state.notes,
            isActive = state.isActive
        )
    }


    /**
     * LiveData for cyclic schedule
     */

    private val _intakeDays = MutableLiveData<Int>()
    val intakeDays: MutableLiveData<Int> = _intakeDays

    private val _pauseDays = MutableLiveData<Int>()
    val pauseDays: MutableLiveData<Int> = _pauseDays


    fun setIntakeDays(days: Int) {
        _intakeDays.value = days
    }

    fun setPauseDays(days: Int) {
        _pauseDays.value = days
    }


    /**
     * LiveData for multiple times schedule
     */

    private val _multipleTimesSchedule = MutableLiveData<Int>()
    val multipleTimesSchedule: MutableLiveData<Int> = _multipleTimesSchedule

    fun setMultipleTimesSchedule(times: Int) {
        _multipleTimesSchedule.value = times
    }


}
