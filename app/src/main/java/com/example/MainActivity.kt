package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.example.ui.theme.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.RecipeEntity
import com.example.ui.FridgeViewModel
import com.example.ui.screens.CookingModeScreen
import com.example.ui.screens.FridgeScanScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.RecipeDetailScreen
import com.example.ui.screens.RecipeListScreen
import com.example.ui.screens.ShoppingListScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme

enum class BottomTab {
    FRIDGE_SCAN, RECIPES, SHOPPING_LIST
}

sealed interface ScreenState {
    data object Onboarding : ScreenState
    data class CookingMode(val recipe: RecipeEntity) : ScreenState
    data class RecipeDetail(val recipe: RecipeEntity) : ScreenState
    data object MainApp : ScreenState
}

class MainActivity : ComponentActivity() {
    private val viewModel: FridgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                SmartFridgeApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartFridgeApp(viewModel: FridgeViewModel) {
    var selectedTab by remember { mutableStateOf(BottomTab.FRIDGE_SCAN) }
    var activeRecipeDetail by remember { mutableStateOf<RecipeEntity?>(null) }
    var isCookingModeActive by remember { mutableStateOf(false) }

    val showOnboarding by viewModel.showOnboarding.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val userAvatar by viewModel.userAvatar.collectAsState()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    val pantryItems by viewModel.pantryItems.collectAsState()
    val recipes by viewModel.filteredRecipes.collectAsState()
    val shoppingList by viewModel.shoppingList.collectAsState()
    val selectedDietaryFilters by viewModel.selectedDietaryFilters.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSortOption by viewModel.selectedSortOption.collectAsState()
    val isAnalyzingPhoto by viewModel.isAnalyzingPhoto.collectAsState()
    val analysisMessage by viewModel.analysisMessage.collectAsState()
    val isScanningMessPlate by viewModel.isScanningMessPlate.collectAsState()
    val lastMessScanResult by viewModel.lastMessScanResult.collectAsState()
    val loggedMeals by viewModel.loggedMeals.collectAsState()
    val totalCaloriesToday by viewModel.totalCaloriesToday.collectAsState()
    val totalProteinToday by viewModel.totalProteinToday.collectAsState()
    val totalCarbsToday by viewModel.totalCarbsToday.collectAsState()
    val totalFatToday by viewModel.totalFatToday.collectAsState()
    val dailyProteinTarget by viewModel.dailyProteinTarget.collectAsState()

    val currentScreenState = remember(showOnboarding, isCookingModeActive, activeRecipeDetail) {
        when {
            showOnboarding -> ScreenState.Onboarding
            isCookingModeActive && activeRecipeDetail != null -> ScreenState.CookingMode(activeRecipeDetail!!)
            activeRecipeDetail != null -> ScreenState.RecipeDetail(activeRecipeDetail!!)
            else -> ScreenState.MainApp
        }
    }

    AnimatedContent(
        targetState = currentScreenState,
        transitionSpec = {
            when {
                targetState is ScreenState.CookingMode || targetState is ScreenState.RecipeDetail -> {
                    (slideInVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy),
                        initialOffsetY = { fullHeight -> fullHeight / 4 }
                    ) + fadeIn(animationSpec = tween(250)))
                        .togetherWith(
                            fadeOut(animationSpec = tween(180))
                        )
                }
                initialState is ScreenState.CookingMode || initialState is ScreenState.RecipeDetail -> {
                    fadeIn(animationSpec = tween(220))
                        .togetherWith(
                            slideOutVertically(
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                targetOffsetY = { fullHeight -> fullHeight / 4 }
                            ) + fadeOut(animationSpec = tween(200))
                        )
                }
                else -> {
                    fadeIn(animationSpec = tween(250)).togetherWith(fadeOut(animationSpec = tween(200)))
                }
            }
        },
        label = "TopScreenTransition",
        modifier = Modifier.fillMaxSize()
    ) { screenState ->
        when (screenState) {
            is ScreenState.Onboarding -> {
                OnboardingScreen(
                    onGoogleSignIn = { name, email -> viewModel.signInWithGoogle(name, email) }
                )
            }

            is ScreenState.CookingMode -> {
                CookingModeScreen(
                    recipe = screenState.recipe,
                    onCloseCookingMode = { isCookingModeActive = false }
                )
            }

            is ScreenState.RecipeDetail -> {
                RecipeDetailScreen(
                    recipe = screenState.recipe,
                    pantryItems = pantryItems,
                    onBackClick = { activeRecipeDetail = null },
                    onAddMissingToShoppingList = {
                        viewModel.addMissingIngredientsToShoppingList(screenState.recipe)
                    },
                    onStartCookingMode = { isCookingModeActive = true }
                )
            }

            is ScreenState.MainApp -> {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable {
                                            val nextAvatar = if (userAvatar == "BOY") "GIRL" else "BOY"
                                            viewModel.setUserAvatar(nextAvatar)
                                        }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (userAvatar == "BOY") EmeraldPrimary else Color(0xFFE91E63),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = if (userAvatar == "BOY") "👦" else "👧",
                                                fontSize = 20.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (userAvatar == "BOY") "Hostel Boy" else "Hostel Girl",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.SwapHoriz,
                                                contentDescription = "Switch Avatar",
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Text(
                                            text = "Tap avatar to change",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            actions = {
                                Button(
                                    onClick = { viewModel.logout() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .testTag("logout_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = "Logout",
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Logout",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                        )
                    },
                    bottomBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(32.dp),
                                color = CharcoalDock,
                                shadowElevation = 12.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bottom_navigation_bar")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Tab 1: Kitchen & Scanner
                                    val isTab1Selected = selectedTab == BottomTab.FRIDGE_SCAN
                                    Surface(
                                        onClick = { selectedTab = BottomTab.FRIDGE_SCAN },
                                        shape = CircleShape,
                                        color = if (isTab1Selected) Color.White else Color.Transparent,
                                        modifier = Modifier.testTag("nav_tab_fridge")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (pantryItems.isNotEmpty()) {
                                                BadgedBox(
                                                    badge = { Badge(containerColor = MangoOrange) { Text(pantryItems.size.toString(), color = CharcoalDock, fontWeight = FontWeight.Bold) } }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Kitchen,
                                                        contentDescription = "Bhook Kitchen",
                                                        tint = if (isTab1Selected) CharcoalDock else Color.White.copy(alpha = 0.85f),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Kitchen,
                                                    contentDescription = "Bhook Kitchen",
                                                    tint = if (isTab1Selected) CharcoalDock else Color.White.copy(alpha = 0.85f),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            if (isTab1Selected) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Kitchen",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = CharcoalDock
                                                )
                                            }
                                        }
                                    }

                                    // Tab 2: Recipes
                                    val isTab2Selected = selectedTab == BottomTab.RECIPES
                                    Surface(
                                        onClick = { selectedTab = BottomTab.RECIPES },
                                        shape = CircleShape,
                                        color = if (isTab2Selected) Color.White else Color.Transparent,
                                        modifier = Modifier.testTag("nav_tab_recipes")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.RestaurantMenu,
                                                contentDescription = "Recipes",
                                                tint = if (isTab2Selected) CharcoalDock else Color.White.copy(alpha = 0.85f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            if (isTab2Selected) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Recipes",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = CharcoalDock
                                                )
                                            }
                                        }
                                    }

                                    // Tab 3: Shopping
                                    val isTab3Selected = selectedTab == BottomTab.SHOPPING_LIST
                                    Surface(
                                        onClick = { selectedTab = BottomTab.SHOPPING_LIST },
                                        shape = CircleShape,
                                        color = if (isTab3Selected) Color.White else Color.Transparent,
                                        modifier = Modifier.testTag("nav_tab_shopping")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val unassignedCount = shoppingList.count { !it.isChecked }
                                            if (unassignedCount > 0) {
                                                BadgedBox(
                                                    badge = { Badge(containerColor = BubblegumPink) { Text(unassignedCount.toString(), color = Color.White, fontWeight = FontWeight.Bold) } }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ShoppingCart,
                                                        contentDescription = "Shopping",
                                                        tint = if (isTab3Selected) CharcoalDock else Color.White.copy(alpha = 0.85f),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.ShoppingCart,
                                                    contentDescription = "Shopping",
                                                    tint = if (isTab3Selected) CharcoalDock else Color.White.copy(alpha = 0.85f),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            if (isTab3Selected) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Shopping",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = CharcoalDock
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            if (targetState.ordinal > initialState.ordinal) {
                                (slideInHorizontally(
                                    animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy),
                                    initialOffsetX = { fullWidth -> (fullWidth * 0.2f).toInt() }
                                ) + fadeIn(animationSpec = tween(220)))
                                    .togetherWith(
                                        slideOutHorizontally(
                                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                                            targetOffsetX = { fullWidth -> (-fullWidth * 0.2f).toInt() }
                                        ) + fadeOut(animationSpec = tween(180))
                                    )
                            } else {
                                (slideInHorizontally(
                                    animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy),
                                    initialOffsetX = { fullWidth -> (-fullWidth * 0.2f).toInt() }
                                ) + fadeIn(animationSpec = tween(220)))
                                    .togetherWith(
                                        slideOutHorizontally(
                                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                                            targetOffsetX = { fullWidth -> (fullWidth * 0.2f).toInt() }
                                        ) + fadeOut(animationSpec = tween(180))
                                    )
                            }
                        },
                        label = "TabContentTransition",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) { tab ->
                        when (tab) {
                            BottomTab.FRIDGE_SCAN -> {
                                FridgeScanScreen(
                                    pantryItems = pantryItems,
                                    isAnalyzing = isAnalyzingPhoto,
                                    analysisMessage = analysisMessage,
                                    isScanningMessPlate = isScanningMessPlate,
                                    lastMessScanResult = lastMessScanResult,
                                    loggedMeals = loggedMeals,
                                    totalCaloriesToday = totalCaloriesToday,
                                    totalProteinToday = totalProteinToday,
                                    totalCarbsToday = totalCarbsToday,
                                    totalFatToday = totalFatToday,
                                    onFridgeImageCaptured = { viewModel.analyzeFridgePhoto(it) },
                                    onMessImageCaptured = { viewModel.scanMessPlatePhoto(it) },
                                    onSaveScannedMessMeal = { viewModel.saveScannedMessMealToTracker(it) },
                                    onLogCustomMeal = { name, p, c, f, cal -> viewModel.logCustomMeal(name, p, c, f, cal) },
                                    onDeleteLoggedMeal = { viewModel.deleteLoggedMeal(it) },
                                    onClearLoggedMeals = { viewModel.clearAllLoggedMeals() },
                                    onDismissMessResult = { viewModel.dismissMessScanResult() },
                                    onSelectPreset = { viewModel.selectPresetFridge(it) },
                                    onAddPantryItem = { viewModel.addPantryItem(it) },
                                    onRemovePantryItem = { viewModel.removePantryItem(it) },
                                    onClearPantry = { viewModel.clearPantry() },
                                    onGenerateAiRecipes = {
                                        viewModel.generateAiRecipesFromFridge()
                                        selectedTab = BottomTab.RECIPES
                                    },
                                    onNavigateToRecipes = { selectedTab = BottomTab.RECIPES },
                                    dailyProteinTarget = dailyProteinTarget,
                                    onUpdateDailyProteinTarget = { viewModel.setDailyProteinTarget(it) }
                                )
                            }

                            BottomTab.RECIPES -> {
                                RecipeListScreen(
                                    recipes = recipes,
                                    pantryItems = pantryItems,
                                    selectedDietaryFilters = selectedDietaryFilters,
                                    searchQuery = searchQuery,
                                    selectedSortOption = selectedSortOption,
                                    onToggleDietaryFilter = { viewModel.toggleDietaryFilter(it) },
                                    onClearDietaryFilters = { viewModel.clearDietaryFilters() },
                                    onUpdateSearchQuery = { viewModel.updateSearchQuery(it) },
                                    onUpdateSortOption = { viewModel.updateSortOption(it) },
                                    onRecipeSelect = { activeRecipeDetail = it },
                                    onAddMissingToShoppingList = { viewModel.addMissingIngredientsToShoppingList(it) },
                                    onToggleFavorite = { viewModel.toggleFavoriteRecipe(it) },
                                    onGenerateAiRecipes = { viewModel.generateAiRecipesFromFridge() },
                                    onSaveCustomRecipe = { viewModel.insertCustomRecipe(it) }
                                )
                            }

                            BottomTab.SHOPPING_LIST -> {
                                ShoppingListScreen(
                                    shoppingList = shoppingList,
                                    onAddCustomItem = { name -> viewModel.addCustomShoppingItem(name) },
                                    onToggleItem = { item -> viewModel.toggleShoppingItem(item) },
                                    onDeleteItem = { item -> viewModel.deleteShoppingItem(item) },
                                    onClearChecked = { viewModel.clearCheckedShoppingItems() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
