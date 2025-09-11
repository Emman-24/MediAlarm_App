package com.emman.android.medialarm.domain.models

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
