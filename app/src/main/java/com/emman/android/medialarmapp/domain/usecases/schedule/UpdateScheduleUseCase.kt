package com.emman.android.medialarmapp.domain.usecases.schedule

import com.emman.android.medialarmapp.domain.calculator.ScheduleCalculator
import com.emman.android.medialarmapp.domain.models.AlarmStatus
import com.emman.android.medialarmapp.domain.models.DosageUnit
import com.emman.android.medialarmapp.domain.models.SchedulePattern
import com.emman.android.medialarmapp.domain.models.ScheduledAlarm
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random

/**
 * Use case responsible for updating an existing schedule and recalculating its associated alarms.
 *
 * This class provides functionality to update the configuration of a schedule, recalculate its
 * scheduled times based on the new configuration, and persist the associated alarms in the repository.
 *
 * @property repository A repository interface to manage schedules and alarms.
 * @property calculator A utility for calculating scheduled times based on a given scheduling pattern.
 */

class UpdateScheduleUseCase(
    private val repository: ScheduleRepository,
    private val calculator: ScheduleCalculator,
) {
    suspend operator fun invoke(
        scheduleId: String,
        newConfig: SchedulePattern.ScheduleConfiguration
    ):Result<Int> {
        return try {

            val existingSchedule = repository.getScheduleById(scheduleId) ?: return Result.failure(IllegalArgumentException("Schedule not found"))

            val updatedSchedule = existingSchedule.copy(
                configuration = newConfig
            )

            repository.updateSchedule(updatedSchedule).getOrElse { return Result.failure(it) }

            repository.deleteAlarmsForSchedule(scheduleId).getOrElse { return Result.failure(it) }

            val now = ZonedDateTime.now()
            val zoneId = ZoneId.systemDefault()

            val scheduledTimes = calculator.calculateNext(
                pattern = newConfig.pattern,
                from = now,
                count = 100,
                zoneId = zoneId
            )

            val newAlarms = scheduledTimes.map { scheduledTime ->
                ScheduledAlarm(
                    id = UUID.randomUUID().toString(),
                    scheduleId = scheduleId,
                    medicineId = existingSchedule.medicineId,
                    medicineName = "",  // Se llenará desde joined query
                    dosageAmount = 0.0,
                    dosageUnit = DosageUnit.MILLIGRAMS,
                    scheduledTime = scheduledTime,
                    status = AlarmStatus.SCHEDULED,
                    alarmRequestCode = generateUniqueRequestCode(),
                    createdAt = ZonedDateTime.now()
                )
            }

            repository.saveAlarms(newAlarms).getOrElse { return Result.failure(it) }

            Result.success(newAlarms.size)

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