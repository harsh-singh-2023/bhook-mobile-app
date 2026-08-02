package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val prepTimeMinutes: Int,
    val calories: Int,
    val difficulty: String, // Easy, Medium, Hard
    val dietaryTags: String, // Comma separated: "Vegetarian, Keto"
    val ingredients: String, // Pipe separated string: "2 Eggs|1/2 cup Spinach|30g Feta Cheese"
    val instructions: String, // Pipe separated step list: "Step 1 text|Step 2 text"
    val imageUrl: String = "",
    val isFavorite: Boolean = false
) {
    fun getDietaryList(): List<String> = dietaryTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    fun getIngredientsList(): List<String> = ingredients.split("|").map { it.trim() }.filter { it.isNotEmpty() }
    fun getInstructionsList(): List<String> = instructions.split("|").map { it.trim() }.filter { it.isNotEmpty() }
}
