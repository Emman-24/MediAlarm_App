package com.emman.android.medialarmapp.data.mappers

import com.emman.android.medialarmapp.data.local.entities.ScheduledAlarmEntity
import com.emman.android.medialarmapp.domain.models.AlarmStatus
import com.emman.android.medialarmapp.domain.models.DosageUnit
import com.emman.android.medialarmapp.domain.models.ScheduledAlarm
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


fun ScheduledAlarm.toEntity(): ScheduledAlarmEntity {
    return ScheduledAlarmEntity(
        id = id,
        scheduleId = scheduleId.toLong(),
        medicineId = medicineId.toLong(),
        medicineName = medicineName,
        dosageAmount = dosageAmount,
        dosageUnit = dosageUnit.name,
        scheduledTime = scheduledTime.toInstant().toEpochMilli(),
        timeZoneId = scheduledTime.zone.id,
        status = status.name,
        takenAt = takenAt?.toInstant()?.toEpochMilli(),
        missedAt = missedAt?.toInstant()?.toEpochMilli(),
        snoozedUntil = snoozedUntil?.toInstant()?.toEpochMilli(),
        alarmRequestCode = alarmRequestCode,
        notificationShown = notificationShown,
        createdAt = createdAt.toInstant().toEpochMilli()
    )
}


fun ScheduledAlarmEntity.toDomain(): ScheduledAlarm {
    val zoneId = ZoneId.of(timeZoneId)

    return ScheduledAlarm(
        id = id,
        scheduleId = scheduleId.toString(),
        medicineId = medicineId.toString(),
        medicineName = medicineName,
        dosageAmount = dosageAmount,
        dosageUnit = DosageUnit.valueOf(dosageUnit),
        scheduledTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(scheduledTime), zoneId),
        status = AlarmStatus.valueOf(status),
        takenAt = takenAt?.let { ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zoneId) },
        missedAt = missedAt?.let { ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zoneId) },
        snoozedUntil = snoozedUntil?.let { ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), zoneId) },
        alarmRequestCode = alarmRequestCode,
        notificationShown = notificationShown,
        createdAt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(createdAt), zoneId)
    )
}


fun List<ScheduledAlarmEntity>.toDomainAlarms(): List<ScheduledAlarm> {
    return map { it.toDomain() }
}
