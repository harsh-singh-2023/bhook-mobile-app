package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.RecipeEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftCream
import com.example.ui.theme.TealAccent
import com.example.ui.theme.WarmAmber

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import com.example.data.remote.GeminiService
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "AI" or "USER"
    val text: String,
    val suggestedRecipe: RecipeEntity? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeChatbotDialog(
    onDismiss: () -> Unit,
    onSaveRecipeToCollection: (RecipeEntity) -> Unit,
    onStartCookingRecipe: (RecipeEntity) -> Unit,
    pantryItems: List<String> = emptyList()
) {
    var userRoomIngredients by remember { mutableStateOf(pantryItems.ifEmpty { listOf("Maggi", "Eggs", "Oats", "Milk", "Cheese", "Paneer", "Peanut Butter") }) }
    var selectedDiet by remember { mutableStateOf("Eggitarian 🥚") }
    var selectedProteinTarget by remember { mutableStateOf("25g+ Protein 💪") }
    var selectedEquipment by remember { mutableStateOf("Electric Kettle 🫖") }
    var selectedPrepTime by remember { mutableStateOf("10 Mins ⚡") }

    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val geminiService = remember { GeminiService() }

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    sender = "AI",
                    text = "Namaste hostel chef! 👨‍🍳 I'm Chef Bhook (Bhook AI).\n\nTell me what ingredients you have in your room and your target protein/carbs goal. I'll create a custom room-cooked recipe for you!"
                )
            )
        )
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun sendMessageToAi(userPrompt: String) {
        if (userPrompt.isBlank() || isThinking) return

        messages = messages + ChatMessage(sender = "USER", text = userPrompt)
        isThinking = true

        coroutineScope.launch {
            val result = geminiService.chatWithHostelChef(
                userMessage = userPrompt,
                pantryItems = userRoomIngredients,
                diet = selectedDiet,
                equipment = selectedEquipment,
                proteinGoal = selectedProteinTarget
            )
            isThinking = false

            if (result.isSuccess) {
                val (aiText, recipe) = result.getOrThrow()
                messages = messages + ChatMessage(
                    sender = "AI",
                    text = aiText,
                    suggestedRecipe = recipe
                )
            } else {
                messages = messages + ChatMessage(
                    sender = "AI",
                    text = "I'm calculating your recipe! Please try sending your ingredients again."
                )
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Hostel Chef AI Chatbot", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Real-Time Kettle & Induction Recipes", style = MaterialTheme.typography.labelSmall, color = TealAccent)
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_chatbot_dialog")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Interactive Preference Selector Chips
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    Text("Customize Meal Profile & Equipment:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Diet Selector
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("Eggitarian 🥚", "Pure Veg 🥬", "Non-Veg 🍗", "Vegan 🌿")) { diet ->
                            val selected = selectedDiet == diet
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selected) EmeraldPrimary else MaterialTheme.colorScheme.surface)
                                    .clickable { selectedDiet = diet }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(diet, fontSize = 11.sp, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Equipment & Protein Selector
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("Electric Kettle 🫖", "Single Induction 🍳", "Microwave ⚡", "No Cooking 🥣")) { equip ->
                            val selected = selectedEquipment == equip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selected) WarmAmber else MaterialTheme.colorScheme.surface)
                                    .clickable { selectedEquipment = equip }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(equip, fontSize = 11.sp, color = if (selected) Color.Black else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            val prompt = "Create a high protein recipe with my room items (${userRoomIngredients.joinToString(", ")}) using $selectedEquipment ($selectedDiet, $selectedProteinTarget)."
                            sendMessageToAi(prompt)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .testTag("generate_chatbot_recipe_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Generate", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate AI Recipe For Targets", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Log Messages
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatBubble(
                            message = msg,
                            onSaveRecipe = {
                                msg.suggestedRecipe?.let { r -> onSaveRecipeToCollection(r) }
                            },
                            onStartCooking = {
                                msg.suggestedRecipe?.let { r -> onStartCookingRecipe(r) }
                            }
                        )
                    }

                    if (isThinking) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = EmeraldPrimary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Chef Bhook is crafting recipe in real-time...", fontSize = 12.sp, color = TealAccent, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Free-text Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask e.g. 'Got eggs & oats in room'") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chatbot_input_text"),
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val textToSend = inputText
                                inputText = ""
                                sendMessageToAi(textToSend)
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                            .testTag("send_chatbot_msg_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onSaveRecipe: () -> Unit,
    onStartCooking: () -> Unit
) {
    val isUser = message.sender == "USER"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.text,
                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        // Render Suggested Recipe Card
        message.suggestedRecipe?.let { recipe ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🍳 " + recipe.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(recipe.description, style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("💪 High Protein", fontWeight = FontWeight.Bold, color = EmeraldPrimary, fontSize = 12.sp)
                        Text("🔥 ${recipe.calories} kcal", fontSize = 12.sp)
                        Text("⏱️ ${recipe.prepTimeMinutes} mins prep", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onSaveRecipe,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Recipe", fontSize = 12.sp)
                        }

                        Button(
                            onClick = onStartCooking,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                        ) {
                            Icon(imageVector = Icons.Default.Restaurant, contentDescription = "Cook", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Start Cooking", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
