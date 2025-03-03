package com.emman.android.medialarm.data.local

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


enum class IntakeAdvice {
    NONE, BEFORE_MEAL, WITH_MEAL, AFTER_MEAL, CUSTOM
}

enum class IntervalUnit {
    HOURS, DAYS
}

sealed class MedicineSchedule {
    data class MultipleTimesDaily(val times: List<LocalTime>) : MedicineSchedule()
    data class SpecificDaysOfWeek(val times: List<LocalTime>, val daysOfWeek: Set<DayOfWeek>) :
        MedicineSchedule()

    data class Cyclic(val times: List<LocalTime>, val daysOn: Int, val daysOff: Int) :
        MedicineSchedule()

    data class Interval(
        val times: List<LocalTime>,
        val intervalUnit: IntervalUnit,
        val interval: Int
    ) : MedicineSchedule()
}

data class Medicine(
    val id: Long,                            // Unique identifier for the medicine
    var name: String,                       // Name of the medicine
    var amount: String,                     // Amount of the medicine
    var dosage: String,                     // Dosage of the medicine (500, 100, 1) ml, etc.
    var unit: String,                       // Unit of the medicine ( mg, ml, etc.)
    var notes: String,                       // Notes or additional information about the medicine
    var pharmaceuticalForm: String,         // Pharmaceutical form of the medicine (tablet, capsule, etc.)
    var intakeAdvice: IntakeAdvice,                // Advice for taking (None,Before meal, with meal,after meal,custom advice))
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val isActive: Boolean = true,            // Flag indicating whether the medicine is active
    val schedule: MedicineSchedule,

    ) {
    val getHour: String = when (schedule) {
        is MedicineSchedule.MultipleTimesDaily -> {
            schedule.times.joinToString(", ") { time ->
                time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))
            }
        }

        is MedicineSchedule.SpecificDaysOfWeek -> {
            schedule.times.joinToString(", ") { time ->
                time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))
            }
        }

        is MedicineSchedule.Cyclic -> {
            schedule.times.joinToString(", ") { time ->
                time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))
            }
        }

        is MedicineSchedule.Interval -> {
            schedule.times.joinToString(", ") { time ->
                time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))
            }
        }

    }
}