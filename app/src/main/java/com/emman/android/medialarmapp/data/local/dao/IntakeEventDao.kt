package com.emman.android.medialarmapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emman.android.medialarmapp.data.local.entities.IntakeEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IntakeEventDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(event: IntakeEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(events: List<IntakeEventEntity>): List<Long>

    @Query("SELECT * FROM intake_events WHERE id = :eventId")
    fun observeEventById(eventId: String): Flow<IntakeEventEntity?>

    @Query("SELECT * FROM intake_events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): IntakeEventEntity?

    @Query("""
        SELECT * FROM intake_events 
        WHERE medicine_id = :medicineId
        ORDER BY actual_taken_time DESC
        LIMIT :limit
    """)
    fun observeHistoryForMedicine(medicineId: Long, limit: Int = 100): Flow<List<IntakeEventEntity>>

    @Query("""
        SELECT * FROM intake_events 
        WHERE medicine_id = :medicineId
        AND actual_taken_time >= :startTime
        AND actual_taken_time < :endTime
        ORDER BY actual_taken_time DESC
    """)
    fun observeEventsInRange(
        medicineId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<IntakeEventEntity>>

    @Query("""
        SELECT * FROM intake_events 
        ORDER BY actual_taken_time DESC
        LIMIT :limit
    """)
    fun observeRecentEvents(limit: Int = 50): Flow<List<IntakeEventEntity>>

    @Query("""
        SELECT * FROM intake_events 
        WHERE medicine_id = :medicineId
        AND delay_minutes > :thresholdMinutes
        ORDER BY actual_taken_time DESC
        LIMIT :limit
    """)
    fun observeLateIntakes(
        medicineId: Long,
        thresholdMinutes: Long = 15,
        limit: Int = 50
    ): Flow<List<IntakeEventEntity>>

    @Query("""
        SELECT COUNT(*) FROM intake_events 
        WHERE medicine_id = :medicineId
        AND actual_taken_time >= :startTime
        AND actual_taken_time < :endTime
    """)
    suspend fun countEventsInRange(
        medicineId: Long,
        startTime: Long,
        endTime: Long
    ): Int

    @Query("""
        SELECT COUNT(*) FROM intake_events 
        WHERE medicine_id = :medicineId
        AND actual_taken_time >= :startTime
        AND actual_taken_time < :endTime
        AND delay_minutes >= -15
        AND delay_minutes <= 15
    """)
    suspend fun countOnTimeEventsInRange(
        medicineId: Long,
        startTime: Long,
        endTime: Long
    ): Int

    @Query("""
        SELECT AVG(delay_minutes) FROM intake_events 
        WHERE medicine_id = :medicineId
        AND actual_taken_time >= :startTime
        AND actual_taken_time < :endTime
    """)
    suspend fun getAverageDelayInRange(
        medicineId: Long,
        startTime: Long,
        endTime: Long
    ): Double?

    @Delete
    suspend fun delete(event: IntakeEventEntity)

    @Query("DELETE FROM intake_events WHERE id = :eventId")
    suspend fun deleteById(eventId: String)

    @Query("DELETE FROM intake_events WHERE actual_taken_time < :olderThan")
    suspend fun deleteOldEvents(olderThan: Long)

    @Query("DELETE FROM intake_events WHERE medicine_id = :medicineId")
    suspend fun deleteAllForMedicine(medicineId: Long)
}