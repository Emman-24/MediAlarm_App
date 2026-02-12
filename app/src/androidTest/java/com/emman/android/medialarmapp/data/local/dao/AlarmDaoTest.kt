package com.emman.android.medialarmapp.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.emman.android.medialarmapp.data.local.MediAlarmDatabase
import com.emman.android.medialarmapp.data.local.entities.MedicineEntity
import com.emman.android.medialarmapp.data.local.entities.ScheduleEntity
import com.emman.android.medialarmapp.data.local.entities.ScheduledAlarmEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.ZoneId
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class AlarmDaoTest {

    private lateinit var database: MediAlarmDatabase
    private lateinit var alarmDao: AlarmDao
    private lateinit var medicineDao: MedicineDao
    private lateinit var scheduleDao: ScheduleDao

    private var testMedicineId: Long = 0
    private var testScheduleId: Long = 0

    @Before
    fun setup() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MediAlarmDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        alarmDao = database.alarmDao()
        medicineDao = database.medicineDao()
        scheduleDao = database.scheduleDao()

        testMedicineId = medicineDao.insert(createTestMedicine())
        testScheduleId = scheduleDao.insert(createTestSchedule(testMedicineId))
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAlarm() = runTest {
        // Given
        val alarm = createTestAlarm()

        // When
        val result = alarmDao.insert(alarm)

        // Then
        assertThat(result).isEqualTo(1L)

        val retrieved = alarmDao.getAlarmById(alarm.id)
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.id).isEqualTo(alarm.id)
    }

    @Test
    fun testInsertMultipleAlarms() = runTest {
        // Given
        val alarms = listOf(
            createTestAlarm(requestCode = 1),
            createTestAlarm(requestCode = 2),
            createTestAlarm(requestCode = 3)
        )

        // When
        val ids = alarmDao.insertAll(alarms)

        // Then
        assertThat(ids).hasSize(3)
    }

    @Test
    fun testUniqueRequestCodeConstraint() = runTest {
        // Given
        val alarm1 = createTestAlarm(requestCode = 100)
        val alarm2 = createTestAlarm(requestCode = 100) // Mismo request code

        // When
        alarmDao.insert(alarm1)

        // Then: Segunda inserción debe fallar
        try {
            alarmDao.insert(alarm2)
            throw AssertionError("Should have thrown constraint violation")
        } catch (e: Exception) {
            // Expected - unique constraint violated
            assertThat(e.message).contains("UNIQUE")
        }
    }

    @Test
    fun testObserveUpcomingAlarms() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val hourInMillis = 3600_000L

        // Alarma pasada (no debe aparecer)
        alarmDao.insert(createTestAlarm(
            scheduledTime = now - hourInMillis,
            status = "SCHEDULED"
        ))

        // Alarmas futuras (deben aparecer)
        alarmDao.insert(createTestAlarm(
            id = "future-1",
            scheduledTime = now + hourInMillis,
            status = "SCHEDULED",
            requestCode = 1
        ))
        alarmDao.insert(createTestAlarm(
            id = "future-2",
            scheduledTime = now + (2 * hourInMillis),
            status = "SCHEDULED",
            requestCode = 2
        ))

        // Alarma futura pero TAKEN (no debe aparecer)
        alarmDao.insert(createTestAlarm(
            scheduledTime = now + (3 * hourInMillis),
            status = "TAKEN",
            requestCode = 3
        ))

        // When & Then
        alarmDao.observeUpcomingAlarms(now, limit = 10).test {
            val alarms = awaitItem()

            assertThat(alarms).hasSize(2)
            assertThat(alarms.map { it.id }).containsExactly("future-1", "future-2")
            assertThat(alarms.all { it.status == "SCHEDULED" }).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testUpcomingAlarmsLimit() = runTest {
        // Given
        val now = System.currentTimeMillis()
        repeat(10) { i ->
            alarmDao.insert(createTestAlarm(
                id = "alarm-$i",
                scheduledTime = now + (i * 3600_000L),
                requestCode = i
            ))
        }

        // When & Then
        alarmDao.observeUpcomingAlarms(now, limit = 5).test {
            val alarms = awaitItem()
            assertThat(alarms).hasSize(5)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testUpcomingAlarmsOrder() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val times = listOf(
            now + 7200_000L,  // +2h
            now + 3600_000L,  // +1h
            now + 10800_000L  // +3h
        )

        times.forEachIndexed { i, time ->
            alarmDao.insert(createTestAlarm(
                id = "alarm-$i",
                scheduledTime = time,
                requestCode = i
            ))
        }

        // When & Then
        alarmDao.observeUpcomingAlarms(now, limit = 10).test {
            val alarms = awaitItem()

            // Should be ordered by scheduledTime ASC
            assertThat(alarms[0].scheduledTime).isLessThan(alarms[1].scheduledTime)
            assertThat(alarms[1].scheduledTime).isLessThan(alarms[2].scheduledTime)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testObserveOverdueAlarms() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val hourInMillis = 3600_000L

        // Alarmas vencidas
        alarmDao.insert(createTestAlarm(
            id = "overdue-1",
            scheduledTime = now - (2 * hourInMillis),
            status = "SCHEDULED",
            requestCode = 1
        ))
        alarmDao.insert(createTestAlarm(
            id = "overdue-2",
            scheduledTime = now - hourInMillis,
            status = "SCHEDULED",
            requestCode = 2
        ))

        // Alarma futura (no debe aparecer)
        alarmDao.insert(createTestAlarm(
            scheduledTime = now + hourInMillis,
            status = "SCHEDULED",
            requestCode = 3
        ))

        // When & Then
        alarmDao.observeOverdueAlarms(now).test {
            val alarms = awaitItem()

            assertThat(alarms).hasSize(2)
            assertThat(alarms.map { it.id }).containsExactly("overdue-2", "overdue-1")
            // Ordenado DESC (más recientes primero)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testMarkAsTaken() = runTest {
        // Given
        val alarm = createTestAlarm(status = "SCHEDULED")
        alarmDao.insert(alarm)
        val takenAt = System.currentTimeMillis()

        // When
        alarmDao.markAsTaken(alarm.id, takenAt)

        // Then
        val updated = alarmDao.getAlarmById(alarm.id)
        assertThat(updated?.status).isEqualTo("TAKEN")
        assertThat(updated?.takenAt).isEqualTo(takenAt)
    }

    @Test
    fun testMarkAsMissed() = runTest {
        // Given
        val alarm = createTestAlarm(status = "SCHEDULED")
        alarmDao.insert(alarm)
        val missedAt = System.currentTimeMillis()

        // When
        alarmDao.markAsMissed(alarm.id, missedAt)

        // Then
        val updated = alarmDao.getAlarmById(alarm.id)
        assertThat(updated?.status).isEqualTo("MISSED")
        assertThat(updated?.missedAt).isEqualTo(missedAt)
    }

    @Test
    fun testMarkAsSnoozed() = runTest {
        // Given
        val alarm = createTestAlarm(status = "SCHEDULED")
        alarmDao.insert(alarm)
        val snoozedUntil = System.currentTimeMillis() + 600_000L  // +10 min

        // When
        alarmDao.markAsSnoozed(alarm.id, snoozedUntil)

        // Then
        val updated = alarmDao.getAlarmById(alarm.id)
        assertThat(updated?.status).isEqualTo("SNOOZED")
        assertThat(updated?.snoozedUntil).isEqualTo(snoozedUntil)
    }

    @Test
    fun testCascadeDeleteFromMedicine() = runTest {
        // Given
        val alarm = createTestAlarm()
        alarmDao.insert(alarm)

        // Verify alarm exists
        assertThat(alarmDao.getAlarmById(alarm.id)).isNotNull()

        // When: Delete medicine (should cascade to alarms)
        medicineDao.deleteById(testMedicineId)

        // Then
        assertThat(alarmDao.getAlarmById(alarm.id)).isNull()
    }

    @Test
    fun testDeleteAllForSchedule() = runTest {
        // Given
        alarmDao.insert(createTestAlarm(id = "alarm-1", requestCode = 1))
        alarmDao.insert(createTestAlarm(id = "alarm-2", requestCode = 2))
        alarmDao.insert(createTestAlarm(id = "alarm-3", requestCode = 3))

        // When
        alarmDao.deleteAllForSchedule(testScheduleId)

        // Then
        val remaining = alarmDao.getAllScheduledAlarms()
        assertThat(remaining).isEmpty()
    }

    @Test
    fun testObservePendingAlarmCount() = runTest {
        // Given
        alarmDao.insert(createTestAlarm(status = "SCHEDULED", requestCode = 1))
        alarmDao.insert(createTestAlarm(status = "SCHEDULED", requestCode = 2))
        alarmDao.insert(createTestAlarm(status = "TAKEN", requestCode = 3))
        alarmDao.insert(createTestAlarm(status = "MISSED", requestCode = 4))

        // When & Then
        alarmDao.observePendingAlarmCount().test {
            val count = awaitItem()
            assertThat(count).isEqualTo(2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testGetAlarmByRequestCode() = runTest {
        // Given
        val alarm = createTestAlarm(requestCode = 12345)
        alarmDao.insert(alarm)

        // When
        val found = alarmDao.getAlarmByRequestCode(12345)

        // Then
        assertThat(found).isNotNull()
        assertThat(found?.id).isEqualTo(alarm.id)
    }

    private fun createTestMedicine(): MedicineEntity {
        val now = System.currentTimeMillis()
        return MedicineEntity(
            id = 0,
            name = "Test Medicine",
            dosageAmount = 400.0,
            dosageUnit = "MILLIGRAMS",
            formType = "TABLET",
            isActive = true,
            createdAt = now,
            updatedAt = now
        )
    }

    private fun createTestSchedule(medicineId: Long): ScheduleEntity {
        return ScheduleEntity(
            id = 0,
            medicineId = medicineId,
            scheduleType = "INTERVAL",
            patternJson = """{"type":"INTERVAL","intervalHours":8,"startTime":"08:00"}""",
            startDate = System.currentTimeMillis(),
            endDate = null,
            timeZoneId = ZoneId.systemDefault().id,
            isActive = true,
            createdAt = System.currentTimeMillis()
        )
    }

    private fun createTestAlarm(
        id: String = UUID.randomUUID().toString(),
        scheduledTime: Long = System.currentTimeMillis() + 3600_000L,
        status: String = "SCHEDULED",
        requestCode: Int = (Math.random() * 100000).toInt()
    ): ScheduledAlarmEntity {
        return ScheduledAlarmEntity(
            id = id,
            scheduleId = testScheduleId,
            medicineId = testMedicineId,
            medicineName = "Test Medicine",
            dosageAmount = 400.0,
            dosageUnit = "MILLIGRAMS",
            scheduledTime = scheduledTime,
            timeZoneId = ZoneId.systemDefault().id,
            status = status,
            takenAt = null,
            missedAt = null,
            snoozedUntil = null,
            alarmRequestCode = requestCode,
            notificationShown = false,
            createdAt = System.currentTimeMillis()
        )
    }

}