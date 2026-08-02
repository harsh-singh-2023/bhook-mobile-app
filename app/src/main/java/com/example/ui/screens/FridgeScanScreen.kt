package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.core.content.ContextCompat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LoggedMeal
import com.example.data.local.PantryItem
import com.example.data.local.SampleData
import com.example.data.local.SampleFridgePreset
import com.example.data.remote.MessNutritionResult
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FridgeScanScreen(
    pantryItems: List<PantryItem>,
    isAnalyzing: Boolean,
    analysisMessage: String?,
    isScanningMessPlate: Boolean,
    lastMessScanResult: MessNutritionResult?,
    loggedMeals: List<LoggedMeal>,
    totalCaloriesToday: Int,
    totalProteinToday: Int,
    totalCarbsToday: Int,
    totalFatToday: Int,
    onFridgeImageCaptured: (Bitmap) -> Unit,
    onMessImageCaptured: (Bitmap) -> Unit,
    onSaveScannedMessMeal: (MessNutritionResult) -> Unit,
    onLogCustomMeal: (String, Int, Int, Int, Int) -> Unit,
    onDeleteLoggedMeal: (LoggedMeal) -> Unit,
    onClearLoggedMeals: () -> Unit,
    onDismissMessResult: () -> Unit,
    onSelectPreset: (SampleFridgePreset) -> Unit,
    onAddPantryItem: (String) -> Unit,
    onRemovePantryItem: (PantryItem) -> Unit,
    onClearPantry: () -> Unit,
    onGenerateAiRecipes: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    dailyProteinTarget: Int = 70,
    onUpdateDailyProteinTarget: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTabMode by remember { mutableIntStateOf(0) } // 0: Mess Food & Protein Scanner, 1: Kitchen & Pantry Scanner
    var customItemName by remember { mutableStateOf("") }
    var showCustomMealDialog by remember { mutableStateOf(false) }

    // User-customizable Daily Protein Goal Target
    var showTargetProteinDialog by remember { mutableStateOf(false) }
    var customTargetInput by remember { mutableStateOf("") }

    // Camera Launchers & Permission Request
    var pendingCameraAction by remember { mutableStateOf<String?>(null) }

    val fridgeCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> bitmap?.let { onFridgeImageCaptured(it) } }

    val messCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> bitmap?.let { onMessImageCaptured(it) } }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (pendingCameraAction == "MESS") {
                messCameraLauncher.launch(null)
            } else if (pendingCameraAction == "FRIDGE") {
                fridgeCameraLauncher.launch(null)
            }
        } else {
            Toast.makeText(context, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
        pendingCameraAction = null
    }

    fun launchCameraWithPermission(action: String) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            if (action == "MESS") {
                messCameraLauncher.launch(null)
            } else {
                fridgeCameraLauncher.launch(null)
            }
        } else {
            pendingCameraAction = action
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Gallery Launchers
    val fridgeGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                onFridgeImageCaptured(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val messGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                onMessImageCaptured(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("fridge_scan_screen")
    ) {
        // Sleek Pill-Shaped Tab Selector
        Surface(
            shape = CircleShape,
            color = LavenderSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    onClick = { selectedTabMode = 0 },
                    shape = CircleShape,
                    color = if (selectedTabMode == 0) CharcoalDock else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_mess_scanner")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Mess Scanner",
                            tint = if (selectedTabMode == 0) Color.White else LavenderPrimaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mess Food & Protein",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTabMode == 0) Color.White else LavenderPrimaryDark
                        )
                    }
                }

                Surface(
                    onClick = { selectedTabMode = 1 },
                    shape = CircleShape,
                    color = if (selectedTabMode == 1) CharcoalDock else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_pantry_scanner")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Kitchen,
                            contentDescription = "Pantry Scanner",
                            tint = if (selectedTabMode == 1) Color.White else LavenderPrimaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pantry & Recipes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTabMode == 1) Color.White else LavenderPrimaryDark
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (analysisMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Status",
                            tint = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = analysisMessage,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (selectedTabMode == 0) {
                // MESS FOOD & PROTEIN TRACKER MODE

                // Daily Macro Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Daily Protein & Macros",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
                                    color = SleekTextDark
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Target: ${dailyProteinTarget}g Protein / Day",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SleekTextMuted
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = {
                                            customTargetInput = dailyProteinTarget.toString()
                                            showTargetProteinDialog = true
                                        },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .testTag("edit_protein_target_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Daily Target",
                                            tint = LavenderPrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                            Surface(
                                onClick = { showCustomMealDialog = true },
                                shape = CircleShape,
                                color = LavenderSurface,
                                modifier = Modifier.testTag("add_custom_meal_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Log Food",
                                        tint = LavenderPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Log",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = LavenderPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Protein Progress Bar
                        val proteinProgress = (totalProteinToday.toFloat() / dailyProteinTarget.toFloat()).coerceIn(0f, 1f)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Protein: $totalProteinToday / ${dailyProteinTarget}g",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = LavenderPrimary
                            )
                            Text(
                                text = "${(proteinProgress * 100).toInt()}% Goal",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (totalProteinToday >= dailyProteinTarget) Color(0xFF15803D) else MangoOrangeDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { proteinProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape),
                            color = LavenderPrimary,
                            trackColor = LavenderSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Macros Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MacroStatBadge(label = "Calories", value = "$totalCaloriesToday", unit = "kcal", containerColor = MangoOrangeLight, contentColor = MangoOrangeDark)
                            MacroStatBadge(label = "Protein", value = "${totalProteinToday}g", unit = "g", containerColor = LavenderSurface, contentColor = LavenderPrimary)
                            MacroStatBadge(label = "Carbs", value = "${totalCarbsToday}g", unit = "g", containerColor = PeriwinkleLight, contentColor = Color(0xFF1D4ED8))
                            MacroStatBadge(label = "Fats", value = "${totalFatToday}g", unit = "g", containerColor = BubblegumPinkLight, contentColor = BubblegumPink)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Mess Photo Capture Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("snap_mess_plate_card"),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(LavenderPrimary, Color(0xFF6D28D9))
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = "Mess Scanner",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Mess Food AI Scanner",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color.White
                            )

                            Text(
                                text = "Snap a photo of your mess thali, eggs, or snacks. AI Vision calculates total protein, carbs, fats & calories!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp),
                                textAlign = TextAlign.Center
                            )

                            if (isScanningMessPlate) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color.White,
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Calculating protein & macros with AI Vision...",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { launchCameraWithPermission("MESS") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("take_mess_photo_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = MangoOrange),
                                        shape = CircleShape
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Snap Mess Plate",
                                            tint = CharcoalDock
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Snap Plate", color = CharcoalDock, fontWeight = FontWeight.ExtraBold)
                                    }

                                    Button(
                                        onClick = { messGalleryLauncher.launch("image/*") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("upload_mess_photo_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        shape = CircleShape
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = "Upload Photo",
                                            tint = CharcoalDock
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Upload", color = CharcoalDock, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Scanned Mess Nutrition Result Card
                if (lastMessScanResult != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mess_scan_result_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lastMessScanResult.mealName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmeraldPrimary)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${lastMessScanResult.totalProteinGrams}g Protein",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Calories: ${lastMessScanResult.totalCalories} kcal  |  Carbs: ${lastMessScanResult.totalCarbsGrams}g  |  Fats: ${lastMessScanResult.totalFatGrams}g",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (lastMessScanResult.hostelNutrientTip.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "💡 ${lastMessScanResult.hostelNutrientTip}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            if (lastMessScanResult.items.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Detected Items:",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                lastMessScanResult.items.forEach { dish ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "• ${dish.name}",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${dish.proteinGrams}g P | ${dish.calories} kcal",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { onSaveScannedMessMeal(lastMessScanResult) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                ) {
                                    Text("Log to Tracker", fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = onDismissMessResult,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Dismiss")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Logged Meals History Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Logged Meals (${loggedMeals.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (loggedMeals.isNotEmpty()) {
                        TextButton(onClick = onClearLoggedMeals) {
                            Text("Clear History", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                if (loggedMeals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No meals logged today yet. Snap your mess plate or add items above!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        loggedMeals.forEach { meal ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = "Meal",
                                            tint = EmeraldPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = meal.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${meal.calories} kcal  |  ${meal.carbsGrams}g C  |  ${meal.fatGrams}g F",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(WarmAmber)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "+${meal.proteinGrams}g Protein",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.Black
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteLoggedMeal(meal) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                // PANTRY & KITCHEN SCANNER MODE (ORIGINAL FRIDGE INVENTORY)

                // Hero Photo Capture Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("snap_fridge_card"),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(EmeraldDark, EmeraldPrimary)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Kitchen,
                                    contentDescription = "Pantry Scanner",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Bhook AI - Smart Hostel Kitchen",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Text(
                                text = "Snap your hostel staples, pantry, or fridge. AI Vision will craft quick kettle & induction recipes!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                                textAlign = TextAlign.Center
                            )

                            if (isAnalyzing) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Analyzing ingredients with AI Vision...",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = Color.White
                                    )
                                }
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { launchCameraWithPermission("FRIDGE") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("take_fridge_photo_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Snap Photo",
                                            tint = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Snap Photo", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { fridgeGalleryLauncher.launch("image/*") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("upload_fridge_photo_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = "Gallery Upload",
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Upload", color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Preset Sample Fridges Section
                Text(
                    text = "Or Select a Hostel Jugaad Preset",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Instant recipe matching for hostel room staples:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SampleData.sampleFridges) { preset ->
                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .clickable { onSelectPreset(preset) }
                                .testTag("preset_fridge_${preset.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = preset.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = preset.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap to load ${preset.ingredients.size} items →",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Active Pantry Inventory Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Hostel Food Supplies (${pantryItems.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Ingredients available for recipe creation:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (pantryItems.isNotEmpty()) {
                        IconButton(
                            onClick = onClearPantry,
                            modifier = Modifier.testTag("clear_pantry_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear Inventory",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ingredient input field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customItemName,
                        onValueChange = { customItemName = it },
                        placeholder = { Text("Add ingredient (e.g. Eggs, Maggi)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_ingredient_text_field"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (customItemName.isNotBlank()) {
                                onAddPantryItem(customItemName)
                                customItemName = ""
                            }
                        },
                        modifier = Modifier.testTag("add_ingredient_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Active Pantry Chips
                if (pantryItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Your pantry is empty! Take a photo or add items above.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        pantryItems.forEach { item ->
                            InputChip(
                                selected = false,
                                onClick = { },
                                label = { Text(item.name) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { onRemovePantryItem(item) }
                                    )
                                },
                                colors = InputChipDefaults.inputChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.testTag("ingredient_chip_${item.id}")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                // Primary Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onGenerateAiRecipes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("generate_ai_recipes_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Recipes")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Custom AI Recipes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Button(
                        onClick = onNavigateToRecipes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("view_matching_recipes_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CulinaryOrange)
                    ) {
                        Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = "Recipes")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View All Matching Recipe Cards (${pantryItems.size} items ready)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Dialog for Logging Custom Food Item
    if (showCustomMealDialog) {
        var foodNameInput by remember { mutableStateOf("") }
        var proteinInput by remember { mutableStateOf("") }
        var carbsInput by remember { mutableStateOf("") }
        var fatInput by remember { mutableStateOf("") }
        var caloriesInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCustomMealDialog = false },
            title = { Text("Log Food / Snack", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = foodNameInput,
                        onValueChange = { foodNameInput = it },
                        label = { Text("Food Name (e.g. 3 Boiled Eggs)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = proteinInput,
                        onValueChange = { proteinInput = it },
                        label = { Text("Protein (Grams)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = carbsInput,
                            onValueChange = { carbsInput = it },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fatInput,
                            onValueChange = { fatInput = it },
                            label = { Text("Fats (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = caloriesInput,
                        onValueChange = { caloriesInput = it },
                        label = { Text("Total Calories (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = proteinInput.toIntOrNull() ?: 0
                        val c = carbsInput.toIntOrNull() ?: 0
                        val f = fatInput.toIntOrNull() ?: 0
                        val cal = caloriesInput.toIntOrNull() ?: 0
                        if (foodNameInput.isNotBlank()) {
                            onLogCustomMeal(foodNameInput, p, c, f, cal)
                            showCustomMealDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomMealDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Set Custom Protein Target Goal Dialog
    if (showTargetProteinDialog) {
        AlertDialog(
            onDismissRequest = { showTargetProteinDialog = false },
            title = { Text("Set Daily Protein Target", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your custom daily protein target in grams (e.g. 70, 100, 120, 150):", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customTargetInput,
                        onValueChange = { customTargetInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Daily Protein Target (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newTarget = customTargetInput.toIntOrNull()
                        if (newTarget != null && newTarget > 0) {
                            onUpdateDailyProteinTarget(newTarget)
                        }
                        showTargetProteinDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save Target", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTargetProteinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MacroStatBadge(
    label: String,
    value: String,
    unit: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        modifier = Modifier.padding(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 15.sp),
                color = contentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                color = contentColor.copy(alpha = 0.85f)
            )
        }
    }
}
