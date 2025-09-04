package com.emman.android.medialarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarm.di.DateProvider
import com.emman.android.medialarm.domain.models.CalendarDay
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val dateProvider: DateProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun initCalendar(initialDate: LocalDate? = null) {
        val startDate = initialDate ?: dateProvider.getCurrentDate()
        generateWeek(startDate)
        onDaySelected(startDate)
    }

    private fun generateWeek(startDate: LocalDate) {
        viewModelScope.launch {
            val weekFields = WeekFields.of(Locale.getDefault())
            val currentDay = startDate.with(weekFields.dayOfWeek(), 1)

            val days = (0..6).map {
                val date = currentDay.plusDays(it.toLong())
                CalendarDay(
                    date = date,
                    isSelected = date.isEqual(_uiState.value.selectedDate)
                )
            }

            _uiState.update { currentState ->
                currentState.copy(
                    days = days,
                    headerText = formatHeaderText(currentState.selectedDate)
                )
            }

        }
    }

    fun onDaySelected(date: LocalDate) {
        _uiState.update { currentState ->
            val newDays = currentState.days.map { day ->
                day.copy(isSelected = day.date.isEqual(date))
            }
            currentState.copy(
                selectedDate = date,
                days = newDays,
                headerText = formatHeaderText(date)
            )
        }
    }


    private fun formatHeaderText(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())
        return date.format(formatter)
    }

    fun selectNextWeek() {
        val nextWeekDate = _uiState.value.days.last().date.plusDays(1)
        generateWeek(nextWeekDate)
    }

    fun selectPreviousWeek() {
        val previousWeekDate = _uiState.value.days.first().date.minusDays(1)
        generateWeek(previousWeekDate)
    }

    data class CalendarUiState(
        val selectedDate: LocalDate = LocalDate.now(),
        val days: List<CalendarDay> = emptyList(),
        val headerText: String = "",
    )
}