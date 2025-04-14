package com.example.smartpillbox.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "med_records")
data class MedRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val time: String,
    val status: String,
    val period: Int,
    val dosage: Int
)