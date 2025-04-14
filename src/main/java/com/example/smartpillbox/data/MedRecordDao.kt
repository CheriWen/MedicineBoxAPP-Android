package com.example.smartpillbox.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MedRecordDao {
    @Query("SELECT * FROM med_records ORDER BY time DESC")
    suspend fun getAll(): List<MedRecord>

    @Insert
    suspend fun insert(record: MedRecord)
}