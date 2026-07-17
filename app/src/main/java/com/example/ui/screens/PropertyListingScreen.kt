package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.models.Property
import com.example.data.ResidoServiceLocator
import com.example.ui.theme.ResidoAccent
import com.example.ui.theme.ResidoBackground
import com.example.ui.theme.ResidoBlue
import com.example.ui.theme.ResidoDarkNavy
import com.example.ui.theme.ResidoTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyListingScreen(
    navController: NavController,
    initialCity: String = "All",
    initialCategory: String = "All",
    initialType: String = "All"
) {
    val context = LocalContext.current
    val propertyRepository = ResidoServiceLocator.propertyRepository
    val properties by propertyRepository.propertiesState.collectAsState()

    // Filtering states
    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf(initialCity) }
    var showFiltersSheet by remember { mutableStateOf(false) }

    // Filters Model States
    var minPriceInput by remember { mutableStateOf("") }
    var maxPriceInput by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(if (initialCategory != "All") setOf(initialCategory) else emptySet<String>()) }
    var selectedBedrooms by remember { mutableStateOf<Int?>(null) } // null is "Any"
    var selectedBathrooms by remember { mutableStateOf<Int?>(null) } // null is "Any"
    var selectedType by remember { mutableStateOf(if (initialType != "All") initialType else "All") } // "All" | "BUY" | "RENT"

    // Applied states that we actually filter by
    var appliedMinPrice by remember { mutableStateOf<Double?>(null) }
    var appliedMaxPrice by remember { mutableStateOf<Double?>(null) }
    var appliedCategories by remember { mutableStateOf(selectedCategories) }
    var appliedBedrooms by remember { mutableStateOf<Int?>(null) }
    var appliedBathrooms by remember { mutableStateOf<Int?>(null) }
    var appliedType by remember { mutableStateOf(selectedType) }

    // Reset All filters
    fun resetFilters() {
        minPriceInput = ""
        maxPriceInput = ""
        selectedCategories = emptySet()
        selectedBedrooms = null
        selectedBathrooms = null
        selectedType = "All"

        appliedMinPrice = null
        appliedMaxPrice = null
        appliedCategories = emptySet()
        appliedBedrooms = null
        appliedBathrooms = null
        appliedType = "All"
        selectedCity = "All"
        searchQuery = ""
    }

    // Filter logic
    val filteredList = properties.filter { property ->
        val matchesSearch = property.title.contains(searchQuery, ignoreCase = true) ||
                property.description.contains(searchQuery, ignoreCase = true) ||
                property.address.contains(searchQuery, ignoreCase = true)

        val matchesCity = selectedCity == "All" || property.city.equals(selectedCity, ignoreCase = true)

        val matchesType = when (appliedType) {
            "All" -> true
            "BUY" -> property.listingType == "BUY"
            "RENT" -> property.listingType == "RENT"
            else -> true
        }

        val matchesCategories = appliedCategories.isEmpty() || appliedCategories.contains(property.category)

        val matchesBedrooms = appliedBedrooms == null || property.bedrooms >= appliedBedrooms!!
        val matchesBathrooms = appliedBathrooms == null || property.bathrooms >= appliedBathrooms!!

        // Pricing matches (handle Rent vs Buy conversion factors if needed, but here we evaluate the raw price field)
        // Note: For consistent filtering, we scale by 300,000 for BUY and 300 for RENT to evaluate standard LKR value.
        val actualLkrPrice = if (property.listingType == "RENT") property.price * 300 else property.price * 300000
        val matchesMinPrice = appliedMinPrice == null || actualLkrPrice >= appliedMinPrice!!
        val matchesMaxPrice = appliedMaxPrice == null || actualLkrPrice <= appliedMaxPrice!!

        matchesSearch && matchesCity && matchesType && matchesCategories && matchesBedrooms && matchesBathrooms && matchesMinPrice && matchesMaxPrice
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (selectedCity == "All") "All Properties" else "Properties in $selectedCity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ResidoDarkNavy
                        )
                        Text(
                            text = "${filteredList.size} matching results",
                            fontSize = 12.sp,
                            color = ResidoTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ResidoDarkNavy)
                    }
                },
                actions = {
                    IconButton(onClick = { showFiltersSheet = true }) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = "Filters", tint = ResidoBlue)
                            // Show dot if any custom filters are applied
                            val isFilterApplied = appliedMinPrice != null || appliedMaxPrice != null ||
                                    appliedCategories.isNotEmpty() || appliedBedrooms != null ||
                                    appliedBathrooms != null || appliedType != "All"
                            if (isFilterApplied) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .align(Alignment.TopEnd)
                                        .background(Color.Red, CircleShape)
                                        .border(1.dp, Color.White, CircleShape)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = ResidoBackground
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Search Input Field
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search title, address...", color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ResidoBlue) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.LightGray)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = ResidoDarkNavy,
                        unfocusedTextColor = ResidoDarkNavy
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Optional Horizontal Quick City Chips if initial was "All"
                if (initialCity == "All") {
                    val quickCities = listOf("All", "Colombo", "Kandy", "Negombo", "Galle", "Badulla")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickCities) { city ->
                            val isSelected = selectedCity == city
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) ResidoBlue else Color.White)
                                    .border(1.dp, if (isSelected) ResidoBlue else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable { selectedCity = city }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = city,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else ResidoDarkNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Results list
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Cabin, contentDescription = "No properties", tint = Color.LightGray, modifier = Modifier.size(72.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No properties match your filter", color = ResidoDarkNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Try adjusting your price range or category criteria", color = ResidoTextSecondary, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { resetFilters() },
                                colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue)
                            ) {
                                Text("Reset Filters")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredList) { property ->
                            PropertyRowCard(
                                property = property,
                                onFavClick = {
                                    propertyRepository.toggleFavorite(property.id)
                                },
                                modifier = Modifier.clickable {
                                    navController.navigate("property_details/${property.id}")
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // High-fidelity Custom slide-up Search Filters Pane
            AnimatedVisibility(
                visible = showFiltersSheet,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showFiltersSheet = false }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .clickable(enabled = false) {}, // prevent click-through
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Search Filters",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResidoDarkNavy
                                )
                                IconButton(onClick = { showFiltersSheet = false }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = ResidoDarkNavy)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Listing Type Select: Buy vs Rent
                            Text(text = "Listing Type", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                listOf("All" to "All Listings", "BUY" to "Buy (💲)", "RENT" to "Rent (🔑)").forEach { option ->
                                    val isSelected = selectedType == option.first
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) ResidoBlue else Color(0xFFF1F5F9))
                                            .clickable { selectedType = option.first }
                                            .padding(vertical = 10.dp),
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

                            // Price Range Fields
                            Text(text = "Price Range (LKR)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                TextField(
                                    value = minPriceInput,
                                    onValueChange = { minPriceInput = it },
                                    placeholder = { Text("Min Price") },
                                    modifier = Modifier.weight(1f).height(50.dp).shadow(1.dp, RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF8FAFC),
                                        unfocusedContainerColor = Color(0xFFF8FAFC),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                TextField(
                                    value = maxPriceInput,
                                    onValueChange = { maxPriceInput = it },
                                    placeholder = { Text("Max Price") },
                                    modifier = Modifier.weight(1f).height(50.dp).shadow(1.dp, RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF8FAFC),
                                        unfocusedContainerColor = Color(0xFFF8FAFC),
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Category selector (Multiple select)
                            Text(text = "Property Category", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("House", "Apartment", "Land", "Commercial").forEach { category ->
                                    val isSelected = selectedCategories.contains(category)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) ResidoBlue.copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                                            .border(1.dp, if (isSelected) ResidoBlue else Color.Transparent, RoundedCornerShape(12.dp))
                                            .clickable {
                                                selectedCategories = if (isSelected) {
                                                    selectedCategories - category
                                                } else {
                                                    selectedCategories + category
                                                }
                                            }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = category,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) ResidoBlue else ResidoDarkNavy
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Bedrooms Selector
                            Text(text = "Bedrooms Required (or more)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                (listOf(null) + (1..5)).forEach { count ->
                                    val isSelected = selectedBedrooms == count
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) ResidoBlue else Color(0xFFF1F5F9))
                                            .clickable { selectedBedrooms = count },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = count?.toString() ?: "Any",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else ResidoDarkNavy
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Bathrooms Selector
                            Text(text = "Bathrooms Required (or more)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                (listOf(null) + (1..5)).forEach { count ->
                                    val isSelected = selectedBathrooms == count
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) ResidoBlue else Color(0xFFF1F5F9))
                                            .clickable { selectedBathrooms = count },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = count?.toString() ?: "Any",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else ResidoDarkNavy
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            // Apply and Reset Buttons
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = {
                                        resetFilters()
                                        showFiltersSheet = false
                                        Toast.makeText(context, "Filters reset successfully", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(0.4f).height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
                                ) {
                                    Text("Reset", color = ResidoDarkNavy, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Button(
                                    onClick = {
                                        appliedMinPrice = minPriceInput.toDoubleOrNull()
                                        appliedMaxPrice = maxPriceInput.toDoubleOrNull()
                                        appliedCategories = selectedCategories
                                        appliedBedrooms = selectedBedrooms
                                        appliedBathrooms = selectedBathrooms
                                        appliedType = selectedType
                                        showFiltersSheet = false
                                        Toast.makeText(context, "Filters applied successfully!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(0.6f).height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue)
                                ) {
                                    Text("Apply Filters", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyRowCard(property: Property, onFavClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(115.dp)
                    .fillMaxHeight()
            ) {
                coil.compose.AsyncImage(
                    model = property.imageUrls.firstOrNull(),
                    contentDescription = property.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (property.listingType == "RENT") ResidoAccent else ResidoBlue)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = property.listingType, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Text(
                    text = property.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResidoDarkNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Loc", tint = ResidoBlue, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${property.city}, Sri Lanka", fontSize = 11.sp, color = ResidoTextSecondary)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (property.listingType == "RENT") "LKR ${String.format("%,.0f", property.price * 300)}/mo" else "LKR ${String.format("%,.0f", property.price * 300000)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ResidoBlue
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "${property.bedrooms} Beds", fontSize = 10.sp, color = ResidoTextSecondary)
                    Text(text = "${property.bathrooms} Baths", fontSize = 10.sp, color = ResidoTextSecondary)
                    Text(text = "${property.areaSqft.toInt()} Sqft", fontSize = 10.sp, color = ResidoTextSecondary)
                }
            }

            IconButton(
                onClick = onFavClick,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 6.dp)
            ) {
                Icon(
                    imageVector = if (property.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (property.isFavorite) Color.Red else Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
