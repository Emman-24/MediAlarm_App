package com.emman.android.medialarmapp.domain.calculator

import com.emman.android.medialarmapp.domain.models.SchedulePattern
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@DisplayName("Schedule Calculator Tests")
class ScheduleCalculatorTest {

    private lateinit var calculator: ScheduleCalculator
    private val colombiaZone = ZoneId.of("America/Bogota")

    @BeforeEach
    fun setup() {
        calculator = DefaultScheduleCalculator()
    }

    @Nested
    @DisplayName("Interval Pattern Tests")
    inner class IntervalTests {

        @Test
        @DisplayName("Every 8 hours starting at 10:00 AM generates correct sequence")
        fun testEvery8HoursFromMorning() {
            val pattern = SchedulePattern.Interval(
                intervalHours = 8,
                startTime = LocalTime.of(10, 0)
            )

            val from = ZonedDateTime.of(
                2026, 1, 15, 10, 0, 0, 0,
                colombiaZone
            )

            val result = calculator.calculateNext(pattern, from, 5, colombiaZone)

            assertEquals(5, result.size)

            assertEquals("2026-01-15T18:00", result[0].toLocalDateTime().toString())
            assertEquals("2026-01-16T02:00", result[1].toLocalDateTime().toString()) // Cruza medianoche
            assertEquals("2026-01-16T10:00", result[2].toLocalDateTime().toString())
            assertEquals("2026-01-16T18:00", result[3].toLocalDateTime().toString())
            assertEquals("2026-01-17T02:00", result[4].toLocalDateTime().toString())
        }

        @Test
        @DisplayName("Every 6 hours starting mid-day calculates correctly")
        fun testEvery6HoursFromAfternoon() {
            val pattern = SchedulePattern.Interval(
                intervalHours = 6,
                startTime = LocalTime.of(14, 0)
            )

            // Empezamos a las 3 PM (una hora después del startTime)
            val from = ZonedDateTime.of(
                2026, 1, 15, 15, 0, 0, 0,
                colombiaZone
            )

            val result = calculator.calculateNext(pattern, from, 4, colombiaZone)

            // Próxima debe ser 20:00 (14:00 + 6h)
            assertEquals("2026-01-15T20:00", result[0].toLocalDateTime().toString())
            assertEquals("2026-01-16T02:00", result[1].toLocalDateTime().toString())
            assertEquals("2026-01-16T08:00", result[2].toLocalDateTime().toString())
            assertEquals("2026-01-16T14:00", result[3].toLocalDateTime().toString())
        }

        @Test
        @DisplayName("First alarm should be interval duration after configuration time")
        fun testFirstAlarmIsNotImmediate() {
            val pattern = SchedulePattern.Interval(
                intervalHours = 8,
                startTime = LocalTime.of(10, 0)
            )

            // Configuramos exactamente a las 10:00 AM
            val configTime = ZonedDateTime.of(
                2026, 1, 15, 10, 0, 0, 0,
                colombiaZone
            )

            val result = calculator.calculateNext(pattern, configTime, 1, colombiaZone)

            // Primera alarma debe ser +8h, NO inmediatamente
            val firstAlarm = result[0]
            val expectedFirstAlarm = configTime.plusHours(8)

            assertEquals(expectedFirstAlarm, firstAlarm)
            assertTrue(firstAlarm.isAfter(configTime),
                "First alarm should be AFTER configuration time, not at the same time")
        }

        @Test
        @DisplayName("Every 4 hours generates correct distribution across days")
        fun testEvery4HoursFullDay() {
            val pattern = SchedulePattern.Interval(
                intervalHours = 4,
                startTime = LocalTime.of(8, 0)
            )

            // Usuario configura a las 8:00 AM del día 15
            val from = ZonedDateTime.of(
                2026, 1, 15, 8, 0, 0, 0,
                colombiaZone
            )

            val result = calculator.calculateNext(pattern, from, 12, colombiaZone)

            // Día 15: Primera alarma es +4h = 12:00, luego 16:00, 20:00
            // Total día 15: 3 alarmas (12:00, 16:00, 20:00)
            val day1 = result.filter { it.toLocalDate() == LocalDate.of(2026, 1, 15) }
            assertEquals(3, day1.size, "Day 1 should have 3 alarms")

            // Día 16: 00:00, 04:00, 08:00, 12:00, 16:00, 20:00
            // Total día 16: 6 alarmas
            val day2 = result.filter { it.toLocalDate() == LocalDate.of(2026, 1, 16) }
            assertEquals(6, day2.size, "Day 2 should have 6 alarms")

            // Verificar tiempos específicos del día 1
            assertEquals("2026-01-15T12:00", day1[0].toLocalDateTime().toString())
            assertEquals("2026-01-15T16:00", day1[1].toLocalDateTime().toString())
            assertEquals("2026-01-15T20:00", day1[2].toLocalDateTime().toString())
        }

        @Test
        @DisplayName("Interval must be between 1 and 24 hours")
        fun testIntervalValidation() {
            // Test 0 hours
            try {
                SchedulePattern.Interval(0, LocalTime.of(10, 0))
                fail("Should have thrown IllegalArgumentException for 0 hours")
            } catch (e: IllegalArgumentException) {
                assertTrue(e.message?.contains("must be between 1 and 24") == true)
            }

            // Test 25 hours
            try {
                SchedulePattern.Interval(25, LocalTime.of(10, 0))
                fail("Should have thrown IllegalArgumentException for 25 hours")
            } catch (e: IllegalArgumentException) {
                assertTrue(e.message?.contains("must be between 1 and 24") == true)
            }

            // Test negative
            try {
                SchedulePattern.Interval(-1, LocalTime.of(10, 0))
                fail("Should have thrown IllegalArgumentException for -1 hours")
            } catch (e: IllegalArgumentException) {
                assertTrue(e.message?.contains("must be between 1 and 24") == true)
            }
        }
    }

