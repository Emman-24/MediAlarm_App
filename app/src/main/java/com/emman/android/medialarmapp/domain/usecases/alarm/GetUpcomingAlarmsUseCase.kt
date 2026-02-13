package com.emman.android.medialarmapp.domain.usecases.alarm

import com.emman.android.medialarmapp.domain.models.ScheduledAlarm
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case that retrieves a list of upcoming scheduled alarms.
 *
 * This class is responsible for interacting with the `ScheduleRepository` to observe a stream
 * of scheduled alarms that are due in the future, up to a specified limit.
 *
 * The alarms retrieved may represent various states (e.g., scheduled, snoozed) but are always
 * ordered based on their scheduled time. This use case ensures that applications can access and
 * react to upcoming alarms dynamically as they become available or change.
 *
 * @property repository The `ScheduleRepository` instance used to fetch upcoming alarms.
 */
class GetUpcomingAlarmsUseCase(
    private val repository: ScheduleRepository,
) {
    operator fun invoke(limit: Int = 10): Flow<List<ScheduledAlarm>> {
        require(limit > 0) { "Limit must be positive, got: $limit" }
        return repository.observeUpcomingAlarms(limit)
    }
}