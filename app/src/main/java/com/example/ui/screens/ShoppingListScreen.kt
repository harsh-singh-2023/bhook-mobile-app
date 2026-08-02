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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ShoppingListItem
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ZeptoPurple
import com.example.utils.ZeptoHelper

@Composable
fun ShoppingListScreen(
    shoppingList: List<ShoppingListItem>,
    onAddCustomItem: (String) -> Unit,
    onToggleItem: (ShoppingListItem) -> Unit,
    onDeleteItem: (ShoppingListItem) -> Unit,
    onClearChecked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var newItemName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("shopping_list_screen")
    ) {
        // Hero Zepto Integration Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("zepto_hero_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZeptoPurple),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Zepto Grocery",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Seamless Zepto Grocery",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Get missing ingredients delivered in 10 minutes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { ZeptoHelper.openZeptoStore(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("open_zepto_store_button")
                ) {
                    Text("Zepto", color = ZeptoPurple, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open Zepto",
                        tint = ZeptoPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header & Clear Checked
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Shopping List (${shoppingList.size} items)",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            if (shoppingList.any { it.isChecked }) {
                IconButton(
                    onClick = onClearChecked,
                    modifier = Modifier.testTag("clear_checked_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Checked Items",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add Item Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newItemName,
                onValueChange = { newItemName = it },
                placeholder = { Text("Add missing ingredient...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("add_shopping_item_field"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (newItemName.isNotBlank()) {
                        onAddCustomItem(newItemName)
                        newItemName = ""
                    }
                },
                modifier = Modifier.testTag("add_shopping_item_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Bulk Zepto Order Bar
        val uncheckedItems = shoppingList.filter { !it.isChecked }.map { it.name }
        if (uncheckedItems.isNotEmpty()) {
            Button(
                onClick = { ZeptoHelper.orderMultipleItemsOnZepto(context, uncheckedItems) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("order_all_zepto_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Order All")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Order All ${uncheckedItems.size} Items on Zepto Express", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Shopping List Items
        if (shoppingList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Empty List",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your shopping list is empty!",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Add missing recipe ingredients in 1 click.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(shoppingList, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shopping_item_${item.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isChecked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { onToggleItem(item) },
                                colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary),
                                modifier = Modifier.testTag("checkbox_${item.id}")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Medium,
                                        textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                                if (item.recipeTitle.isNotBlank()) {
                                    Text(
                                        text = "From: ${item.recipeTitle}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldPrimary
                                    )
                                }
                            }

                            // Zepto Quick Search for this item
                            Button(
                                onClick = { ZeptoHelper.openZeptoSearch(context, item.name) },
                                colors = ButtonDefaults.buttonColors(containerColor = ZeptoPurple.copy(alpha = 0.15f)),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("buy_zepto_btn_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = "Buy on Zepto",
                                    tint = ZeptoPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Zepto", color = ZeptoPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(
                                onClick = { onDeleteItem(item) },
                                modifier = Modifier.testTag("delete_shopping_item_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete item",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
