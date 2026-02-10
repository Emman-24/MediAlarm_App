package com.emman.android.medialarmapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emman.android.medialarmapp.data.local.entities.ScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(schedule: ScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(schedules: List<ScheduleEntity>): List<Long>


    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    fun observeScheduleById(scheduleId: Long): Flow<ScheduleEntity?>

    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: Long): ScheduleEntity?

    @Query("""
        SELECT * FROM schedules 
        WHERE medicine_id = :medicineId 
        AND is_active = 1
        ORDER BY created_at DESC
    """)
    fun observeSchedulesForMedicine(medicineId: Long): Flow<List<ScheduleEntity>>

    @Query("""
        SELECT * FROM schedules 
        WHERE is_active = 1
        ORDER BY created_at DESC
    """)
    fun observeActiveSchedules(): Flow<List<ScheduleEntity>>

    @Query("""
        SELECT * FROM schedules 
        WHERE schedule_type = :scheduleType 
        AND is_active = 1
    """)
    fun observeSchedulesByType(scheduleType: String): Flow<List<ScheduleEntity>>

    @Update
    suspend fun update(schedule: ScheduleEntity)

    @Query("UPDATE schedules SET is_active = :isActive WHERE id = :scheduleId")
    suspend fun updateActiveStatus(scheduleId: Long, isActive: Boolean)


    @Delete
    suspend fun delete(schedule: ScheduleEntity)

    @Query("DELETE FROM schedules WHERE id = :scheduleId")
    suspend fun deleteById(scheduleId: Long)

    @Query("DELETE FROM schedules WHERE medicine_id = :medicineId")
    suspend fun deleteAllForMedicine(medicineId: Long)

}