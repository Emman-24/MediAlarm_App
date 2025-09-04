package com.emman.android.medialarm.domain.usecases

import com.emman.android.medialarm.data.local.entities.ScheduleType
import com.emman.android.medialarm.data.local.relations.MedicineWithSchedules
import com.emman.android.medialarm.data.repository.CyclicRepositoryImpl
import com.emman.android.medialarm.data.repository.IntakeTimeRepositoryImpl
import com.emman.android.medialarm.data.repository.MedicineRepositoryImpl
import com.emman.android.medialarm.domain.models.MedicineScheduleState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetScheduleForDateUseCase @Inject constructor(
    private val medicineRepository: MedicineRepositoryImpl,
    private val intakeTimeRepository: IntakeTimeRepositoryImpl,
    private val cyclicRepository: CyclicRepositoryImpl,
) {

    operator fun invoke(date: LocalDate): Flow<List<MedicineScheduleState>> {
        return medicineRepository.getAllActiveMedicinesWithSchedules()
            .map { medicines ->
                val schedulesList = mutableListOf<MedicineScheduleState>()
                
                for (medicineWithSchedules in medicines) {
                    val medicineSchedules = processSchedules(medicineWithSchedules, date)
                    schedulesList.addAll(medicineSchedules)
                }
                
                schedulesList
            }
    }

    private suspend fun processSchedules(
        medicineWithSchedules: MedicineWithSchedules,
        date: LocalDate
    ): List<MedicineScheduleState> {
        val schedules = medicineWithSchedules.schedules
        val result = mutableListOf<MedicineScheduleState>()

        for (schedule in schedules) {
            when (schedule.scheduleType) {
                ScheduleType.MULTIPLE_TIMES_DAILY -> {
                    // TODO: Implement for MULTIPLE_TIMES_DAILY
                }
                ScheduleType.SPECIFIC_DAYS -> {
                    // TODO: Implement for SPECIFIC_DAYS
                }
                ScheduleType.CYCLIC -> {
                    val scheduleId: Long = schedule.id
                    val cyclicEntity = cyclicRepository.getByScheduleId(scheduleId)
                    val intakeTimes = intakeTimeRepository.getIntakeTimesByScheduleId(scheduleId)

                    cyclicEntity?.let { cyclic ->
                        val startTime = cyclic.startTime
                        val cycleDuration = cyclic.intakeDays + cyclic.pauseDays
                        val dateTime = LocalDateTime.of(date, LocalDateTime.now().toLocalTime())
                        
                        val daysSinceStart = ChronoUnit.DAYS.between(startTime, dateTime)
                        val dayInCycle = (daysSinceStart % cycleDuration).toInt()
                        val isIntakePeriod = dayInCycle < cyclic.intakeDays

                        if (isIntakePeriod) {
                            // We're in an intake period, process the intake times
                            for (intakeTime in intakeTimes) {
                                val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
                                val formattedTime = intakeTime.intakeTime.format(timeFormatter)
                                
                                result.add(
                                    MedicineScheduleState(
                                        id = medicineWithSchedules.medicine.id,
                                        name = medicineWithSchedules.medicine.name,
                                        dosage = intakeTime.quantity.toString(),
                                        unit = medicineWithSchedules.medicine.dosageUnit.symbol,
                                        formType = medicineWithSchedules.medicine.formType.displayName,
                                        timeToTake = formattedTime
                                    )
                                )
                            }
                        }
                    }
                }
                ScheduleType.INTERVAL -> {
                    // TODO: Implement for INTERVAL
                }
            }
        }
        
        return result
    }
}