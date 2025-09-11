package com.emman.android.medialarm.data.repository

import com.emman.android.medialarm.data.local.dao.MedicineDao
import com.emman.android.medialarm.data.local.dao.ScheduleDao
import com.emman.android.medialarm.data.local.entities.MedicineEntity
import com.emman.android.medialarm.data.local.entities.ScheduleEntity
import com.emman.android.medialarm.data.local.relations.MedicineWithSchedules
import com.emman.android.medialarm.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MedicineRepositoryImpl @Inject constructor(
    private val medicineDao: MedicineDao,
    private val scheduleDao: ScheduleDao
) : MedicineRepository {
    
    // Medicine operations
    override suspend fun insertMedicine(medicine: MedicineEntity): Long {
        return medicineDao.insertMedicine(medicine)
    }
    
    override suspend fun updateMedicine(medicine: MedicineEntity) {
        medicineDao.updateMedicine(medicine)
    }
    
    override suspend fun deleteMedicine(medicine: MedicineEntity) {
        medicineDao.deleteMedicine(medicine)
    }
    
    override suspend fun deleteMedicineById(id: Long) {
        medicineDao.deleteMedicineById(id)
    }
    
    override suspend fun updateMedicineActiveStatus(id: Long, isActive: Boolean) {
        medicineDao.updateMedicineActiveStatus(id, isActive)
    }
    
    override fun getMedicineById(id: Long): Flow<MedicineEntity?> {
        return medicineDao.getMedicineById(id)
    }
    
    override fun getAllMedicines(): Flow<List<MedicineEntity>> {
        return medicineDao.getAllMedicines()
    }
    
    override fun getAllActiveMedicines(): Flow<List<MedicineEntity>> {
        return medicineDao.getAllActiveMedicines()
    }
    
    // Schedule operations
    override suspend fun insertSchedule(schedule: ScheduleEntity): Long {
        return scheduleDao.insertSchedule(schedule)
    }
    
    override suspend fun updateSchedule(schedule: ScheduleEntity) {
        scheduleDao.updateSchedule(schedule)
    }
    
    override suspend fun deleteSchedule(schedule: ScheduleEntity) {
        scheduleDao.deleteSchedule(schedule)
    }
    
    override suspend fun deleteScheduleById(id: Long) {
        scheduleDao.deleteScheduleById(id)
    }
    
    override suspend fun deleteSchedulesForMedicine(medicineId: Long) {
        scheduleDao.deleteSchedulesForMedicine(medicineId)
    }
    
    override fun getScheduleById(id: Long): Flow<ScheduleEntity?> {
        return scheduleDao.getScheduleById(id)
    }
    
    override fun getSchedulesForMedicine(medicineId: Long): Flow<List<ScheduleEntity>> {
        return scheduleDao.getSchedulesForMedicine(medicineId)
    }
    
    // Relationship operations
    override fun getMedicineWithSchedules(id: Long): Flow<MedicineWithSchedules> {
        return medicineDao.getMedicineWithSchedules(id)
    }
    
    override fun getAllMedicinesWithSchedules(): Flow<List<MedicineWithSchedules>> {
        return medicineDao.getAllMedicinesWithSchedules()
    }
    
    override fun getAllActiveMedicinesWithSchedules(): Flow<List<MedicineWithSchedules>> {
        return medicineDao.getAllActiveMedicinesWithSchedules()
    }
}