package com.emman.android.medialarmapp.domain.usecases.schedule

import com.emman.android.medialarmapp.domain.calculator.ScheduleCalculator
import com.emman.android.medialarmapp.domain.models.AlarmStatus
import com.emman.android.medialarmapp.domain.models.DosageUnit
import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.models.MedicineForm
import com.emman.android.medialarmapp.domain.models.SchedulePattern
import com.emman.android.medialarmapp.domain.models.ScheduleTransactionResult
import com.emman.android.medialarmapp.domain.models.ScheduledAlarm
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime


@DisplayName("Schedule Medication Use Case Tests")
class ScheduleMedicationUseCaseTest {

    private lateinit var useCase: ScheduleMedicationUseCase
    private lateinit var mockRepository: ScheduleRepository
    private lateinit var mockCalculator: ScheduleCalculator

    @BeforeEach
    fun setup() {
        mockRepository = mockk()
        mockCalculator = mockk()
        useCase = ScheduleMedicationUseCase(mockRepository, mockCalculator)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    // ========== SUCCESS TESTS ==========

    @Test
    @DisplayName("Successfully schedules medication with alarms")
    fun testSuccessfulScheduling() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()

        val calculatedTimes = listOf(
            ZonedDateTime.now().plusHours(8),
            ZonedDateTime.now().plusHours(16),
            ZonedDateTime.now().plusHours(24)
        )

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns calculatedTimes

        coEvery {
            mockRepository.saveMedicineWithScheduleAndAlarms(any(), any(), any())
        } returns Result.success(
            ScheduleTransactionResult(
                medicineId = 1L,
                scheduleId = 1L,
                alarmIds = listOf(1L, 2L, 3L)
            )
        )

        // When
        val result = useCase(medicine, scheduleConfig, alarmsToGenerate = 3)

        // Then
        assertThat(result.isSuccess).isTrue()

        val scheduleResult = result.getOrNull()
        assertThat(scheduleResult).isNotNull()
        assertThat(scheduleResult?.alarmsCreated).isEqualTo(3)

        // Verify
        coVerify(exactly = 1) {
            mockRepository.saveMedicineWithScheduleAndAlarms(
                medicine = medicine,
                schedule = any(),
                alarms = match { it.size == 3 }
            )
        }
    }

    @Test
    @DisplayName("Generated alarms have unique request codes")
    fun testUniqueRequestCodes() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()
        val alarmsSlot = slot<List<ScheduledAlarm>>()

        val calculatedTimes = List(10) { i ->
            ZonedDateTime.now().plusHours(i.toLong() * 8)
        }

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns calculatedTimes

        coEvery {
            mockRepository.saveMedicineWithScheduleAndAlarms(any(), any(), capture(alarmsSlot))
        } returns Result.success(
            ScheduleTransactionResult(
                medicineId = 1L,
                scheduleId = 1L,
                alarmIds = List(10) { it.toLong() }
            )
        )

        // When
        useCase(medicine, scheduleConfig, alarmsToGenerate = 10)

