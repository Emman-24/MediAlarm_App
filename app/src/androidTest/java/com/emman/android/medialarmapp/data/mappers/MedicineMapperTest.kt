package com.emman.android.medialarmapp.data.mappers

import com.emman.android.medialarmapp.data.local.entities.MedicineEntity
import com.emman.android.medialarmapp.domain.models.DosageUnit
import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.models.MedicineForm
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime


class MedicineMapperTest {

    private val testZone = ZoneId.of("America/Bogota")

    @Test
    fun testEntityToDomain() {
        // Given
        val entity = MedicineEntity(
            id = 1L,
            name = "Ibuprofeno",
            dosageAmount = 400.0,
            dosageUnit = "MILLIGRAMS",
            formType = "TABLET",
            notes = "Tomar con comida",
            isActive = true,
            createdAt = 1704067200000L,  // 2024-01-01 00:00:00 UTC
            updatedAt = 1704067200000L
        )

        // When
        val domain = entity.toDomain(testZone)

        // Then
        assertThat(domain.id).isEqualTo("1")
        assertThat(domain.name).isEqualTo("Ibuprofeno")
        assertThat(domain.dosageAmount).isEqualTo(400.0)
        assertThat(domain.dosageUnit).isEqualTo(DosageUnit.MILLIGRAMS)
        assertThat(domain.form).isEqualTo(MedicineForm.TABLET)
        assertThat(domain.notes).isEqualTo("Tomar con comida")
        assertThat(domain.isActive).isTrue()
    }

    @Test
    fun testDomainToEntity() {
        // Given
        val now = ZonedDateTime.now(testZone)
        val domain = Medicine(
            id = "1",
            name = "Aspirina",
            dosageAmount = 500.0,
            dosageUnit = DosageUnit.MILLIGRAMS,
            form = MedicineForm.TABLET,
            notes = "Para dolor de cabeza",
            isActive = true,
            createdAt = now,
            updatedAt = now
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertThat(entity.id).isEqualTo(1L)
        assertThat(entity.name).isEqualTo("Aspirina")
        assertThat(entity.dosageAmount).isEqualTo(500.0)
        assertThat(entity.dosageUnit).isEqualTo("MILLIGRAMS")
        assertThat(entity.formType).isEqualTo("TABLET")
        assertThat(entity.notes).isEqualTo("Para dolor de cabeza")
        assertThat(entity.isActive).isTrue()
    }

    @Test
    fun testRoundTripConversion() {
        // Given
        val now = ZonedDateTime.now(testZone)
        val originalDomain = Medicine(
            id = "42",
            name = "Paracetamol",
            dosageAmount = 650.0,
            dosageUnit = DosageUnit.MILLIGRAMS,
            form = MedicineForm.CAPSULE,
            notes = null,
            isActive = true,
            createdAt = now,
            updatedAt = now
        )

        // When: Domain → Entity → Domain
        val entity = originalDomain.toEntity()
        val roundTripDomain = entity.toDomain(testZone)

        // Then: Should be equivalent (ignoring minor timestamp precision)
        assertThat(roundTripDomain.name).isEqualTo(originalDomain.name)
        assertThat(roundTripDomain.dosageAmount).isEqualTo(originalDomain.dosageAmount)
        assertThat(roundTripDomain.dosageUnit).isEqualTo(originalDomain.dosageUnit)
        assertThat(roundTripDomain.form).isEqualTo(originalDomain.form)
        assertThat(roundTripDomain.isActive).isEqualTo(originalDomain.isActive)
    }

    @Test
    fun testListConversion() {
        // Given
        val entities = listOf(
            MedicineEntity(
                id = 1L,
                name = "Med 1",
                dosageAmount = 100.0,
                dosageUnit = "MILLIGRAMS",
                formType = "TABLET",
                isActive = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            MedicineEntity(
                id = 2L,
                name = "Med 2",
                dosageAmount = 200.0,
                dosageUnit = "MILLIGRAMS",
                formType = "CAPSULE",
                isActive = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )

        // When
        val domainList = entities.toDomain(testZone)

        // Then
        assertThat(domainList).hasSize(2)
        assertThat(domainList[0].name).isEqualTo("Med 1")
        assertThat(domainList[0].isActive).isTrue()
        assertThat(domainList[1].name).isEqualTo("Med 2")
        assertThat(domainList[1].isActive).isFalse()
    }

    @Test
    fun testNullNotesHandling() {
        // Given
        val entity = MedicineEntity(
            id = 1L,
            name = "Test Med",
            dosageAmount = 100.0,
            dosageUnit = "MILLIGRAMS",
            formType = "TABLET",
            notes = null,  // Null notes
            isActive = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        // When
        val domain = entity.toDomain(testZone)

        // Then
        assertThat(domain.notes).isNull()
    }

    @Test
    fun testAllDosageUnits() {
        DosageUnit.entries.forEach { unit ->
            // Given
            val entity = MedicineEntity(
                id = 1L,
                name = "Test",
                dosageAmount = 100.0,
                dosageUnit = unit.name,
                formType = "TABLET",
                isActive = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            // When
            val domain = entity.toDomain(testZone)

            // Then
            assertThat(domain.dosageUnit).isEqualTo(unit)
        }
    }

    @Test
    fun testAllMedicineForms() {
        MedicineForm.entries.forEach { form ->
            // Given
            val entity = MedicineEntity(
                id = 1L,
                name = "Test",
                dosageAmount = 100.0,
                dosageUnit = "MILLIGRAMS",
                formType = form.name,
                isActive = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            // When
            val domain = entity.toDomain(testZone)

            // Then
            assertThat(domain.form).isEqualTo(form)
        }
    }
}