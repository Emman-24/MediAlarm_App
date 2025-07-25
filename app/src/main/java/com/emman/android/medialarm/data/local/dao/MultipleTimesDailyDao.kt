package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emman.android.medialarm.data.local.entities.MultipleTimesDailyEntity

@Dao
interface MultipleTimesDailyDao {

    @Query("SELECT * FROM multiple_times_daily WHERE schedule_id = :scheduleId")
    suspend fun getByScheduleId(scheduleId: Long): MultipleTimesDailyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(multipleTimesDaily: MultipleTimesDailyEntity): Long

    @Update
    suspend fun update(multipleTimesDaily: MultipleTimesDailyEntity)

    @Delete
    suspend fun delete(multipleTimesDaily: MultipleTimesDailyEntity)

}