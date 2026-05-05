package com.emman.android.medialarmapp.domain.usecases.alarm

import com.emman.android.medialarmapp.domain.alarm.AlarmScheduler
import com.emman.android.medialarmapp.domain.models.AlarmStatus
import com.emman.android.medialarmapp.domain.models.DosageUnit
import com.emman.android.medialarmapp.domain.models.ScheduledAlarm
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

@DisplayName("SnoozeAlarm Use Case Tests")
class SnoozeAlarmUseCaseTest {

    private lateinit var useCase: SnoozeAlarmUseCase
    private lateinit var mockRepository: ScheduleRepository
    private lateinit var mockAlarmScheduler: AlarmScheduler

    @BeforeEach
    fun setup() {
        mockRepository = mockk()
        mockAlarmScheduler = mockk(relaxed = true)
        useCase = SnoozeAlarmUseCase(mockRepository, mockAlarmScheduler)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("Successful snooze")
    inner class SuccessfulSnooze {

        @Test
        @DisplayName("Returns snoozedUntil time on success")
        fun testSuccessfulSnooze() = runTest {
            // Given
            val alarm = createTestAlarm(status = AlarmStatus.SCHEDULED)
            val snoozeDuration = 10

            coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm
            coEvery { mockRepository.updateAlarm(any()) } returns Result.success(Unit)

            val beforeCall = ZonedDateTime.now()

            // When
            val result = useCase(alarm.id, snoozeDuration)

            // Then
            assertThat(result.isSuccess).isTrue()
            val snoozedUntil = result.getOrThrow()
            assertThat(snoozedUntil.isAfter(beforeCall.plusMinutes(snoozeDuration - 1L))).isTrue()
            assertThat(snoozedUntil.isBefore(beforeCall.plusMinutes(snoozeDuration + 1L))).isTrue()
        }

        @Test
        @DisplayName("Alarm status is set to SNOOZED before calling updateAlarm")
        fun testAlarmUpdatedWithSnoozedStatus() = runTest {
            // Given
            val alarm = createTestAlarm(status = AlarmStatus.SCHEDULED)
            val alarmSlot = slot<ScheduledAlarm>()

            coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm
            coEvery { mockRepository.updateAlarm(capture(alarmSlot)) } returns Result.success(Unit)

            // When
            useCase(alarm.id, 10)

            // Then
            assertThat(alarmSlot.captured.status).isEqualTo(AlarmStatus.SNOOZED)
            assertThat(alarmSlot.captured.snoozedUntil).isNotNull()
        }

        @Test
        @DisplayName("snoozedUntil in updated alarm matches returned value")
        fun testSnoozedUntilConsistency() = runTest {
            // Given
            val alarm = createTestAlarm()
            val alarmSlot = slot<ScheduledAlarm>()

            coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm
            coEvery { mockRepository.updateAlarm(capture(alarmSlot)) } returns Result.success(Unit)

            // When
            val result = useCase(alarm.id, 15)

            // Then: the returned time matches what was stored in the alarm
            val returnedTime = result.getOrThrow()
            val storedTime = alarmSlot.captured.snoozedUntil!!
            assertThat(returnedTime).isEqualTo(storedTime)
        }

        @Test
        @DisplayName("Default snooze duration is 10 minutes")
        fun testDefaultSnoozeDuration() = runTest {
            // Given
            val alarm = createTestAlarm()
            val alarmSlot = slot<ScheduledAlarm>()

            coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm
            coEvery { mockRepository.updateAlarm(capture(alarmSlot)) } returns Result.success(Unit)

            val beforeCall = ZonedDateTime.now()

            // When: no duration specified
            useCase(alarm.id)

            // Then: snoozedUntil should be ~10 minutes from now
            val snoozedUntil = alarmSlot.captured.snoozedUntil!!
            assertThat(snoozedUntil.isAfter(beforeCall.plusMinutes(9))).isTrue()
            assertThat(snoozedUntil.isBefore(beforeCall.plusMinutes(11))).isTrue()
        }

        @Test
        @DisplayName("Can snooze an already SNOOZED alarm (re-snooze)")
        fun testResnooze() = runTest {
            // Given: alarm that was already snoozed
            val alarm = createTestAlarm(status = AlarmStatus.SNOOZED)

            coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm
            coEvery { mockRepository.updateAlarm(any()) } returns Result.success(Unit)

            // When
            val result = useCase(alarm.id, 10)

            // Then: should succeed (re-snooze allowed)
            assertThat(result.isSuccess).isTrue()
        }

    }

    @Nested
    @DisplayName("Input validation")
    inner class Validation{

        @Test
        @DisplayName("Duration of 0 returns failure with IllegalArgumentException")
        fun testZeroDuration() = runTest {
            // When
            val result = useCase("any-id", snoozeDurationMinutes = 0)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)

            // No repository calls made
            coVerify(exactly = 0) { mockRepository.getAlarmById(any()) }
        }

        @Test
        @DisplayName("Negative duration returns failure")
        fun testNegativeDuration() = runTest {
            // When
            val result = useCase("any-id", snoozeDurationMinutes = -5)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        @DisplayName("Duration exceeding 60 minutes returns failure")
        fun testExceedsMaxDuration() = runTest {
            // When
            val result = useCase("any-id", snoozeDurationMinutes = 61)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)

            coVerify(exactly = 0) { mockRepository.getAlarmById(any()) }
        }

        @Test
        @DisplayName("Duration of exactly 60 minutes is valid")
        fun testMaxValidDuration() = runTest {
            // Given
            val alarm = createTestAlarm()
            coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm
            coEvery { mockRepository.updateAlarm(any()) } returns Result.success(Unit)

            // When
            val result = useCase(alarm.id, snoozeDurationMinutes = 60)

            // Then: exactly 60 should be allowed
            assertThat(result.isSuccess).isTrue()
        }

        @Test
        @DisplayName("Duration of 1 minute is valid")
        fun testMinValidDuration() = runTest {
            // Given
            val alarm = createTestAlarm()
            coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm
            coEvery { mockRepository.updateAlarm(any()) } returns Result.success(Unit)

            // When
            val result = useCase(alarm.id, snoozeDurationMinutes = 1)

            // Then
            assertThat(result.isSuccess).isTrue()
        }

    }

