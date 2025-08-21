package com.emman.android.medialarm.domain.models

import java.time.LocalTime
import java.util.UUID

data class MedicationTime(
    var time: LocalTime,
    var amount: Double,
    var id: UUID = UUID.randomUUID(),
)