    // ========== TIMES PER DAY TESTS ==========

    @Nested
    @DisplayName("Times Per Day Pattern Tests")
    inner class TimesPerDayTests {

        @Test
        @DisplayName("3 times daily at specific hours generates correctly")
        fun testThreeTimesDaily() {
            val pattern = SchedulePattern.TimesPerDay(
                timesPerDay = 3,
                intakeTimes = listOf(
                    LocalTime.of(8, 0),
                    LocalTime.of(14, 0),
                    LocalTime.of(20, 0)
                )
            )

            val from = ZonedDateTime.of(
                2026, 1, 15, 7, 0, 0, 0,
                colombiaZone
            )

            val result = calculator.calculateNext(pattern, from, 9, colombiaZone)

            // Día 1: 8:00, 14:00, 20:00
            assertEquals("2026-01-15T08:00", result[0].toLocalDateTime().toString())
            assertEquals("2026-01-15T14:00", result[1].toLocalDateTime().toString())
            assertEquals("2026-01-15T20:00", result[2].toLocalDateTime().toString())

            // Día 2
            assertEquals("2026-01-16T08:00", result[3].toLocalDateTime().toString())
            assertEquals("2026-01-16T14:00", result[4].toLocalDateTime().toString())
            assertEquals("2026-01-16T20:00", result[5].toLocalDateTime().toString())

            // Día 3
            assertEquals("2026-01-17T08:00", result[6].toLocalDateTime().toString())
            assertEquals("2026-01-17T14:00", result[7].toLocalDateTime().toString())
            assertEquals("2026-01-17T20:00", result[8].toLocalDateTime().toString())
        }

        @Test
        @DisplayName("Skip past times on current day")
        fun testSkipPastTimesToday() {
            val pattern = SchedulePattern.TimesPerDay(
                timesPerDay = 3,
                intakeTimes = listOf(
                    LocalTime.of(8, 0),
                    LocalTime.of(14, 0),
                    LocalTime.of(20, 0)
                )
            )

            // Empezamos a las 15:00 (después de 8:00 y 14:00)
            val from = ZonedDateTime.of(
                2026, 1, 15, 15, 0, 0, 0,
                colombiaZone
            )

            val result = calculator.calculateNext(pattern, from, 4, colombiaZone)

            // Primera debe ser 20:00 hoy
            assertEquals("2026-01-15T20:00", result[0].toLocalDateTime().toString())
            // Luego las 3 del día siguiente
            assertEquals("2026-01-16T08:00", result[1].toLocalDateTime().toString())
            assertEquals("2026-01-16T14:00", result[2].toLocalDateTime().toString())
            assertEquals("2026-01-16T20:00", result[3].toLocalDateTime().toString())
        }

        @Test
        @DisplayName("Intake times must match count")
        fun testIntakeTimesCountValidation() {
            assertThrows<IllegalArgumentException> {
                SchedulePattern.TimesPerDay(
                    timesPerDay = 3,
                    intakeTimes = listOf(LocalTime.of(8, 0)) // Solo 1, necesita 3
                )
            }
        }

        @Test
        @DisplayName("Intake times must be in chronological order")
        fun testIntakeTimesOrderValidation() {
            assertThrows<IllegalArgumentException> {
                SchedulePattern.TimesPerDay(
                    timesPerDay = 2,
                    intakeTimes = listOf(
                        LocalTime.of(14, 0),
                        LocalTime.of(8, 0) // Desordenado
                    )
                )
            }
        }
    }

