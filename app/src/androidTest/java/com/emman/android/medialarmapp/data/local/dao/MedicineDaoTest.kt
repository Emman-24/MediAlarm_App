package com.emman.android.medialarmapp.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.emman.android.medialarmapp.data.local.MediAlarmDatabase
import com.emman.android.medialarmapp.data.local.entities.MedicineEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MedicineDaoTest {
    private lateinit var database: MediAlarmDatabase
    private lateinit var medicineDao: MedicineDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MediAlarmDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        medicineDao = database.medicineDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertReturnsValidId() = runTest {
        // Given
        val medicine = createTestMedicine(name = "Ibuprofeno")

        // When
        val id = medicineDao.insert(medicine)

        // Then
        assertThat(id).isGreaterThan(0)
    }

    @Test
    fun testInsertMultiple() = runTest {
        // Given
        val medicines = listOf(
            createTestMedicine(name = "Med 1"),
            createTestMedicine(name = "Med 2"),
            createTestMedicine(name = "Med 3")
        )

        // When
        val ids = medicineDao.insertAll(medicines)

        // Then
        assertThat(ids).hasSize(3)
        assertThat(ids).containsNoDuplicates()
        ids.forEach { id ->
            assertThat(id).isGreaterThan(0)
        }
    }

    // ========== READ TESTS ==========

    @Test
    fun testObserveMedicineById() = runTest {
        // Given
        val medicine = createTestMedicine(name = "Aspirina")
        val id = medicineDao.insert(medicine)

        // When & Then
        medicineDao.observeMedicineById(id).test {
            val emitted = awaitItem()
            assertThat(emitted).isNotNull()
            assertThat(emitted?.name).isEqualTo("Aspirina")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testObserveMedicineByIdNotFound() = runTest {
        // When & Then
        medicineDao.observeMedicineById(999L).test {
            val emitted = awaitItem()
            assertThat(emitted).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testObserveActiveMedicines() = runTest {
        // Given
        medicineDao.insert(createTestMedicine(name = "Active 1", isActive = true))
        medicineDao.insert(createTestMedicine(name = "Inactive", isActive = false))
        medicineDao.insert(createTestMedicine(name = "Active 2", isActive = true))

        // When & Then
        medicineDao.observeActiveMedicines().test {
            val medicines = awaitItem()

            assertThat(medicines).hasSize(2)
            assertThat(medicines.map { it.name }).containsExactly("Active 1", "Active 2")
            assertThat(medicines.all { it.isActive }).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testObserveActiveMedicinesSorted() = runTest {
        // Given
        medicineDao.insert(createTestMedicine(name = "Zinc"))
        medicineDao.insert(createTestMedicine(name = "Aspirina"))
        medicineDao.insert(createTestMedicine(name = "Ibuprofeno"))

        // When & Then
        medicineDao.observeActiveMedicines().test {
            val medicines = awaitItem()

            assertThat(medicines.map { it.name })
                .containsExactly("Aspirina", "Ibuprofeno", "Zinc")
                .inOrder()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testSearchMedicines() = runTest {
        // Given
        medicineDao.insert(createTestMedicine(name = "Ibuprofeno"))
        medicineDao.insert(createTestMedicine(name = "Paracetamol"))
        medicineDao.insert(createTestMedicine(name = "Aspirina"))

        // When & Then
        medicineDao.searchMedicines("ibu").test {
            val results = awaitItem()

            assertThat(results).hasSize(1)
            assertThat(results[0].name).isEqualTo("Ibuprofeno")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testSearchMedicinesCaseInsensitive() = runTest {
        // Given
        medicineDao.insert(createTestMedicine(name = "Ibuprofeno"))

        // When & Then
        medicineDao.searchMedicines("IBU").test {
            val results = awaitItem()
            assertThat(results).hasSize(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== UPDATE TESTS ==========

    @Test
    fun testUpdate() = runTest {
        // Given
        val original = createTestMedicine(name = "Original Name")
        val id = medicineDao.insert(original)

        // When
        val updated = original.copy(
            id = id,
            name = "Updated Name",
            dosageAmount = 999.0
        )
        medicineDao.update(updated)

        // Then
        val retrieved = medicineDao.getMedicineById(id)
        assertThat(retrieved?.name).isEqualTo("Updated Name")
        assertThat(retrieved?.dosageAmount).isEqualTo(999.0)
    }

    @Test
    fun testUpdateActiveStatus() = runTest {
        // Given
        val medicine = createTestMedicine(name = "Test Med", isActive = true)
        val id = medicineDao.insert(medicine)

        // When
        medicineDao.updateActiveStatus(id, false, System.currentTimeMillis())

        // Then
        val retrieved = medicineDao.getMedicineById(id)
        assertThat(retrieved?.isActive).isFalse()
        assertThat(retrieved?.name).isEqualTo("Test Med")  // Other fields unchanged
    }

    // ========== DELETE TESTS ==========

    @Test
    fun testDelete() = runTest {
        // Given
        val medicine = createTestMedicine(name = "To Delete")
        val id = medicineDao.insert(medicine)

        // Verify it exists
        assertThat(medicineDao.getMedicineById(id)).isNotNull()

        // When
        medicineDao.delete(medicine.copy(id = id))

        // Then
        assertThat(medicineDao.getMedicineById(id)).isNull()
    }

    @Test
    fun testDeleteById() = runTest {
        // Given
        val id = medicineDao.insert(createTestMedicine(name = "To Delete"))

        // When
        medicineDao.deleteById(id)

        // Then
        assertThat(medicineDao.getMedicineById(id)).isNull()
    }

    // ========== STATISTICS TESTS ==========

    @Test
    fun testObserveActiveMedicineCount() = runTest {
        // Given
        medicineDao.insert(createTestMedicine(name = "Active 1", isActive = true))
        medicineDao.insert(createTestMedicine(name = "Inactive", isActive = false))
        medicineDao.insert(createTestMedicine(name = "Active 2", isActive = true))

        // When & Then
        medicineDao.observeActiveMedicineCount().test {
            val count = awaitItem()
            assertThat(count).isEqualTo(2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testObserveActiveMedicineCountReactive() = runTest {
        // Given
        medicineDao.observeActiveMedicineCount().test {
            // Initial count
            assertThat(awaitItem()).isEqualTo(0)

            // Insert one
            val id1 = medicineDao.insert(createTestMedicine(name = "Med 1"))
            assertThat(awaitItem()).isEqualTo(1)

            // Insert another
            val id2 = medicineDao.insert(createTestMedicine(name = "Med 2"))
            assertThat(awaitItem()).isEqualTo(2)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== HELPER METHODS ==========

    private fun createTestMedicine(
        name: String,
        dosageAmount: Double = 400.0,
        dosageUnit: String = "MILLIGRAMS",
        formType: String = "TABLET",
        notes: String? = null,
        isActive: Boolean = true,
    ): MedicineEntity {
        val now = System.currentTimeMillis()
        return MedicineEntity(
            id = 0,  // Auto-generate
            name = name,
            dosageAmount = dosageAmount,
            dosageUnit = dosageUnit,
            formType = formType,
            notes = notes,
            isActive = isActive,
            createdAt = now,
            updatedAt = now
        )
    }
}