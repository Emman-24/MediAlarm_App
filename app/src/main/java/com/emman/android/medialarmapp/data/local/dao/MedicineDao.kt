package com.emman.android.medialarmapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emman.android.medialarmapp.data.local.entities.MedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(medicine: MedicineEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(medicines: List<MedicineEntity>): List<Long>

    @Query("SELECT * FROM medicines WHERE id = :medicineId")
    fun observeMedicineById(medicineId: Long): Flow<MedicineEntity?>

    @Query(
        """
        SELECT * FROM medicines 
        WHERE is_active = 1 
        ORDER BY name ASC
    """
    )
    fun observeActiveMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines ORDER BY name ASC")
    fun observeAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :medicineId")
    suspend fun getMedicineById(medicineId: Long): MedicineEntity?

    @Query("""
        SELECT * FROM medicines 
        WHERE name LIKE '%' || :searchQuery || '%' 
        AND is_active = 1
        ORDER BY name ASC
    """)
    fun searchMedicines(searchQuery: String): Flow<List<MedicineEntity>>

    @Update
    suspend fun update(medicine: MedicineEntity)

    @Query("UPDATE medicines SET is_active = :isActive, updated_at = :updatedAt WHERE id = :medicineId")
    suspend fun updateActiveStatus(medicineId: Long, isActive: Boolean, updatedAt: Long)

    @Delete
    suspend fun delete(medicine: MedicineEntity)

    @Query("DELETE FROM medicines WHERE id = :medicineId")
    suspend fun deleteById(medicineId: Long)

    @Query("SELECT COUNT(*) FROM medicines WHERE is_active = 1")
    fun observeActiveMedicineCount(): Flow<Int>


}