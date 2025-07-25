package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.emman.android.medialarm.data.local.entities.MedicineEntity
import com.emman.android.medialarm.data.local.entities.ScheduleType
import com.emman.android.medialarm.data.local.relations.MedicineWithSchedules
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineScheduleDao {

    /**
     * Gets all active medicines with their associated schedules.
     * Uses the MedicineWithSchedules relation class for better type safety.
     * 
     * @return A list of MedicineWithSchedules objects containing active medicines and their schedules
     */
    @Transaction
    @Query("SELECT * FROM medicines WHERE isActive = 1")
    suspend fun getActiveMedicinesWithSchedules(): List<MedicineWithSchedules>

    /**
     * Gets all active medicines with their associated schedules as a Flow.
     * This is useful for observing changes in the data.
     * 
     * @return A Flow of list of MedicineWithSchedules objects
     */
    @Transaction
    @Query("SELECT * FROM medicines WHERE isActive = 1")
    fun getActiveMedicinesWithSchedulesFlow(): Flow<List<MedicineWithSchedules>>

    /**
     * Gets medicines by schedule type.
     * 
     * @param scheduleType The type of schedule to filter by
     * @return A list of MedicineEntity objects with the specified schedule type
     */
    @Query("""
        SELECT m.* 
        FROM medicines m 
        INNER JOIN schedule s ON m.id = s.medicine_id 
        WHERE m.isActive = 1 AND s.schedule_type = :scheduleType
    """)
    suspend fun getMedicinesByScheduleType(scheduleType: ScheduleType): List<MedicineEntity>

    /**
     * Gets medicines by schedule type as a Flow.
     * 
     * @param scheduleType The type of schedule to filter by
     * @return A Flow of list of MedicineEntity objects
     */
    @Query("""
        SELECT m.* 
        FROM medicines m 
        INNER JOIN schedule s ON m.id = s.medicine_id 
        WHERE m.isActive = 1 AND s.schedule_type = :scheduleType
    """)
    fun getMedicinesByScheduleTypeFlow(scheduleType: ScheduleType): Flow<List<MedicineEntity>>

    /**
     * Gets medicines with their schedules filtered by schedule type.
     * 
     * @param scheduleType The type of schedule to filter by
     * @return A list of MedicineWithSchedules objects
     */
    @Transaction
    @Query("""
        SELECT DISTINCT m.* 
        FROM medicines m 
        INNER JOIN schedule s ON m.id = s.medicine_id 
        WHERE m.isActive = 1 AND s.schedule_type = :scheduleType
    """)
    suspend fun getMedicinesWithSchedulesByType(scheduleType: ScheduleType): List<MedicineWithSchedules>
}
