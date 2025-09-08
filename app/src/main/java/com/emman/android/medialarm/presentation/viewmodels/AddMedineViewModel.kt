package com.emman.android.medialarm.presentation.viewmodels

import android.util.Log
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarm.R
import com.emman.android.medialarm.data.local.entities.CyclicEntity
import com.emman.android.medialarm.data.local.entities.DosageUnit
import com.emman.android.medialarm.data.local.entities.IntakeTimeEntity
import com.emman.android.medialarm.data.local.entities.IntervalEntity
import com.emman.android.medialarm.data.local.entities.IntervalUnit
import com.emman.android.medialarm.data.local.entities.MedicineEntity
import com.emman.android.medialarm.data.local.entities.MedicineForm
import com.emman.android.medialarm.data.local.entities.MultipleTimesDailyEntity
import com.emman.android.medialarm.data.local.entities.ScheduleEntity
import com.emman.android.medialarm.data.local.entities.ScheduleType
import com.emman.android.medialarm.data.local.entities.SpecificDaysEntity
import com.emman.android.medialarm.data.repository.CyclicRepositoryImpl
import com.emman.android.medialarm.data.repository.IntakeTimeRepositoryImpl
import com.emman.android.medialarm.data.repository.IntervalRepositoryImpl
import com.emman.android.medialarm.data.repository.MedicineRepositoryImpl
import com.emman.android.medialarm.data.repository.MultipleTimesRepositoryImpl
import com.emman.android.medialarm.data.repository.SpecificRepositoryImpl
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
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddMedineViewModel @Inject constructor(
    private val medicineRepository: MedicineRepositoryImpl,
    private val intakeTimeRepository: IntakeTimeRepositoryImpl,
    private val cyclicRepository: CyclicRepositoryImpl,
    private val multipleTimesRepository: MultipleTimesRepositoryImpl,
    private val specificRepository: SpecificRepositoryImpl,
    private val intervalRepository: IntervalRepositoryImpl,
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

    private val _startDateCyclic = MutableLiveData<LocalDateTime>()
    val startDateCyclic: MutableLiveData<LocalDateTime> = _startDateCyclic

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

    fun setStartDateCyclic(date: LocalDateTime) {
        _startDateCyclic.value = date
    }


    sealed class SaveResult {
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }


    private val _saveResultCyclic = MutableLiveData<SaveResult>()
    val saveResultCyclic: MutableLiveData<SaveResult> = _saveResultCyclic

    fun saveMedicineCyclic() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Validate required data
                    val medTimes = medicationTimes.value
                    val intakeDaysValue = intakeDays.value
                    val pauseDaysValue = pauseDays.value
                    val startDateValue = startDateCyclic.value

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

                    _saveResultCyclic.postValue(SaveResult.Success)
                }
            } catch (e: Exception) {
                Log.e("AddMedicineViewModel", "Error saving medicine: ${e.message}")
                _saveResultCyclic.postValue(SaveResult.Error("Error saving medicine cycle: ${e.message}"))
            }
        }
    }


    /**
     * LiveData for multiple times schedule
     */
    private val _multipleTimesSchedule = MutableLiveData<Int>()
    val multipleTimesSchedule: MutableLiveData<Int> = _multipleTimesSchedule

    private val _startDateMultiple = MutableLiveData<LocalDateTime>()
    val startDateMultiple: MutableLiveData<LocalDateTime> = _startDateMultiple

    fun setMultipleTimesSchedule(times: Int) {
        _multipleTimesSchedule.value = times
    }

    fun setMultipleTimesMedication(times: List<MedicationTime>) {
        _medicationTimes.value = times
    }

    fun setStartDateMultiple(date: LocalDateTime) {
        _startDateMultiple.value = date
    }

    private val _saveResultMultiple = MutableLiveData<SaveResult>()
    val saveResultMultiple: MutableLiveData<SaveResult> = _saveResultMultiple

    fun saveMedicineMultiple() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val medTimes = medicationTimes.value
                    val startDateValue = startDateMultiple.value

                    if (startDateValue == null) {
                        _saveResultMultiple.postValue(SaveResult.Error("No start date provided"))
                        return@withContext
                    }

                    val medicine = mapStateToEntity(uiStateMedicine.value)
                    val medicineId = medicineRepository.insertMedicine(medicine)

                    val schedule = ScheduleEntity(
                        medicineId = medicineId,
                        scheduleType = ScheduleType.MULTIPLE_TIMES_DAILY
                    )
                    val scheduleId = medicineRepository.insertSchedule(schedule)


                    val intakeTimes: List<IntakeTimeEntity> = mapStateToEntity(scheduleId, medTimes)
                    intakeTimes.forEach {
                        intakeTimeRepository.insertIntakeTime(it)
                    }

                    val multiple = MultipleTimesDailyEntity(
                        scheduleId = scheduleId,
                        timesToTake = multipleTimesSchedule.value,
                        startTime = startDateMultiple.value
                    )
                    multipleTimesRepository.insert(multiple)
                    _saveResultMultiple.postValue(SaveResult.Success)
                }
            } catch (e: Exception) {
                Log.e("AddMedicineViewModel", "Error saving medicine: ${e.message}")
                _saveResultMultiple.postValue(SaveResult.Error("Error saving multiple times medicine: ${e.message}"))
            }
        }
    }


    /**
     * LiveData for Specific Days of the week schedule
     */

    private val _specificDays = MutableLiveData<List<DayOfWeek>>()
    val specificDays: MutableLiveData<List<DayOfWeek>> = _specificDays

    private val _saveResultSpecific = MutableLiveData<SaveResult>()
    val saveResultSpecific: MutableLiveData<SaveResult> = _saveResultSpecific

    private val _medicationTimesSpecificDays = MutableLiveData<List<MedicationTime>>()
    val medicationTimesSpecificDays: MutableLiveData<List<MedicationTime>> =
        _medicationTimesSpecificDays

    fun setSpecificDays(days: List<DayOfWeek>) {
        _specificDays.value = days
    }

    private val weekdays = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
    )
    private val weekend = listOf(
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    )

    fun getDisplayState(days: List<DayOfWeek>): DayDisplayState {

        return when {
            days.size == 7 -> DayDisplayState.Summary(stringRes = R.string.every_day)
            days.size == 5 && days == weekdays -> DayDisplayState.Summary(stringRes = R.string.weekdays)
            days.size >= 5 -> DayDisplayState.Summary(
                pluralRes = R.plurals.days_a_week,
                quantity = days.size
            )

            days.size == 2 && days == weekend -> DayDisplayState.Summary(stringRes = R.string.weekends)
            days.isNotEmpty() -> DayDisplayState.Chips(days)
            else -> DayDisplayState.Summary(stringRes = R.string.no_days_selected)
        }
    }


    fun saveMedicineSpecific() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val medTimes = medicationTimesSpecificDays.value
                    val specificDaysValue = specificDays.value

                    val medicine = mapStateToEntity(uiStateMedicine.value)
                    val medicineId = medicineRepository.insertMedicine(medicine)

                    val schedule = ScheduleEntity(
                        medicineId = medicineId,
                        scheduleType = ScheduleType.SPECIFIC_DAYS
                    )
                    val scheduleId = medicineRepository.insertSchedule(schedule)

                    val intakeTimes: List<IntakeTimeEntity> = mapStateToEntity(scheduleId, medTimes)
                    intakeTimes.forEach {
                        intakeTimeRepository.insertIntakeTime(it)
                    }

                    val specificDays = SpecificDaysEntity(
                        scheduleId = scheduleId,
                        onSunday = specificDaysValue?.contains(DayOfWeek.SUNDAY) ?: false,
                        onMonday = specificDaysValue?.contains(DayOfWeek.MONDAY) ?: false,
                        onTuesday = specificDaysValue?.contains(DayOfWeek.TUESDAY) ?: false,
                        onWednesday = specificDaysValue?.contains(DayOfWeek.WEDNESDAY) ?: false,
                        onThursday = specificDaysValue?.contains(DayOfWeek.THURSDAY) ?: false,
                        onFriday = specificDaysValue?.contains(DayOfWeek.FRIDAY) ?: false,
                        onSaturday = specificDaysValue?.contains(DayOfWeek.SATURDAY) ?: false

                    )
                    specificRepository.insert(specificDays)
                    _saveResultSpecific.postValue(SaveResult.Success)
                }
            } catch (e: Exception) {
                Log.e("AddMedicineViewModel", "Error saving medicine: ${e.message}")
                _saveResultSpecific.postValue(SaveResult.Error("Error saving specific days medicine: ${e.message}"))
            }
        }
    }

    enum class DayOfWeek(val displayName: String) {
        MONDAY("Monday"), TUESDAY("Tuesday"), WEDNESDAY("Wednesday"),
        THURSDAY("Thursday"), FRIDAY("Friday"), SATURDAY("Saturday"), SUNDAY("Sunday")
    }

    sealed class DayDisplayState {
        data class Summary(
            @StringRes val stringRes: Int = 0,
            @PluralsRes val pluralRes: Int = 0,
            val quantity: Int = 0,
        ) : DayDisplayState()

        data class Chips(val days: List<DayOfWeek>) : DayDisplayState()
    }


    /**
     * Interval Schedule
     */

    /*
     *   Hours
     */

    private val _intervalDays = MutableLiveData<Int>()
    val intervalDays: MutableLiveData<Int> = _intervalDays

    private val _intervalValue = MutableLiveData<Int>()
    val intervalValue: MutableLiveData<Int> = _intervalValue

    private val _intervalUnit = MutableLiveData<IntervalUnit>()
    val intervalUnit: MutableLiveData<IntervalUnit> = _intervalUnit

    private val _startDateInterval = MutableLiveData<LocalDateTime>()
    val startDateInterval: MutableLiveData<LocalDateTime> = _startDateInterval

    private val _startTimeInterval = MutableLiveData<LocalTime>()
    val startTimeInterval: MutableLiveData<LocalTime> = _startTimeInterval

    private val _endTimeInterval = MutableLiveData<LocalTime>()
    val endTimeInterval: MutableLiveData<LocalTime> = _endTimeInterval

    private val _pillCount = MutableLiveData<Int>(1)
    val pillCount: MutableLiveData<Int> = _pillCount

    private val _saveResultInterval = MutableLiveData<SaveResult>()
    val saveResultInterval: MutableLiveData<SaveResult> = _saveResultInterval

    fun setStartIntervalTime(time: LocalTime) {
        _startTimeInterval.value = time
    }

    fun setEndIntervalTime(time: LocalTime) {
        _endTimeInterval.value = time
    }

    fun setIntervalUnit(unit: IntervalUnit) {
        _intervalUnit.value = unit
    }

    fun setStartDateInterval(date: LocalDateTime) {
        _startDateInterval.value = date
    }

    fun setIntervalDays(days: Int) {
        _intervalDays.value = days
    }

    fun setIntervalValue(value: Int) {
        _intervalValue.value = value
    }

    fun incrementPillCount() {
        _pillCount.value = _pillCount.value?.plus(1)
    }

    fun decrementPillCount() {
        if (_pillCount.value!! > 1) {
            _pillCount.value = _pillCount.value?.minus(1)
        }
    }

    fun saveMedicineIntervalHours() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {

                    val medicine = mapStateToEntity(uiStateMedicine.value)
                    val medicineId = medicineRepository.insertMedicine(medicine)

                    val schedule = ScheduleEntity(
                        medicineId = medicineId,
                        scheduleType = ScheduleType.INTERVAL
                    )
                    val scheduleId = medicineRepository.insertSchedule(schedule)

                    //Save intake times
                    val intakeTimes = IntakeTimeEntity(
                        scheduleId = scheduleId,
                        intakeTime = _startTimeInterval.value,
                        quantity = _pillCount.value.toDouble(),
                    )
                    intakeTimeRepository.insertIntakeTime(intakeTimes)

                    // Save interval data
                    val interval = IntervalEntity(
                        scheduleId = scheduleId,
                        intervalUnit = _intervalUnit.value ?: IntervalUnit.HOURS,
                        intervalValue = _intervalValue.value ?: 1,
                        startTime = _startTimeInterval.value,
                        endTime = _endTimeInterval.value,
                        startDate = _startDateInterval.value
                    )
                    intervalRepository.insert(interval)

                    _saveResultInterval.postValue(SaveResult.Success)
                }
            } catch (e: Exception) {
                Log.e("AddMedicineViewModel", "Error saving medicine: ${e.message}")
                _saveResultInterval.postValue(SaveResult.Error("Error saving interval medicine: ${e.message}"))
            }
        }

    }

    /**
     * Interval Schedule
     */

    /**
     * Days
     */

    val _medicationTimesIntervalDays = MutableLiveData<List<MedicationTime>>()
    val medicationTimesIntervalDays: MutableLiveData<List<MedicationTime>> =
        _medicationTimesIntervalDays


    fun saveMedicineIntervalDays() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val medicine = mapStateToEntity(uiStateMedicine.value)
                    val medicineId = medicineRepository.insertMedicine(medicine)

                    val schedule = ScheduleEntity(
                        medicineId = medicineId,
                        scheduleType = ScheduleType.INTERVAL
                    )
                    val scheduleId = medicineRepository.insertSchedule(schedule)

                    // Save Intake Times
                    val intakeTimes: List<IntakeTimeEntity> =
                        mapStateToEntity(scheduleId, medicationTimesIntervalDays.value)
                    intakeTimes.forEach {
                        intakeTimeRepository.insertIntakeTime(it)
                    }

                    //Save interval data
                    val interval = IntervalEntity(
                        scheduleId = scheduleId,
                        intervalUnit = _intervalUnit.value ?: IntervalUnit.DAYS,
                        intervalValue = _intervalDays.value ?: 1,
                        startDate = _startDateInterval.value
                    )
                    intervalRepository.insert(interval)
                    _saveResultInterval.postValue(SaveResult.Success)
                }

            } catch (e: Exception) {
                Log.e("AddMedicineViewModel", "Error saving medicine: ${e.message}")
                _saveResultInterval.postValue(SaveResult.Error("Error saving interval medicine: ${e.message}"))
            }
        }

    }


}
