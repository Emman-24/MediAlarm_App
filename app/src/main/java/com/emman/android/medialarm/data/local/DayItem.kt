package com.emman.android.medialarm.data.local

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DayItem(
    val date: LocalDate,
    val time: LocalTime,
    val isSelected: Boolean = false,
    val isToday: Boolean = date == LocalDate.now(ZoneId.systemDefault())
) {
    /*
    * Class representing a day item in the calendar.
    * Example : 8
     */
    val calendarDay: String
        get() = date.format(DateTimeFormatter.ofPattern("EE", Locale.getDefault()))


    /**
     * Getter for the calendar date string.
     * mon,tue,wed,thu,fri,sat,sun
     */

    val calendarDate: String
        get() = date.format(DateTimeFormatter.ofPattern("dd", Locale.getDefault()))


}