    @Nested
    @DisplayName("Forbidden alarm statuses")
    inner class ForbiddenStatuses{

        @Test
        @DisplayName("Cannot snooze a TAKEN alarm")
        fun testCannotSnoozeTakenAlarm() = runTest {
            // Given
            val alarm = createTestAlarm(status = AlarmStatus.TAKEN)
            coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm

            // When
            val result = useCase(alarm.id, 10)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
            coVerify(exactly = 0) { mockRepository.updateAlarm(any()) }
        }

        @Test
        @DisplayName("Cannot snooze a MISSED alarm")
        fun testCannotSnoozeMissedAlarm() = runTest {
            // Given
            val alarm = createTestAlarm(status = AlarmStatus.MISSED)
            coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm

            // When
            val result = useCase(alarm.id, 10)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
            coVerify(exactly = 0) { mockRepository.updateAlarm(any()) }
        }
    }

    @Test
    @DisplayName("Returns failure when alarm does not exist")
    fun testAlarmNotFound() = runTest {
        // Given
        coEvery { mockRepository.getAlarmById(any()) } returns null

        // When
        val result = useCase("non-existent", 10)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(NoSuchElementException::class.java)
        coVerify(exactly = 0) { mockRepository.updateAlarm(any()) }
    }

    @Test
    @DisplayName("Returns failure when updateAlarm fails")
    fun testUpdateAlarmFailure() = runTest {
        // Given
        val alarm = createTestAlarm()
        coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm
        coEvery { mockRepository.updateAlarm(any()) } returns
                Result.failure(RuntimeException("Write failed"))

        // When
        val result = useCase(alarm.id, 10)

        // Then
        assertThat(result.isFailure).isTrue()
    }

    private fun createTestAlarm(
        id: String = "test-alarm-id",
        status: AlarmStatus = AlarmStatus.SCHEDULED,
    ): ScheduledAlarm {
        val now = ZonedDateTime.now()
        return ScheduledAlarm(
            id = id,
            scheduleId = "schedule-1",
            medicineId = "medicine-1",
            medicineName = "Aspirina",
            dosageAmount = 500.0,
            dosageUnit = DosageUnit.MILLIGRAMS,
            scheduledTime = now.plusHours(1),
            status = status,
            alarmRequestCode = 2001,
            createdAt = now
        )
    }
}