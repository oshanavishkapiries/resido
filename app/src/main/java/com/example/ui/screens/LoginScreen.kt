package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.ResidoServiceLocator
import com.example.data.models.User
import com.example.ui.theme.ResidoBackground
import com.example.ui.theme.ResidoBlue
import com.example.ui.theme.ResidoDarkNavy
import com.example.ui.theme.ResidoTextSecondary

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val userRepository = ResidoServiceLocator.userRepository

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ResidoBackground)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Branding Section with 16x16 Box (64dp)
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                .background(ResidoBlue, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.HomeWork,
                contentDescription = "Resido Logo",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Resido",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ResidoDarkNavy,
            letterSpacing = (-0.5).sp
        )

        Text(
            text = "PROPERTIES SRI LANKA",
            fontSize = 11.sp,
            color = ResidoDarkNavy.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Login Heading
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Enter credentials to login",
                fontSize = 14.sp,
                color = ResidoDarkNavy.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Email label + field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Email Address",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                placeholder = { Text("sample@gmail.com", color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon", tint = ResidoBlue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .testTag("username_input"),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = emailError.isNotEmpty(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = ResidoDarkNavy,
                    unfocusedTextColor = ResidoDarkNavy
                )
            )
            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password label & forget link + field
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Password",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResidoDarkNavy.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )

                Text(
                    text = "Forget password?",
                    color = ResidoBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { navController.navigate("forgot_password") }
                        .padding(bottom = 6.dp, end = 4.dp)
                )
            }

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ""
                },
                placeholder = { Text("••••••••••••", color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Password Icon", tint = ResidoBlue)
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password", tint = ResidoBlue)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = passwordError.isNotEmpty(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = ResidoDarkNavy,
                    unfocusedTextColor = ResidoDarkNavy
                )
            )
            if (passwordError.isNotEmpty()) {
                Text(
                    text = passwordError,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Sign In Button
        Button(
            onClick = {
                var hasError = false
                if (email.isBlank()) {
                    emailError = "Please enter email"
                    hasError = true
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = "Please enter a valid email address"
                    hasError = true
                }
                if (password.isBlank()) {
                    passwordError = "Please enter password"
                    hasError = true
                }

                if (!hasError) {
                    // Set default current user in repo
                    val existing = userRepository.getCurrentUser()
                    if (existing == null || existing.email != email) {
                        val newUser = User(
                            id = "user_${System.currentTimeMillis()}",
                            firstName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                            lastName = "User",
                            email = email,
                            phoneCountryCode = "+94",
                            phoneNumber = "771234567",
                            dateOfBirth = "1995-10-10",
                            gender = "Male",
                            profileImageUrl = null,
                            activeRole = "BUYER"
                        )
                        userRepository.registerUser(newUser)
                    }
                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                .testTag("submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Sign In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider Row matching the HTML CSS exactly
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFE2E8F0))
            )
            Text(
                text = "OR",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFE2E8F0))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Continue with Google Button
        OutlinedButton(
            onClick = {
                val googleUser = User(
                    id = "google_user",
                    firstName = "Google",
                    lastName = "User",
                    email = "googleuser@gmail.com",
                    phoneCountryCode = "+94",
                    phoneNumber = "770000000",
                    dateOfBirth = "1998-01-01",
                    gender = "Male",
                    profileImageUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=200",
                    activeRole = "BUYER"
                )
                userRepository.updateCurrentUser(googleUser)
                Toast.makeText(context, "Signed in with Google (Mock)", Toast.LENGTH_SHORT).show()
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Beautiful inline mini Google color G logo matching tailwind spec
                Text(
                    text = "G",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = ResidoBlue,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Continue with Google",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResidoDarkNavy
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sign up bottom text
        Text(
            text = buildAnnotatedString {
                append("Don't have an account? ")
                withStyle(style = SpanStyle(color = ResidoBlue, fontWeight = FontWeight.Bold)) {
                    append("Sign Up")
                }
            },
            fontSize = 15.sp,
            color = ResidoDarkNavy.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable {
                    navController.navigate("signup")
                }
                .padding(8.dp)
        )
    }
}
