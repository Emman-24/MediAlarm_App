package com.emman.android.medialarmapp.domain.usecases.medicine

import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository

class CreateMedicineUseCase(
    private val repository: ScheduleRepository,
) {
    suspend operator fun invoke(medicine: Medicine):Result<Long> {
        return try {
            if (medicine.name.isBlank()) {
                return Result.failure(IllegalArgumentException("Medicine name cannot be blank"))
            }
            if (medicine.dosageAmount <= 0) {
                return Result.failure(IllegalArgumentException("Dosage amount must be positive"))
            }
            repository.saveMedicine(medicine)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}