    // ========== SPECIFIC DAYS TESTS ==========

    @Nested
    @DisplayName("Specific Days Pattern Tests")
    inner class SpecificDaysTests {

        @Test
        @DisplayName("Monday-Wednesday-Friday pattern generates correctly")
        fun testMondayWednesdayFriday() {
            val pattern = SchedulePattern.SpecificDays(
                daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                timeOfDay = LocalTime.of(9, 0)
            )

            // Empezar un lunes
            val from = ZonedDateTime.of(
                2026, 1, 12, 8, 0, 0, 0, // Lunes 12 de enero
                colombiaZone
            )

            val result = calculator.calculateNext(pattern, from, 6, colombiaZone)

            // Lunes 12
            assertEquals(DayOfWeek.MONDAY, result[0].dayOfWeek)
            assertEquals("2026-01-12T09:00", result[0].toLocalDateTime().toString())

            // Miércoles 14
            assertEquals(DayOfWeek.WEDNESDAY, result[1].dayOfWeek)
            assertEquals("2026-01-14T09:00", result[1].toLocalDateTime().toString())

            // Viernes 16
            assertEquals(DayOfWeek.FRIDAY, result[2].dayOfWeek)
            assertEquals("2026-01-16T09:00", result[2].toLocalDateTime().toString())

            // Lunes 19
            assertEquals(DayOfWeek.MONDAY, result[3].dayOfWeek)
            assertEquals("2026-01-19T09:00", result[3].toLocalDateTime().toString())
        }

        @Test
        @DisplayName("Weekend-only pattern works")
        fun testWeekendsOnly() {
            val pattern = SchedulePattern.SpecificDays(
                daysOfWeek = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
                timeOfDay = LocalTime.of(10, 0)
            )

            // Empezar un viernes
            val from = ZonedDateTime.of(
                2026, 1, 16, 15, 0, 0, 0, // Viernes 16
                colombiaZone
            )

            val result = calculator.calculateNext(pattern, from, 4, colombiaZone)

            // Sábado 17
            assertEquals(DayOfWeek.SATURDAY, result[0].dayOfWeek)
            // Domingo 18
            assertEquals(DayOfWeek.SUNDAY, result[1].dayOfWeek)
            // Sábado 24
            assertEquals(DayOfWeek.SATURDAY, result[2].dayOfWeek)
            // Domingo 25
            assertEquals(DayOfWeek.SUNDAY, result[3].dayOfWeek)
        }

        @Test
        @DisplayName("Must specify at least one day")
        fun testAtLeastOneDayRequired() {
            assertThrows<IllegalArgumentException> {
                SchedulePattern.SpecificDays(
                    daysOfWeek = emptySet(),
                    timeOfDay = LocalTime.of(9, 0)
                )
            }
        }
    }

