package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.Property
import com.example.data.ResidoServiceLocator
import com.example.ui.theme.ResidoBackground
import com.example.ui.theme.ResidoBlue
import com.example.ui.theme.ResidoDarkNavy
import com.example.ui.theme.ResidoTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(navController: NavController, propertyId: String? = null) {
    val context = LocalContext.current
    val propertyRepository = ResidoServiceLocator.propertyRepository

    val existingProperty = remember(propertyId) {
        if (propertyId != null) propertyRepository.getPropertyById(propertyId) else null
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Colombo") }
    var address by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var areaSqft by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("House") }
    var listingType by remember { mutableStateOf("BUY") } // "BUY" | "RENT"

    LaunchedEffect(existingProperty) {
        existingProperty?.let {
            title = it.title
            description = it.description
            val displayPrice = if (it.listingType == "RENT") it.price * 300 else it.price * 300000
            price = String.format("%.0f", displayPrice)
            city = it.city
            address = it.address
            bedrooms = it.bedrooms.toString()
            bathrooms = it.bathrooms.toString()
            areaSqft = String.format("%.0f", it.areaSqft)
            category = it.category
            listingType = it.listingType
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingProperty != null) "Edit Property Listing" else "Post New Property", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ResidoDarkNavy) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ResidoDarkNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = ResidoBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = if (existingProperty != null) "Update your Property on Resido" else "List your Property on Resido",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ResidoDarkNavy
            )
            Text(
                text = if (existingProperty != null) "Modify your property specifications and pricing below." else "Provide details to publish your listing to thousands of buyers.",
                fontSize = 13.sp,
                color = ResidoTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Listing Type Switch: Buy vs Rent
            Text(text = "Listing Purpose", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("BUY" to "Sell Property", "RENT" to "Rent Property").forEach { option ->
                    val isSelected = listingType == option.first
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ResidoBlue else Color.White)
                            .shadow(if (isSelected) 2.dp else 0.dp, RoundedCornerShape(12.dp))
                            .clickable { listingType = option.first }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.second,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else ResidoDarkNavy
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Property Photos Box (Mock)
            Text(text = "Property Media", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable { Toast.makeText(context, "Opening photo gallery...", Toast.LENGTH_SHORT).show() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = "Upload Photo", tint = ResidoBlue, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Upload Property Images", fontSize = 13.sp, color = ResidoBlue, fontWeight = FontWeight.Bold)
                    Text("Support formats: PNG, JPG (Max 5MB)", fontSize = 11.sp, color = ResidoTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Property Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Property Title") },
                placeholder = { Text("e.g. Modern 3BR Villa with Pool") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Price Field
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(if (listingType == "RENT") "Monthly Rent (LKR)" else "Total Price (LKR)") },
                placeholder = { Text("e.g. 45000000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Address Field
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Street Address") },
                placeholder = { Text("e.g. Galle Road, Colombo 03") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Row for Beds & Baths
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = bedrooms,
                    onValueChange = { bedrooms = it },
                    label = { Text("Beds") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = bathrooms,
                    onValueChange = { bathrooms = it },
                    label = { Text("Baths") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Area Field
            OutlinedTextField(
                value = areaSqft,
                onValueChange = { areaSqft = it },
                label = { Text("Total Area (Sqft)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Detailed Description") },
                placeholder = { Text("Write about features, neighborhood, proximity to schools...") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(14.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (title.isBlank() || price.isBlank() || address.isBlank()) {
                        Toast.makeText(context, "Please fill in title, price, and address fields", Toast.LENGTH_SHORT).show()
                    } else {
                        val parsedPrice = price.toDoubleOrNull() ?: 10000.0
                        // Convert back from LKR inputs to local mock prices if needed
                        val rawPriceValue = if (listingType == "RENT") parsedPrice / 300 else parsedPrice / 300000

                        if (existingProperty != null) {
                            val updatedProp = existingProperty.copy(
                                title = title,
                                description = description.ifBlank { "Beautiful property located in $city." },
                                price = rawPriceValue,
                                listingType = listingType,
                                category = category,
                                city = city,
                                address = address,
                                bedrooms = bedrooms.toIntOrNull() ?: 2,
                                bathrooms = bathrooms.toIntOrNull() ?: 2,
                                areaSqft = areaSqft.toDoubleOrNull() ?: 1200.0
                            )
                            propertyRepository.updateProperty(updatedProp)
                            Toast.makeText(context, "Property listing updated successfully!", Toast.LENGTH_LONG).show()
                        } else {
                            val newProp = Property(
                                id = "prop_${System.currentTimeMillis()}",
                                title = title,
                                description = description.ifBlank { "Beautiful property located in $city." },
                                price = rawPriceValue,
                                listingType = listingType,
                                category = category,
                                city = city,
                                address = address,
                                latitude = 6.9,
                                longitude = 79.8,
                                bedrooms = bedrooms.toIntOrNull() ?: 2,
                                bathrooms = bathrooms.toIntOrNull() ?: 2,
                                areaSqft = areaSqft.toDoubleOrNull() ?: 1200.0,
                                imageUrls = listOf("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?q=80&w=600"),
                                ownerId = "user_current",
                                postedDate = "2026-07-17",
                                isFeatured = false,
                                isFavorite = false
                            )
                            propertyRepository.addProperty(newProp)
                            Toast.makeText(context, "Property listing published successfully!", Toast.LENGTH_LONG).show()
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue)
            ) {
                Text(if (existingProperty != null) "Save Changes" else "Publish Property Listing", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
