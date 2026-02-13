package com.emman.android.medialarmapp.domain.usecases.alarm

import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import java.time.ZonedDateTime

/**
 * Use case responsible for reconciling the state of scheduled alarms.
 *
 * The `ReconcileAlarmsUseCase` identifies overdue scheduled alarms and marks them as missed,
 * and determines which alarms need to be rescheduled. This process is essential to ensure that
 * alarms are accurately tracked and managed within the system.
 *
 * This use case interacts with the `ScheduleRepository` to fetch all scheduled alarms,
 * evaluate their status based on the current timestamp, and apply necessary updates.
 *
 * The reconciliation process produces a `ReconciliationReport`, which summarizes the total
 * number of alarms checked, the number of alarms marked as missed, the count of alarms
 * requiring rescheduling, and the timestamp when the operation was executed.
 *
 * @property repository The `ScheduleRepository` used to fetch and update scheduled alarm data.
 */

class ReconcileAlarmsUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke():Result<ReconciliationReport>{
        return try {

            val now = ZonedDateTime.now()
            val gracePeriodMinutes = 30

            val allScheduledAlarms = repository.getAllScheduledAlarms()

            val overdueAlarms = allScheduledAlarms.filter { alarm ->
                alarm.isOverdue(now, gracePeriodMinutes)
            }

            var missedCount = 0
            overdueAlarms.forEach { alarm ->
                repository.markAlarmAsMissed(alarm.id, now)
                    .onSuccess { missedCount++ }
            }

            val alarmsToReschedule = allScheduledAlarms.filter { alarm ->
                !alarm.isOverdue(now, gracePeriodMinutes)
            }

            Result.success(
                ReconciliationReport(
                    totalAlarmsChecked = allScheduledAlarms.size,
                    alarmsMissed = missedCount,
                    alarmsToReschedule = alarmsToReschedule.size,
                    reconciledAt = now
                )
            )
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}


data class ReconciliationReport(
    val totalAlarmsChecked: Int,
    val alarmsMissed: Int,
    val alarmsToReschedule: Int,
    val reconciledAt: ZonedDateTime
) {
    val needsAttention: Boolean = alarmsMissed > 0
}