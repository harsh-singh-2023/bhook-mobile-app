package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logged_meals")
data class LoggedMeal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val mealType: String = "Mess Meal", // e.g., "Mess Plate", "Hostel Snack", "Kettle Meal"
    val timestamp: Long = System.currentTimeMillis(),
    val hostelTip: String = ""
)
