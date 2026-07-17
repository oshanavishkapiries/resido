package com.example.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import java.util.Calendar

@Composable
fun SignupScreen(navController: NavController) {
    val context = LocalContext.current
    val userRepository = ResidoServiceLocator.userRepository

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var dob by remember { mutableStateOf("") }

    // Errors
    var firstNameError by remember { mutableStateOf("") }
    var lastNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var dobError by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Country Code Picker states
    var selectedCountryCode by remember { mutableStateOf("+94") }
    var selectedCountryFlag by remember { mutableStateOf("🇱🇰") }
    var countryCodeMenuExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Date Picker Setup
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dob = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            dobError = ""
        },
        calendar.get(Calendar.YEAR) - 25, // default around 2000s
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ResidoBackground)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))

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

        Spacer(modifier = Modifier.height(28.dp))

        // Create Account Heading
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Create Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fill in the details to register your Resido account",
                fontSize = 14.sp,
                color = ResidoDarkNavy.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // First Name & Last Name in Row
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "First Name",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResidoDarkNavy.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )

                TextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        firstNameError = ""
                    },
                    placeholder = { Text("John", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "User", tint = ResidoBlue, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    isError = firstNameError.isNotEmpty(),
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
                if (firstNameError.isNotEmpty()) {
                    Text(text = firstNameError, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Last Name",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResidoDarkNavy.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )

                TextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        lastNameError = ""
                    },
                    placeholder = { Text("Doe", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "User", tint = ResidoBlue, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    isError = lastNameError.isNotEmpty(),
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
                if (lastNameError.isNotEmpty()) {
                    Text(text = lastNameError, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email Address
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
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = ResidoBlue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
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
                Text(text = emailError, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phone with country code picker
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Phone Number",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Row(
                        modifier = Modifier
                            .height(56.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .clickable { countryCodeMenuExpanded = true }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "$selectedCountryFlag $selectedCountryCode", fontSize = 14.sp, color = ResidoDarkNavy, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = ResidoDarkNavy, modifier = Modifier.size(18.dp))
                    }

                    DropdownMenu(
                        expanded = countryCodeMenuExpanded,
                        onDismissRequest = { countryCodeMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("🇱🇰 Sri Lanka (+94)") },
                            onClick = {
                                selectedCountryCode = "+94"
                                selectedCountryFlag = "🇱🇰"
                                countryCodeMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🇬🇧 United Kingdom (+44)") },
                            onClick = {
                                selectedCountryCode = "+44"
                                selectedCountryFlag = "🇬🇧"
                                countryCodeMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🇺🇸 United States (+1)") },
                            onClick = {
                                selectedCountryCode = "+1"
                                selectedCountryFlag = "🇺🇸"
                                countryCodeMenuExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    TextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            phoneError = ""
                        },
                        placeholder = { Text("771234567", color = Color(0xFF94A3B8)) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", tint = ResidoBlue)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        isError = phoneError.isNotEmpty(),
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
                }
            }
            if (phoneError.isNotEmpty()) {
                Text(text = phoneError, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date of Birth & Gender Row
        Row(modifier = Modifier.fillMaxWidth()) {
            // Date of Birth
            Column(modifier = Modifier.weight(1.1f)) {
                Text(
                    text = "Date of Birth",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResidoDarkNavy.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )

                TextField(
                    value = dob,
                    onValueChange = {},
                    placeholder = { Text("YYYY-MM-DD", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = ResidoBlue,
                            modifier = Modifier.clickable { datePickerDialog.show() }
                        )
                    },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clickable { datePickerDialog.show() },
                    shape = RoundedCornerShape(16.dp),
                    isError = dobError.isNotEmpty(),
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
                if (dobError.isNotEmpty()) {
                    Text(text = dobError, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Gender Selector Dropdown
            var genderMenuExpanded by remember { mutableStateOf(false) }
            Column(modifier = Modifier.weight(0.9f)) {
                Text(
                    text = "Gender",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResidoDarkNavy.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )

                Box {
                    TextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = ResidoBlue, modifier = Modifier.clickable { genderMenuExpanded = true })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .clickable { genderMenuExpanded = true },
                        shape = RoundedCornerShape(16.dp),
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

                    DropdownMenu(
                        expanded = genderMenuExpanded,
                        onDismissRequest = { genderMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Male") },
                            onClick = {
                                gender = "Male"
                                genderMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Female") },
                            onClick = {
                                gender = "Female"
                                genderMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Other") },
                            onClick = {
                                gender = "Other"
                                genderMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Password",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ""
                },
                placeholder = { Text("••••••••••••", color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", tint = ResidoBlue)
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
                Text(text = passwordError, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Confirm Password",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            TextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = ""
                },
                placeholder = { Text("••••••••••••", color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", tint = ResidoBlue)
                },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password", tint = ResidoBlue)
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = confirmPasswordError.isNotEmpty(),
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
            if (confirmPasswordError.isNotEmpty()) {
                Text(text = confirmPasswordError, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Create Account Button
        Button(
            onClick = {
                var hasError = false
                if (firstName.isBlank()) {
                    firstNameError = "First Name required"
                    hasError = true
                }
                if (lastName.isBlank()) {
                    lastNameError = "Last Name required"
                    hasError = true
                }
                if (email.isBlank()) {
                    emailError = "Email required"
                    hasError = true
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = "Invalid email"
                    hasError = true
                }
                if (phone.isBlank()) {
                    phoneError = "Phone required"
                    hasError = true
                }
                if (dob.isBlank()) {
                    dobError = "Birth date required"
                    hasError = true
                }
                if (password.isBlank()) {
                    passwordError = "Password required"
                    hasError = true
                } else if (password.length < 6) {
                    passwordError = "At least 6 characters"
                    hasError = true
                }
                if (confirmPassword != password) {
                    confirmPasswordError = "Passwords do not match"
                    hasError = true
                }

                if (!hasError) {
                    val registered = userRepository.registerUser(
                        User(
                            id = "user_${System.currentTimeMillis()}",
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            phoneCountryCode = selectedCountryCode,
                            phoneNumber = phone,
                            dateOfBirth = dob,
                            gender = gender,
                            profileImageUrl = null,
                            activeRole = "BUYER"
                        )
                    )
                    if (registered) {
                        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("signup") { inclusive = true }
                        }
                    } else {
                        emailError = "Email already registered"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Create Account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Link back to Login
        Text(
            text = buildAnnotatedString {
                append("Already have an account? ")
                withStyle(style = SpanStyle(color = ResidoBlue, fontWeight = FontWeight.Bold)) {
                    append("Login")
                }
            },
            fontSize = 15.sp,
            color = ResidoDarkNavy.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
                .padding(8.dp)
        )
    }
}
