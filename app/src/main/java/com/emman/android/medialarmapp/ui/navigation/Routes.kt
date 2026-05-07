package com.emman.android.medialarmapp.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object MedicineList : Routes()

    @Serializable
    data class MedicineDetail(val medicineId: String) : Routes()

    @Serializable
    data class ScheduleCreation(
        val medicineId: String,
        val medicineName: String
    ):Routes()

    @Serializable
    data object AddMedicine : Routes()

    @Serializable
    data object Error : Routes()

}