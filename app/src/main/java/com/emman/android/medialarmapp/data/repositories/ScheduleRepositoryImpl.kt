package com.emman.android.medialarmapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.withTransaction
import com.emman.android.medialarmapp.data.local.MediAlarmDatabase
import com.emman.android.medialarmapp.data.local.dao.AlarmDao
import com.emman.android.medialarmapp.data.local.dao.IntakeEventDao
import com.emman.android.medialarmapp.data.local.dao.MedicineDao
import com.emman.android.medialarmapp.data.local.dao.ScheduleDao
import com.emman.android.medialarmapp.data.mappers.toDomain
import com.emman.android.medialarmapp.data.mappers.toDomainAlarms
import com.emman.android.medialarmapp.data.mappers.toDomainEvents
import com.emman.android.medialarmapp.data.mappers.toEntity
import com.emman.android.medialarmapp.domain.models.IntakeEvent
import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.models.MedicineSchedule
import com.emman.android.medialarmapp.domain.models.ScheduleTransactionResult
import com.emman.android.medialarmapp.domain.models.ScheduledAlarm
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val database: MediAlarmDatabase,
    private val medicineDao: MedicineDao,
    private val scheduleDao: ScheduleDao,
    private val alarmDao: AlarmDao,
    private val intakeEventDao: IntakeEventDao,
) : ScheduleRepository {
    override fun observeActiveMedicines(): Flow<List<Medicine>> {
        return medicineDao.observeActiveMedicines().map { entities ->
            entities.toDomain()
        }
    }

    override fun observeMedicineById(id: String): Flow<Medicine?> {
        return medicineDao.observeMedicineById(id.toLong()).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun getMedicineById(id: String): Medicine? {
        return try {
            medicineDao.getMedicineById(id.toLong())?.toDomain()
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun saveMedicine(medicine: Medicine): Result<Long> {
        return try {
            val id = medicineDao.insert(medicine.toEntity())
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMedicine(medicine: Medicine): Result<Unit> {
        return try {
            medicineDao.update(medicine.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMedicine(medicineId: String): Result<Unit> {
        return try {
            medicineDao.deleteById(medicineId.toLong())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMedicineActiveStatus(
        medicineId: String,
        isActive: Boolean,
    ): Result<Unit> {
        return try {
            medicineDao.updateActiveStatus(
                medicineId.toLong(),
                isActive,
                System.currentTimeMillis()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeSchedulesForMedicine(medicineId: String): Flow<List<MedicineSchedule>> {
        return scheduleDao.observeSchedulesForMedicine(medicineId.toLong()).map { entities ->
            entities.toDomain()
        }
    }

    override fun observeActiveSchedules(): Flow<List<MedicineSchedule>> {
        return scheduleDao.observeActiveSchedules().map { entities ->
            entities.toDomain()
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun getScheduleById(id: String): MedicineSchedule? {
        return try {
            scheduleDao.getScheduleById(id.toLong())?.toDomain()
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun saveSchedule(schedule: MedicineSchedule): Result<Long> {
        return try {
            val id = scheduleDao.insert(schedule.toEntity())
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSchedule(schedule: MedicineSchedule): Result<Unit> {
        return try {
            scheduleDao.update(schedule.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSchedule(scheduleId: String): Result<Unit> {
        return try {
            scheduleDao.deleteById(scheduleId.toLong())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeUpcomingAlarms(limit: Int): Flow<List<ScheduledAlarm>> {
        val currentTime = System.currentTimeMillis()
        return alarmDao.observeUpcomingAlarms(currentTime, limit).map { entities ->
            entities.toDomainAlarms()
        }
    }

    override fun observeAlarmsForMedicine(medicineId: String): Flow<List<ScheduledAlarm>> {
        return alarmDao.observeAlarmsForMedicine(medicineId.toLong()).map { entities ->
            entities.toDomainAlarms()
        }
    }

    override suspend fun getAlarmById(id: String): ScheduledAlarm? {
        return try {
            alarmDao.getAlarmById(id)?.toDomain()
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun getAllScheduledAlarms(): List<ScheduledAlarm> {
        return try {
            alarmDao.getAllScheduledAlarms().toDomainAlarms()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun saveAlarm(alarm: ScheduledAlarm): Result<Long> {
        return try {
            alarmDao.insert(alarm.toEntity())
            Result.success(1L)  // String Id , 1 for success
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveAlarms(alarms: List<ScheduledAlarm>): Result<List<Long>> {
        return try {
            val entities = alarms.map { it.toEntity() }
            alarmDao.insertAll(entities)
            Result.success(List(alarms.size) { 1L })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAlarm(alarm: ScheduledAlarm): Result<Unit> {
        return try {
            alarmDao.update(alarm.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAlarmAsTaken(
        alarmId: String,
        takenAt: ZonedDateTime,
    ): Result<Unit> {
        return try {
            alarmDao.markAsTaken(alarmId, takenAt.toInstant().toEpochMilli())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAlarmAsMissed(
        alarmId: String,
        missedAt: ZonedDateTime,
    ): Result<Unit> {
        return try {
            alarmDao.markAsMissed(alarmId, missedAt.toInstant().toEpochMilli())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAlarm(alarmId: String): Result<Unit> {
        return try {
            alarmDao.deleteById(alarmId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAlarmsForSchedule(scheduleId: String): Result<Unit> {
        return try {
            alarmDao.deleteAllForSchedule(scheduleId.toLong())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeHistoryForMedicine(
        medicineId: String,
        limit: Int,
    ): Flow<List<IntakeEvent>> {
        return intakeEventDao.observeHistoryForMedicine(medicineId.toLong(), limit)
            .map { entities ->
                entities.toDomainEvents()
            }
    }

    override suspend fun saveIntakeEvent(event: IntakeEvent): Result<Long> {
        return try {
            intakeEventDao.insert(event.toEntity())
            Result.success(1L)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getIntakeEventsInRange(
        medicineId: String,
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
    ): List<IntakeEvent> {
        return try {
            intakeEventDao.observeEventsInRange(
                medicineId.toLong(),
                startTime.toInstant().toEpochMilli(),
                endTime.toInstant().toEpochMilli()
            ).map { it.toDomainEvents() }
                .toString()
            emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun saveMedicineWithScheduleAndAlarms(
        medicine: Medicine,
        schedule: MedicineSchedule,
        alarms: List<ScheduledAlarm>,
    ): Result<ScheduleTransactionResult> {
        return try {
            database.withTransaction {

                val medicineId = medicineDao.insert(medicine.toEntity())

                val scheduleWithMedicineId = schedule.copy(medicineId = medicineId.toString())
                val scheduleId = scheduleDao.insert(scheduleWithMedicineId.toEntity())


                val alarmWithIds = alarms.map { alarm ->
                    alarm.copy(
                        medicineId = medicineId.toString(),
                        scheduleId = scheduleId.toString()
                    )
                }

                val alarmEntities = alarmWithIds.map { it.toEntity() }
                alarmDao.insertAll(alarmEntities)

                Result.success(
                    ScheduleTransactionResult(
                        medicineId = medicineId,
                        scheduleId = scheduleId,
                        alarmIds = List(alarms.size) { index -> index.toLong() + 1 }
                    )
                )
            }


        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}