package com.emman.android.medialarmapp.domain.models

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

sealed interface SchedulePattern {

    data class Interval(
        val intervalHours: Int,
        val startTime: LocalTime,
    ) : SchedulePattern {
        init {
            require(intervalHours in 1..24) {
                "Interval must be between 1 and 24 hours, got: $intervalHours"
            }
        }
    }


    data class TimesPerDay(
        val timesPerDay: Int,
        val intakeTimes: List<LocalTime>,
    ) : SchedulePattern {
        init {
            require(timesPerDay in 1..10) {
                "Times per day must be between 1 and 10, got: $timesPerDay"
            }
            require(intakeTimes.size == timesPerDay) {
                "Number of intake times (${intakeTimes.size}) must match timesPerDay ($timesPerDay)"
            }
            require(intakeTimes == intakeTimes.sorted()) {
                "Intake times must be in chronological order"
            }
        }
    }


    data class SpecificDays(
        val daysOfWeek: Set<DayOfWeek>,
        val timeOfDay: LocalTime,
    ) : SchedulePattern {
        init {
            require(daysOfWeek.isNotEmpty()) {
                "Must specify at least one day of the week"
            }
        }
    }


    data class Cyclic(
        val activeDays: Int,
        val restDays: Int,
        val timeOfDay: LocalTime,
        val cycleStartDate: LocalDate,
    ) : SchedulePattern {
        init {
            require(activeDays >= 1) {
                "Active days must be at least 1, got: $activeDays"
            }
            require(restDays >= 0) {
                "Rest days must be 0 or more, got: $restDays"
            }
            require(activeDays + restDays <= 365) {
                "Total cycle length cannot exceed 365 days"
            }
        }

        val totalCycleDays: Int = activeDays + restDays
    }


    data class AsNeeded(
        val minimumHoursBetween: Int,
        val maxDosesPerDay: Int,
    ) : SchedulePattern {
        init {
            require(minimumHoursBetween in 1..24) {
                "Minimum hours between doses must be 1-24, got: $minimumHoursBetween"
            }
            require(maxDosesPerDay in 1..10) {
                "Max doses per day must be 1-10, got: $maxDosesPerDay"
            }
        }
    }


    /*
    Represent the flexibility window for a schedule pattern.
    Used for cases when its acceptable to have a schedule pattern that is slightly off from the user's desired schedule.
     */
    data class FlexibilityWindow(
        val before: Duration,
        val after: Duration,
    ) {
        companion object {
            val STRICT = FlexibilityWindow(Duration.ZERO, Duration.ZERO)
            val FLEXIBLE_15MIN = FlexibilityWindow(Duration.ofMinutes(15), Duration.ofMinutes(15))
            val FLEXIBLE_30MIN = FlexibilityWindow(Duration.ofMinutes(30), Duration.ofMinutes(30))
            val FLEXIBLE_1HOUR = FlexibilityWindow(Duration.ofHours(1), Duration.ofHours(1))
        }
    }


    /**
     * Configuration of "Do not disturb" to avoid alarming during sleep.
     */
    data class QuietHours(
        val startTime: LocalTime,
        val endTime: LocalTime,
    ) {
        companion object {
            val DEFAULT_SLEEP = QuietHours(
                startTime = LocalTime.of(22, 0),
                endTime = LocalTime.of(6, 0)
            )
        }

        fun isWithinQuietHours(time: LocalTime): Boolean {
            return if (endTime > startTime){
                time !in endTime..<startTime
            }else{
                time !in endTime..<startTime
            }
        }
    }

    data class ScheduleConfiguration(
        val pattern: SchedulePattern,
        val flexibility: FlexibilityWindow = FlexibilityWindow.STRICT,
        val quietHours: QuietHours? = null,
        val isActive: Boolean = true,
        val startDate: LocalDate,
        val endDate: LocalDate? = null
    ){
        init {
            endDate?.let { end ->
                require(!end.isBefore(startDate)) {
                    "End date ($end) cannot be before start date ($startDate)"
                }
            }
        }
    }


}