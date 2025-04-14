package com.example.smartpillbox.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val expiryDate: String,
    val type: String,
    val slot: Int,
    val photo: Bitmap? = null
)