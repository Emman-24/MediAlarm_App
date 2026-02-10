package com.emman.android.medialarmapp.data.local.converters

import androidx.room.TypeConverter
import com.emman.android.medialarmapp.domain.models.AlarmStatus
import com.emman.android.medialarmapp.domain.models.DosageUnit
import com.emman.android.medialarmapp.domain.models.MedicineForm

class Converters {
    @TypeConverter
    fun fromDosageUnit(value: DosageUnit): String {
        return value.name
    }

    @TypeConverter
    fun toDosageUnit(value: String): DosageUnit {
        return DosageUnit.valueOf(value)
    }

    @TypeConverter
    fun fromMedicineForm(value: MedicineForm): String {
        return value.name
    }

    @TypeConverter
    fun toMedicineForm(value: String): MedicineForm {
        return MedicineForm.valueOf(value)
    }

    @TypeConverter
    fun fromAlarmStatus(value: AlarmStatus): String {
        return value.name
    }

    @TypeConverter
    fun toAlarmStatus(value: String): AlarmStatus {
        return AlarmStatus.valueOf(value)
    }
}