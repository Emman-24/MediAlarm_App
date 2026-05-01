package com.emman.android.medialarmapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarmapp.domain.models.ScheduledAlarm
import com.emman.android.medialarmapp.domain.usecases.alarm.ConfirmMedicationTakenUseCase
import com.emman.android.medialarmapp.domain.usecases.alarm.GetUpcomingAlarmsUseCase
import com.emman.android.medialarmapp.domain.usecases.alarm.ReconcileAlarmsUseCase
import com.emman.android.medialarmapp.domain.usecases.alarm.SnoozeAlarmUseCase
import com.emman.android.medialarmapp.domain.usecases.medicine.GetActiveMedicinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val upcomingAlarms: List<ScheduledAlarm> = emptyList(),
    val activeMedicineCount: Int = 0,
    val isReconciling: Boolean = false,
    val error: String? = null,
) {
    val hasUpcomingAlarms: Boolean get() = upcomingAlarms.isNotEmpty()
    val nextAlarm: ScheduledAlarm? get() = upcomingAlarms.firstOrNull()
}

sealed interface HomeUiEffect {
    data class ShowSnackbar(val message: String) : HomeUiEffect
    data class AlarmSnoozed(val snoozedUntil: ZonedDateTime) : HomeUiEffect
    data object NavigateToMedicines : HomeUiEffect
    data object AlarmMarkedTaken : HomeUiEffect
}

sealed interface HomeIntent {
    data class MarkAlarmTaken(val alarmId: String, val notes: String? = null) : HomeIntent
    data class SnoozeAlarm(
        val alarmId: String,
        val durationMinutes: Int = SnoozeAlarmUseCase.DEFAULT_SNOOZE_MINUTES,
    ) : HomeIntent

    data object ReconcileAlarms : HomeIntent
    data object NavigateToMedicines : HomeIntent
    data object DismissError : HomeIntent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUpcomingAlarms: GetUpcomingAlarmsUseCase,
    private val getActiveMedicines: GetActiveMedicinesUseCase,
    private val confirmMedicationTaken: ConfirmMedicationTakenUseCase,
    private val snoozeAlarm: SnoozeAlarmUseCase,
    private val reconcileAlarms: ReconcileAlarmsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effect = Channel<HomeUiEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    companion object {
        private const val UPCOMING_ALARM_LIMIT = 10
    }

    init {
        observeDashboard()
        handleIntent(HomeIntent.ReconcileAlarms)
    }


    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.MarkAlarmTaken -> markAlarmTaken(intent.alarmId, intent.notes)
            is HomeIntent.SnoozeAlarm -> snoozeAlarm(intent.alarmId, intent.durationMinutes)
            is HomeIntent.ReconcileAlarms -> reconcile()
            is HomeIntent.NavigateToMedicines -> emitEffect(HomeUiEffect.NavigateToMedicines)
            is HomeIntent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    fun observeDashboard() {
        viewModelScope.launch {
            combine(
                getUpcomingAlarms(limit = UPCOMING_ALARM_LIMIT),
                getActiveMedicines(),
            ) { alarms, medicines ->
                Pair(alarms, medicines.size)
            }
                .catch { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Failed to load dashboard",
                        )
                    }
                }
                .collect { (alarms, medicineCount) ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            upcomingAlarms = alarms,
                            activeMedicineCount = medicineCount,
                            error = null,
                        )
                    }
                }
        }
    }

    private fun markAlarmTaken(alarmId: String, notes: String? = null) {
        viewModelScope.launch {
            confirmMedicationTaken(alarmId, notes = notes)
                .onSuccess {
                    emitEffect(HomeUiEffect.AlarmMarkedTaken)
                    emitEffect(HomeUiEffect.ShowSnackbar("Medication marked as taken ✓"))
                }
                .onFailure { error ->
                    val message = resolveErrorMessage(error, "Could not mark medication as taken")
                    _state.update { it.copy(error = message) }
                    emitEffect(HomeUiEffect.ShowSnackbar(message))
                }
        }
    }

    private fun snoozeAlarm(
        alarmId: String,
        durationMinutes: Int
    ) {
        viewModelScope.launch {
            snoozeAlarm(alarmId = alarmId, snoozeDurationMinutes = durationMinutes)
                .onSuccess { snoozedUntil ->
                    emitEffect(HomeUiEffect.AlarmSnoozed(snoozedUntil))
                    emitEffect(HomeUiEffect.ShowSnackbar("Medication snoozed for $durationMinutes minutes"))
                }
                .onFailure { error ->
                    val message = resolveErrorMessage(error, "Could not snooze medication")
                    _state.update { it.copy(error = message) }
                    emitEffect(HomeUiEffect.ShowSnackbar(message))
                }
        }
    }

    private fun reconcile() {
        viewModelScope.launch {
            _state.update { it.copy(isReconciling = true) }
            reconcileAlarms()
                .onSuccess { report ->
                    if (report.needsAttention) {
                        emitEffect(
                            HomeUiEffect.ShowSnackbar(
                                "${report.alarmsMissed} alarm(s) marked as missed"
                            )
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(error = error.message ?: "Reconciliation failed")
                    }
                }
            _state.update { it.copy(isReconciling = false) }
        }
    }


    private fun emitEffect(effect: HomeUiEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private fun resolveErrorMessage(throwable: Throwable, fallback: String): String {
        return when (throwable) {
            is IllegalArgumentException -> throwable.message ?: fallback
            is NoSuchElementException -> throwable.message ?: fallback
            is IllegalStateException -> throwable.message ?: fallback
            else -> fallback
        }
    }
}