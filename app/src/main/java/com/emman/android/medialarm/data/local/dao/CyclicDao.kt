package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emman.android.medialarm.data.local.entities.CyclicEntity

@Dao
interface CyclicDao {

    @Query("SELECT * FROM cyclic WHERE schedule_id = :scheduleId")
    suspend fun getByScheduleId(scheduleId: Long): CyclicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cyclic: CyclicEntity): Long

    @Update
    suspend fun update(cyclic: CyclicEntity)

    @Delete
    suspend fun delete(cyclic: CyclicEntity)


}