    // ========== CYCLIC TESTS ==========

    @Nested
    @DisplayName("Cyclic Pattern Tests")
    inner class CyclicTests {

        @Test
        @DisplayName("21 days on, 7 days off (birth control) calculates correctly")
        fun testBirthControlPattern() {
            val cycleStart = LocalDate.of(2026, 1, 1)

            val pattern = SchedulePattern.Cyclic(
                activeDays = 21,
                restDays = 7,
                timeOfDay = LocalTime.of(20, 0),
                cycleStartDate = cycleStart
            )

            // Empezar en día 1 del ciclo
            val from = ZonedDateTime.of(
                2026, 1, 1, 19, 0, 0, 0,
                colombiaZone
            )

            val result = calculator.calculateNext(pattern, from, 30, colombiaZone)

            // Primeros 21 días deben ser consecutivos
            for (i in 0 until 21) {
                assertEquals(cycleStart.plusDays(i.toLong()), result[i].toLocalDate())
            }

            // Día 22-28 deben ser salteados (rest days)
            // Día 29 debe ser el inicio del nuevo ciclo
            assertEquals(cycleStart.plusDays(28), result[21].toLocalDate())
        }

        @Test
        @DisplayName("Cyclic pattern handles mid-cycle start correctly")
        fun testMidCycleStart() {
            val cycleStart = LocalDate.of(2026, 1, 1)

            val pattern = SchedulePattern.Cyclic(
                activeDays = 5,
                restDays = 2,
                timeOfDay = LocalTime.of(8, 0),
                cycleStartDate = cycleStart
            )

            // Empezar en día 10 (segundo ciclo, día 3)
            // Ciclo 1: días 1-5 (activo), 6-7 (descanso)
            // Ciclo 2: días 8-12 (activo), 13-14 (descanso)
            val from = ZonedDateTime.of(
                2026, 1, 10, 7, 0, 0, 0, // Día 10 = día 3 del ciclo 2
                colombiaZone
            )

            val result = calculator.calculateNext(pattern, from, 7, colombiaZone)

            // Día 10, 11, 12 (fin ciclo 2 activo)
            assertEquals(LocalDate.of(2026, 1, 10), result[0].toLocalDate())
            assertEquals(LocalDate.of(2026, 1, 11), result[1].toLocalDate())
            assertEquals(LocalDate.of(2026, 1, 12), result[2].toLocalDate())

            // Días 13-14 salteados (descanso)
            // Ciclo 3 empieza día 15
            assertEquals(LocalDate.of(2026, 1, 15), result[3].toLocalDate())
        }

        @Test
        @DisplayName("Cyclic validation: active days >= 1")
        fun testActiveDaysValidation() {
            assertThrows<IllegalArgumentException> {
                SchedulePattern.Cyclic(
                    activeDays = 0,
                    restDays = 7,
                    timeOfDay = LocalTime.of(8, 0),
                    cycleStartDate = LocalDate.now()
                )
            }
        }

        @Test
        @DisplayName("Cyclic validation: total cycle <= 365 days")
        fun testTotalCycleDaysValidation() {
            assertThrows<IllegalArgumentException> {
                SchedulePattern.Cyclic(
                    activeDays = 300,
                    restDays = 70,
                    timeOfDay = LocalTime.of(8, 0),
                    cycleStartDate = LocalDate.now()
                )
            }
        }
    }

    // ========== EDGE CASES & TIMEZONE TESTS ==========

