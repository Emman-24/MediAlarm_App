package com.emman.android.medialarmapp.domain.calculator

import com.emman.android.medialarmapp.domain.models.SchedulePattern
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

/**
 * Interface for calculating schedules based on various patterns and constraints.
 * This is intended to provide functionality for generating future or ranged
 * occurrences of scheduled events given a specific schedule pattern.
 */

interface ScheduleCalculator {
    fun calculateNext(
        pattern: SchedulePattern,
        from: ZonedDateTime,
        count: Int,
        zoneId: ZoneId = from.zone
    ): List<ZonedDateTime>

    fun calculateInRange(
        pattern: SchedulePattern,
        startDate: LocalDate,
        endDate: LocalDate,
        zoneId: ZoneId
    ): List<ZonedDateTime>
}

/**
 * Default implementation of the `ScheduleCalculator` interface, providing mechanisms for
 * calculating upcoming schedule events or occurrences within a specific date range based on
 * various schedule patterns.
 *
 * The `DefaultScheduleCalculator` supports the following schedule patterns:
 * - `Interval`: Calculates occurrences based on repeated intervals of time.
 * - `TimesPerDay`: Calculates occurrences for specific times during the day.
 * - `SpecificDays`: Calculates occurrences on specific days of the week.
 * - `Cyclic`: Calculates occurrences based on an active/inactive cycle pattern.
 * - `AsNeeded`: No specific occurrences are returned for this pattern.
 */

class DefaultScheduleCalculator @Inject constructor() :ScheduleCalculator {

    override fun calculateNext(
        pattern: SchedulePattern,
        from: ZonedDateTime,
        count: Int,
        zoneId: ZoneId
    ): List<ZonedDateTime> {
        require(count > 0) { "Count must be positive, got: $count" }

        return when (pattern) {
            is SchedulePattern.Interval -> calculateIntervalNext(pattern, from, count, zoneId)
            is SchedulePattern.TimesPerDay -> calculateTimesPerDayNext(pattern, from, count, zoneId)
            is SchedulePattern.SpecificDays -> calculateSpecificDaysNext(pattern, from, count, zoneId)
            is SchedulePattern.Cyclic -> calculateCyclicNext(pattern, from, count, zoneId)
            is SchedulePattern.AsNeeded -> emptyList()
        }
    }

    override fun calculateInRange(
        pattern: SchedulePattern,
        startDate: LocalDate,
        endDate: LocalDate,
        zoneId: ZoneId
    ): List<ZonedDateTime> {
        require(!endDate.isBefore(startDate)) {
            "End date must be after or equal to start date"
        }

        val start = ZonedDateTime.of(startDate, LocalTime.MIN, zoneId)
        val end = ZonedDateTime.of(endDate, LocalTime.MAX, zoneId)

        val daysInRange = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
        val estimatedCount = daysInRange * 10

        return calculateNext(pattern, start, estimatedCount, zoneId)
            .filter { it.isBefore(end) || it.isEqual(end) }
    }

    // ========== Implementaciones por tipo de patrón ==========

    private fun calculateIntervalNext(
        pattern: SchedulePattern.Interval,
        from: ZonedDateTime,
        count: Int,
        zoneId: ZoneId
    ): List<ZonedDateTime> {
        val result = mutableListOf<ZonedDateTime>()
        var current = findNextIntervalOccurrence(pattern, from, zoneId)

        repeat(count) {
            result.add(current)


            current = if (pattern.intervalHours % 24 == 0) {

                val daysToAdd = pattern.intervalHours / 24
                val nextDate = current.toLocalDate().plusDays(daysToAdd.toLong())
                ZonedDateTime.of(nextDate, pattern.startTime, zoneId)

            } else {
                current.plusHours(pattern.intervalHours.toLong())
            }
        }

        return result
    }

