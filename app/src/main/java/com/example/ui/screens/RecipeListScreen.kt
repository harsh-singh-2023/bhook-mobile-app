package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.PantryItem
import com.example.data.local.RecipeEntity
import com.example.ui.SortOption
import com.example.ui.components.DietaryFilterSidebar
import com.example.ui.components.RecipeCard
import com.example.ui.theme.EmeraldPrimary
import kotlinx.coroutines.launch

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.ui.components.RecipeChatbotDialog
import com.example.ui.theme.TealAccent

@Composable
fun RecipeListScreen(
    recipes: List<RecipeEntity>,
    pantryItems: List<PantryItem>,
    selectedDietaryFilters: Set<String>,
    searchQuery: String,
    selectedSortOption: SortOption,
    onToggleDietaryFilter: (String) -> Unit,
    onClearDietaryFilters: () -> Unit,
    onUpdateSearchQuery: (String) -> Unit,
    onUpdateSortOption: (SortOption) -> Unit,
    onRecipeSelect: (RecipeEntity) -> Unit,
    onAddMissingToShoppingList: (RecipeEntity) -> Unit,
    onToggleFavorite: (RecipeEntity) -> Unit,
    onGenerateAiRecipes: () -> Unit,
    onSaveCustomRecipe: (RecipeEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val pantryNames = pantryItems.map { it.name }

    var showChatbotDialog by remember { mutableStateOf(false) }

    if (showChatbotDialog) {
        RecipeChatbotDialog(
            onDismiss = { showChatbotDialog = false },
            onSaveRecipeToCollection = { recipe ->
                onSaveCustomRecipe(recipe)
                showChatbotDialog = false
            },
            onStartCookingRecipe = { recipe ->
                onRecipeSelect(recipe)
                showChatbotDialog = false
            },
            pantryItems = pantryNames
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DietaryFilterSidebar(
                selectedFilters = selectedDietaryFilters,
                onToggleFilter = onToggleDietaryFilter,
                onClearAll = onClearDietaryFilters
            )
        },
        modifier = modifier.testTag("recipe_list_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Hero Hostel Chef AI Chatbot Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chatbot_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Chatbot",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hostel Chef AI Chatbot",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ask what's in your room • Custom Protein & Kettle Recipes",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Button(
                        onClick = { showChatbotDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("open_chatbot_btn")
                    ) {
                        Text("Ask AI", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Top Bar with Search & Filter Drawer Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onUpdateSearchQuery,
                    placeholder = { Text("Search recipes or ingredients...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_recipe_text_field"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Sidebar Drawer Trigger
                IconButton(
                    onClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    modifier = Modifier.testTag("open_filter_drawer_button")
                ) {
                    if (selectedDietaryFilters.isNotEmpty()) {
                        BadgedBox(
                            badge = {
                                Badge { Text(selectedDietaryFilters.size.toString()) }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter Sidebar",
                                tint = EmeraldPrimary
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter Sidebar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sort Selector Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort by",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sort:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SortOption.values()) { option ->
                        val isSelected = selectedSortOption == option
                        val label = when (option) {
                            SortOption.MATCH_COUNT -> "Fridge Match"
                            SortOption.PREP_TIME -> "Prep Time"
                            SortOption.CALORIES -> "Calories"
                            SortOption.DIFFICULTY -> "Difficulty"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { onUpdateSortOption(option) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("sort_chip_${option.name}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Results count header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Suggested Recipes (${recipes.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                if (selectedDietaryFilters.isNotEmpty()) {
                    Text(
                        text = "Filtered by: ${selectedDietaryFilters.joinToString()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recipe Cards List
            if (recipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No matching recipes found.",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onGenerateAiRecipes,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            modifier = Modifier.testTag("empty_state_generate_recipes_button")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ask AI Chef for New Ideas")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            pantryIngredientNames = pantryNames,
                            onRecipeClick = { onRecipeSelect(recipe) },
                            onAddMissingToShoppingList = { onAddMissingToShoppingList(recipe) },
                            onToggleFavorite = { onToggleFavorite(recipe) }
                        )
                    }
                }
            }
        }
    }
}
