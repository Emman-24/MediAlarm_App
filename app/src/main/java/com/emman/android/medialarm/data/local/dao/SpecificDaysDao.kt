package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emman.android.medialarm.data.local.entities.SpecificDaysEntity

@Dao
interface SpecificDaysDao {

    @Query("SELECT * FROM specific_days WHERE schedule_id = :scheduleId")
    suspend fun getByScheduleId(scheduleId: Long): SpecificDaysEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(specificDays: SpecificDaysEntity): Long

    @Update
    suspend fun update(specificDays: SpecificDaysEntity): Int

    @Delete
    suspend fun delete(specificDays: SpecificDaysEntity)
}