    private fun findNextIntervalOccurrence(
        pattern: SchedulePattern.Interval,
        from: ZonedDateTime,
        zoneId: ZoneId
    ): ZonedDateTime {

        val today = from.toLocalDate()
        val todayAtStartTime = ZonedDateTime.of(today, pattern.startTime, zoneId)

        if (todayAtStartTime.isAfter(from)) {
            return todayAtStartTime
        }

        val hoursSinceStart = java.time.Duration.between(todayAtStartTime, from).toHours()
        val intervalsPassed = (hoursSinceStart / pattern.intervalHours) + 1

        return if (pattern.intervalHours % 24 == 0) {
            val daysToAdd = (intervalsPassed * pattern.intervalHours) / 24
            val nextDate = today.plusDays(daysToAdd)
            ZonedDateTime.of(nextDate, pattern.startTime, zoneId)
        } else {
            todayAtStartTime.plusHours(intervalsPassed * pattern.intervalHours)
        }
    }

    private fun calculateTimesPerDayNext(
        pattern: SchedulePattern.TimesPerDay,
        from: ZonedDateTime,
        count: Int,
        zoneId: ZoneId
    ): List<ZonedDateTime> {
        val result = mutableListOf<ZonedDateTime>()
        var currentDate = from.toLocalDate()

        val todayTimes = pattern.intakeTimes.map { time ->
            ZonedDateTime.of(currentDate, time, zoneId)
        }.filter { it.isAfter(from) }

        if (todayTimes.isNotEmpty()) {
            result.addAll(todayTimes)
            if (result.size >= count) {
                return result.take(count)
            }
            currentDate = currentDate.plusDays(1)
        } else {
            currentDate = currentDate.plusDays(1)
        }

        while (result.size < count) {
            pattern.intakeTimes.forEach { time ->
                if (result.size < count) {
                    result.add(ZonedDateTime.of(currentDate, time, zoneId))
                }
            }
            currentDate = currentDate.plusDays(1)
        }

        return result.take(count)
    }

    private fun calculateSpecificDaysNext(
        pattern: SchedulePattern.SpecificDays,
        from: ZonedDateTime,
        count: Int,
        zoneId: ZoneId
    ): List<ZonedDateTime> {
        val result = mutableListOf<ZonedDateTime>()
        var currentDate = from.toLocalDate()

        val todayAtTime = ZonedDateTime.of(currentDate, pattern.timeOfDay, zoneId)
        if (pattern.daysOfWeek.contains(currentDate.dayOfWeek) && from.isBefore(todayAtTime)) {
            result.add(todayAtTime)
        }

        var daysChecked = 0
        while (result.size < count && daysChecked < 365) { // Safety limit
            currentDate = currentDate.plusDays(1)
            daysChecked++

            if (pattern.daysOfWeek.contains(currentDate.dayOfWeek)) {
                result.add(ZonedDateTime.of(currentDate, pattern.timeOfDay, zoneId))
            }
        }

        return result.take(count)
    }

    private fun calculateCyclicNext(
        pattern: SchedulePattern.Cyclic,
        from: ZonedDateTime,
        count: Int,
        zoneId: ZoneId
    ): List<ZonedDateTime> {
        val result = mutableListOf<ZonedDateTime>()
        var currentDate = from.toLocalDate()

        val daysSinceCycleStart = java.time.temporal.ChronoUnit.DAYS
            .between(pattern.cycleStartDate, currentDate).toInt()
        val dayInCycle = daysSinceCycleStart % pattern.totalCycleDays

        val todayAtTime = ZonedDateTime.of(currentDate, pattern.timeOfDay, zoneId)
        if (dayInCycle < pattern.activeDays && from.isBefore(todayAtTime)) {
            result.add(todayAtTime)
        }

        var currentDayInCycle = dayInCycle
        var daysChecked = 0

        while (result.size < count && daysChecked < 365) {
            currentDate = currentDate.plusDays(1)
            currentDayInCycle = (currentDayInCycle + 1) % pattern.totalCycleDays
            daysChecked++

            if (currentDayInCycle < pattern.activeDays) {
                result.add(ZonedDateTime.of(currentDate, pattern.timeOfDay, zoneId))
            }
        }

        return result.take(count)
    }
}

/**
 * Helper para validar si una fecha cae en un ciclo activo.
 */
fun SchedulePattern.Cyclic.isActiveOn(date: LocalDate): Boolean {
    val daysSinceStart = java.time.temporal.ChronoUnit.DAYS
        .between(cycleStartDate, date).toInt()
    val dayInCycle = daysSinceStart % totalCycleDays
    return dayInCycle < activeDays
}