package com.emman.android.medialarm.domain.models

data class MedicineFormState(
    val medicineName: String = "",
    val dosage: String = "",
    val unit: String = "",
    val formType: String = "",
    val notes: String = "",
    var isActive: Boolean = true
)
