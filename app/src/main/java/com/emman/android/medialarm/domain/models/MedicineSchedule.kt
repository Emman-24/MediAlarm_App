package com.emman.android.medialarm.domain.models

import com.emman.android.medialarm.data.local.entities.IntervalUnit
import java.time.DayOfWeek

sealed class MedicineSchedule {
    data class MultipleTimesDaily(val times: List<MedicationTime>) : MedicineSchedule()
    data class SpecificDays(val times: List<MedicationTime>, val daysOfWeek: Set<DayOfWeek>) : MedicineSchedule()
    data class Cyclic(val intakeDays: Int, val pauseDays : Int, val times: List<MedicationTime>) : MedicineSchedule()
    data class Interval(val times: List<MedicationTime>, val intervalUnit: IntervalUnit, val interval: Int ) : MedicineSchedule()
}