package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry_items ORDER BY addedDate DESC")
    fun getAllPantryItems(): Flow<List<PantryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PantryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PantryItem>)

    @Delete
    suspend fun deleteItem(item: PantryItem)

    @Query("DELETE FROM pantry_items")
    suspend fun clearAll()
}

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Int): RecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes")
    suspend fun clearAll()
}

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_list ORDER BY isChecked ASC, addedTimestamp DESC")
    fun getAllShoppingItems(): Flow<List<ShoppingListItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingListItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ShoppingListItem>)

    @Update
    suspend fun updateItem(item: ShoppingListItem)

    @Delete
    suspend fun deleteItem(item: ShoppingListItem)

    @Query("DELETE FROM shopping_list WHERE isChecked = 1")
    suspend fun deleteCheckedItems()

    @Query("DELETE FROM shopping_list")
    suspend fun clearAll()
}

@Dao
interface LoggedMealDao {
    @Query("SELECT * FROM logged_meals ORDER BY timestamp DESC")
    fun getAllLoggedMeals(): Flow<List<LoggedMeal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: LoggedMeal)

    @Delete
    suspend fun deleteMeal(meal: LoggedMeal)

    @Query("DELETE FROM logged_meals")
    suspend fun clearAll()
}
