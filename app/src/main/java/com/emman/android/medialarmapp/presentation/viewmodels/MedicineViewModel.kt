package com.emman.android.medialarmapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarmapp.domain.models.DosageUnit
import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.models.MedicineForm
import com.emman.android.medialarmapp.domain.models.SchedulePattern
import com.emman.android.medialarmapp.domain.usecases.medicine.CreateMedicineUseCase
import com.emman.android.medialarmapp.domain.usecases.medicine.DeleteMedicineUseCase
import com.emman.android.medialarmapp.domain.usecases.medicine.GetActiveMedicinesUseCase
import com.emman.android.medialarmapp.domain.usecases.medicine.UpdateMedicineUseCase
import com.emman.android.medialarmapp.domain.usecases.schedule.ScheduleMedicationUseCase
import com.emman.android.medialarmapp.domain.usecases.schedule.ScheduleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

data class MedicineUiState(
    val isLoading: Boolean = true,
    val medicines: List<Medicine> = emptyList(),
    val filteredMedicines: List<Medicine> = emptyList(),
    val searchQuery: String = "",
    val selectedMedicine: Medicine? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val error: String? = null,
) {
    val isEmpty: Boolean get() = medicines.isEmpty() && !isLoading
    val hasActiveFilters: Boolean get() = searchQuery.isNotBlank()
    val displayedMedicines: List<Medicine>
        get() = if (hasActiveFilters) filteredMedicines else medicines
}

sealed interface MedicineUiEffect {
    data class ShowSnackbar(val message: String) : MedicineUiEffect
    data class MedicineCreated(val medicineId: String) : MedicineUiEffect
    data class MedicineScheduled(val result: ScheduleResult) : MedicineUiEffect
    data class MedicineDeleted(val medicineName: String) : MedicineUiEffect
    data object NavigateBack : MedicineUiEffect
    data class NavigateToSchedule(val medicineId: String) : MedicineUiEffect
}

sealed interface MedicineIntent {
    data class CreateMedicine(val medicine: Medicine) : MedicineIntent
    data class UpdateMedicine(val medicine: Medicine) : MedicineIntent
    data class DeleteMedicine(val medicineId: String) : MedicineIntent
    data class ScheduleMedicine(
        val medicine: Medicine,
        val scheduleConfig: SchedulePattern.ScheduleConfiguration,
        val alarmsToGenerate: Int = 100,
    ) : MedicineIntent

    data class SelectMedicine(val medicine: Medicine?) : MedicineIntent
    data class SearchMedicines(val query: String) : MedicineIntent
    data object DismissError : MedicineIntent
}


