package com.emman.android.medialarmapp.domain.models

import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dosageAmount: Double,
    val dosageUnit: DosageUnit,
    val form: MedicineForm,
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
){
    init {
        require(name.isNotBlank()) { "Medicine name cannot be blank" }
        require(dosageAmount > 0) { "Dosage amount must be positive, got: $dosageAmount" }
    }
}


/**
 * Dosage units
 */
enum class DosageUnit(val displayName: String, val abbreviation: String) {
    MILLIGRAMS("Milligrams", "mg"),
    GRAMS("Grams", "g"),
    MILLILITERS("Milliliters", "ml"),
    UNITS("Units", "U"),
    MICROGRAMS("Micrograms", "mcg"),
    INTERNATIONAL_UNITS("International Units", "IU"),
    PERCENTAGE("Percentage", "%"),
    DROPS("Drops", "drops")
}

/*
 * Medicine forms
 */
enum class MedicineForm(val displayName: String) {
    TABLET("Tablet"),
    CAPSULE("Capsule"),
    LIQUID("Liquid"),
    INJECTION("Injection"),
    CREAM("Cream"),
    DROPS("Drops"),
    INHALER("Inhaler"),
    PATCH("Patch"),
    SUPPOSITORY("Suppository"),
    OTHER("Other")
}


/*
Represents a medicine schedule associate to a medicine
 */
data class MedicineSchedule(
    val id: String = UUID.randomUUID().toString(),
    val medicineId: String,
    val configuration: SchedulePattern.ScheduleConfiguration,
    val createdAt: ZonedDateTime,
)

data class ScheduledAlarm(
    val id: String = UUID.randomUUID().toString(),
    val scheduleId: String,
    val medicineId: String,

    val medicineName: String,
    val dosageAmount: Double,
    val dosageUnit: DosageUnit,

    val scheduledTime: ZonedDateTime,

    val status: AlarmStatus,

    val takenAt: ZonedDateTime? = null,
    val missedAt: ZonedDateTime? = null,
    val snoozedUntil: ZonedDateTime? = null,

    val alarmRequestCode: Int,
    val notificationShown: Boolean = false,

    val createdAt: ZonedDateTime,
) {
    fun isOverdue(
        now: ZonedDateTime,
        gracePeriodMinutes: Int = 30,
    ): Boolean {
        if (status != AlarmStatus.SCHEDULED) return false
        return now.isAfter(scheduledTime.plusMinutes(gracePeriodMinutes.toLong()))
    }

    fun isWithinFlexibilityWindow(
        now: ZonedDateTime,
        window: SchedulePattern.FlexibilityWindow,
    ): Boolean {
        val earliestTime = scheduledTime.minus(window.before)
        val latestTime = scheduledTime.plus(window.after)
        return now.isAfter(earliestTime) && now.isBefore(latestTime)
    }
}


/*
States of a scheduled alarm
 */
enum class AlarmStatus {
    SCHEDULED,
    TAKEN,
    MISSED,
    SNOOZED,
    CANCELLED
}


data class IntakeEvent(
    val id: String = UUID.randomUUID().toString(),
    val alarmId: String,
    val medicineId: String,
    val scheduledTime: ZonedDateTime,
    val actualTakenTime: ZonedDateTime,
    val delayMinutes: Long = calculateDelay(scheduledTime, actualTakenTime),
    val notes: String? = null,
    val createdAt: ZonedDateTime,
) {
    companion object {
        private fun calculateDelay(scheduled: ZonedDateTime, actual: ZonedDateTime): Long {
            return Duration.between(scheduled, actual).toMinutes()
        }
    }

    val wasTakenOnTime: Boolean = delayMinutes in -15..15
    val wasTakenEarly: Boolean = delayMinutes < -15
    val wasTakenLate: Boolean = delayMinutes > 15
}


/*
Report of medicine adherence for a medicine
 */
data class AdherenceReport(
    val medicineId: String,
    val medicineName: String,
    val periodStart: ZonedDateTime,
    val periodEnd: ZonedDateTime,
    val totalScheduled: Int,
    val totalTaken: Int,
    val totalMissed: Int,
    val totalOnTime: Int,
    val adherencePercentage: Double = if (totalScheduled > 0) {
        (totalTaken.toDouble() / totalScheduled) * 100
    } else 0.0,
) {
    val isGoodAdherence: Boolean = adherencePercentage >= 80.0
}