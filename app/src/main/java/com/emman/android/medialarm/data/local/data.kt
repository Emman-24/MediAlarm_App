package com.emman.android.medialarm.data.local

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Data @Inject constructor() {
    val listMedicines: List<Medicine> = listOf(
        Medicine(
            id = 1,
            name = "Medicine A",
            amount = "1",
            dosage = "500",
            unit = "mg",
            notes = "Take with water",
            pharmaceuticalForm = "Tablet",
            intakeAdvice = IntakeAdvice.WITH_MEAL,
            startDate = LocalDate.of(2025,3,10),
            endDate =  LocalDate.of(2025,3,10).plusDays(30),
            schedule = MedicineSchedule.MultipleTimesDaily(
                times = listOf(LocalTime.of(8, 0), LocalTime.of(14, 0), LocalTime.of(20, 0))
            )
        ),
        Medicine(
            id = 2,
            name = "Medicine B",
            amount = "2",
            dosage = "100",
            unit = "ml",
            notes = "Shake well before use",
            pharmaceuticalForm = "Syrup",
            intakeAdvice = IntakeAdvice.BEFORE_MEAL,
            startDate =  LocalDate.of(2025,3,10),
            endDate = null, // Ongoing
            schedule = MedicineSchedule.SpecificDaysOfWeek(
                times = listOf(LocalTime.of(9, 0)),
                daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
            )
        ),
        Medicine(
            id = 3,
            name = "Medicine C",
            amount = "1",
            dosage = "1",
            unit = "capsule",
            notes = "Take on an empty stomach",
            pharmaceuticalForm = "Capsule",
            intakeAdvice = IntakeAdvice.NONE,
            startDate =  LocalDate.of(2025,3,10),
            endDate =  LocalDate.of(2025,3,10).plusDays(40),
            schedule = MedicineSchedule.Cyclic(
                times = listOf(LocalTime.of(10, 0)),
                daysOn = 10,
                daysOff = 3
            )
        ),
        Medicine(
            id = 4,
            name = "Medicine D",
            amount = "1",
            dosage = "250",
            unit = "mg",
            notes = "Take with food",
            pharmaceuticalForm = "Tablet",
            intakeAdvice = IntakeAdvice.WITH_MEAL,
            startDate =  LocalDate.of(2025,3,10),
            endDate = null, // Ongoing
            schedule = MedicineSchedule.Interval(
                times = listOf(LocalTime.of(8, 0)),
                intervalUnit = IntervalUnit.HOURS,
                interval = 4 // Every 4 hours
            )
        ),
        Medicine(
            id = 5,
            name = "Medicine E",
            amount = "1",
            dosage = "1",
            unit = "ml",
            notes = "Take on an empty stomach",
            pharmaceuticalForm = "Syrup",
            intakeAdvice = IntakeAdvice.NONE,
            startDate =  LocalDate.of(2025,3,10),
            endDate =  LocalDate.of(2025,3,10).plusDays(14),
            schedule = MedicineSchedule.Interval(
                times = listOf(LocalTime.of(9, 0), LocalTime.of(21, 0)), // 9:00 AM and 9:00 PM
                intervalUnit = IntervalUnit.DAYS,
                interval = 2 // Every 2 days
            )
        )
    )

}
