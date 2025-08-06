package com.emman.android.medialarm.domain.models

import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class MedicationTime(
    var time: LocalTime,
    var amount: Double,
    val taken: Boolean = false,
    val timeTaken: LocalDateTime? = null,
    var id: UUID = UUID.randomUUID(),
) {
    val isLate: Boolean
        get() = timeTaken?.isAfter(LocalDateTime.now().plusMinutes(30)) ?: false

    val isEarly: Boolean
        get() = timeTaken?.isBefore(LocalDateTime.now().minusMinutes(30)) ?: false
}
