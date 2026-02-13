package com.emman.android.medialarmapp.domain.usecases.alarm

import com.emman.android.medialarmapp.domain.models.IntakeEvent
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Use case responsible for confirming that a medication has been taken.
 *
 * This class is designed to facilitate the process of marking a scheduled alarm as completed
 * and recording an intake event for tracking purposes. It interacts with the `ScheduleRepository` to
 * retrieve, update, and store relevant data.
 *
 * @property repository The `ScheduleRepository` used to interact with schedule, alarm, and intake event data.
 */

class ConfirmMedicationTakenUseCase(
    private val repository: ScheduleRepository,
) {
    suspend operator fun invoke(
        alarmId: String,
        takenAt: ZonedDateTime = ZonedDateTime.now(),
        notes: String? = null,
    ): Result<Unit> {
        return try {
            val alarm = repository.getAlarmById(alarmId) ?: return Result.failure(
                IllegalArgumentException("Alarm not found")
            )
            repository.markAlarmAsTaken(alarmId, takenAt).onFailure { return Result.failure(it) }

            val delayMinutes = Duration.between(
                alarm.scheduledTime,
                takenAt
            ).toMinutes()

            val event = IntakeEvent(
                id = UUID.randomUUID().toString(),
                alarmId = alarmId,
                medicineId = alarm.medicineId,
                scheduledTime = alarm.scheduledTime,
                actualTakenTime = takenAt,
                delayMinutes = delayMinutes,
                notes = notes,
                createdAt = ZonedDateTime.now()
            )

            repository.saveIntakeEvent(event).onFailure { return Result.failure(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}