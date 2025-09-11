package com.emman.android.medialarm.domain.models

import com.emman.android.medialarm.data.local.entities.IntervalUnit
import java.time.LocalDateTime
import java.time.LocalTime

sealed class MedicineDetails(
    val name: String,
    val dosage: String,
    val unit: String,
    val formType: MedicineForm,
    val notes: String?,
) {

    class MultipleTimes(
        name: String,
        dosage: String,
        unit: String,
        formType: MedicineForm,
        notes: String?,
        val timesToTake: Int,
        val startTime: LocalDateTime,
    ) : MedicineDetails(name, dosage, unit, formType, notes)

    class SpecificDays(
        name: String,
        dosage: String,
        unit: String,
        formType: MedicineForm,
        notes: String?,
        val onSunday: Boolean = false,
        val onMonday: Boolean = false,
        val onTuesday: Boolean = false,
        val onWednesday: Boolean = false,
        val onThursday: Boolean = false,
        val onFriday: Boolean = false,
        val onSaturday: Boolean = false,
    ) : MedicineDetails(name, dosage, unit, formType, notes)

    class Cyclic(
        name: String,
        dosage: String,
        unit: String,
        formType: MedicineForm,
        notes: String?,
        val intakeDays: Int,
        val pauseDays: Int,
        val startTime: LocalDateTime,
    ) : MedicineDetails(name, dosage, unit, formType, notes)

    class Interval(
        name: String,
        dosage: String,
        unit: String,
        formType: MedicineForm,
        notes: String?,
        val intervalUnit: IntervalUnit,
        val intervalValue: Int,
        val startDate: LocalDateTime,
        val startTime: LocalTime? = null,
        val endTime: LocalTime? = null,
    ) : MedicineDetails(name, dosage, unit, formType, notes)


    val scheduleDisplayText: String
        get() = when (this) {
            is Interval -> "Every ${intervalValue} ${intervalUnit.name.lowercase()}${if (intervalValue > 1) "s" else ""}"
            is MultipleTimes -> "${timesToTake} times per day"
            is SpecificDays -> {
                val days = mutableListOf<String>()
                if (onSunday) days.add("Sun")
                if (onMonday) days.add("Mon")
                if (onTuesday) days.add("Tue")
                if (onWednesday) days.add("Wed")
                if (onThursday) days.add("Thu")
                if (onFriday) days.add("Fri")
                if (onSaturday) days.add("Sat")
                days.joinToString(", ")
            }

            is Cyclic -> "${intakeDays} days on, ${pauseDays} days off"
        }

    val scheduleTypeText: String
        get() = when (this) {
            is Interval -> "Interval"
            is MultipleTimes -> "Multiple Times"
            is SpecificDays -> "Specific Days"
            is Cyclic -> "Cyclic"
        }

    val dosageDisplayText: String
        get() = "$dosage $unit"

}
