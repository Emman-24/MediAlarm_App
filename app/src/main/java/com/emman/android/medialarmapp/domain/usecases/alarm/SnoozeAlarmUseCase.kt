package com.emman.android.medialarmapp.domain.usecases.alarm

import com.emman.android.medialarmapp.domain.alarm.AlarmScheduler
import com.emman.android.medialarmapp.domain.models.AlarmStatus
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import java.time.ZonedDateTime

class SnoozeAlarmUseCase(
    private val repository: ScheduleRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(
        alarmId: String,
        snoozeDurationMinutes: Int = DEFAULT_SNOOZE_MINUTES,
    ): Result<ZonedDateTime> = runCatching {

        require(snoozeDurationMinutes > 0) {
            "Snooze duration must be positive, got: $snoozeDurationMinutes"
        }

        require(snoozeDurationMinutes <= MAX_SNOOZE_MINUTES) {
            "Snooze duration cannot exceed $MAX_SNOOZE_MINUTES minutes"
        }

        val alarm = repository.getAlarmById(alarmId)
            ?: throw NoSuchElementException("Alarm not found: $alarmId")

        check(alarm.status != AlarmStatus.TAKEN && alarm.status != AlarmStatus.MISSED) {
            "Cannot snooze an alarm that is already ${alarm.status}"
        }


        val snoozedUntil = ZonedDateTime.now().plusMinutes(snoozeDurationMinutes.toLong())


        val snoozedAlarm = alarm.copy(
            status = AlarmStatus.SNOOZED,
            snoozedUntil = snoozedUntil
        )

        repository.updateAlarm(snoozedAlarm).getOrThrow()

        alarmScheduler.cancel(alarm)

        alarmScheduler.schedule(snoozedAlarm.copy(scheduledTime = snoozedUntil))

        snoozedUntil

    }

    companion object {
        const val DEFAULT_SNOOZE_MINUTES = 10
        const val MAX_SNOOZE_MINUTES = 60
    }

}