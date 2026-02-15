package com.emman.android.medialarmapp.domain.models

/**
 * Represents the result of scheduling a transaction for a medicine.
 *
 * This data class is used to capture and return the essential information
 * after a medicine schedule and its associated alarms have been created.
 * Such information includes the identifiers of the medicine, schedule,
 * and the alarms associated with the schedule.
 *
 * @property medicineId The unique identifier of the medicine associated with the schedule.
 * @property scheduleId The unique identifier of the created schedule.
 * @property alarmIds A list of unique identifiers for the alarms created as part of the schedule.
 */

data class ScheduleTransactionResult(
    val medicineId : Long,
    val scheduleId: Long,
    val alarmIds: List<Long>
)
