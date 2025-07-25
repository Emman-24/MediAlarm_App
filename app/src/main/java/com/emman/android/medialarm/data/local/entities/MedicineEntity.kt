package com.emman.android.medialarm.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "medicines"
)
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dosageAmount: Double,
    val dosageUnit: DosageUnit,
    val formType: MedicineForm,
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)

enum class MedicineForm(val displayName: String) {
    TABLET("Tablet"),
    CAPSULE("Capsule"),
    PILL("Pill"),
    POWDER("Powder"),
    GRANULES("Granules"),
    LOZENGE("Lozenge"),

    LIQUID("Liquid"),
    SYRUP("Syrup"),
    SUSPENSION("Suspension"),
    DROPS("Drops"),
    INJECTION("Injection"),

    OINTMENT("Ointment"),
    CREAM("Cream"),
    GEL("Gel"),
    LOTION("Lotion"),
    FOAM("Foam"),
    PATCH("Patch"),

    INHALER("Inhaler"),
    SPRAY("Spray"),
    NEBULIZER("Nebulizer Solution"),

    SUPPOSITORY("Suppository"),
    IMPLANT("Implant")
}


enum class DosageUnit(val symbol: String, val displayName: String) {
    MILLIGRAM("mg", "Milligrams"),
    GRAM("g", "Grams"),
    MILLILITER("ml", "Milliliters"),
    LITER("l", "Liters"),
    INTERNATIONAL_UNIT("IU", "International Units"),
    MICROGRAM("mcg", "Micrograms"),
    PERCENT("%", "Percent"),
    DROPS("drops", "Drops"),
    PUFF("puff", "Puffs")
}
