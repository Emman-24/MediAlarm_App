package com.emman.android.medialarmapp.domain.usecases.medicine

import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository

class DeleteMedicineUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(medicineId: String):Result<Unit>{
        return try {
            val medicine = repository.getMedicineById(medicineId) ?: return Result.failure(IllegalArgumentException("Medicine not found"))
            repository.deleteMedicine(medicineId)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}