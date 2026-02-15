package com.emman.android.medialarmapp.domain.usecases.medicine

import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import java.time.ZonedDateTime

class UpdateMedicineUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(medicine: Medicine): Result<Unit> {
        return try {
            repository.getMedicineById(medicine.id)
                ?: return Result.failure(NoSuchElementException("Medicine with id ${medicine.id} not found"))

            if (medicine.name.isBlank()) {
                return Result.failure(IllegalArgumentException("Medicine name cannot be blank"))
            }
            if (medicine.dosageAmount <= 0) {
                return Result.failure(IllegalArgumentException("Dosage amount must be positive"))
            }

            val updatedMedicine = medicine.copy(updatedAt = ZonedDateTime.now())
            repository.updateMedicine(updatedMedicine)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}