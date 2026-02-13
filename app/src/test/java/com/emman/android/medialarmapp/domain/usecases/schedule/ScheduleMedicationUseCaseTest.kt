package com.emman.android.medialarmapp.domain.usecases.schedule

import com.emman.android.medialarmapp.domain.calculator.ScheduleCalculator
import com.emman.android.medialarmapp.domain.models.AlarmStatus
import com.emman.android.medialarmapp.domain.models.DosageUnit
import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.models.MedicineForm
import com.emman.android.medialarmapp.domain.models.SchedulePattern
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

/**
 * Unit tests para ScheduleMedicationUseCase.
 *
 * IMPORTANTE: Estos tests usan MOCKS, no Room real.
 * Testean solo la lógica del use case.
 */
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

        // Mock repository responses
        coEvery { mockRepository.saveMedicine(any()) } returns Result.success(1L)
        coEvery { mockRepository.saveSchedule(any()) } returns Result.success(1L)
        coEvery { mockRepository.saveAlarms(any()) } returns Result.success(listOf(1L, 1L, 1L))

        // Mock calculator response
        every {
            mockCalculator.calculateNext(any(), any(), any(), any())
        } returns calculatedTimes

        // When
        val result = useCase(medicine, scheduleConfig, alarmsToGenerate = 3)

        // Then
        assertThat(result.isSuccess).isTrue()

        val scheduleResult = result.getOrNull()
        assertThat(scheduleResult).isNotNull()
        assertThat(scheduleResult?.alarmsCreated).isEqualTo(3)

        // Verify interactions
        coVerify(exactly = 1) { mockRepository.saveMedicine(medicine) }
        coVerify(exactly = 1) { mockRepository.saveSchedule(any()) }
        coVerify(exactly = 1) { mockRepository.saveAlarms(match { it.size == 3 }) }
        verify(exactly = 1) { mockCalculator.calculateNext(any(), any(), eq(3), any()) }
    }

    @Test
    @DisplayName("Generated alarms have unique request codes")
    fun testUniqueRequestCodes() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()

        val calculatedTimes = List(10) { i ->
            ZonedDateTime.now().plusHours(i.toLong() * 8)
        }

        coEvery { mockRepository.saveMedicine(any()) } returns Result.success(1L)
        coEvery { mockRepository.saveSchedule(any()) } returns Result.success(1L)

        var capturedAlarms: List<ScheduledAlarm>? = null
        coEvery { mockRepository.saveAlarms(capture(slot<List<ScheduledAlarm>>())) } answers {
            capturedAlarms = firstArg()
            Result.success(List(10) { 1L })
        }

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns calculatedTimes

        // When
        useCase(medicine, scheduleConfig, alarmsToGenerate = 10)

        // Then
        assertThat(capturedAlarms).isNotNull()
        val requestCodes = capturedAlarms!!.map { it.alarmRequestCode }

        // All request codes should be unique
        assertThat(requestCodes).containsNoDuplicates()
    }

    @Test
    @DisplayName("Generated alarms have correct medicine info")
    fun testAlarmsHaveCorrectMedicineInfo() = runTest {
        // Given
        val medicine = createTestMedicine(name = "Ibuprofeno", dosage = 400.0)
        val scheduleConfig = createTestScheduleConfig()

        coEvery { mockRepository.saveMedicine(any()) } returns Result.success(1L)
        coEvery { mockRepository.saveSchedule(any()) } returns Result.success(1L)

        var capturedAlarms: List<ScheduledAlarm>? = null
        coEvery { mockRepository.saveAlarms(capture(slot<List<ScheduledAlarm>>())) } answers {
            capturedAlarms = firstArg()
            Result.success(listOf(1L))
        }

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns listOf(
            ZonedDateTime.now().plusHours(8)
        )

        // When
        useCase(medicine, scheduleConfig, alarmsToGenerate = 1)

        // Then
        assertThat(capturedAlarms).hasSize(1)
        val alarm = capturedAlarms!![0]

        assertThat(alarm.medicineName).isEqualTo("Ibuprofeno")
        assertThat(alarm.dosageAmount).isEqualTo(400.0)
        assertThat(alarm.dosageUnit).isEqualTo(DosageUnit.MILLIGRAMS)
        assertThat(alarm.status).isEqualTo(AlarmStatus.SCHEDULED)
    }

    // ========== ERROR HANDLING TESTS ==========

    @Test
    @DisplayName("Returns failure when medicine save fails")
    fun testMedicineSaveFailure() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()

        val error = RuntimeException("Database error")
        coEvery { mockRepository.saveMedicine(any()) } returns Result.failure(error)

        // When
        val result = useCase(medicine, scheduleConfig)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(error)

        // Verify nothing else was called
        coVerify(exactly = 0) { mockRepository.saveSchedule(any()) }
        coVerify(exactly = 0) { mockRepository.saveAlarms(any()) }
    }

    @Test
    @DisplayName("Returns failure when schedule save fails")
    fun testScheduleSaveFailure() = runTest {
        // Given
        val medicine = createTestMedicine()
        val scheduleConfig = createTestScheduleConfig()

        coEvery { mockRepository.saveMedicine(any()) } returns Result.success(1L)

        val error = RuntimeException("Schedule save failed")
        coEvery { mockRepository.saveSchedule(any()) } returns Result.failure(error)

        // When
        val result = useCase(medicine, scheduleConfig)

        // Then
        assertThat(result.isFailure).isTrue()

        // Verify alarms were NOT saved
        coVerify(exactly = 0) { mockRepository.saveAlarms(any()) }
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

        coEvery { mockRepository.saveMedicine(any()) } returns Result.success(1L)
        coEvery { mockRepository.saveSchedule(any()) } returns Result.success(1L)
        coEvery { mockRepository.saveAlarms(any()) } returns Result.success(listOf(1L))

        every { mockCalculator.calculateNext(any(), any(), any(), any()) } returns listOf(
            ZonedDateTime.now().plusHours(8)
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