        // Then
        assertThat(alarmsSlot.captured).hasSize(10)
        val requestCodes = alarmsSlot.captured.map { it.alarmRequestCode }
        assertThat(requestCodes).containsNoDuplicates()
    }

    @Test
    @DisplayName("Generated alarms have correct medicine info")
    fun testAlarmsHaveCorrectMedicineInfo() = runTest {
        // Given
        val medicine = createTestMedicine(name = "Ibuprofeno", dosage = 400.0)
        val scheduleConfig = createTestScheduleConfig()
        val alarmsSlot = slot<List<ScheduledAlarm>>()

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns listOf(
            ZonedDateTime.now().plusHours(8)
        )

        coEvery {
            mockRepository.saveMedicineWithScheduleAndAlarms(any(), any(), capture(alarmsSlot))
        } returns Result.success(
            ScheduleTransactionResult(medicineId = 1L, scheduleId = 1L, alarmIds = listOf(1L))
        )


        // When
        useCase(medicine, scheduleConfig, alarmsToGenerate = 1)

        // Then
        assertThat(alarmsSlot.captured).hasSize(1)
        val alarm = alarmsSlot.captured[0]

        assertThat(alarm.medicineName).isEqualTo("Ibuprofeno")
        assertThat(alarm.dosageAmount).isEqualTo(400.0)
        assertThat(alarm.dosageUnit).isEqualTo(DosageUnit.MILLIGRAMS)
        assertThat(alarm.status).isEqualTo(AlarmStatus.SCHEDULED)
    }

    // ========== ERROR HANDLING TESTS ==========

    @Test
    @DisplayName("Transaction failure returns failure result - no orphan data")
    fun testAtomicRollbackOnFailure() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns listOf(
            ZonedDateTime.now().plusHours(8)
        )

        val error = RuntimeException("Database constraint violation")
        coEvery {
            mockRepository.saveMedicineWithScheduleAndAlarms(any(), any(), any())
        } returns Result.failure(error)

        // When
        val result = useCase(medicine, scheduleConfig)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(error)

        // Only ONE atomic call - no individual saves that could create orphans
        coVerify(exactly = 1) { mockRepository.saveMedicineWithScheduleAndAlarms(any(), any(), any()) }
    }

    @Test
    @DisplayName("Calculator exception is propagated as failure")
    fun testCalculatorExceptionHandled() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } throws
                IllegalArgumentException("Invalid pattern")

        // When
        val result = useCase(medicine, scheduleConfig)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)

        // Repository never called because calculation failed first
        coVerify(exactly = 0) { mockRepository.saveMedicineWithScheduleAndAlarms(any(), any(), any()) }
    }


    @Test
    @DisplayName("Alarms are created with SCHEDULED status")
    fun testAlarmsCreatedWithScheduledStatus() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()
        val alarmsSlot = slot<List<ScheduledAlarm>>()

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns listOf(
            ZonedDateTime.now().plusHours(8),
            ZonedDateTime.now().plusHours(16)
        )

        coEvery {
            mockRepository.saveMedicineWithScheduleAndAlarms(any(), any(), capture(alarmsSlot))
        } returns Result.success(
            ScheduleTransactionResult(medicineId = 1L, scheduleId = 1L, alarmIds = listOf(1L, 2L))
        )

        // When
        useCase(medicine, scheduleConfig, alarmsToGenerate = 2)

        // Then
        alarmsSlot.captured.forEach { alarm ->
            assertThat(alarm.status).isEqualTo(AlarmStatus.SCHEDULED)
        }
    }

    @Test
    @DisplayName("Returns failure when alarms save fails")
    fun testAlarmsSaveFailure() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()

        coEvery { mockRepository.saveMedicine(any()) } returns Result.success(1L)
        coEvery { mockRepository.saveSchedule(any()) } returns Result.success(1L)

        val error = RuntimeException("Alarms save failed")
        coEvery { mockRepository.saveAlarms(any()) } returns Result.failure(error)

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns listOf(
            ZonedDateTime.now().plusHours(8)
        )

        // When
        val result = useCase(medicine, scheduleConfig)

        // Then
        assertThat(result.isFailure).isTrue()
    }

    // ========== CALCULATOR INTEGRATION TESTS ==========

    @Test
    @DisplayName("Passes correct parameters to calculator")
    fun testCalculatorParameters() = runTest {
        // Given
        val medicine = createTestMedicine()
        val pattern = SchedulePattern.Interval(8, LocalTime.of(10, 0))
        val scheduleConfig = SchedulePattern.ScheduleConfiguration(
            pattern = pattern,
            startDate = LocalDate.now(),
            endDate = null,
            isActive = true
        )

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns listOf(
            ZonedDateTime.now().plusHours(8)
        )


        coEvery {
            mockRepository.saveMedicineWithScheduleAndAlarms(any(), any(), any())
        } returns Result.success(
            ScheduleTransactionResult(medicineId = 1L, scheduleId = 1L, alarmIds = listOf(1L))
        )

        // When
        useCase(medicine, scheduleConfig, alarmsToGenerate = 50)

        // Then
        verify {
            mockCalculator.calculateNext(
                pattern = pattern,
                from = any(),
                count = 50,
                zoneId = any()
            )
        }
    }

    @Test
    @DisplayName("Handles zero alarms to generate")
    fun testZeroAlarmsToGenerate() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()

        every { mockCalculator.calculateNext(any(), any(), eq(0), any()) } returns emptyList()

        coEvery {
            mockRepository.saveMedicineWithScheduleAndAlarms(any(), any(), any())
        } returns Result.success(
            ScheduleTransactionResult(medicineId = 1L, scheduleId = 1L, alarmIds = emptyList())
        )

        // When
        val result = useCase(medicine, scheduleConfig, alarmsToGenerate = 0)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.alarmsCreated).isEqualTo(0)
    }


    @Test
    @DisplayName("Result contains correct IDs from repository")
    fun testResultContainsCorrectIds() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()
        val expectedMedicineId = 123L
        val expectedScheduleId = 456L

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns listOf(
            ZonedDateTime.now().plusHours(8)
        )

        coEvery {
            mockRepository.saveMedicineWithScheduleAndAlarms(any(), any(), any())
        } returns Result.success(
            ScheduleTransactionResult(
                medicineId = expectedMedicineId,
                scheduleId = expectedScheduleId,
                alarmIds = listOf(1L)
            )
        )

        // When
        val result = useCase(medicine, scheduleConfig, alarmsToGenerate = 1)

        // Then
        val scheduleResult = result.getOrThrow()
        assertThat(scheduleResult.medicineId).isEqualTo("123")
        assertThat(scheduleResult.scheduleId).isEqualTo("456")
        assertThat(scheduleResult.alarmsCreated).isEqualTo(1)
    }



    // ========== HELPER METHODS ==========

    private fun createTestMedicine(
        name: String = "Test Medicine",
        dosage: Double = 400.0
    ): Medicine {
        val now = ZonedDateTime.now()
        return Medicine(
            id = "1",
            name = name,
            dosageAmount = dosage,
            dosageUnit = DosageUnit.MILLIGRAMS,
            form = MedicineForm.TABLET,
            notes = null,
            isActive = true,
            createdAt = now,
            updatedAt = now
        )
    }

    private fun createTestScheduleConfig(): SchedulePattern.ScheduleConfiguration {
        return SchedulePattern.ScheduleConfiguration(
            pattern = SchedulePattern.Interval(8, LocalTime.of(10, 0)),
            startDate = LocalDate.now(),
            endDate = null,
            isActive = true
        )
    }
}