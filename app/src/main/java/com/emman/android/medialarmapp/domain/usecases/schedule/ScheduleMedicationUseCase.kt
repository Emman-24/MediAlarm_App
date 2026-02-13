package com.emman.android.medialarmapp.domain.usecases.schedule

import com.emman.android.medialarmapp.domain.calculator.ScheduleCalculator
import com.emman.android.medialarmapp.domain.models.AlarmStatus
import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.models.MedicineSchedule
import com.emman.android.medialarmapp.domain.models.SchedulePattern
import com.emman.android.medialarmapp.domain.models.ScheduledAlarm
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random

/**
 * Use case responsible for scheduling medication and persisting the schedules and associated alarms.
 *
 * This use case handles the following operations:
 * - Saving a medicine entity to the repository.
 * - Creating and saving a medication schedule based on the provided configuration.
 * - Generating a specified number of scheduled alarm instances.
 * - Persisting generated alarms in the repository.
 *
 * @constructor Creates a new instance of the use case with the specified dependencies.
 * @param repository The repository responsible for managing medicines, schedules, and alarms.
 * @param calculator The calculator responsible for generating scheduled times based on a pattern.
 */

class ScheduleMedicationUseCase(
    private val repository: ScheduleRepository,
    private val calculator: ScheduleCalculator,
) {

    suspend operator fun invoke(
        medicine: Medicine,
        scheduleConfig: SchedulePattern.ScheduleConfiguration,
        alarmsToGenerate: Int = 100
    ): Result<ScheduleResult> {
        return try {
            val medicineId = repository.saveMedicine(medicine).getOrElse { return Result.failure(it) }

            val schedule = MedicineSchedule(
                id = "0",
                medicineId = medicineId.toString(),
                configuration = scheduleConfig,
                createdAt = ZonedDateTime.now()
            )

            val scheduleId = repository.saveSchedule(schedule).getOrElse { return Result.failure(it) }

            val now = ZonedDateTime.now()
            val zoneId = ZoneId.systemDefault()

            val scheduledTimes = calculator.calculateNext(
                pattern = scheduleConfig.pattern,
                from = now,
                count = alarmsToGenerate,
                zoneId = zoneId
            )

            val alarms = scheduledTimes.map { scheduledTime ->
                ScheduledAlarm(
                    id = UUID.randomUUID().toString(),
                    scheduleId = scheduleId.toString(),
                    medicineId = medicineId.toString(),
                    medicineName = medicine.name,
                    dosageAmount = medicine.dosageAmount,
                    dosageUnit = medicine.dosageUnit,
                    scheduledTime = scheduledTime,
                    status = AlarmStatus.SCHEDULED,
                    alarmRequestCode = generateUniqueRequestCode(),
                    createdAt = ZonedDateTime.now()
                )
            }

            repository.saveAlarms(alarms).getOrElse { return Result.failure(it) }

            Result.success(
                ScheduleResult(
                    medicineId = medicineId.toString(),
                    scheduleId = scheduleId.toString(),
                    alarmsCreated = alarms.size
                )
            )
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    private fun generateUniqueRequestCode(): Int {
        val timestamp = System.currentTimeMillis() and 0xFFFFFF
        val random = Random.nextInt(0, 256)
        return ((timestamp shl 8) or random.toLong()).toInt()
    }
}

data class ScheduleResult(
    val medicineId: String,
    val scheduleId: String,
    val alarmsCreated: Int
)