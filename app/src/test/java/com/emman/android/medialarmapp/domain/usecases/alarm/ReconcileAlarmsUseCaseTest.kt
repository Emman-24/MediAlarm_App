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
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

@DisplayName("ReconcileAlarms Use Case Tests")
class ReconcileAlarmsUseCaseTest {

    private lateinit var useCase: ReconcileAlarmsUseCase
    private lateinit var mockRepository: ScheduleRepository

    private val gracePeriodMinutes = 30


    @BeforeEach
    fun setup() {
        mockRepository = mockk()
        useCase = ReconcileAlarmsUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("Empty alarm list")
    inner class EmptyState {

        @Test
        @DisplayName("Returns success with zeroed report when no alarms exist")
        fun testNoAlarms() = runTest {
            // Given
            coEvery { mockRepository.getAllScheduledAlarms() } returns emptyList()

            // When
            val result = useCase()

            // Then
            assertThat(result.isSuccess).isTrue()
            val report = result.getOrThrow()
            assertThat(report.totalAlarmsChecked).isEqualTo(0)
            assertThat(report.alarmsMissed).isEqualTo(0)
            assertThat(report.alarmsToReschedule).isEqualTo(0)
            assertThat(report.needsAttention).isFalse()
        }
    }

    @Nested
    @DisplayName("Alarms within grace period")
    inner class WithinGracePeriod {

        @Test
        @DisplayName("Upcoming alarms are not marked missed")
        fun testUpcomingAlarmsNotMissed() = runTest {
            // Given: alarms scheduled in the future
            val alarms = listOf(
                createTestAlarm("future-1", scheduledTime = ZonedDateTime.now().plusHours(1)),
                createTestAlarm("future-2", scheduledTime = ZonedDateTime.now().plusHours(2)),
            )
            coEvery { mockRepository.getAllScheduledAlarms() } returns alarms

            // When
            val result = useCase()

            // Then
            assertThat(result.isSuccess).isTrue()
            val report = result.getOrThrow()
            assertThat(report.totalAlarmsChecked).isEqualTo(2)
            assertThat(report.alarmsMissed).isEqualTo(0)
            assertThat(report.alarmsToReschedule).isEqualTo(2)
            assertThat(report.needsAttention).isFalse()

            coVerify(exactly = 0) { mockRepository.markAlarmAsMissed(any(), any()) }
        }

        @Test
        @DisplayName("Alarm just within grace period is not marked missed")
        fun testAlarmJustWithinGracePeriod() = runTest {
            // Given: scheduled 29 minutes ago (within 30min grace period)
            val alarm = createTestAlarm(
                scheduledTime = ZonedDateTime.now().minusMinutes(29)
            )
            coEvery { mockRepository.getAllScheduledAlarms() } returns listOf(alarm)

            // When
            val result = useCase()

            // Then: should NOT be marked as missed
            assertThat(result.getOrThrow().alarmsMissed).isEqualTo(0)
            coVerify(exactly = 0) { mockRepository.markAlarmAsMissed(any(), any()) }
        }

    }

    @Nested
    @DisplayName("Alarms overdue")
    inner class OverdueAlarms {
        @Test
        @DisplayName("Overdue alarm is marked as missed")
        fun testSingleOverdueAlarm() = runTest {
            // Given: alarm past grace period
            val alarm = createTestAlarm(
                id = "overdue-1",
                scheduledTime = ZonedDateTime.now().minusMinutes((gracePeriodMinutes + 5).toLong())
            )
            coEvery { mockRepository.getAllScheduledAlarms() } returns listOf(alarm)
            coEvery { mockRepository.markAlarmAsMissed(alarm.id, any()) } returns Result.success(
                Unit
            )

            // When
            val result = useCase()

            // Then
            assertThat(result.isSuccess).isTrue()
            val report = result.getOrThrow()
            assertThat(report.alarmsMissed).isEqualTo(1)
            assertThat(report.alarmsToReschedule).isEqualTo(0)
            assertThat(report.needsAttention).isTrue()

            coVerify(exactly = 1) { mockRepository.markAlarmAsMissed(alarm.id, any()) }
        }

        @Test
        @DisplayName("All overdue alarms are marked missed")
        fun testMultipleOverdueAlarms() = runTest {
            // Given: 3 alarms all past grace period
            val alarms = listOf(
                createTestAlarm("overdue-1", scheduledTime = ZonedDateTime.now().minusHours(3)),
                createTestAlarm("overdue-2", scheduledTime = ZonedDateTime.now().minusHours(5)),
                createTestAlarm("overdue-3", scheduledTime = ZonedDateTime.now().minusHours(1)),
            )
            coEvery { mockRepository.getAllScheduledAlarms() } returns alarms
            alarms.forEach { alarm ->
                coEvery {
                    mockRepository.markAlarmAsMissed(
                        alarm.id,
                        any()
                    )
                } returns Result.success(Unit)
            }

            // When
            val result = useCase()

            // Then
            val report = result.getOrThrow()
            assertThat(report.totalAlarmsChecked).isEqualTo(3)
            assertThat(report.alarmsMissed).isEqualTo(3)
            assertThat(report.alarmsToReschedule).isEqualTo(0)
        }

    }

    @Nested
    @DisplayName("Mixed overdue and upcoming")
    inner class MixedState {
        @Test
        @DisplayName("Correctly splits overdue vs upcoming alarms")
        fun testMixedOverdueAndUpcoming() = runTest {
            // Given: 2 overdue, 3 upcoming
            val overdueAlarms = listOf(
                createTestAlarm("overdue-1", scheduledTime = ZonedDateTime.now().minusHours(2)),
                createTestAlarm("overdue-2", scheduledTime = ZonedDateTime.now().minusHours(4)),
            )
            val upcomingAlarms = listOf(
                createTestAlarm("future-1", scheduledTime = ZonedDateTime.now().plusHours(1)),
                createTestAlarm("future-2", scheduledTime = ZonedDateTime.now().plusHours(3)),
                createTestAlarm("future-3", scheduledTime = ZonedDateTime.now().plusHours(5)),
            )

            coEvery { mockRepository.getAllScheduledAlarms() } returns overdueAlarms + upcomingAlarms
            overdueAlarms.forEach { alarm ->
                coEvery {
                    mockRepository.markAlarmAsMissed(
                        alarm.id,
                        any()
                    )
                } returns Result.success(Unit)
            }

            // When
            val result = useCase()

            // Then
            val report = result.getOrThrow()
            assertThat(report.totalAlarmsChecked).isEqualTo(5)
            assertThat(report.alarmsMissed).isEqualTo(2)
            assertThat(report.alarmsToReschedule).isEqualTo(3)
            assertThat(report.needsAttention).isTrue()
        }
    }

    @Nested
    @DisplayName("Partial failure when marking alarms missed")
    inner class PartialFailure {

        @Test
        @DisplayName("missedCount only reflects successfully marked alarms")
        fun testPartialMarkMissedFailure() = runTest {
            // Given: 3 overdue alarms, but only 2 mark successfully
            val alarms = listOf(
                createTestAlarm("overdue-1", scheduledTime = ZonedDateTime.now().minusHours(2)),
                createTestAlarm("overdue-2", scheduledTime = ZonedDateTime.now().minusHours(3)),
                createTestAlarm("overdue-3", scheduledTime = ZonedDateTime.now().minusHours(4)),
            )

            coEvery { mockRepository.getAllScheduledAlarms() } returns alarms
            coEvery { mockRepository.markAlarmAsMissed("overdue-1", any()) } returns Result.success(
                Unit
            )
            coEvery { mockRepository.markAlarmAsMissed("overdue-2", any()) } returns
                    Result.failure(RuntimeException("DB locked"))
            coEvery { mockRepository.markAlarmAsMissed("overdue-3", any()) } returns Result.success(
                Unit
            )

            // When
            val result = useCase()

            // Then: use case still succeeds, but missedCount = 2 (not 3)
            assertThat(result.isSuccess).isTrue()
            val report = result.getOrThrow()
            assertThat(report.alarmsMissed).isEqualTo(2)
            assertThat(report.totalAlarmsChecked).isEqualTo(3)
        }

    }

    @Nested
    @DisplayName("Exception handling")
    inner class ExceptionHandling{
        @Test
        @DisplayName("Returns failure when getAllScheduledAlarms throws")
        fun testRepositoryThrows() = runTest {
            // Given
            coEvery { mockRepository.getAllScheduledAlarms() } throws
                    RuntimeException("Database unavailable")

            // When
            val result = useCase()

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
        }
    }

    @Nested
    @DisplayName("Report metadata")
    inner class ReportMetadata{

        @Test
        @DisplayName("reconciledAt is set to a recent time")
        fun testReconciledAtIsPopulated() = runTest {
            // Given
            val beforeTest = ZonedDateTime.now()
            coEvery { mockRepository.getAllScheduledAlarms() } returns emptyList()

            // When
            val result = useCase()

            // Then
            val reconciledAt = result.getOrThrow().reconciledAt
            assertThat(reconciledAt.isBefore(ZonedDateTime.now())).isTrue()
            assertThat(reconciledAt.isAfter(beforeTest.minusSeconds(1))).isTrue()
        }

        @Test
        @DisplayName("needsAttention is false when no alarms missed")
        fun testNeedsAttentionFalse() = runTest {
            // Given
            coEvery { mockRepository.getAllScheduledAlarms() } returns emptyList()

            // When
            val report = useCase().getOrThrow()

            // Then
            assertThat(report.needsAttention).isFalse()
        }

        @Test
        @DisplayName("needsAttention is true when at least one alarm missed")
        fun testNeedsAttentionTrue() = runTest {
            // Given
            val alarm = createTestAlarm(scheduledTime = ZonedDateTime.now().minusHours(2))
            coEvery { mockRepository.getAllScheduledAlarms() } returns listOf(alarm)
            coEvery { mockRepository.markAlarmAsMissed(any(), any()) } returns Result.success(Unit)

            // When
            val report = useCase().getOrThrow()

            // Then
            assertThat(report.needsAttention).isTrue()
        }
    }

    private fun createTestAlarm(
        id: String = "test-alarm-${System.nanoTime()}",
        scheduledTime: ZonedDateTime = ZonedDateTime.now().plusHours(1),
    ): ScheduledAlarm {
        val now = ZonedDateTime.now()
        return ScheduledAlarm(
            id = id,
            scheduleId = "schedule-1",
            medicineId = "medicine-1",
            medicineName = "Test Medicine",
            dosageAmount = 400.0,
            dosageUnit = DosageUnit.MILLIGRAMS,
            scheduledTime = scheduledTime,
            status = AlarmStatus.SCHEDULED,
            alarmRequestCode = (Math.random() * 100000).toInt(),
            createdAt = now
        )
    }

}