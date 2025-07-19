package com.emman.android.medialarm.domain.models

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    var isSelected: Boolean = false,
)
