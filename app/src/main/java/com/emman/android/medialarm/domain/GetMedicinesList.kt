package com.emman.android.medialarm.domain

import com.emman.android.medialarm.data.repository.MedicineRepository
import javax.inject.Inject


class CheckMedicinesListEmpty @Inject constructor(
    private val repository: MedicineRepository
) {
    operator fun invoke(): Boolean {
        return repository.getAllMedicines().isEmpty()
    }
}