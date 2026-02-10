package com.emman.android.medialarmapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emman.android.medialarmapp.data.local.entities.ScheduledAlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(alarm: ScheduledAlarmEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(alarms: List<ScheduledAlarmEntity>): List<Long>

    @Query("SELECT * FROM scheduled_alarms WHERE id = :alarmId")
    fun observeAlarmById(alarmId: String): Flow<ScheduledAlarmEntity?>

    @Query("SELECT * FROM scheduled_alarms WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: String): ScheduledAlarmEntity?

    @Query("""
        SELECT * FROM scheduled_alarms 
        WHERE status = 'SCHEDULED' 
        AND scheduled_time >= :currentTime
        ORDER BY scheduled_time ASC 
        LIMIT :limit
    """)
    fun observeUpcomingAlarms(currentTime: Long, limit: Int = 10): Flow<List<ScheduledAlarmEntity>>

    @Query("""
        SELECT * FROM scheduled_alarms 
        WHERE status = 'SCHEDULED'
        AND scheduled_time >= :startOfDay
        AND scheduled_time < :endOfDay
        ORDER BY scheduled_time ASC
    """)
    fun observeTodaysPendingAlarms(startOfDay: Long, endOfDay: Long): Flow<List<ScheduledAlarmEntity>>

    @Query("""
        SELECT * FROM scheduled_alarms 
        WHERE status = 'SCHEDULED'
        AND scheduled_time < :currentTime
        ORDER BY scheduled_time DESC
    """)
    fun observeOverdueAlarms(currentTime: Long): Flow<List<ScheduledAlarmEntity>>

    @Query("""
        SELECT * FROM scheduled_alarms 
        WHERE medicine_id = :medicineId
        ORDER BY scheduled_time DESC
        LIMIT :limit
    """)
    fun observeAlarmsForMedicine(medicineId: Long, limit: Int = 50): Flow<List<ScheduledAlarmEntity>>

    @Query("""
        SELECT * FROM scheduled_alarms 
        WHERE status = :status
        ORDER BY scheduled_time DESC
        LIMIT :limit
    """)
    fun observeAlarmsByStatus(status: String, limit: Int = 50): Flow<List<ScheduledAlarmEntity>>

    @Query("""
        SELECT * FROM scheduled_alarms 
        WHERE status = 'SCHEDULED'
        ORDER BY scheduled_time ASC
    """)
    suspend fun getAllScheduledAlarms(): List<ScheduledAlarmEntity>

    @Query("SELECT * FROM scheduled_alarms WHERE alarm_request_code = :requestCode")
    suspend fun getAlarmByRequestCode(requestCode: Int): ScheduledAlarmEntity?

    @Update
    suspend fun update(alarm: ScheduledAlarmEntity)

    @Query("""
        UPDATE scheduled_alarms 
        SET status = 'TAKEN', taken_at = :takenAt 
        WHERE id = :alarmId
    """)
    suspend fun markAsTaken(alarmId: String, takenAt: Long)

    @Query("""
        UPDATE scheduled_alarms 
        SET status = 'MISSED', missed_at = :missedAt 
        WHERE id = :alarmId
    """)
    suspend fun markAsMissed(alarmId: String, missedAt: Long)

    @Query("""
        UPDATE scheduled_alarms 
        SET status = 'SNOOZED', snoozed_until = :snoozedUntil 
        WHERE id = :alarmId
    """)
    suspend fun markAsSnoozed(alarmId: String, snoozedUntil: Long)

    @Query("UPDATE scheduled_alarms SET notification_shown = 1 WHERE id = :alarmId")
    suspend fun markNotificationShown(alarmId: String)

    @Delete
    suspend fun delete(alarm: ScheduledAlarmEntity)

    @Query("DELETE FROM scheduled_alarms WHERE id = :alarmId")
    suspend fun deleteById(alarmId: String)

    @Query("DELETE FROM scheduled_alarms WHERE schedule_id = :scheduleId")
    suspend fun deleteAllForSchedule(scheduleId: Long)

    @Query("DELETE FROM scheduled_alarms WHERE medicine_id = :medicineId")
    suspend fun deleteAllForMedicine(medicineId: Long)

    @Query("""
        DELETE FROM scheduled_alarms 
        WHERE (status = 'TAKEN' OR status = 'MISSED') 
        AND scheduled_time < :olderThan
    """)
    suspend fun deleteOldCompletedAlarms(olderThan: Long)

    @Query("""
        SELECT COUNT(*) FROM scheduled_alarms 
        WHERE status = 'SCHEDULED'
    """)
    fun observePendingAlarmCount(): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM scheduled_alarms 
        WHERE status = 'SCHEDULED' 
        AND scheduled_time < :currentTime
    """)
    fun observeOverdueAlarmCount(currentTime: Long): Flow<Int>
}