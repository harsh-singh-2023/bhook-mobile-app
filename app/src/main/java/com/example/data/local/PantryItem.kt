package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantry_items")
data class PantryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String = "General",
    val quantity: String = "1",
    val isFromPhoto: Boolean = false,
    val addedDate: Long = System.currentTimeMillis()
)
