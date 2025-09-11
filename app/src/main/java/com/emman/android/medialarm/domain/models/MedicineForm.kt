package com.emman.android.medialarm.domain.models

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