package com.emman.android.medialarmapp.domain.usecases.alarm

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

class ConfirmMedicationTakenUseCaseTest {

    private lateinit var useCase: ConfirmMedicationTakenUseCase
    private lateinit var mockRepository: ScheduleRepository

    @BeforeEach
    fun setup() {
        mockRepository = mockk()
        useCase = ConfirmMedicationTakenUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("Happy path")
    inner class HappyPath {

        @Test
        @DisplayName("Alarm found, marks as taken and saves intake event")
        fun testSuccessfulConfirmation() = runTest {
            // Given
            val alarm = createTestAlarm()
            val takenAt = ZonedDateTime.now()

            coEvery { mockRepository.getAlarmById(alarm.id) } returns alarm
            coEvery { mockRepository.markAlarmAsTaken(alarm.id, takenAt) } returns Result.success(
                Unit
            )
            coEvery { mockRepository.saveIntakeEvent(any()) } returns Result.success(1L)

            // When
            val result = useCase(alarm.id, takenAt)

            // Then
            assertThat(result.isSuccess).isTrue()

            // Both operations must be called
            coVerify(exactly = 1) { mockRepository.markAlarmAsTaken(alarm.id, takenAt) }
            coVerify(exactly = 1) { mockRepository.saveIntakeEvent(any()) }
        }

        @Test
        @DisplayName("IntakeEvent is created with correct alarmId and medicineId")
        fun testIntakeEventFields() = runTest {
            // Given
            val alarm = createTestAlarm(id = "alarm-42", medicineId = "medicine-99")
            val takenAt = ZonedDateTime.now()
            val eventSlot = slot<com.emman.android.medialarmapp.domain.models.IntakeEvent>()

            coEvery { mockRepository.getAlarmById(any()) } returns alarm
            coEvery { mockRepository.markAlarmAsTaken(any(), any()) } returns Result.success(Unit)
            coEvery { mockRepository.saveIntakeEvent(capture(eventSlot)) } returns Result.success(1L)

            // When
            useCase(alarm.id, takenAt)

            // Then
            val event = eventSlot.captured
            assertThat(event.alarmId).isEqualTo("alarm-42")
            assertThat(event.medicineId).isEqualTo("medicine-99")
            assertThat(event.actualTakenTime).isEqualTo(takenAt)
        }

        @Test
        @DisplayName("IntakeEvent scheduledTime matches alarm scheduledTime")
        fun testIntakeEventScheduledTime() = runTest {
            // Given
            val scheduledTime = ZonedDateTime.now().minusHours(1)
            val alarm = createTestAlarm(scheduledTime = scheduledTime)
            val takenAt = ZonedDateTime.now()
            val eventSlot = slot<com.emman.android.medialarmapp.domain.models.IntakeEvent>()

            coEvery { mockRepository.getAlarmById(any()) } returns alarm
            coEvery { mockRepository.markAlarmAsTaken(any(), any()) } returns Result.success(Unit)
            coEvery { mockRepository.saveIntakeEvent(capture(eventSlot)) } returns Result.success(1L)

            // When
            useCase(alarm.id, takenAt)

            // Then
            assertThat(eventSlot.captured.scheduledTime).isEqualTo(scheduledTime)
        }

        @Test
        @DisplayName("delayMinutes is positive when taken late")
        fun testDelayMinutesLate() = runTest {
            // Given
            val scheduledTime = ZonedDateTime.now().minusMinutes(45)
            val alarm = createTestAlarm(scheduledTime = scheduledTime)
            val takenAt = ZonedDateTime.now()
            val eventSlot = slot<com.emman.android.medialarmapp.domain.models.IntakeEvent>()

            coEvery { mockRepository.getAlarmById(any()) } returns alarm
            coEvery { mockRepository.markAlarmAsTaken(any(), any()) } returns Result.success(Unit)
            coEvery { mockRepository.saveIntakeEvent(capture(eventSlot)) } returns Result.success(1L)

            // When
            useCase(alarm.id, takenAt)

            // Then: taken 45min after schedule → delay = +45
            val delay = eventSlot.captured.delayMinutes
            assertThat(delay).isGreaterThan(0L)
            assertThat(delay).isAtLeast(44L)
            assertThat(delay).isAtMost(46L)
        }

        @Test
        @DisplayName("delayMinutes is negative when taken early")
        fun testDelayMinutesEarly() = runTest {
            // Given: scheduled 30min in future
            val scheduledTime = ZonedDateTime.now().plusMinutes(30)
            val alarm = createTestAlarm(scheduledTime = scheduledTime)
            val takenAt = ZonedDateTime.now()
            val eventSlot = slot<com.emman.android.medialarmapp.domain.models.IntakeEvent>()

            coEvery { mockRepository.getAlarmById(any()) } returns alarm
            coEvery { mockRepository.markAlarmAsTaken(any(), any()) } returns Result.success(Unit)
            coEvery { mockRepository.saveIntakeEvent(capture(eventSlot)) } returns Result.success(1L)

            // When
            useCase(alarm.id, takenAt)

            // Then: taken 30min before schedule → delay = -30
            val delay = eventSlot.captured.delayMinutes
            assertThat(delay).isLessThan(0L)
        }

        @Test
        @DisplayName("Notes are propagated to the IntakeEvent")
        fun testNotesPropagated() = runTest {
            // Given
            val alarm = createTestAlarm()
            val notes = "Tomé con mucha agua"
            val eventSlot = slot<com.emman.android.medialarmapp.domain.models.IntakeEvent>()

            coEvery { mockRepository.getAlarmById(any()) } returns alarm
            coEvery { mockRepository.markAlarmAsTaken(any(), any()) } returns Result.success(Unit)
            coEvery { mockRepository.saveIntakeEvent(capture(eventSlot)) } returns Result.success(1L)

            // When
            useCase(alarm.id, notes = notes)

            // Then
            assertThat(eventSlot.captured.notes).isEqualTo(notes)
        }

        @Test
        @DisplayName("Null notes are allowed")
        fun testNullNotes() = runTest {
            // Given
            val alarm = createTestAlarm()
            val eventSlot = slot<com.emman.android.medialarmapp.domain.models.IntakeEvent>()

            coEvery { mockRepository.getAlarmById(any()) } returns alarm
            coEvery { mockRepository.markAlarmAsTaken(any(), any()) } returns Result.success(Unit)
            coEvery { mockRepository.saveIntakeEvent(capture(eventSlot)) } returns Result.success(1L)

            // When
            useCase(alarm.id, notes = null)

            // Then
            assertThat(eventSlot.captured.notes).isNull()
        }

    }

    @Nested
    @DisplayName("Alarm not found")
    inner class AlarmNotFound {

        @Test
        @DisplayName("Returns failure when alarm does not exist")
        fun testAlarmNotFound() = runTest {
            // Given
            coEvery { mockRepository.getAlarmById(any()) } returns null

            // When
            val result = useCase("non-existent-id")

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        @DisplayName("Does NOT call markAlarmAsTaken when alarm is not found")
        fun testNoMarkCalledWhenNotFound() = runTest {
            // Given
            coEvery { mockRepository.getAlarmById(any()) } returns null

            // When
            useCase("ghost-alarm")

            // Then: repository never called for status update or event saving
            coVerify(exactly = 0) { mockRepository.markAlarmAsTaken(any(), any()) }
            coVerify(exactly = 0) { mockRepository.saveIntakeEvent(any()) }
        }
    }

    @Nested
    @DisplayName("Operation ordering and atomicity")
    inner class OperationOrdering {

        @Test
        @DisplayName("IntakeEvent is NOT saved when markAlarmAsTaken fails")
        fun testNoEventSavedOnMarkFailure() = runTest {
            // Given
            val alarm = createTestAlarm()
            coEvery { mockRepository.getAlarmById(any()) } returns alarm
            coEvery { mockRepository.markAlarmAsTaken(any(), any()) } returns
                    Result.failure(RuntimeException("DB write failed"))

            // When
            val result = useCase(alarm.id)

            // Then: must fail AND not attempt to save event
            assertThat(result.isFailure).isTrue()
            coVerify(exactly = 0) { mockRepository.saveIntakeEvent(any()) }
        }

        @Test
        @DisplayName("Returns failure when saveIntakeEvent fails")
        fun testFailureOnEventSaveError() = runTest {
            // Given
            val alarm = createTestAlarm()
            coEvery { mockRepository.getAlarmById(any()) } returns alarm
            coEvery { mockRepository.markAlarmAsTaken(any(), any()) } returns Result.success(Unit)
            coEvery { mockRepository.saveIntakeEvent(any()) } returns
                    Result.failure(RuntimeException("Disk full"))

            // When
            val result = useCase(alarm.id)

            // Then
            assertThat(result.isFailure).isTrue()
        }

        @Test
        @DisplayName("markAlarmAsTaken is called BEFORE saveIntakeEvent")
        fun testCallOrder() = runTest {
            // Given
            val alarm = createTestAlarm()
            val callOrder = mutableListOf<String>()

            coEvery { mockRepository.getAlarmById(any()) } returns alarm
            coEvery { mockRepository.markAlarmAsTaken(any(), any()) } coAnswers {
                callOrder.add("markTaken")
                Result.success(Unit)
            }
            coEvery { mockRepository.saveIntakeEvent(any()) } coAnswers {
                callOrder.add("saveEvent")
                Result.success(1L)
            }

            // When
            useCase(alarm.id)

            // Then
            assertThat(callOrder).containsExactly("markTaken", "saveEvent").inOrder()
        }
    }

    // ========== EXCEPTION HANDLING ==========

    @Nested
    @DisplayName("Exception handling")
    inner class ExceptionHandling {

        @Test
        @DisplayName("Unexpected exception from getAlarmById is wrapped in failure")
        fun testUnexpectedException() = runTest {
            // Given
            coEvery { mockRepository.getAlarmById(any()) } throws RuntimeException("Unexpected crash")

            // When
            val result = useCase("any-id")

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
        }
    }

    private fun createTestAlarm(
        id: String = "test-alarm-id",
        medicineId: String = "test-medicine-id",
        scheduledTime: ZonedDateTime = ZonedDateTime.now().minusMinutes(5),
        status: AlarmStatus = AlarmStatus.SCHEDULED,
    ): ScheduledAlarm {
        val now = ZonedDateTime.now()
        return ScheduledAlarm(
            id = id,
            scheduleId = "schedule-1",
            medicineId = medicineId,
            medicineName = "Ibuprofeno",
            dosageAmount = 400.0,
            dosageUnit = DosageUnit.MILLIGRAMS,
            scheduledTime = scheduledTime,
            status = status,
            alarmRequestCode = 1001,
            createdAt = now
        )
    }

}