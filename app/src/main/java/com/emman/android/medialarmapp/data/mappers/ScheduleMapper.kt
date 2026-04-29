package com.emman.android.medialarmapp.data.mappers

import com.emman.android.medialarmapp.data.local.entities.ScheduleEntity
import com.emman.android.medialarmapp.domain.models.MedicineSchedule
import com.emman.android.medialarmapp.domain.models.SchedulePattern
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


fun SchedulePattern.toJson(): String {
    return when (this) {
        is SchedulePattern.Interval -> {
            """{"type":"INTERVAL","intervalHours":$intervalHours,"startTime":"$startTime"}"""
        }
        is SchedulePattern.TimesPerDay -> {
            val times = intakeTimes.joinToString(",") { "\"$it\"" }
            """{"type":"TIMES_PER_DAY","timesPerDay":$timesPerDay,"intakeTimes":[$times]}"""
        }
        is SchedulePattern.SpecificDays -> {
            val days = daysOfWeek.joinToString(",") { "\"$it\"" }
            """{"type":"SPECIFIC_DAYS","daysOfWeek":[$days],"timeOfDay":"$timeOfDay"}"""
        }
        is SchedulePattern.Cyclic -> {
            """{"type":"CYCLIC","activeDays":$activeDays,"restDays":$restDays,"timeOfDay":"$timeOfDay","cycleStartDate":"$cycleStartDate"}"""
        }
        is SchedulePattern.AsNeeded -> {
            """{"type":"AS_NEEDED","minimumHoursBetween":$minimumHoursBetween,"maxDosesPerDay":$maxDosesPerDay}"""
        }
    }
}


fun String.toSchedulePattern(): SchedulePattern {
    return when {
        contains("\"type\":\"INTERVAL\"") -> {
            val hours = Regex(""""intervalHours":(\d+)""").find(this)?.groupValues?.get(1)?.toInt() ?: 8
            val time = Regex(""""startTime":"([^"]+)"""").find(this)?.groupValues?.get(1) ?: "08:00"
            SchedulePattern.Interval(hours, java.time.LocalTime.parse(time))
        }
        else -> SchedulePattern.Interval(8, java.time.LocalTime.of(8, 0))  // Default
    }
}



fun MedicineSchedule.toEntity(): ScheduleEntity {
    val medicineIdLong = medicineId.toLongOrNull() ?: 0
    val pattern = configuration.pattern

    return ScheduleEntity(
        id = if (id.toLongOrNull() != null) id.toLong() else 0,
        medicineId = medicineIdLong,
        scheduleType = when (pattern) {
            is SchedulePattern.Interval -> "INTERVAL"
            is SchedulePattern.TimesPerDay -> "TIMES_PER_DAY"
            is SchedulePattern.SpecificDays -> "SPECIFIC_DAYS"
            is SchedulePattern.Cyclic -> "CYCLIC"
            is SchedulePattern.AsNeeded -> "AS_NEEDED"
        },
        patternJson = pattern.toJson(),
        startDate = configuration.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli(),
        endDate = configuration.endDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()
            ?.toEpochMilli(),
        timeZoneId = ZoneId.systemDefault().id,
        isActive = configuration.isActive,
        createdAt = createdAt.toInstant().toEpochMilli()
    )
}


fun ScheduleEntity.toDomain(): MedicineSchedule {
    val zoneId = ZoneId.of(timeZoneId)
    val pattern = patternJson.toSchedulePattern()

    return MedicineSchedule(
        id = id.toString(),
        medicineId = medicineId.toString(),
        configuration = SchedulePattern.ScheduleConfiguration(
            pattern = pattern,
            startDate = Instant.ofEpochMilli(startDate).atZone(zoneId).toLocalDate(),
            endDate = endDate?.let { Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate() },
            isActive = isActive
        ),
        createdAt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(createdAt), zoneId)
    )
}


fun List<ScheduleEntity>.toDomain(): List<MedicineSchedule> {
    return map { it.toDomain() }
}