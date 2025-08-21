package com.emman.android.medialarm.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarm.data.local.entities.CyclicEntity
import com.emman.android.medialarm.data.local.entities.DosageUnit
import com.emman.android.medialarm.data.local.entities.IntakeTimeEntity
import com.emman.android.medialarm.data.local.entities.MedicineEntity
import com.emman.android.medialarm.data.local.entities.MedicineForm
import com.emman.android.medialarm.data.local.entities.ScheduleEntity
import com.emman.android.medialarm.data.local.entities.ScheduleType
import com.emman.android.medialarm.data.repository.CyclicRepositoryImpl
import com.emman.android.medialarm.data.repository.IntakeTimeRepositoryImpl
import com.emman.android.medialarm.data.repository.MedicineRepositoryImpl
import com.emman.android.medialarm.domain.models.MedicationTime
import com.emman.android.medialarm.domain.models.MedicineFormState
import com.emman.android.medialarm.utils.ValidationResult
import com.emman.android.medialarm.utils.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddMedineViewModel @Inject constructor(
    private val medicineRepository: MedicineRepositoryImpl,
    private val intakeTimeRepository: IntakeTimeRepositoryImpl,
    private val cyclicRepository: CyclicRepositoryImpl,
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

    fun findMedicineForm(formType: String): MedicineForm {
        return when (formType) {
            "Tablet" -> MedicineForm.TABLET
            "Capsule" -> MedicineForm.CAPSULE
            "Pill" -> MedicineForm.PILL
            "Powder" -> MedicineForm.POWDER
            "Granules" -> MedicineForm.GRANULES
            "Lozenge" -> MedicineForm.LOZENGE
            "Liquid" -> MedicineForm.LIQUID
            "Syrup" -> MedicineForm.SYRUP
            "Suspension" -> MedicineForm.SUSPENSION
            "Drops" -> MedicineForm.DROPS
            "Injection" -> MedicineForm.INJECTION
            "Ointment" -> MedicineForm.OINTMENT
            "Cream" -> MedicineForm.CREAM
            "Gel" -> MedicineForm.GEL
            "Lotion" -> MedicineForm.LOTION
            "Foam" -> MedicineForm.FOAM
            "Patch" -> MedicineForm.PATCH
            "Inhaler" -> MedicineForm.INHALER
            "Spray" -> MedicineForm.SPRAY
            "Nebulizer Solution" -> MedicineForm.NEBULIZER
            "Suppository" -> MedicineForm.SUPPOSITORY
            "Implant" -> MedicineForm.IMPLANT
            else -> {
                MedicineForm.TABLET
            }
        }
    }

    fun mapStateToEntity(state: MedicineFormState): MedicineEntity {
        return MedicineEntity(
            name = state.medicineName,
            dosageUnit = findDosageUnit(state.unit),
            dosageAmount = state.dosage.toDouble(),
            formType = findMedicineForm(state.formType),
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

    private val _startDate = MutableLiveData<LocalDateTime>()
    val startDate: MutableLiveData<LocalDateTime> = _startDate

    private val _intakeTime = MutableLiveData<List<IntakeTimeEntity>>()
    val intakeTime: MutableLiveData<List<IntakeTimeEntity>> = _intakeTime

    private val _medicationTimes = MutableLiveData<List<MedicationTime>>()
    val medicationTimes: MutableLiveData<List<MedicationTime>> = _medicationTimes

    fun mapStateToEntity(scheduleId: Long, list: List<MedicationTime>): List<IntakeTimeEntity> {
        return list.map {
            IntakeTimeEntity(
                scheduleId = scheduleId.toLong(),
                intakeTime = it.time,
                quantity = it.amount
            )
        }
    }

    fun setIntakeDays(days: Int) {
        _intakeDays.value = days
    }

    fun setPauseDays(days: Int) {
        _pauseDays.value = days
    }

    fun setStartDate(date: LocalDateTime) {
        _startDate.value = date
    }

    // Result class for save operation
    sealed class SaveResult {
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }

    // MutableLiveData to observe save result
    private val _saveResult = MutableLiveData<SaveResult>()
    val saveResult: MutableLiveData<SaveResult> = _saveResult

    fun saveMedicineCyclic() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Validate required data
                    val medTimes = medicationTimes.value
                    val intakeDaysValue = intakeDays.value
                    val pauseDaysValue = pauseDays.value
                    val startDateValue = startDate.value

                    // Save Medicine
                    val medicine = mapStateToEntity(uiStateMedicine.value)
                    val medicineId = medicineRepository.insertMedicine(medicine)

                    // Save Schedule
                    val schedule = ScheduleEntity(
                        medicineId = medicineId,
                        scheduleType = ScheduleType.CYCLIC
                    )
                    val scheduleId = medicineRepository.insertSchedule(schedule)

                    // Save Intake Times
                    val intakeTimes: List<IntakeTimeEntity> = mapStateToEntity(scheduleId, medTimes)
                    intakeTimes.forEach {
                        intakeTimeRepository.insertIntakeTime(it)
                    }

                    // Save Cyclic
                    val cyclic = CyclicEntity(
                        scheduleId = scheduleId,
                        intakeDays = intakeDaysValue,
                        pauseDays = pauseDaysValue,
                        startTime = startDateValue
                    )
                    cyclicRepository.insert(cyclic)

                    _saveResult.postValue(SaveResult.Success)
                }
            } catch (e: Exception) {
                Log.e("AddMedicineViewModel", "Error saving medicine: ${e.message}")
                _saveResult.postValue(SaveResult.Error("Error saving medicine cycle: ${e.message}"))
            }
        }
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
