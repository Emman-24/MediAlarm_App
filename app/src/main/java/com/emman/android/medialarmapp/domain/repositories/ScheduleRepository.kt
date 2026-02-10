package com.emman.android.medialarmapp.domain.repositories

import com.emman.android.medialarmapp.domain.models.IntakeEvent
import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.models.MedicineSchedule
import com.emman.android.medialarmapp.domain.models.ScheduledAlarm
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    fun observeActiveMedicines(): Flow<List<Medicine>>
    fun observeMedicineById(id: String): Flow<Medicine?>
    suspend fun getMedicineById(id: String): Medicine?
    suspend fun saveMedicine(medicine: Medicine): Result<Long>
    suspend fun updateMedicine(medicine: Medicine): Result<Unit>
    suspend fun deleteMedicine(medicineId: String): Result<Unit>
    suspend fun updateMedicineActiveStatus(medicineId: String, isActive: Boolean): Result<Unit>

    // ========== SCHEDULES ==========

    fun observeSchedulesForMedicine(medicineId: String): Flow<List<MedicineSchedule>>
    fun observeActiveSchedules(): Flow<List<MedicineSchedule>>
    suspend fun getScheduleById(id: String): MedicineSchedule?
    suspend fun saveSchedule(schedule: MedicineSchedule): Result<Long>
    suspend fun updateSchedule(schedule: MedicineSchedule): Result<Unit>
    suspend fun deleteSchedule(scheduleId: String): Result<Unit>

    // ========== ALARMS ==========

    fun observeUpcomingAlarms(limit: Int = 10): Flow<List<ScheduledAlarm>>
    fun observeAlarmsForMedicine(medicineId: String): Flow<List<ScheduledAlarm>>
    suspend fun getAlarmById(id: String): ScheduledAlarm?
    suspend fun getAllScheduledAlarms(): List<ScheduledAlarm>
    suspend fun saveAlarm(alarm: ScheduledAlarm): Result<Long>
    suspend fun saveAlarms(alarms: List<ScheduledAlarm>): Result<List<Long>>
    suspend fun updateAlarm(alarm: ScheduledAlarm): Result<Unit>
    suspend fun markAlarmAsTaken(alarmId: String, takenAt: java.time.ZonedDateTime): Result<Unit>
    suspend fun markAlarmAsMissed(alarmId: String, missedAt: java.time.ZonedDateTime): Result<Unit>
    suspend fun deleteAlarm(alarmId: String): Result<Unit>
    suspend fun deleteAlarmsForSchedule(scheduleId: String): Result<Unit>

    // ========== INTAKE EVENTS ==========

    fun observeHistoryForMedicine(medicineId: String, limit: Int = 100): Flow<List<IntakeEvent>>
    suspend fun saveIntakeEvent(event: IntakeEvent): Result<Long>
    suspend fun getIntakeEventsInRange(
        medicineId: String,
        startTime: java.time.ZonedDateTime,
        endTime: java.time.ZonedDateTime
    ): List<IntakeEvent>

}