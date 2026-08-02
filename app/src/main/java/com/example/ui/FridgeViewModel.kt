package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PantryItem
import com.example.data.local.RecipeEntity
import com.example.data.local.SampleData
import com.example.data.local.SampleFridgePreset
import com.example.data.local.ShoppingListItem
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption {
    MATCH_COUNT, PREP_TIME, CALORIES, DIFFICULTY
}

class FridgeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val pantryDao = db.pantryDao()
    private val recipeDao = db.recipeDao()
    private val shoppingListDao = db.shoppingListDao()
    private val loggedMealDao = db.loggedMealDao()

    private val geminiService = GeminiService()

    // Database flows
    val pantryItems: StateFlow<List<PantryItem>> = pantryDao.getAllPantryItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawRecipes: StateFlow<List<RecipeEntity>> = recipeDao.getAllRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingList: StateFlow<List<ShoppingListItem>> = shoppingListDao.getAllShoppingItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loggedMeals: StateFlow<List<com.example.data.local.LoggedMeal>> = loggedMealDao.getAllLoggedMeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily Macro Totals
    val totalCaloriesToday: StateFlow<Int> = loggedMeals.combine(MutableStateFlow(0)) { meals, _ ->
        meals.sumOf { it.calories }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalProteinToday: StateFlow<Int> = loggedMeals.combine(MutableStateFlow(0)) { meals, _ ->
        meals.sumOf { it.proteinGrams }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCarbsToday: StateFlow<Int> = loggedMeals.combine(MutableStateFlow(0)) { meals, _ ->
        meals.sumOf { it.carbsGrams }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalFatToday: StateFlow<Int> = loggedMeals.combine(MutableStateFlow(0)) { meals, _ ->
        meals.sumOf { it.fatGrams }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // UI State for Filters & Search
    private val _selectedDietaryFilters = MutableStateFlow<Set<String>>(emptySet())
    val selectedDietaryFilters: StateFlow<Set<String>> = _selectedDietaryFilters.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSortOption = MutableStateFlow(SortOption.MATCH_COUNT)
    val selectedSortOption: StateFlow<SortOption> = _selectedSortOption.asStateFlow()

    // Photo Analysis UI State
    private val _isAnalyzingPhoto = MutableStateFlow(false)
    val isAnalyzingPhoto: StateFlow<Boolean> = _isAnalyzingPhoto.asStateFlow()

    private val _analysisMessage = MutableStateFlow<String?>(null)
    val analysisMessage: StateFlow<String?> = _analysisMessage.asStateFlow()

    private val _isGeneratingRecipes = MutableStateFlow(false)
    val isGeneratingRecipes: StateFlow<Boolean> = _isGeneratingRecipes.asStateFlow()

    // Mess Plate Nutrition Scan State
    private val _isScanningMessPlate = MutableStateFlow(false)
    val isScanningMessPlate: StateFlow<Boolean> = _isScanningMessPlate.asStateFlow()

    private val _lastMessScanResult = MutableStateFlow<com.example.data.remote.MessNutritionResult?>(null)
    val lastMessScanResult: StateFlow<com.example.data.remote.MessNutritionResult?> = _lastMessScanResult.asStateFlow()

    // Daily Protein Target State (Persisted)
    private val _dailyProteinTarget = MutableStateFlow(70)
    val dailyProteinTarget: StateFlow<Int> = _dailyProteinTarget.asStateFlow()

    fun setDailyProteinTarget(target: Int) {
        if (target > 0) {
            _dailyProteinTarget.value = target
            try {
                val prefs = getApplication<Application>().getSharedPreferences("bhook_prefs", Context.MODE_PRIVATE)
                prefs.edit().putInt("daily_protein_target", target).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // Filtered & Sorted Recipes
    val filteredRecipes: StateFlow<List<RecipeEntity>> = combine(
        rawRecipes,
        pantryItems,
        _selectedDietaryFilters,
        _searchQuery,
        _selectedSortOption
    ) { recipes, pantry, dietaryFilters, query, sort ->
        val pantryNames = pantry.map { it.name.lowercase().trim() }

        var result = recipes.filter { recipe ->
            // Search query filter
            val matchesQuery = query.isBlank() ||
                    recipe.title.contains(query, ignoreCase = true) ||
                    recipe.description.contains(query, ignoreCase = true) ||
                    recipe.ingredients.contains(query, ignoreCase = true)

            // Dietary filter (All selected filters must match)
            val recipeTags = recipe.getDietaryList().map { it.lowercase().trim() }
            val matchesDietary = dietaryFilters.isEmpty() || dietaryFilters.all { filter ->
                recipeTags.any { tag -> tag.contains(filter.lowercase().trim()) }
            }

            matchesQuery && matchesDietary
        }

        // Sorting
        result = when (sort) {
            SortOption.MATCH_COUNT -> result.sortedByDescending { recipe ->
                recipe.getIngredientsList().count { ingredient ->
                    pantryNames.any { pantryItem -> ingredient.lowercase().contains(pantryItem) }
                }
            }
            SortOption.PREP_TIME -> result.sortedBy { it.prepTimeMinutes }
            SortOption.CALORIES -> result.sortedBy { it.calories }
            SortOption.DIFFICULTY -> result.sortedBy {
                when (it.difficulty.lowercase()) {
                    "easy" -> 1
                    "medium" -> 2
                    "hard" -> 3
                    else -> 4
                }
            }
        }

        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Load saved daily protein target preference
        try {
            val prefs = application.getSharedPreferences("bhook_prefs", Context.MODE_PRIVATE)
            val savedTarget = prefs.getInt("daily_protein_target", 70)
            _dailyProteinTarget.value = savedTarget
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Seed default recipes and sample pantry items if empty
        viewModelScope.launch {
            pantryDao.getAllPantryItems().collect { items ->
                if (items.isEmpty()) {
                    val defaultPantry = listOf(
                        PantryItem(name = "Eggs", category = "Protein", isFromPhoto = true),
                        PantryItem(name = "Spinach", category = "Vegetables", isFromPhoto = true),
                        PantryItem(name = "Feta Cheese", category = "Dairy", isFromPhoto = true),
                        PantryItem(name = "Tomatoes", category = "Vegetables", isFromPhoto = true),
                        PantryItem(name = "Garlic", category = "Condiment", isFromPhoto = true)
                    )
                    pantryDao.insertItems(defaultPantry)
                }
            }
        }

        viewModelScope.launch {
            recipeDao.getAllRecipes().collect { recipes ->
                if (recipes.isEmpty()) {
                    recipeDao.insertRecipes(SampleData.defaultRecipes)
                }
            }
        }
    }

    // Photo Analysis with AI
    fun analyzeFridgePhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            _isAnalyzingPhoto.value = true
            _analysisMessage.value = "AI is scanning your fridge photo..."

            val result = geminiService.analyzeFridgeImage(bitmap)
            _isAnalyzingPhoto.value = false

            result.onSuccess { ingredients ->
                if (ingredients.isNotEmpty()) {
                    val newItems = ingredients.map { name ->
                        PantryItem(name = name, isFromPhoto = true)
                    }
                    pantryDao.insertItems(newItems)
                    _analysisMessage.value = "Detected ${ingredients.size} ingredients! Recipes updated."
                } else {
                    _analysisMessage.value = "No ingredients clearly spotted. Try adjusting photo lighting."
                }
            }.onFailure { error ->
                _analysisMessage.value = "AI Scan Notice: ${error.localizedMessage ?: "Loaded preset sample ingredients."}"
            }
        }
    }

    fun selectPresetFridge(preset: SampleFridgePreset) {
        viewModelScope.launch {
            pantryDao.clearAll()
            val newItems = preset.ingredients.map { PantryItem(name = it, category = "Fridge Preset", isFromPhoto = true) }
            pantryDao.insertItems(newItems)
            _analysisMessage.value = "Loaded '${preset.title}' into your virtual fridge."
        }
    }

    fun addPantryItem(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            pantryDao.insertItem(PantryItem(name = name.trim()))
        }
    }

    fun removePantryItem(item: PantryItem) {
        viewModelScope.launch {
            pantryDao.deleteItem(item)
        }
    }

    fun clearPantry() {
        viewModelScope.launch {
            pantryDao.clearAll()
        }
    }

    // Dietary Filter Management
    fun toggleDietaryFilter(tag: String) {
        val current = _selectedDietaryFilters.value.toMutableSet()
        if (current.contains(tag)) {
            current.remove(tag)
        } else {
            current.add(tag)
        }
        _selectedDietaryFilters.value = current
    }

    fun clearDietaryFilters() {
        _selectedDietaryFilters.value = emptySet()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortOption(sort: SortOption) {
        _selectedSortOption.value = sort
    }

    // AI Recipe Generation
    fun generateAiRecipesFromFridge() {
        viewModelScope.launch {
            val currentPantry = pantryItems.value.map { it.name }
            if (currentPantry.isEmpty()) {
                _analysisMessage.value = "Add or snap ingredients first to generate recipes!"
                return@launch
            }

            _isGeneratingRecipes.value = true
            _analysisMessage.value = "Chef AI is creating recipes from your fridge ingredients..."

            val result = geminiService.generateAiRecipes(
                availableIngredients = currentPantry,
                dietaryRestrictions = selectedDietaryFilters.value.toList()
            )
            _isGeneratingRecipes.value = false

            result.onSuccess { newRecipes ->
                recipeDao.insertRecipes(newRecipes)
                _analysisMessage.value = "Created ${newRecipes.size} custom AI recipes!"
            }.onFailure { error ->
                _analysisMessage.value = "Recipe Note: ${error.localizedMessage ?: "Using offline recipes."}"
            }
        }
    }

    // Shopping List Management
    fun addMissingIngredientsToShoppingList(recipe: RecipeEntity) {
        viewModelScope.launch {
            val pantryNames = pantryItems.value.map { it.name.lowercase().trim() }
            val missingIngredients = recipe.getIngredientsList().filter { ingredient ->
                pantryNames.none { pantryItem -> ingredient.lowercase().contains(pantryItem) }
            }

            val shoppingItems = missingIngredients.map { name ->
                ShoppingListItem(
                    name = name,
                    quantity = "1 recipe portion",
                    recipeTitle = recipe.title
                )
            }

            if (shoppingItems.isNotEmpty()) {
                shoppingListDao.insertItems(shoppingItems)
                _analysisMessage.value = "Added ${shoppingItems.size} missing items to Shopping List!"
            } else {
                _analysisMessage.value = "You already have all ingredients in your fridge!"
            }
        }
    }

    fun addCustomShoppingItem(name: String, quantity: String = "1 unit") {
        if (name.isBlank()) return
        viewModelScope.launch {
            shoppingListDao.insertItem(ShoppingListItem(name = name.trim(), quantity = quantity))
        }
    }

    fun toggleShoppingItem(item: ShoppingListItem) {
        viewModelScope.launch {
            shoppingListDao.updateItem(item.copy(isChecked = !item.isChecked))
        }
    }

    fun deleteShoppingItem(item: ShoppingListItem) {
        viewModelScope.launch {
            shoppingListDao.deleteItem(item)
        }
    }

    fun clearCheckedShoppingItems() {
        viewModelScope.launch {
            shoppingListDao.deleteCheckedItems()
        }
    }

    fun toggleFavoriteRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            recipeDao.updateRecipe(recipe.copy(isFavorite = !recipe.isFavorite))
        }
    }

    fun scanMessPlatePhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            _isScanningMessPlate.value = true
            _analysisMessage.value = "AI is analyzing your mess food plate & protein content..."

            val result = geminiService.analyzeMessPlateOrFood(bitmap)
            _isScanningMessPlate.value = false

            result.onSuccess { nutritionResult ->
                _lastMessScanResult.value = nutritionResult
                _analysisMessage.value = "Analyzed '${nutritionResult.mealName}': ${nutritionResult.totalProteinGrams}g Protein, ${nutritionResult.totalCalories} kcal!"
            }.onFailure { error ->
                _analysisMessage.value = "Scan note: ${error.localizedMessage ?: "Could not analyze plate. Try better lighting."}"
            }
        }
    }

    fun saveScannedMessMealToTracker(result: com.example.data.remote.MessNutritionResult) {
        viewModelScope.launch {
            val meal = com.example.data.local.LoggedMeal(
                name = result.mealName,
                calories = result.totalCalories,
                proteinGrams = result.totalProteinGrams,
                carbsGrams = result.totalCarbsGrams,
                fatGrams = result.totalFatGrams,
                mealType = "Mess Plate",
                hostelTip = result.hostelNutrientTip
            )
            loggedMealDao.insertMeal(meal)
            _analysisMessage.value = "Logged '${result.mealName}' (+${result.totalProteinGrams}g protein) to Daily Tracker!"
            _lastMessScanResult.value = null
        }
    }

    fun logCustomMeal(
        name: String,
        protein: Int,
        carbs: Int,
        fat: Int,
        calories: Int,
        mealType: String = "Hostel Meal"
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val meal = com.example.data.local.LoggedMeal(
                name = name.trim(),
                calories = if (calories > 0) calories else (protein * 4 + carbs * 4 + fat * 9),
                proteinGrams = protein,
                carbsGrams = carbs,
                fatGrams = fat,
                mealType = mealType,
                hostelTip = "Custom logged meal"
            )
            loggedMealDao.insertMeal(meal)
            _analysisMessage.value = "Logged '$name' (+${protein}g protein)!"
        }
    }

    fun deleteLoggedMeal(meal: com.example.data.local.LoggedMeal) {
        viewModelScope.launch {
            loggedMealDao.deleteMeal(meal)
            _analysisMessage.value = "Removed '${meal.name}'"
        }
    }

    fun clearAllLoggedMeals() {
        viewModelScope.launch {
            loggedMealDao.clearAll()
            _analysisMessage.value = "Cleared today's meal history."
        }
    }

    fun dismissMessScanResult() {
        _lastMessScanResult.value = null
    }

    // User Profile, Session Persistence & Onboarding State
    private val prefs = application.getSharedPreferences("bhook_auth_prefs", android.content.Context.MODE_PRIVATE)

    private val _userName = MutableStateFlow(prefs.getString("user_name", "Google Student") ?: "Google Student")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(prefs.getString("user_email", "student@gmail.com") ?: "student@gmail.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userAvatar = MutableStateFlow(prefs.getString("user_avatar", "BOY") ?: "BOY")
    val userAvatar: StateFlow<String> = _userAvatar.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", false))
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(prefs.getBoolean("has_completed_onboarding", false))

    private val _showOnboarding = MutableStateFlow(!_isUserLoggedIn.value)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    fun signInWithGoogle(name: String, email: String) {
        _userName.value = name
        _userEmail.value = email
        _isUserLoggedIn.value = true
        _hasCompletedOnboarding.value = true
        _showOnboarding.value = false

        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putBoolean("has_completed_onboarding", true)
            .putString("user_name", name)
            .putString("user_email", email)
            .apply()

        _analysisMessage.value = "Signed in as $name"
    }

    fun setUserAvatar(avatarType: String) {
        val validAvatar = if (avatarType == "GIRL") "GIRL" else "BOY"
        _userAvatar.value = validAvatar
        prefs.edit().putString("user_avatar", validAvatar).apply()
    }

    fun logout() {
        try {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            // Ignore if Firebase Auth is not initialized
        }
        _isUserLoggedIn.value = false
        _showOnboarding.value = true

        prefs.edit()
            .putBoolean("is_logged_in", false)
            .remove("user_name")
            .remove("user_email")
            .apply()

        _analysisMessage.value = "Logged out successfully"
    }

    fun triggerOnboarding() {
        _showOnboarding.value = true
    }

    fun insertCustomRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            recipeDao.insertRecipe(recipe)
            _analysisMessage.value = "Saved '${recipe.title}' to your recipes!"
        }
    }

    fun clearAnalysisMessage() {
        _analysisMessage.value = null
    }
}


