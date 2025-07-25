package com.emman.android.medialarm.domain.models

import java.time.LocalDateTime
import java.time.LocalTime

data class MedicationTime(
    val time: LocalTime,
    val amount: Double,
    val taken: Boolean = false,
    val timeTaken : LocalDateTime? = null
){
    val isLate : Boolean
        get() = timeTaken?.isAfter(LocalDateTime.now().plusMinutes(30)) ?: false

    val isEarly : Boolean
        get() = timeTaken?.isBefore(LocalDateTime.now().minusMinutes(30)) ?: false
}