@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val getActiveMedicines: GetActiveMedicinesUseCase,
    private val createMedicine: CreateMedicineUseCase,
    private val updateMedicine: UpdateMedicineUseCase,
    private val deleteMedicine: DeleteMedicineUseCase,
    private val scheduleMedication: ScheduleMedicationUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MedicineUiState())
    val state: StateFlow<MedicineUiState> = _state.asStateFlow()

    private val _effect = Channel<MedicineUiEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        observeMedicines()
    }

    fun handleIntent(intent: MedicineIntent) {
        when (intent) {
            is MedicineIntent.CreateMedicine -> create(intent.medicine)
            is MedicineIntent.UpdateMedicine -> update(intent.medicine)
            is MedicineIntent.DeleteMedicine -> delete(intent.medicineId)
            is MedicineIntent.ScheduleMedicine -> schedule(
                intent.medicine,
                intent.scheduleConfig,
                intent.alarmsToGenerate,
            )

            is MedicineIntent.SelectMedicine -> _state.update {
                it.copy(selectedMedicine = intent.medicine)
            }

            is MedicineIntent.SearchMedicines -> applySearch(intent.query)
            is MedicineIntent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMedicines() {
        viewModelScope.launch {
            searchQuery
                .flatMapLatest { query ->
                    getActiveMedicines().map { medicines ->
                        Pair(medicines, query)
                    }
                }
                .catch { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Failed to load medicines",
                        )
                    }
                }
                .collect { (medicines, query) ->
                    val filtered = if (query.isBlank()) {
                        emptyList()
                    } else {
                        medicines.filter {
                            it.name.contains(query, ignoreCase = true)
                        }
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            medicines = medicines,
                            filteredMedicines = filtered,
                            searchQuery = query,
                        )
                    }
                }
        }
    }

    private fun create(medicine: Medicine) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            createMedicine(medicine)
                .onSuccess { id ->
                    emitEffect(MedicineUiEffect.MedicineCreated(id.toString()))
                    emitEffect(MedicineUiEffect.ShowSnackbar("${medicine.name} added successfully"))
                }
                .onFailure { error ->
                    val message = resolveErrorMessage(error, "Failed to create medicine")
                    _state.update { it.copy(error = message) }
                    emitEffect(MedicineUiEffect.ShowSnackbar(message))
                }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun update(medicine: Medicine) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            updateMedicine(medicine)
                .onSuccess {
                    emitEffect(MedicineUiEffect.ShowSnackbar("${medicine.name} updated"))
                    emitEffect(MedicineUiEffect.NavigateBack)
                }
                .onFailure { error ->
                    val message = resolveErrorMessage(error, "Failed to update medicine")
                    _state.update { it.copy(error = message) }
                    emitEffect(MedicineUiEffect.ShowSnackbar(message))
                }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun delete(medicineId: String) {
        viewModelScope.launch {
            val medicineName =
                _state.value.medicines.firstOrNull { it.id == medicineId }?.name ?: "Medicine"
            _state.update { it.copy(isDeleting = true) }
            deleteMedicine(medicineId)
                .onSuccess {
                    emitEffect(MedicineUiEffect.MedicineDeleted(medicineName))
                    emitEffect(MedicineUiEffect.ShowSnackbar("$medicineName removed"))
                }
                .onFailure { error ->
                    val message = resolveErrorMessage(error, "Failed to delete medicine")
                    _state.update { it.copy(error = message) }
                    emitEffect(MedicineUiEffect.ShowSnackbar(message))
                }
            _state.update { it.copy(isDeleting = false) }
        }
    }

    private fun schedule(
        medicine: Medicine,
        scheduleConfig: SchedulePattern.ScheduleConfiguration,
        alarmsToGenerate: Int,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            scheduleMedication(medicine, scheduleConfig, alarmsToGenerate)
                .onSuccess { result ->
                    emitEffect(MedicineUiEffect.MedicineScheduled(result))
                    emitEffect(MedicineUiEffect.ShowSnackbar("${medicine.name} scheduled — ${result.alarmsCreated} alarms created"))
                }
                .onFailure { error ->
                    val message = resolveErrorMessage(error, "Failed to schedule medicine")
                    _state.update { it.copy(error = message) }
                    emitEffect(MedicineUiEffect.ShowSnackbar(message))
                }
            _state.update { it.copy(isSaving = false) }
        }

    }

    private fun applySearch(query: String) {
        searchQuery.value = query
    }

    private fun emitEffect(effect: MedicineUiEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private fun resolveErrorMessage(throwable: Throwable, fallback: String): String =
        when (throwable) {
            is IllegalArgumentException -> throwable.message ?: fallback
            is NoSuchElementException -> throwable.message ?: fallback
            is IllegalStateException -> throwable.message ?: fallback
            else -> fallback
        }

    fun buildMedicine(
        id: String = "0",
        name: String,
        dosageAmount: Double,
        dosageUnit: DosageUnit,
        form: MedicineForm,
        notes: String? = null,
    ): Medicine {
        val now = ZonedDateTime.now()
        return Medicine(
            id = id,
            name = name,
            dosageAmount = dosageAmount,
            dosageUnit = dosageUnit,
            form = form,
            notes = notes,
            createdAt = now,
            updatedAt = now,
        )
    }

}