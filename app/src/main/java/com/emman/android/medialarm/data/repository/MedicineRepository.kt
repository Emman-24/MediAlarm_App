package com.emman.android.medialarm.data.repository

import com.emman.android.medialarm.data.local.Data
import com.emman.android.medialarm.data.local.Medicine
import javax.inject.Inject

class MedicineRepository @Inject constructor(
    private val data: Data
) {

    fun getAllMedicines(): List<Medicine> {
        return data.listMedicines
    }

    fun getMedicineById(medicineId: Long): Medicine? {
        return data.listMedicines.find { it.id == medicineId }
    }

    fun getActiveMedicines(): List<Medicine> {
        return data.listMedicines.filter { it.isActive }
    }





}