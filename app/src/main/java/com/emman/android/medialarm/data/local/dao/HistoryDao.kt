package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emman.android.medialarm.data.local.entities.HistoryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(histories: List<HistoryEntity>): List<Long>

    @Update
    suspend fun updateHistory(history: HistoryEntity)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("SELECT * FROM history WHERE id = :id")
    fun getHistoryById(id: Long): Flow<HistoryEntity?>

    @Query("SELECT * FROM history WHERE medicine_id = :medicineId ORDER BY created_at DESC")
    fun getHistoryByMedicineId(medicineId: Long): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE schedule_id = :scheduleId ORDER BY created_at DESC")
    fun getHistoryByScheduleId(scheduleId: Long): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history ORDER BY created_at DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    fun getHistoryBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<HistoryEntity>>

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM history WHERE medicine_id = :medicineId")
    suspend fun deleteHistoryByMedicineId(medicineId: Long)

    @Query("DELETE FROM history WHERE schedule_id = :scheduleId")
    suspend fun deleteHistoryByScheduleId(scheduleId: Long)
}