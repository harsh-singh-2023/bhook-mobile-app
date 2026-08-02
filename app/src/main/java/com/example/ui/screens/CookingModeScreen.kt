package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.RecipeEntity
import com.example.ui.theme.CulinaryOrange
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.utils.TextToSpeechHelper
import kotlinx.coroutines.delay

@Composable
fun CookingModeScreen(
    recipe: RecipeEntity,
    onCloseCookingMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ttsHelper = remember { TextToSpeechHelper(context) }
    val isSpeaking by ttsHelper.isSpeaking.collectAsState()

    val steps = recipe.getInstructionsList()
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var autoReadAloud by remember { mutableStateOf(true) }

    // Built-in Timer State
    var timerDurationSeconds by remember { mutableLongStateOf(60L) }
    var timerRemainingSeconds by remember { mutableLongStateOf(60L) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Speak current step when step changes or when autoReadAloud is toggled on
    LaunchedEffect(currentStepIndex, autoReadAloud) {
        if (autoReadAloud && steps.isNotEmpty()) {
            val stepText = "Step ${currentStepIndex + 1}. ${steps[currentStepIndex]}"
            ttsHelper.speak(stepText)
        }
    }

    // Timer coroutine loop
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timerRemainingSeconds > 0) {
            delay(1000L)
            timerRemainingSeconds -= 1
        }
        if (timerRemainingSeconds == 0L && isTimerRunning) {
            isTimerRunning = false
            ttsHelper.speak("Timer complete! Time to check your dish.")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("cooking_mode_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        ttsHelper.stop()
                        onCloseCookingMode()
                    },
                    modifier = Modifier.testTag("close_cooking_mode_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Exit Cooking Mode")
                }

                Text(
                    text = "Hands-Free Cooking Mode",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldDark
                )

                // Auto read aloud switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Read Aloud",
                        tint = if (autoReadAloud) EmeraldPrimary else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = autoReadAloud,
                        onCheckedChange = { autoReadAloud = it },
                        modifier = Modifier
                            .scale(0.8f)
                            .testTag("toggle_read_aloud_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Step Progress
            val totalSteps = steps.size
            val progress = if (totalSteps > 0) (currentStepIndex + 1).toFloat() / totalSteps.toFloat() else 0f

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = EmeraldPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Step ${currentStepIndex + 1} of $totalSteps",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Text Cooking Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cooking_step_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Read Aloud Animation Speaker Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .scale(if (isSpeaking) pulseScale else 1.0f)
                            .clip(CircleShape)
                            .background(if (isSpeaking) EmeraldPrimary else MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    ttsHelper.stop()
                                } else {
                                    ttsHelper.speak("Step ${currentStepIndex + 1}. ${steps[currentStepIndex]}")
                                }
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .testTag("speak_step_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Pause else Icons.Default.VolumeUp,
                                contentDescription = "Read Step Aloud",
                                tint = if (isSpeaking) Color.White else EmeraldPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Text(
                        text = if (isSpeaking) "Reading Step Aloud..." else "Tap Speaker to Read Aloud",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )

                    // Large Instruction Display
                    val stepText = steps.getOrNull(currentStepIndex) ?: "Ready to serve!"
                    Text(
                        text = stepText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 36.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Timer Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = "Timer", tint = CulinaryOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Step Timer: ${timerRemainingSeconds / 60}m ${timerRemainingSeconds % 60}s",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { isTimerRunning = !isTimerRunning },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) CulinaryOrange else EmeraldPrimary
                            ),
                            modifier = Modifier.testTag("toggle_timer_button")
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Timer toggle"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isTimerRunning) "Pause" else "Start Timer")
                        }

                        OutlinedButton(
                            onClick = {
                                isTimerRunning = false
                                timerRemainingSeconds = timerDurationSeconds
                            },
                            modifier = Modifier.testTag("reset_timer_button")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Timer")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))

            // Hands-Free Navigation Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentStepIndex > 0) {
                            currentStepIndex -= 1
                        }
                    },
                    enabled = currentStepIndex > 0,
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1f)
                        .testTag("previous_step_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Previous")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (currentStepIndex < totalSteps - 1) {
                            currentStepIndex += 1
                        } else {
                            ttsHelper.speak("Bon Appétit! You have completed cooking ${recipe.title}.")
                            onCloseCookingMode()
                        }
                    },
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1.2f)
                        .testTag("next_step_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text(
                        text = if (currentStepIndex < totalSteps - 1) "Next Step" else "Finish Cooking!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (currentStepIndex < totalSteps - 1) Icons.Default.ChevronRight else Icons.Default.CheckCircle,
                        contentDescription = "Next"
                    )
                }
            }
        }
    }
}
