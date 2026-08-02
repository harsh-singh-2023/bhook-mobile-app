package com.example.ui.screens

import kotlinx.coroutines.tasks.await //gemini ka kraya hua kaam
import com.example.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.auth.FirebaseAuth
// import com.example.BuildConfig
import android.util.Log
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TealAccent
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val badge: String,
    val gradientColors: List<Color>
)

@Composable
fun OnboardingScreen(
    onGoogleSignIn: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentPage by remember { mutableIntStateOf(0) }
    var isSigningIn by remember { mutableStateOf(false) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            title = "Bhook AI Kitchen",
            subtitle = "Smart Hostel AI Assistant",
            description = "The ultimate AI culinary companion built specifically for Indian hostel students.",
            icon = Icons.Default.Kitchen,
            badge = "Hostel Room Jugaad",
            gradientColors = listOf(Color(0xFFDC8E47), Color(0xFF1A0D0A))
        ),
        OnboardingPage(
            title = "Mess Plate Protein AI",
            subtitle = "Snap Thali & Track Macros",
            description = "Take a photo of your mess food thali or eggs. AI instantly calculates protein, carbs, fats & calories!",
            icon = Icons.Default.Restaurant,
            badge = "AI Vision Scanner",
            gradientColors = listOf(Color(0xFF3AC9FA), Color(0xFF6352CA))
        ),
        OnboardingPage(
            title = "Interactive AI Chef Chatbot",
            subtitle = "Personalized Room Recipes",
            description = "Tell your AI chef what's in your room and target protein goals. It crafts custom kettle & induction recipes!",
            icon = Icons.Default.AutoAwesome,
            badge = "Tailored Nutrition",
            gradientColors = listOf(Color(0xFFDC8E47), Color(0xFF6352CA))
        ),
        OnboardingPage(
            title = "Zepto Express Grocery",
            subtitle = "10-Minute Ingredient Refills",
            description = "Missing milk, paneer, eggs or spices? Order instantly via Zepto express grocery delivery.",
            icon = Icons.Default.ShoppingBag,
            badge = "Quick Grocery Cart",
            gradientColors = listOf(Color(0xFF6352CA), Color(0xFF1A0D0A))
        )
    )

    val page = pages[currentPage]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(EmeraldPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Bhook AI Kitchen v2.5",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )
            }
        }

        // Hero Card Page Content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = page.gradientColors))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = page.badge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = page.icon,
                            contentDescription = page.title,
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = page.subtitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Page Indicator Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            pages.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) 24.dp else 8.dp, 8.dp)
                        .clip(CircleShape)
                        .background(if (index == currentPage) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { currentPage = index }
                )
            }
        }

        // Action Section (Page Navigation & Auth)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (currentPage < pages.size - 1) {
                Button(
                    onClick = { currentPage++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("next_onboarding_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Next Feature", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next")
                }
            } else {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var isLoginMode by remember { mutableStateOf(true) } // true = Log In, false = Sign Up

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    androidx.compose.material3.OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isSigningIn = true
                                authErrorMessage = null

                                if (email.isBlank() || password.isBlank()) {
                                    authErrorMessage = "Please enter both email and password."
                                    isSigningIn = false
                                    return@launch
                                }
                                if (password.length < 6) {
                                    authErrorMessage = "Password must be at least 6 characters."
                                    isSigningIn = false
                                    return@launch
                                }

                                try {
                                    val auth = FirebaseAuth.getInstance()
                                    val authResult = if (isLoginMode) {
                                        auth.signInWithEmailAndPassword(email.trim(), password).await()
                                    } else {
                                        auth.createUserWithEmailAndPassword(email.trim(), password).await()
                                    }

                                    val user = authResult.user
                                    val displayName = user?.email?.substringBefore("@") ?: "Student"
                                    val userEmail = user?.email ?: email.trim()

                                    isSigningIn = false
                                    onGoogleSignIn(displayName, userEmail)

                                } catch (e: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                                    isSigningIn = false
                                    authErrorMessage = "No account found with this email. Try Sign Up instead."
                                } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                    isSigningIn = false
                                    authErrorMessage = "An account already exists with this email. Try Log In instead."
                                } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                                    isSigningIn = false
                                    authErrorMessage = "Incorrect email or password."
                                } catch (e: Exception) {
                                    isSigningIn = false
                                    authErrorMessage = "Something went wrong: ${e.message ?: "please try again"}"
                                }
                            }
                        },
                        enabled = !isSigningIn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("email_auth_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        if (isSigningIn) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(if (isLoginMode) "Logging in..." else "Creating account...", color = Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Text(if (isLoginMode) "Log In" else "Sign Up", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    TextButton(
                        onClick = { isLoginMode = !isLoginMode; authErrorMessage = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Log In"
                        )
                    }

                    authErrorMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}