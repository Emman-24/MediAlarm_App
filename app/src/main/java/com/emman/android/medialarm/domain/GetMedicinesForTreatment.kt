package com.emman.android.medialarm.domain

import com.emman.android.medialarm.data.local.IntervalUnit
import com.emman.android.medialarm.data.local.Medicine
import com.emman.android.medialarm.data.local.MedicineSchedule
import com.emman.android.medialarm.data.repository.MedicineRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetMedicinesForTreatment @Inject constructor(
    private val repository: MedicineRepository
) {

    operator fun invoke(selectedDay: LocalDate): List<Medicine> {
        return repository.getAllMedicines()
            .filter { isMedicineScheduleForDay(it, selectedDay) }
            .flatMap { expandedMedicineSchedule(it, selectedDay) }
    }

    private fun expandedMedicineSchedule(
        medicine: Medicine,
        selectedDay: LocalDate
    ): List<Medicine> {
        return when (val schedule = medicine.schedule) {
            is MedicineSchedule.MultipleTimesDaily -> {
                schedule.times.map { time ->
                    medicine.copy(
                        schedule = MedicineSchedule.MultipleTimesDaily(
                            listOf(time)
                        )
                    )
                }
            }

            is MedicineSchedule.Interval -> handleIntervalSchedule(medicine, schedule, selectedDay)
            else -> listOf(medicine)
        }
    }

    private fun handleIntervalSchedule(
        medicine: Medicine,
        schedule: MedicineSchedule.Interval,
        selectedDay: LocalDate
    ): List<Medicine> {
        return when (schedule.intervalUnit) {
            IntervalUnit.HOURS -> generateHourlyIntervalTimes(medicine, schedule, selectedDay)
            IntervalUnit.DAYS -> {
                schedule.times.map { time ->
                    medicine.copy(
                        schedule = schedule.copy(times = listOf(time))
                    )
                }
            }
        }
    }

    private fun generateHourlyIntervalTimes(
        medicine: Medicine,
        schedule: MedicineSchedule.Interval,
        selectedDay: LocalDate
    ): List<Medicine> {

        val times = mutableListOf<LocalTime>()
        val intervalHours = schedule.interval
        val selectedDayStart = selectedDay.atStartOfDay()
        val selectedDayEnd = selectedDay.plusDays(1).atStartOfDay()

        for (initialTime in schedule.times) {

            var currentDateTime = medicine.startDate.atTime(initialTime)

            while (currentDateTime.isBefore(selectedDayStart)) {
                currentDateTime = currentDateTime.plusHours(intervalHours.toLong())
                if (medicine.endDate != null && currentDateTime.toLocalDate()
                        .isAfter(medicine.endDate)
                ) {
                    break
                }
            }
            while (currentDateTime.isBefore(selectedDayEnd)) {
                if (
                    currentDateTime.toLocalDate() == selectedDay &&
                    (medicine.endDate == null || !currentDateTime.toLocalDate()
                        .isAfter(medicine.endDate))
                ) {
                    times.add(currentDateTime.toLocalTime())
                }
                currentDateTime = currentDateTime.plusHours(intervalHours.toLong())
            }
        }
        return times.distinct().map { time ->
            medicine.copy(
                schedule = MedicineSchedule.Interval(
                    times = listOf(time),
                    intervalUnit = IntervalUnit.HOURS,
                    interval = intervalHours
                )
            )
        }
    }

    private fun isMedicineScheduleForDay(medicine: Medicine, selectedDay: LocalDate): Boolean {
        if (selectedDay.isBefore(medicine.startDate) ||
            (medicine.endDate != null && selectedDay.isAfter(medicine.endDate))
        ) {
            return false
        }
        return when (val schedule = medicine.schedule) {
            is MedicineSchedule.MultipleTimesDaily -> true
            is MedicineSchedule.SpecificDaysOfWeek -> selectedDay.dayOfWeek in schedule.daysOfWeek
            is MedicineSchedule.Cyclic -> {
                val daysSinceStart =
                    ChronoUnit.DAYS.between(medicine.startDate, selectedDay).toInt()
                daysSinceStart % (schedule.daysOn + schedule.daysOff) < schedule.daysOn
            }

            is MedicineSchedule.Interval -> {
                when (schedule.intervalUnit) {
                    IntervalUnit.DAYS -> {
                        val daysSinceStart =
                            ChronoUnit.DAYS.between(medicine.startDate, selectedDay).toInt()
                        daysSinceStart % schedule.interval == 0
                    }

                    IntervalUnit.HOURS -> true
                }
            }
        }
    }


}