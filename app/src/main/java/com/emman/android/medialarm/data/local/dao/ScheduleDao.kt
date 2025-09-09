package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emman.android.medialarm.data.local.entities.ScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity): Long

    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)

    @Query("SELECT * FROM schedule WHERE id = :id")
    fun getScheduleById(id: Long): Flow<ScheduleEntity?>

    @Query("SELECT * FROM schedule WHERE medicine_id = :medicineId")
    fun getSchedulesForMedicine(medicineId: Long): Flow<List<ScheduleEntity>>

    @Query("DELETE FROM schedule WHERE id = :id")
    suspend fun deleteScheduleById(id: Long)

    @Query("DELETE FROM schedule WHERE medicine_id = :medicineId")
    suspend fun deleteSchedulesForMedicine(medicineId: Long)
}