    @Nested
    @DisplayName("Edge Cases and Timezone Tests")
    inner class EdgeCasesTests {

        @Test
        @DisplayName("DEBUG: Check findNextIntervalOccurrence")
        fun debugFindNextInterval() {
            val nyZone = ZoneId.of("America/New_York")
            val calculator = DefaultScheduleCalculator()

            val pattern = SchedulePattern.Interval(
                intervalHours = 24,
                startTime = LocalTime.of(10, 0)
            )

            val from = ZonedDateTime.of(2026, 3, 7, 10, 0, 0, 0, nyZone)

            println("=== Testing calculateNext ===")
            println("From: $from")
            println("Pattern: Every ${pattern.intervalHours}h starting at ${pattern.startTime}")

            val result = calculator.calculateNext(pattern, from, 3, nyZone)

            result.forEachIndexed { i, dt ->
                println("Result[$i]: $dt (hour=${dt.hour}, offset=${dt.offset})")
            }

            // Manual test of what SHOULD happen
            println("\n=== Manual Construction ===")
            val march8 = LocalDate.of(2026, 3, 8)
            val manual1 = march8.atTime(10, 0).atZone(nyZone)
            println("March 8 at 10:00: $manual1 (hour=${manual1.hour})")

            val march8Alt = ZonedDateTime.of(march8, LocalTime.of(10, 0), nyZone)
            println("March 8 using ZonedDateTime.of: $march8Alt (hour=${march8Alt.hour})")
        }

        @Test
        @DisplayName("DEBUG: Understand DST behavior")
        fun debugDSTBehavior() {
            val nyZone = ZoneId.of("America/New_York")

            // March 8, 2026: 2:00 AM → 3:00 AM (spring forward)
            val before = ZonedDateTime.of(2026, 3, 7, 10, 0, 0, 0, nyZone)
            val after = ZonedDateTime.of(2026, 3, 8, 10, 0, 0, 0, nyZone)

            println("=== SPRING FORWARD DEBUG ===")
            println("Before DST: $before")
            println("Before offset: ${before.offset}")
            println("After DST:  $after")
            println("After offset: ${after.offset}")
            println("Hour (before): ${before.hour}")
            println("Hour (after):  ${after.hour}")

            // Test plusHours
            val plus24h = before.plusHours(24)
            println("\nUsing plusHours(24): $plus24h")
            println("Hour after plusHours: ${plus24h.hour}")

            // Test plusDays
            val plus1day = before.toLocalDate().plusDays(1)
            val atTime = ZonedDateTime.of(plus1day, LocalTime.of(10, 0), nyZone)
            println("\nUsing plusDays + atTime: $atTime")
            println("Hour after plusDays: ${atTime.hour}")

            println("\n=== FALL BACK DEBUG ===")
            // November 1, 2026: 2:00 AM → 1:00 AM (fall back)
            val beforeFall = ZonedDateTime.of(2026, 10, 31, 10, 0, 0, 0, nyZone)
            val afterFall = ZonedDateTime.of(2026, 11, 1, 10, 0, 0, 0, nyZone)

            println("Before DST: $beforeFall")
            println("Before offset: ${beforeFall.offset}")
            println("After DST:  $afterFall")
            println("After offset: ${afterFall.offset}")

            val plus24hFall = beforeFall.plusHours(24)
            println("\nUsing plusHours(24): $plus24hFall")
            println("Hour: ${plus24hFall.hour}")

            val plus1dayFall = beforeFall.toLocalDate().plusDays(1)
            val atTimeFall = ZonedDateTime.of(plus1dayFall, LocalTime.of(10, 0), nyZone)
            println("\nUsing plusDays + atTime: $atTimeFall")
            println("Hour: ${atTimeFall.hour}")
        }

        @Test
        @DisplayName("Calculate across daylight saving time change")
        fun testDaylightSavingTime() {
            // En Colombia no hay DST, usar zona que sí tenga
            val nyZone = ZoneId.of("America/New_York")

            val pattern = SchedulePattern.Interval(
                intervalHours = 24,
                startTime = LocalTime.of(10, 0)
            )

            // Marzo 2026: DST empieza segundo domingo (8 de marzo)
            val from = ZonedDateTime.of(
                2026, 3, 7, 10, 0, 0, 0,
                nyZone
            )

            val result = calculator.calculateNext(pattern, from, 3, nyZone)


            println("=== testDaylightSavingTime Results ===")
            result.forEachIndexed { i, dt ->
                println("[$i] $dt - Hour: ${dt.hour} - Offset: ${dt.offset}")
            }

            // Todas deben ser a las 10:00 AM local
            result.forEach { dateTime ->
                assertEquals(10, dateTime.hour)
            }
        }

        @Test
        @DisplayName("Calculate across DST fall back (autumn)")
        fun testDaylightSavingTimeFallBack() {
            val nyZone = ZoneId.of("America/New_York")

            val pattern = SchedulePattern.Interval(
                intervalHours = 24,
                startTime = LocalTime.of(10, 0)
            )

            // Noviembre 2026: DST termina primer domingo (1 de noviembre)
            // Reloj retrocede de 2:00 AM a 1:00 AM
            val from = ZonedDateTime.of(
                2026, 10, 31, 10, 0, 0, 0,
                nyZone
            )

            val result = calculator.calculateNext(pattern, from, 3, nyZone)

            result.forEach { dateTime ->
                assertEquals(10, dateTime.hour,
                    "Expected 10 AM but got ${dateTime.hour} AM on ${dateTime.toLocalDate()}")
            }
        }

        @Test
        @DisplayName("8-hour intervals cross DST correctly (absolute time)")
        fun testShortIntervalCrossesDST() {
            val nyZone = ZoneId.of("America/New_York")

            val pattern = SchedulePattern.Interval(
                intervalHours = 8,
                startTime = LocalTime.of(22, 0) // 10 PM
            )

            // Start before DST spring forward
            val from = ZonedDateTime.of(
                2026, 3, 7, 22, 0, 0, 0,
                nyZone
            )

            val result = calculator.calculateNext(pattern, from, 4, nyZone)

            // 8-hour intervals should be ABSOLUTE time, not local time
            // So hours will shift during DST
            assertEquals("2026-03-08T07:00", result[0].toLocalDateTime().toString()) // 7 AM (not 6 AM due to DST)
            // Next +8h crosses DST (2 AM → 3 AM skip)
            assertEquals("2026-03-08T15:00", result[1].toLocalDateTime().toString()) // 3 PM (not 2 PM)

            // Verify they are exactly 8 hours apart in absolute time
            val duration1 = java.time.Duration.between(from, result[0])
            assertEquals(8, duration1.toHours())

            val duration2 = java.time.Duration.between(result[0], result[1])
            assertEquals(8, duration2.toHours())
        }

        @Test
        @DisplayName("calculateInRange respects date boundaries")
        fun testCalculateInRangeRespectsBoundaries() {
            val pattern = SchedulePattern.TimesPerDay(
                timesPerDay = 2,
                intakeTimes = listOf(LocalTime.of(9, 0), LocalTime.of(21, 0))
            )

            val startDate = LocalDate.of(2026, 1, 15)
            val endDate = LocalDate.of(2026, 1, 17) // 3 días

            val result = calculator.calculateInRange(
                pattern, startDate, endDate, colombiaZone
            )

            assertEquals(6, result.size)

            result.forEach { dateTime ->
                assertTrue(
                    !dateTime.toLocalDate().isBefore(startDate) &&
                            !dateTime.toLocalDate().isAfter(endDate)
                )
            }
        }

        @Test
        @DisplayName("Count must be positive")
        fun testCountValidation() {
            val pattern = SchedulePattern.Interval(8, LocalTime.of(10, 0))
            val from = ZonedDateTime.now(colombiaZone)

            assertThrows<IllegalArgumentException> {
                calculator.calculateNext(pattern, from, 0, colombiaZone)
            }

            assertThrows<IllegalArgumentException> {
                calculator.calculateNext(pattern, from, -1, colombiaZone)
            }
        }

        @Test
        @DisplayName("AsNeeded pattern returns empty list")
        fun testAsNeededReturnsEmpty() {
            val pattern = SchedulePattern.AsNeeded(
                minimumHoursBetween = 6,
                maxDosesPerDay = 4
            )

            val from = ZonedDateTime.now(colombiaZone)
            val result = calculator.calculateNext(pattern, from, 10, colombiaZone)

            assertTrue(result.isEmpty())
        }
    }
}