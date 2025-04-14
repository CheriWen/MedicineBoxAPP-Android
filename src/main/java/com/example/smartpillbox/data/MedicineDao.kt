package com.example.smartpillbox.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines")
    suspend fun getAll(): List<Medicine>

    @Insert
    suspend fun insert(medicine: Medicine)

    @Delete
    suspend fun delete(medicine: Medicine)
}