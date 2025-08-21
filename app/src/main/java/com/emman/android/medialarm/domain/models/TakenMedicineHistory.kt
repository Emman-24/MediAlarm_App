package com.emman.android.medialarm.domain.models

import com.emman.android.medialarm.data.local.entities.ScheduleType
import java.time.LocalDateTime

data class TakenMedicineHistory(
    val id: Long,
    val medicineId: Long,
    val scheduleId: Long,
    val scheduleType: ScheduleType,
    val scheduleDateTime: LocalDateTime,
    val actualDateTime: LocalDateTime?,
    val taken: Boolean,
    val amount : Int? = null,
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
){
    val isLate: Boolean
        get() = actualDateTime?.isAfter(scheduleDateTime.plusMinutes(30)) ?: false

    val isEarly: Boolean
        get() = actualDateTime?.isBefore(scheduleDateTime.minusMinutes(30)) ?: false
}
