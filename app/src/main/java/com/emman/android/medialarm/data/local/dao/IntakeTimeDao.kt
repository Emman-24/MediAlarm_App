package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.emman.android.medialarm.data.local.entities.IntakeTimeEntity

@Dao
interface IntakeTimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(intakeTime: IntakeTimeEntity): Long

}