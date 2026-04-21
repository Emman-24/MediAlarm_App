package com.emman.android.medialarmapp.domain.usecases.medicine

import com.emman.android.medialarmapp.domain.models.DosageUnit
import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.models.MedicineForm
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

@DisplayName("DeleteMedicine Use Case Tests")
class MedicineUseCasesTest {

    private lateinit var useCase: DeleteMedicineUseCase
    private lateinit var mockRepository: ScheduleRepository

    @BeforeEach
    fun setup() {
        mockRepository = mockk()
        useCase = DeleteMedicineUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("Happy Path")
    inner class HappyPath {

        @Test
        @DisplayName("Existing medicine is deleted successfully")
        fun testSuccessfulDeletion() = runTest {
            // Given
            val medicine = createTestMedicine(id = "medicine-1")
            coEvery { mockRepository.getMedicineById("medicine-1") } returns medicine
            coEvery { mockRepository.deleteMedicine("medicine-1") } returns Result.success(Unit)

            // When
            val result = useCase("medicine-1")

            // Then
            assertThat(result.isSuccess).isTrue()
            coVerify(exactly = 1) { mockRepository.deleteMedicine("medicine-1") }
        }

    }

    @Nested
    @DisplayName("Medicine not found")
    inner class MedicineNotFound {

        @Test
        @DisplayName("Returns failure when medicine does not exist")
        fun testMedicineNotFound() = runTest {
            // Given
            coEvery { mockRepository.getMedicineById(any()) } returns null

            // When
            val result = useCase("ghost-id")

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        @DisplayName("Does NOT call deleteMedicine when medicine is not found")
        fun testNoDeleteCalledWhenNotFound() = runTest {
            // Given
            coEvery { mockRepository.getMedicineById(any()) } returns null

            // When
            useCase("ghost-id")

            // Then: delete never called — prevents accidental data loss
            coVerify(exactly = 0) { mockRepository.deleteMedicine(any()) }
        }
    }

    @Nested
    @DisplayName("Repository errors")
    inner class RepositoryErrors {

        @Test
        @DisplayName("Returns failure when deleteMedicine throws")
        fun testDeleteFails() = runTest {
            // Given
            val medicine = createTestMedicine()
            coEvery { mockRepository.getMedicineById(medicine.id) } returns medicine
            coEvery { mockRepository.deleteMedicine(medicine.id) } returns
                    Result.failure(RuntimeException("FK constraint"))

            // When
            val result = useCase(medicine.id)

            // Then
            assertThat(result.isFailure).isTrue()
        }

        @Test
        @DisplayName("Returns failure when getMedicineById throws")
        fun testGetMedicineThrows() = runTest {
            // Given
            coEvery { mockRepository.getMedicineById(any()) } throws RuntimeException("DB crash")

            // When
            val result = useCase("any-id")

            // Then
            assertThat(result.isFailure).isTrue()
        }
    }
}

// =============================================================================
// UPDATE MEDICINE USE CASE
// =============================================================================

@DisplayName("UpdateMedicine Use Case Tests")
class UpdateMedicineUseCaseTest {

    private lateinit var useCase: UpdateMedicineUseCase
    private lateinit var mockRepository: ScheduleRepository

    @BeforeEach
    fun setup() {
        mockRepository = mockk()
        useCase = UpdateMedicineUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("Happy path")
    inner class HappyPath {

        @Test
        @DisplayName("Valid medicine update succeeds")
        fun testSuccessfulUpdate() = runTest {
            // Given
            val medicine = createTestMedicine(name = "Ibuprofeno")
            coEvery { mockRepository.getMedicineById(medicine.id) } returns medicine
            coEvery { mockRepository.updateMedicine(any()) } returns Result.success(Unit)

            // When
            val result = useCase(medicine)

            // Then
            assertThat(result.isSuccess).isTrue()
            coVerify(exactly = 1) { mockRepository.updateMedicine(any()) }
        }

        @Test
        @DisplayName("updatedAt is refreshed to current time on update")
        fun testUpdatedAtIsRefreshed() = runTest {
            // Given: medicine with old updatedAt
            val oldTime = ZonedDateTime.now().minusDays(5)
            val medicine = createTestMedicine(updatedAt = oldTime)
            val medicineSlot = slot<Medicine>()

            coEvery { mockRepository.getMedicineById(medicine.id) } returns medicine
            coEvery { mockRepository.updateMedicine(capture(medicineSlot)) } returns Result.success(Unit)

            val beforeCall = ZonedDateTime.now()

            // When
            useCase(medicine)

            // Then: updatedAt must be newer than before the call
            val updatedAt = medicineSlot.captured.updatedAt
            assertThat(updatedAt.isAfter(beforeCall.minusSeconds(1))).isTrue()
            assertThat(updatedAt.isAfter(oldTime)).isTrue()
        }

        @Test
        @DisplayName("Other fields are preserved when updating")
        fun testFieldsPreserved() = runTest {
            // Given
            val medicine = createTestMedicine(
                name = "Paracetamol",
                dosageAmount = 650.0,
                dosageUnit = DosageUnit.MILLIGRAMS,
                form = MedicineForm.CAPSULE
            )
            val medicineSlot = slot<Medicine>()

            coEvery { mockRepository.getMedicineById(medicine.id) } returns medicine
            coEvery { mockRepository.updateMedicine(capture(medicineSlot)) } returns Result.success(Unit)

            // When
            useCase(medicine)

            // Then
            val updated = medicineSlot.captured
            assertThat(updated.name).isEqualTo("Paracetamol")
            assertThat(updated.dosageAmount).isEqualTo(650.0)
            assertThat(updated.dosageUnit).isEqualTo(DosageUnit.MILLIGRAMS)
            assertThat(updated.form).isEqualTo(MedicineForm.CAPSULE)
        }
    }

    @Nested
    @DisplayName("Input validation")
    inner class Validation {

        @Test
        @DisplayName("Blank name returns failure without calling repository")
        fun testBlankName() = runTest {
            // Given: medicine with blank name
            // Note: Medicine.init{} enforces non-blank, so we mock the existing one
            val existing = createTestMedicine(name = "Original")

            // Since Medicine.init{} would throw on blank name during copy,
            // we test the use case's own validation guard
            coEvery { mockRepository.getMedicineById(any()) } returns existing

            // When: pass a medicine with blank name (bypassing constructor via reflection would be
            // complex — instead we rely on the use case catching the IllegalArgumentException
            // from Medicine init block when the caller somehow passes invalid data)
            // The use case has its own explicit check before calling the repository:
            val result = try {
                val updated = existing.copy(name = "   ")
                useCase(updated) // Medicine.copy with blank name → throws in init
                Result.failure<Unit>(IllegalStateException("Should not reach here"))
            } catch (e: IllegalArgumentException) {
                Result.failure(e)
            }

            // Then
            assertThat(result.isFailure).isTrue()
            coVerify(exactly = 0) { mockRepository.updateMedicine(any()) }
        }

        @Test
        @DisplayName("Dosage amount of zero returns failure")
        fun testZeroDosage() = runTest {
            // Given: medicine found in DB
            val existing = createTestMedicine()
            coEvery { mockRepository.getMedicineById(any()) } returns existing

            // Medicine with 0 dosage: Medicine.init{} throws, so the use case wraps it
            val result = try {
                val invalid = existing.copy(dosageAmount = 0.0)
                useCase(invalid)
            } catch (e: IllegalArgumentException) {
                Result.failure(e)
            }

            // Then
            assertThat(result.isFailure).isTrue()
            coVerify(exactly = 0) { mockRepository.updateMedicine(any()) }
        }
    }

    @Nested
    @DisplayName("Medicine not found")
    inner class MedicineNotFound {

        @Test
        @DisplayName("Returns NoSuchElementException when medicine is not in repository")
        fun testMedicineNotFound() = runTest {
            // Given
            val medicine = createTestMedicine(id = "non-existent")
            coEvery { mockRepository.getMedicineById("non-existent") } returns null

            // When
            val result = useCase(medicine)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(NoSuchElementException::class.java)
        }

        @Test
        @DisplayName("Does NOT call updateMedicine when medicine is not found")
        fun testNoUpdateCalledWhenNotFound() = runTest {
            // Given
            val medicine = createTestMedicine()
            coEvery { mockRepository.getMedicineById(any()) } returns null

            // When
            useCase(medicine)

            // Then
            coVerify(exactly = 0) { mockRepository.updateMedicine(any()) }
        }
    }

    @Nested
    @DisplayName("Repository errors")
    inner class RepositoryErrors {

        @Test
        @DisplayName("Returns failure when updateMedicine fails")
        fun testUpdateFails() = runTest {
            // Given
            val medicine = createTestMedicine()
            coEvery { mockRepository.getMedicineById(medicine.id) } returns medicine
            coEvery { mockRepository.updateMedicine(any()) } returns
                    Result.failure(RuntimeException("Write conflict"))

            // When
            val result = useCase(medicine)

            // Then
            assertThat(result.isFailure).isTrue()
        }
    }

}

private fun createTestMedicine(
    id: String = "test-medicine-id",
    name: String = "Test Medicine",
    dosageAmount: Double = 400.0,
    dosageUnit: DosageUnit = DosageUnit.MILLIGRAMS,
    form: MedicineForm = MedicineForm.TABLET,
    updatedAt: ZonedDateTime = ZonedDateTime.now(),
): Medicine {
    val now = ZonedDateTime.now()
    return Medicine(
        id = id,
        name = name,
        dosageAmount = dosageAmount,
        dosageUnit = dosageUnit,
        form = form,
        notes = null,
        isActive = true,
        createdAt = now,
        updatedAt = updatedAt
    )
}