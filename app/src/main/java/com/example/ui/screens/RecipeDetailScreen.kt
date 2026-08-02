package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.local.PantryItem
import com.example.data.local.RecipeEntity
import com.example.ui.theme.*
import com.example.utils.ZeptoHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailScreen(
    recipe: RecipeEntity,
    pantryItems: List<PantryItem>,
    onBackClick: () -> Unit,
    onAddMissingToShoppingList: () -> Unit,
    onStartCookingMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pantryNames = pantryItems.map { it.name.lowercase().trim() }
    val ingredients = recipe.getIngredientsList()

    val missingIngredients = ingredients.filter { ing ->
        pantryNames.none { pantry -> ing.lowercase().contains(pantry) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("recipe_detail_screen")
    ) {
        // Navigation Back Button & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("back_button")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = SleekTextDark)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Recipe Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = SleekTextDark
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hero Header Card with Image
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                if (recipe.imageUrl.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(LavenderSurface)
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(recipe.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = recipe.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(LavenderSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        color = LavenderPrimary,
                                        strokeWidth = 2.dp
                                    )
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(LavenderPrimary, CharcoalDock)
                                            )
                                        )
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restaurant,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                                        startY = 100f
                                    )
                                )
                        )
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = SleekTextDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = Color(0xFF374151)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = "Time", tint = LavenderPrimaryDark)
                            Text(text = "Prep Time", style = MaterialTheme.typography.labelSmall, color = SleekTextMuted)
                            Text(text = "${recipe.prepTimeMinutes} mins", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = SleekTextDark)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = "Calories", tint = MangoOrangeDark)
                            Text(text = "Calories", style = MaterialTheme.typography.labelSmall, color = SleekTextMuted)
                            Text(text = "${recipe.calories} kcal", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = SleekTextDark)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(LavenderSurface)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = recipe.difficulty,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = LavenderPrimaryDark
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Difficulty", style = MaterialTheme.typography.labelSmall, color = SleekTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        recipe.getDietaryList().forEach { tag ->
                            SuggestionChip(onClick = {}, label = { Text(tag, color = LavenderPrimaryDark, fontWeight = FontWeight.SemiBold) })
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Ingredients Checklist Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ingredients (${ingredients.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = SleekTextDark
                    )

                    val matchedCount = ingredients.size - missingIngredients.size
                    Text(
                        text = "$matchedCount in Fridge",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (missingIngredients.isEmpty()) Color(0xFF15803D) else MangoOrangeDark
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ingredients.forEach { ingredient ->
                    val isAvailable = pantryNames.any { pantry -> ingredient.lowercase().contains(pantry) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAvailable) Icons.Default.CheckCircle else Icons.Default.AddShoppingCart,
                            contentDescription = if (isAvailable) "In Fridge" else "Missing",
                            tint = if (isAvailable) Color(0xFF15803D) else MangoOrangeDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = ingredient,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isAvailable) FontWeight.SemiBold else FontWeight.Medium
                            ),
                            color = if (isAvailable) SleekTextDark else Color(0xFF374151),
                            modifier = Modifier.weight(1f)
                        )
                        if (!isAvailable) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MangoOrangeLight)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Missing",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MangoOrangeDark
                                )
                            }
                        }
                    }
                    Divider(color = Color(0xFFE5E7EB))
                }

                if (missingIngredients.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAddMissingToShoppingList,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_missing_to_shopping_list_button"),
                            shape = CircleShape
                        ) {
                            Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = "Add missing", modifier = Modifier.size(18.dp), tint = LavenderPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Missing to List", color = LavenderPrimary, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val firstMissing = missingIngredients.firstOrNull()?.split(" ")?.lastOrNull() ?: "Groceries"
                                ZeptoHelper.openZeptoSearch(context, firstMissing)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ZeptoPurple),
                            shape = CircleShape,
                            modifier = Modifier.testTag("zepto_delivery_button")
                        ) {
                            Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Zepto", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Zepto Delivery", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Start Cooking Mode FAB Button
        Button(
            onClick = onStartCookingMode,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("start_cooking_mode_button"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary)
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start Cooking", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start Step-by-Step Cooking Mode", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

