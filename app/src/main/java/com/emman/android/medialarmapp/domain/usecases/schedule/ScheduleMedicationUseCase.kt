package com.emman.android.medialarmapp.domain.usecases.schedule

import com.emman.android.medialarmapp.domain.alarm.AlarmScheduler
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


class ScheduleMedicationUseCase(
    private val repository: ScheduleRepository,
    private val calculator: ScheduleCalculator,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(
        medicine: Medicine,
        scheduleConfig: SchedulePattern.ScheduleConfiguration,
        alarmsToGenerate: Int = 100,
    ): Result<ScheduleResult> = runCatching {

        val now = ZonedDateTime.now()
        val zoneId = ZoneId.systemDefault()

        val scheduledTimes = calculator.calculateNext(
            pattern = scheduleConfig.pattern,
            from = now,
            count = alarmsToGenerate,
            zoneId = zoneId
        )

        val schedule = MedicineSchedule(
            id = "0",
            medicineId = "0",
            configuration = scheduleConfig,
            createdAt = now
        )

        val alarms = scheduledTimes.map { scheduledTime ->
            ScheduledAlarm(
                id = UUID.randomUUID().toString(),
                scheduleId = "0",
                medicineId = "0",
                medicineName = medicine.name,
                dosageAmount = medicine.dosageAmount,
                dosageUnit = medicine.dosageUnit,
                scheduledTime = scheduledTime,
                status = AlarmStatus.SCHEDULED,
                alarmRequestCode = generateUniqueRequestCode(),
                createdAt = now
            )
        }

        val transactionResult = repository.saveMedicineWithScheduleAndAlarms(
            medicine = medicine,
            schedule = schedule,
            alarms = alarms
        ).getOrThrow()

        alarmScheduler.rescheduleAll(alarms)

        ScheduleResult(
            medicineId    = transactionResult.medicineId.toString(),
            scheduleId    = transactionResult.scheduleId.toString(),
            alarmsCreated = transactionResult.alarmIds.size,
        )

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
    val alarmsCreated: Int,
)