package com.emman.android.medialarmapp.domain.usecases.medicine

import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import kotlinx.coroutines.flow.Flow

/**
 * A use case responsible for retrieving a stream of active medicines from the repository.
 *
 * This use case interacts with the underlying `ScheduleRepository` to observe and emit
 * a flow of medicines that are currently active. Active medicines are identified by their
 * `isActive` property being `true`.
 *
 * @constructor Initializes the use case with the given repository.
 * @param repository The repository used to fetch active medicines.
 *
 * @return A flow that emits a list of active medicines whenever there is a change in the data.
 */

class GetActiveMedicinesUseCase(
    private val repository : ScheduleRepository
) {
    operator fun invoke(): Flow<List<Medicine>> {
        return repository.observeActiveMedicines()
    }
}