package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.emman.android.medialarm.data.local.entities.MedicineEntity
import com.emman.android.medialarm.data.local.relations.MedicineWithSchedules
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicines(medicines: List<MedicineEntity>): List<Long>

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)

    @Query("SELECT * FROM medicines WHERE id = :id")
    fun getMedicineById(id: Long): Flow<MedicineEntity?>

    @Query("SELECT * FROM medicines WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines ORDER BY name ASC")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Long)

    @Query("UPDATE medicines SET isActive = :isActive WHERE id = :id")
    suspend fun updateMedicineActiveStatus(id: Long, isActive: Boolean)

    @Transaction
    @Query("SELECT * FROM medicines WHERE id = :id")
    fun getMedicineWithSchedules(id: Long): Flow<MedicineWithSchedules>

    @Transaction
    @Query("SELECT * FROM medicines")
    fun getAllMedicinesWithSchedules(): Flow<List<MedicineWithSchedules>>

    @Transaction
    @Query("SELECT * FROM medicines WHERE isActive = 1")
    fun getAllActiveMedicinesWithSchedules(): Flow<List<MedicineWithSchedules>>
}
