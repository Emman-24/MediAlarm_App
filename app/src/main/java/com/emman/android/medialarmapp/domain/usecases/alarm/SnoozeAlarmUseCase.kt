package com.emman.android.medialarmapp.domain.usecases.alarm

import com.emman.android.medialarmapp.domain.models.AlarmStatus
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import java.time.ZonedDateTime

class SnoozeAlarmUseCase(
    private val repository: ScheduleRepository,
) {
    suspend operator fun invoke(
        alarmId: String,
        snoozeDurationMinutes: Int = DEFAULT_SNOOZE_MINUTES,
    ): Result<ZonedDateTime> {
        return try {
            if (snoozeDurationMinutes <= 0) {
                return Result.failure(IllegalArgumentException("Snooze duration must be positive"))
            }
            if (snoozeDurationMinutes > MAX_SNOOZE_MINUTES) {
                return Result.failure(IllegalArgumentException("Snooze duration cannot exceed $MAX_SNOOZE_MINUTES minutes"))
            }

            val alarm = repository.getAlarmById(alarmId)
                ?: return Result.failure(NoSuchElementException("Alarm not found"))

            if (alarm.status == AlarmStatus.TAKEN || alarm.status == AlarmStatus.MISSED) {
                return Result.failure(IllegalStateException("Cannot snooze an alarm that is already ${alarm.status}"))
            }

            val snoozedUntil = ZonedDateTime.now().plusMinutes(snoozeDurationMinutes.toLong())


            val snoozedAlarm = alarm.copy(
                status = AlarmStatus.SNOOZED,
                snoozedUntil = snoozedUntil
            )

            repository.updateAlarm(snoozedAlarm).map { snoozedUntil }
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val DEFAULT_SNOOZE_MINUTES = 10
        const val MAX_SNOOZE_MINUTES = 60
    }

}