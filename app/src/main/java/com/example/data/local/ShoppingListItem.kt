package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: String = "1 item",
    val recipeTitle: String = "",
    val isChecked: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis()
)
