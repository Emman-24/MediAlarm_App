package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.emman.android.medialarm.data.local.entities.IntervalEntity

@Dao
interface IntervalDao {

    @Query("SELECT * FROM interval WHERE schedule_id = :scheduleId")
    suspend fun getByScheduleId(scheduleId: Long): IntervalEntity?

    @Insert
    suspend fun insert(entity: IntervalEntity): Long

    @Update
    suspend fun update(entity: IntervalEntity)

    @Delete
    suspend fun delete(entity: IntervalEntity)
}