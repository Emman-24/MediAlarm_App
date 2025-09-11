package com.emman.android.medialarm.domain.usecases

import com.emman.android.medialarm.data.local.entities.IntervalUnit
import com.emman.android.medialarm.data.local.entities.ScheduleType
import com.emman.android.medialarm.data.local.relations.MedicineWithSchedules
import com.emman.android.medialarm.data.repository.CyclicRepositoryImpl
import com.emman.android.medialarm.data.repository.IntervalRepositoryImpl
import com.emman.android.medialarm.data.repository.MedicineRepositoryImpl
import com.emman.android.medialarm.data.repository.MultipleTimesRepositoryImpl
import com.emman.android.medialarm.data.repository.SpecificRepositoryImpl
import com.emman.android.medialarm.domain.models.MedicineDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMedicineUseCase @Inject constructor(
    private val medicineRepository: MedicineRepositoryImpl,
    private val multipleTimesRepository: MultipleTimesRepositoryImpl,
    private val specificRepository: SpecificRepositoryImpl,
    private val cyclicRepository: CyclicRepositoryImpl,
    private val intervalRepository: IntervalRepositoryImpl,
) {
    operator fun invoke(id: Long): Flow<MedicineDetails> {
        return medicineRepository.getMedicineWithSchedules(id).map { medicineWithSchedules ->
            getDetailsOfEachSchedule(medicineWithSchedules, id)
        }


    }

    private suspend fun getDetailsOfEachSchedule(
        medicineWithSchedules: MedicineWithSchedules,
        id: Long,
    ): MedicineDetails {
        val medicine = medicineWithSchedules.medicine

        for (schedule in medicineWithSchedules.schedules) {
            when (schedule.scheduleType) {
                ScheduleType.MULTIPLE_TIMES_DAILY -> {
                    val multipleEntity = multipleTimesRepository.getByScheduleId(schedule.id)
                    multipleEntity?.let { details ->
                        return MedicineDetails.MultipleTimes(
                            name = medicine.name,
                            dosage = medicine.dosageAmount.toString(),
                            unit = medicine.dosageUnit.symbol,
                            formType = medicine.formType,
                            notes = medicine.notes,
                            timesToTake = details.timesToTake,
                            startTime = details.startTime
                        )
                    }
                }

                ScheduleType.SPECIFIC_DAYS -> {
                    val specificEntity = specificRepository.getByScheduleId(schedule.id)
                    specificEntity?.let { details ->
                        return MedicineDetails.SpecificDays(
                            name = medicine.name,
                            dosage = medicine.dosageAmount.toString(),
                            unit = medicine.dosageUnit.symbol,
                            formType = medicine.formType,
                            notes = medicine.notes,
                            onSunday = details.onSunday,
                            onMonday = details.onMonday,
                            onTuesday = details.onTuesday,
                            onWednesday = details.onWednesday,
                            onThursday = details.onThursday,
                            onFriday = details.onFriday,
                            onSaturday = details.onSaturday,
                        )
                    }
                }

                ScheduleType.CYCLIC -> {
                    val cyclicEntity = cyclicRepository.getByScheduleId(schedule.id)
                    cyclicEntity?.let { details ->
                        return MedicineDetails.Cyclic(
                            name = medicine.name,
                            dosage = medicine.dosageAmount.toString(),
                            unit = medicine.dosageUnit.symbol,
                            formType = medicine.formType,
                            notes = medicine.notes,
                            intakeDays = details.intakeDays,
                            pauseDays = details.pauseDays,
                            startTime = details.startTime
                        )
                    }
                }

                ScheduleType.INTERVAL -> {
                    /**
                     * Get schedule
                     * Get the difference between days and hours and see the difference between them.
                     *
                     *
                     * Interval Days
                     * - get id
                     * - get schedule id
                     * - get the interval unit
                     * - get the interval value
                     * - get the start date
                     * - get the start time hour
                     * - get the end time hour
                     *
                     *
                     * Interval Hours
                     * - get id
                     * - get schedule id
                     * - get the interval unit
                     * - get the interval value
                     * - get the start date
                     *
                     * note: the rest values of the entity is null when the unit is hours
                     */
                    val intervalEntity = intervalRepository.getByScheduleId(schedule.id)
                    when (intervalEntity?.intervalUnit) {
                        IntervalUnit.DAYS -> {
                            return MedicineDetails.Interval(
                                name = medicine.name,
                                dosage = medicine.dosageAmount.toString(),
                                unit = medicine.dosageUnit.symbol,
                                formType = medicine.formType,
                                notes = medicine.notes,
                                intervalUnit = intervalEntity.intervalUnit,
                                intervalValue = intervalEntity.intervalValue,
                                startDate = intervalEntity.startDate
                            )
                        }

                        IntervalUnit.HOURS -> {
                            return MedicineDetails.Interval(
                                name = medicine.name,
                                dosage = medicine.dosageAmount.toString(),
                                unit = medicine.dosageUnit.symbol,
                                formType = medicine.formType,
                                notes = medicine.notes,
                                intervalUnit = intervalEntity.intervalUnit,
                                intervalValue = intervalEntity.intervalValue,
                                startDate = intervalEntity.startDate,
                                startTime = intervalEntity.startTime,
                                endTime = intervalEntity.endTime
                            )
                        }

                        null -> null
                    }
                }
            }
        }
        throw IllegalStateException("Medicine with ID $id not found")
    }
}