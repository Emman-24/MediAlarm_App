package com.emman.android.medialarm.domain.repository

import com.emman.android.medialarm.data.local.entities.MedicineEntity
import com.emman.android.medialarm.data.local.entities.ScheduleEntity
import com.emman.android.medialarm.data.local.relations.MedicineWithSchedules
import kotlinx.coroutines.flow.Flow

interface MedicineRepository {
    // Medicine operations
    suspend fun insertMedicine(medicine: MedicineEntity): Long
    suspend fun updateMedicine(medicine: MedicineEntity)
    suspend fun deleteMedicine(medicine: MedicineEntity)
    suspend fun deleteMedicineById(id: Long)
    suspend fun updateMedicineActiveStatus(id: Long, isActive: Boolean)
    fun getMedicineById(id: Long): Flow<MedicineEntity?>
    fun getAllMedicines(): Flow<List<MedicineEntity>>
    fun getAllActiveMedicines(): Flow<List<MedicineEntity>>
    
    // Schedule operations
    suspend fun insertSchedule(schedule: ScheduleEntity): Long
    suspend fun updateSchedule(schedule: ScheduleEntity)
    suspend fun deleteSchedule(schedule: ScheduleEntity)
    suspend fun deleteScheduleById(id: Long)
    suspend fun deleteSchedulesForMedicine(medicineId: Long)
    fun getScheduleById(id: Long): Flow<ScheduleEntity?>
    fun getSchedulesForMedicine(medicineId: Long): Flow<List<ScheduleEntity>>
    
    // Relationship operations
    fun getMedicineWithSchedules(id: Long): Flow<MedicineWithSchedules?>
    fun getAllMedicinesWithSchedules(): Flow<List<MedicineWithSchedules>>
    fun getAllActiveMedicinesWithSchedules(): Flow<List<MedicineWithSchedules>>
}