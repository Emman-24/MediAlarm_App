package com.emman.android.medialarmapp.data.mappers

import com.emman.android.medialarmapp.data.local.entities.IntakeEventEntity
import com.emman.android.medialarmapp.domain.models.IntakeEvent
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


fun IntakeEvent.toEntity(): IntakeEventEntity {
    return IntakeEventEntity(
        id = id,
        alarmId = alarmId,
        medicineId = medicineId.toLong(),
        scheduledTime = scheduledTime.toInstant().toEpochMilli(),
        actualTakenTime = actualTakenTime.toInstant().toEpochMilli(),
        delayMinutes = delayMinutes,
        notes = notes,
        createdAt = createdAt.toInstant().toEpochMilli()
    )
}



fun IntakeEventEntity.toDomain(zoneId: ZoneId = ZoneId.systemDefault()): IntakeEvent {
    return IntakeEvent(
        id = id,
        alarmId = alarmId,
        medicineId = medicineId.toString(),
        scheduledTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(scheduledTime), zoneId),
        actualTakenTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(actualTakenTime), zoneId),
        delayMinutes = delayMinutes,
        notes = notes,
        createdAt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(createdAt), zoneId)
    )
}


fun List<IntakeEventEntity>.toDomainEvents(zoneId: ZoneId = ZoneId.systemDefault()): List<IntakeEvent> {
    return map { it.toDomain(zoneId) }
}