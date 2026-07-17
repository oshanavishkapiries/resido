package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.Property
import com.example.data.ResidoServiceLocator
import com.example.data.User
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class CityData(val name: String, val imageUrl: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val userRepository = ResidoServiceLocator.userRepository
    val propertyRepository = ResidoServiceLocator.propertyRepository

    val currentUser by userRepository.currentUserState.collectAsState()
    val properties by propertyRepository.propertiesState.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Home | 1: Search | 2: Messages | 3: Profile

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account?", fontWeight = FontWeight.Bold, color = ResidoDarkNavy) },
            text = { Text("Are you sure you want to permanently delete your Resido account? This action cannot be undone and all your listings will be removed.", color = ResidoTextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        userRepository.setCurrentUser(null)
                        Toast.makeText(context, "Account permanently deleted.", Toast.LENGTH_LONG).show()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Text("Delete Account", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = ResidoDarkNavy)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout", fontWeight = FontWeight.Bold, color = ResidoDarkNavy) },
            text = { Text("Are you sure you want to log out of Resido?", color = ResidoTextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        userRepository.setCurrentUser(null)
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ResidoBlue)
                ) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = ResidoDarkNavy)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Drawer Header (small profile photo, name, email)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        AsyncImage(
                            model = currentUser?.profileImageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=150",
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, ResidoBlue, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "${currentUser?.firstName ?: "Janaka"} ${currentUser?.lastName ?: "Perera"}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ResidoDarkNavy
                            )
                            Text(
                                text = currentUser?.email ?: "janaka@resido.lk",
                                fontSize = 12.sp,
                                color = ResidoTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu items
                    DrawerMenuItem(icon = Icons.Default.Home, label = "Home Dashboard", isSelected = selectedTab == 0) {
                        scope.launch { drawerState.close() }
                        selectedTab = 0
                    }
                    DrawerMenuItem(icon = Icons.Default.Search, label = "Explore Property Map", isSelected = selectedTab == 1) {
                        scope.launch { drawerState.close() }
                        selectedTab = 1
                    }
                    DrawerMenuItem(icon = Icons.Default.Favorite, label = "Favorite Properties", isSelected = false) {
                        scope.launch { drawerState.close() }
                        navController.navigate("favorites")
                    }
                    DrawerMenuItem(icon = Icons.Default.ListAlt, label = "My Properties", isSelected = false) {
                        scope.launch { drawerState.close() }
                        navController.navigate("my_properties")
                    }
                    DrawerMenuItem(icon = Icons.Default.Security, label = "Privacy Policy", isSelected = false) {
                        scope.launch { drawerState.close() }
                        navController.navigate("privacy_policy")
                    }
                    DrawerMenuItem(icon = Icons.Default.Description, label = "Terms and Conditions", isSelected = false) {
                        scope.launch { drawerState.close() }
                        navController.navigate("terms")
                    }
                    DrawerMenuItem(icon = Icons.Default.Delete, label = "Delete Account", isSelected = false) {
                        scope.launch { drawerState.close() }
                        showDeleteAccountDialog = true
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Logout Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                scope.launch { drawerState.close() }
                                showLogoutDialog = true
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = ResidoBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Logout",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ResidoBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Footer Section (Resido logo, Version 1.0, Need Help? button)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HomeWork,
                                contentDescription = "Logo",
                                tint = ResidoBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Resido Sri Lanka",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResidoDarkNavy
                            )
                        }
                        Text(
                            text = "Version 1.0",
                            fontSize = 11.sp,
                            color = ResidoTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("support")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue.copy(alpha = 0.08f), contentColor = ResidoBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Text("Need Help?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_navigation")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ResidoBlue,
                            selectedTextColor = ResidoBlue,
                            indicatorColor = ResidoBlue.copy(alpha = 0.12f),
                            unselectedIconColor = ResidoTextSecondary,
                            unselectedTextColor = ResidoTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Search/Map") },
                        label = { Text("Search/Map", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ResidoBlue,
                            selectedTextColor = ResidoBlue,
                            indicatorColor = ResidoBlue.copy(alpha = 0.12f),
                            unselectedIconColor = ResidoTextSecondary,
                            unselectedTextColor = ResidoTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            BadgedBox(badge = { Badge { Text("3") } }) {
                                Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = "Messages")
                            }
                        },
                        label = { Text("Messages", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ResidoBlue,
                            selectedTextColor = ResidoBlue,
                            indicatorColor = ResidoBlue.copy(alpha = 0.12f),
                            unselectedIconColor = ResidoTextSecondary,
                            unselectedTextColor = ResidoTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ResidoBlue,
                            selectedTextColor = ResidoBlue,
                            indicatorColor = ResidoBlue.copy(alpha = 0.12f),
                            unselectedIconColor = ResidoTextSecondary,
                            unselectedTextColor = ResidoTextSecondary
                        )
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ResidoBackground)
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> HomeTabContent(
                        currentUser = currentUser,
                        properties = properties,
                        navController = navController,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                    1 -> SearchMapTabContent(
                        properties = properties,
                        propertyRepository = propertyRepository,
                        navController = navController
                    )
                    2 -> MessagesTabContent(navController = navController)
                    3 -> ProfileTabContent(
                        currentUser = currentUser,
                        userRepository = userRepository,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) ResidoBlue.copy(alpha = 0.12f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ResidoBlue else ResidoDarkNavy.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ResidoBlue else ResidoDarkNavy,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ======================== HOME TAB CONTENT ========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTabContent(
    currentUser: User?,
    properties: List<Property>,
    navController: NavController,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current

    // Home selections search criteria
    var searchContextType by remember { mutableStateOf("BUY") } // "BUY" | "RENT"
    var selectedCityDropdown by remember { mutableStateOf("Colombo") }
    var selectedCategoryDropdown by remember { mutableStateOf("House") }

    var cityMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val cities = listOf("All", "Colombo", "Kandy", "Negombo", "Galle", "Badulla", "Jaffna", "Anuradhapura", "Kurunegala")
    val categories = listOf("All", "House", "Apartment", "Land", "Commercial")

    val citiesList = listOf(
        CityData("Colombo", "https://images.unsplash.com/photo-1586861635167-e5223aadc9fe?q=80&w=400"),
        CityData("Kandy", "https://images.unsplash.com/photo-1625736319842-88390e1c26b7?q=80&w=400"),
        CityData("Galle", "https://images.unsplash.com/photo-1568230315894-1edd16d248b7?q=80&w=400"),
        CityData("Negombo", "https://images.unsplash.com/photo-1544644181-1484b3fdfc62?q=80&w=400"),
        CityData("Badulla", "https://images.unsplash.com/photo-1545231027-63b3f16260cd?q=80&w=400"),
        CityData("Jaffna", "https://images.unsplash.com/photo-1590001155093-a3c66ab0c3ff?q=80&w=400"),
        CityData("Anuradhapura", "https://images.unsplash.com/photo-1608958416710-e7f09319803b?q=80&w=400"),
        CityData("Kurunegala", "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?q=80&w=400")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Customized Elegant Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu Drawer", tint = ResidoDarkNavy, modifier = Modifier.size(28.dp))
            }

            Text(
                text = "Greetings, ${currentUser?.firstName ?: "Guest"} 👋",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoDarkNavy,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            val notifications by ResidoServiceLocator.notificationRepository.notificationsState.collectAsState()
            val hasUnread = notifications.any { !it.isRead }

            IconButton(onClick = { navController.navigate("notifications") }) {
                Box {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = ResidoDarkNavy, modifier = Modifier.size(26.dp))
                    if (hasUnread) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .align(Alignment.TopEnd)
                                .background(Color.Red, CircleShape)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                }
            }
        }

        // 2. Hero Banner Section (Premium redesigned hero)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            // Unsplash house background
            AsyncImage(
                model = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?q=80&w=600",
                contentDescription = "Explore Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dynamic blue/navy gradient overlay for visual depth and premium contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1B2A4A).copy(alpha = 0.35f), // ResidoDarkNavy alpha 0.35
                                Color(0xFF1B2A4A).copy(alpha = 0.75f), // ResidoDarkNavy transition
                                Color(0xFF172554).copy(alpha = 0.95f)  // Deepest blue-navy at bottom
                            )
                        )
                    )
            )

            // Content inside Banner
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Header Group
                Column {
                    Text(
                        text = "PREMIUM REAL ESTATE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF93C5FD), // Light blue tonal accent
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore Property In Sri Lanka",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 30.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Find premium houses, lands, and condos easily",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Cohesive, elevated Segmented Control container
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.45f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Segment: Buy
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (searchContextType == "BUY") Color.White else Color.Transparent)
                            .clickable { searchContextType = "BUY" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Buy 💲",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (searchContextType == "BUY") ResidoDarkNavy else Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // Segment: Rent
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (searchContextType == "RENT") Color.White else Color.Transparent)
                            .clickable { searchContextType = "RENT" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Rent 🔑",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (searchContextType == "RENT") ResidoDarkNavy else Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // Segment: Post Property
                    Box(
                        modifier = Modifier
                            .weight(1.7f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ResidoAccent)
                            .clickable { navController.navigate("add_property") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Post Property 🏠",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Dropdowns side-by-side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // City Dropdown Selector
                    Box(modifier = Modifier.weight(1f)) {
                        DropdownSelector(
                            label = selectedCityDropdown,
                            title = "Select City",
                            icon = Icons.Default.LocationOn,
                            onClick = { cityMenuExpanded = true }
                        )
                        DropdownMenu(
                            expanded = cityMenuExpanded,
                            onDismissRequest = { cityMenuExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            cities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city, fontWeight = FontWeight.Bold, color = ResidoDarkNavy) },
                                    onClick = {
                                        selectedCityDropdown = city
                                        cityMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Category Dropdown Selector
                    Box(modifier = Modifier.weight(1f)) {
                        DropdownSelector(
                            label = selectedCategoryDropdown,
                            title = "Select Category",
                            icon = Icons.Default.Home,
                            onClick = { categoryMenuExpanded = true }
                        )
                        DropdownMenu(
                            expanded = categoryMenuExpanded,
                            onDismissRequest = { categoryMenuExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, fontWeight = FontWeight.Bold, color = ResidoDarkNavy) },
                                    onClick = {
                                        selectedCategoryDropdown = cat
                                        categoryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Full-width Search Properties Button with a rich Blue Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(ResidoBlue, Color(0xFF1D4ED8))
                            )
                        )
                        .clickable {
                            navController.navigate("property_listing?city=$selectedCityDropdown&category=$selectedCategoryDropdown&type=$searchContextType")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Search Properties", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.White, letterSpacing = 0.5.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Contextual Role-Based Promotion Banner
        if (currentUser?.activeRole == "AGENT") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { navController.navigate("my_properties") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ResidoBlue.copy(alpha = 0.08f)),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(ResidoBlue, ResidoAccent)
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ResidoBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.HomeWork, contentDescription = null, tint = ResidoBlue)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Agent Dashboard Active 🚀",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ResidoDarkNavy
                        )
                        Text(
                            text = "Manage your property listings, update descriptions, set prices, and post new listings instantly.",
                            fontSize = 12.sp,
                            color = ResidoTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                    Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = ResidoBlue)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { navController.navigate("favorites") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ResidoAccent.copy(alpha = 0.08f)),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(ResidoAccent, ResidoBlue)
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ResidoAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = ResidoAccent)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Browse & Save Favorites! ❤️",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ResidoDarkNavy
                        )
                        Text(
                            text = "Save your dream homes or land plots to your Favorites. Track price reductions and compare easily.",
                            fontSize = 12.sp,
                            color = ResidoTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                    Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = ResidoAccent)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Grid Section Header
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Find your property in your preferred city",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ResidoDarkNavy,
                letterSpacing = (-0.3).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Browse residential or commercial locations across the island",
                fontSize = 13.sp,
                color = ResidoTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Grid of City Cards (2 Columns)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val chunkedCities = citiesList.chunked(2)
            chunkedCities.forEach { rowCities ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCities.forEach { city ->
                        CityCard(
                            city = city,
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clickable {
                                    navController.navigate("property_listing?city=${city.name}&category=All&type=All")
                                }
                        )
                    }
                    if (rowCities.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PillToggle(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = ResidoBlue
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) accentColor else Color.White.copy(alpha = 0.25f))
            .border(1.dp, if (isSelected) accentColor else Color.White.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.White
        )
    }
}

@Composable
fun DropdownSelector(
    label: String,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ResidoBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = ResidoTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResidoDarkNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown Indicator",
                tint = ResidoTextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun CityCard(city: CityData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = city.imageUrl,
                contentDescription = city.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dim tint overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )

            // City name
            Text(
                text = city.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            )
        }
    }
}


// ======================== SEARCH/MAP TAB CONTENT ========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMapTabContent(
    properties: List<Property>,
    propertyRepository: com.example.data.PropertyRepository,
    navController: NavController
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Map dragging position offsets
    var mapOffsetX by remember { mutableStateOf(0f) }
    var mapOffsetY by remember { mutableStateOf(0f) }

    // Selected Property to show overlay review card
    var selectedPropertyOnMap by remember { mutableStateOf<Property?>(null) }

    // Shimmer simulation of map search list
    LaunchedEffect(Unit) {
        delay(1200)
        isLoading = false
    }

    // Interactive custom coordinates map pins: Scale the lat/lng into a local coordinate space
    val activePins = properties.filter { it.city.isNotEmpty() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0)) // Sand map floor color
    ) {
        // Full screen interactive Map Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        mapOffsetX += dragAmount.x
                        mapOffsetY += dragAmount.y
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // Base Map water and road grid lines drawing
            drawRect(
                color = Color(0xFFE2E8F0) // Sand land base
            )

            // Draw a styled Sri Lanka Island shoreline mockup
            drawCircle(
                color = Color(0xFFF1F5F9), // elevated island plateau
                radius = width * 0.45f,
                center = Offset(width / 2 + mapOffsetX, height / 2.2f + mapOffsetY)
            )

            // Draw Ocean blue edge
            drawCircle(
                color = Color(0xFFE0F2FE), // Beach shoreline tint
                radius = width * 0.42f,
                center = Offset(width / 2 + mapOffsetX, height / 2.2f + mapOffsetY)
            )

            // Draw Colombo harbour bay circle
            drawCircle(
                color = Color(0xFFBAE6FD), // ocean deep bay
                radius = 70.dp.toPx(),
                center = Offset(width / 2.8f + mapOffsetX, height / 2.5f + mapOffsetY)
            )

            // Draw stylized white main highways
            drawLine(
                color = Color.White,
                start = Offset(width / 3 + mapOffsetX, height / 4 + mapOffsetY),
                end = Offset(width * 2/3 + mapOffsetX, height * 3/4 + mapOffsetY),
                strokeWidth = 6.dp.toPx()
            )

            drawLine(
                color = Color.White,
                start = Offset(width / 2 + mapOffsetX, height / 6 + mapOffsetY),
                end = Offset(width / 3 + mapOffsetX, height * 4/5 + mapOffsetY),
                strokeWidth = 4.dp.toPx()
            )

            // Draw smaller local road gridlines
            for (i in 1..8) {
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(0f, i * 150.dp.toPx() + mapOffsetY),
                    end = Offset(width, i * 120.dp.toPx() + mapOffsetY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // Draw Interactive Markers on top of Canvas using Compose layout so they can be clicked easily
        activePins.forEachIndexed { index, prop ->
            // Convert coordinate positions locally (distribute across map)
            // Pin Colombo to the left center, Kandy center, Negombo north-left, Galle south-center, Badulla east-center
            val markerX = remember { (100..900).random() }
            val markerY = remember { (200..1200).random() }

            val offsetPos = Offset(
                x = markerX.dp.value + mapOffsetX,
                y = markerY.dp.value + mapOffsetY
            )

            val isSelected = selectedPropertyOnMap?.id == prop.id

            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetPos.x.roundToInt(), offsetPos.y.roundToInt()) }
                    .size(if (isSelected) 56.dp else 44.dp)
                    .clickable {
                        selectedPropertyOnMap = prop
                        Toast.makeText(context, "Selected: ${prop.title}", Toast.LENGTH_SHORT).show()
                    }
            ) {
                // Pin Marker glow animation
                val infiniteTransition = rememberInfiniteTransition(label = "glow")
                val glowScale by infiniteTransition.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 1.6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "scale"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                ) {
                    // Pulsing Ring
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 40.dp else 28.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(
                                (if (isSelected) Color.Red else ResidoBlue).copy(alpha = 0.25f * (2f - glowScale))
                            )
                    )

                    // Pin Bubble Icon
                    Card(
                        modifier = Modifier
                            .size(if (isSelected) 36.dp else 28.dp)
                            .align(Alignment.Center),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color.Red else ResidoBlue),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (prop.category == "Land") Icons.Default.Landscape else Icons.Default.Apartment,
                                contentDescription = "Marker",
                                tint = Color.White,
                                modifier = Modifier.size(if (isSelected) 20.dp else 16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Top Search Location Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .shadow(6.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ResidoBlue)
                Spacer(modifier = Modifier.width(12.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search location or place...", color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        navController.navigate("property_listing?city=All&category=All&type=All")
                    }
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Filters Shortcut", tint = ResidoBlue)
                }
            }
        }

        // Selected Property Preview Card Hover (gorgeous overlay!)
        AnimatedVisibility(
            visible = selectedPropertyOnMap != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 100 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { 100 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 160.dp, start = 16.dp, end = 16.dp)
        ) {
            val hoveredProp = selectedPropertyOnMap
            if (hoveredProp != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable {
                            navController.navigate("property_details/${hoveredProp.id}")
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.width(130.dp).fillMaxHeight()) {
                            AsyncImage(
                                model = hoveredProp.imageUrls.firstOrNull(),
                                contentDescription = hoveredProp.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { selectedPropertyOnMap = null },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(6.dp)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }

                        Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                            Text(text = hoveredProp.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Loc", tint = ResidoBlue, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = "${hoveredProp.city}, SL", fontSize = 11.sp, color = ResidoTextSecondary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (hoveredProp.listingType == "RENT") "LKR ${String.format("%,.0f", hoveredProp.price * 300)}/mo" else "LKR ${String.format("%,.0f", hoveredProp.price * 300000)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = ResidoBlue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "${hoveredProp.bedrooms} Beds", fontSize = 10.sp, color = ResidoTextSecondary)
                                Text(text = "${hoveredProp.bathrooms} Baths", fontSize = 10.sp, color = ResidoTextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // Draggable/Fixed Bottom Sheet Drawer matching: "Searching for properties..." initially, then list of properties
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter)
                .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Pull-handle visual anchor
                Box(
                    modifier = Modifier
                        .size(40.dp, 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.LightGray)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = ResidoBlue, strokeWidth = 2.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Searching for properties in Sri Lanka...", fontSize = 12.sp, color = ResidoTextSecondary, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Matching properties (${properties.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ResidoDarkNavy
                        )
                        Text(
                            text = "Drag up to view",
                            fontSize = 11.sp,
                            color = ResidoTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(properties) { prop ->
                            Card(
                                modifier = Modifier
                                    .width(240.dp)
                                    .height(70.dp)
                                    .clickable {
                                        navController.navigate("property_details/${prop.id}")
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
                            ) {
                                Row(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                                    AsyncImage(
                                        model = prop.imageUrls.firstOrNull(),
                                        contentDescription = prop.title,
                                        modifier = Modifier.size(58.dp).clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = prop.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ResidoDarkNavy,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (prop.listingType == "RENT") "LKR ${String.format("%,.0f", prop.price * 300)}/mo" else "LKR ${String.format("%,.0f", prop.price * 300000)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ResidoBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ======================== MESSAGES TAB CONTENT ========================

@Composable
fun MessagesTabContent(navController: NavController) {
    val context = LocalContext.current
    val chatRepository = ResidoServiceLocator.chatRepository
    val userRepository = ResidoServiceLocator.userRepository
    val propertyRepository = ResidoServiceLocator.propertyRepository

    val currentUser by userRepository.currentUserState.collectAsState()
    val conversations by chatRepository.conversationsState.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val userConversations = remember(conversations, currentUser) {
        conversations.filter { it.buyerId == currentUser?.id || it.agentId == currentUser?.id }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ResidoBlue)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Messages",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.testTag("messages_title")
                )
                
                IconButton(
                    onClick = {
                        scope.launch {
                            isRefreshing = true
                            delay(800)
                            isRefreshing = false
                            Toast.makeText(context, "Inbox updated", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = ResidoAccent,
                    trackColor = ResidoBlue.copy(alpha = 0.2f)
                )
            }
        }

        if (userConversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(ResidoBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "No Chats Icon",
                                tint = ResidoBlue,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 8.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "No Chats",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResidoDarkNavy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "We'll let you know when there will be something to update you.",
                        fontSize = 14.sp,
                        color = ResidoTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(userConversations) { conv ->
                    val otherPartyId = if (currentUser?.id == conv.buyerId) conv.agentId else conv.buyerId
                    val otherPartyUser = remember(otherPartyId) { userRepository.getUserById(otherPartyId) }
                    val associatedProperty = remember(conv.propertyId) { propertyRepository.getPropertyById(conv.propertyId) }

                    val displayName = otherPartyUser?.let { "${it.firstName} ${it.lastName}" } ?: "Resido Agent"
                    val avatarUrl = otherPartyUser?.profileImageUrl

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("chat/${conv.id}")
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF08A)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!avatarUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = avatarUrl,
                                        contentDescription = "Profile Photo",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = displayName.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = ResidoDarkNavy,
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = displayName,
                                        fontWeight = FontWeight.Bold,
                                        color = ResidoDarkNavy,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = conv.lastMessageTimestamp,
                                        fontSize = 11.sp,
                                        color = ResidoTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                associatedProperty?.let { prop ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFF1F5F9))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = "Property Reference",
                                            tint = ResidoBlue,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = prop.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = ResidoBlue,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = conv.lastMessage,
                                        fontSize = 13.sp,
                                        color = if (conv.unreadCount > 0) ResidoDarkNavy else ResidoTextSecondary,
                                        fontWeight = if (conv.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    if (conv.unreadCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(ResidoBlue),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = conv.unreadCount.toString(),
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ======================== PROFILE TAB CONTENT ========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTabContent(currentUser: User?, userRepository: com.example.data.UserRepository, navController: NavController) {
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf(currentUser?.firstName ?: "") }
    var lastName by remember { mutableStateOf(currentUser?.lastName ?: "") }
    var dob by remember { mutableStateOf(currentUser?.dateOfBirth ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phoneNumber ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var gender by remember { mutableStateOf(currentUser?.gender ?: "Male") }
    var selectedAvatar by remember { mutableStateOf(currentUser?.profileImageUrl ?: "") }

    var genderMenuExpanded by remember { mutableStateOf(false) }
    var showAvatarPickerDialog by remember { mutableStateOf(false) }

    val curatedAvatars = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=200",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=200",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=200",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?q=80&w=200",
        "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?q=80&w=200"
    )

    LaunchedEffect(currentUser, isEditing) {
        if (!isEditing && currentUser != null) {
            firstName = currentUser.firstName
            lastName = currentUser.lastName
            dob = currentUser.dateOfBirth
            phone = currentUser.phoneNumber
            email = currentUser.email
            gender = currentUser.gender
            selectedAvatar = currentUser.profileImageUrl ?: ""
        }
    }

    if (showAvatarPickerDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarPickerDialog = false },
            title = { Text("Choose Profile Photo", fontWeight = FontWeight.Bold, color = ResidoDarkNavy) },
            text = {
                Column {
                    Text("Select a beautiful avatar for your Resido profile:", fontSize = 13.sp, color = ResidoTextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        curatedAvatars.forEach { avatarUrl ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (selectedAvatar == avatarUrl) 3.dp else 0.dp,
                                        color = if (selectedAvatar == avatarUrl) ResidoBlue else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedAvatar = avatarUrl
                                        if (!isEditing && currentUser != null) {
                                            userRepository.updateCurrentUser(currentUser.copy(profileImageUrl = avatarUrl))
                                        }
                                        showAvatarPickerDialog = false
                                        Toast.makeText(context, "Profile photo updated!", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "Avatar Options",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarPickerDialog = false }) {
                    Text("Cancel", color = ResidoDarkNavy)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ResidoDarkNavy,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Manage your account and preferences",
                    fontSize = 13.sp,
                    color = ResidoTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Role Toggle Segmented Control (Buyer vs. Agent)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE2E8F0))
                .padding(4.dp)
        ) {
            val isBuyer = currentUser?.activeRole == "BUYER"
            
            // Buyer Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isBuyer) ResidoBlue else Color.Transparent)
                    .clickable { 
                        if (!isBuyer) {
                            userRepository.toggleUserRole()
                            Toast.makeText(context, "Switched to Buyer Mode", Toast.LENGTH_SHORT).show()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Buyer Mode",
                    color = if (isBuyer) Color.White else ResidoDarkNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Agent Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (!isBuyer) ResidoBlue else Color.Transparent)
                    .clickable { 
                        if (isBuyer) {
                            userRepository.toggleUserRole()
                            Toast.makeText(context, "Switched to Agent / Owner Mode", Toast.LENGTH_SHORT).show()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Agent / Owner",
                    color = if (!isBuyer) Color.White else ResidoDarkNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large circular profile photo with colored/yellow-tinted background circle
        Box(
            modifier = Modifier
                .size(124.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEF08A)) // soft yellow-tinted background circle
                .clickable {
                    showAvatarPickerDialog = true
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = selectedAvatar.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=200" },
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
            // Edit Badge Overlay
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(ResidoBlue)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change Photo",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Profile Display/Edit Fields
        ProfileField(
            label = "First Name",
            value = firstName,
            icon = Icons.Default.Person,
            isEditing = isEditing,
            onValueChange = { firstName = it }
        )

        ProfileField(
            label = "Last Name",
            value = lastName,
            icon = Icons.Default.Person,
            isEditing = isEditing,
            onValueChange = { lastName = it }
        )

        ProfileField(
            label = "Date of Birth",
            value = dob,
            icon = Icons.Default.DateRange,
            isEditing = isEditing,
            onValueChange = { dob = it }
        )

        ProfileField(
            label = "Phone Number",
            value = phone,
            icon = Icons.Default.Phone,
            isEditing = isEditing,
            onValueChange = { phone = it },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text("🇱🇰", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+94", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResidoTextSecondary)
                }
            }
        )

        ProfileField(
            label = "Email Address",
            value = email,
            icon = Icons.Default.Email,
            isEditing = isEditing,
            onValueChange = { email = it }
        )

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Text(
                text = "Gender",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ResidoTextSecondary,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            if (isEditing) {
                Box {
                    OutlinedButton(
                        onClick = { genderMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color.LightGray)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ResidoDarkNavy)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Wc, contentDescription = null, tint = ResidoBlue)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(gender, fontSize = 14.sp)
                            }
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = ResidoTextSecondary)
                        }
                    }
                    DropdownMenu(
                        expanded = genderMenuExpanded,
                        onDismissRequest = { genderMenuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        listOf("Male", "Female", "Other").forEach { g ->
                            DropdownMenuItem(
                                text = { Text(g, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    gender = g
                                    genderMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Wc, contentDescription = null, tint = ResidoTextSecondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(gender, fontSize = 14.sp, color = ResidoDarkNavy)
                    }
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.LightGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action button (Edit/Save)
        Button(
            onClick = {
                if (isEditing) {
                    if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
                        Toast.makeText(context, "First Name, Last Name and Email are required!", Toast.LENGTH_SHORT).show()
                    } else {
                        val updatedUser = currentUser?.copy(
                            firstName = firstName,
                            lastName = lastName,
                            dateOfBirth = dob,
                            phoneNumber = phone,
                            email = email,
                            gender = gender,
                            profileImageUrl = selectedAvatar.ifBlank { null }
                        )
                        if (updatedUser != null) {
                            userRepository.updateCurrentUser(updatedUser)
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        }
                        isEditing = false
                    }
                } else {
                    isEditing = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (isEditing) "Save Settings" else "Edit Profile",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ResidoTextSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ResidoBlue,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                leadingIcon = {
                    Icon(imageVector = icon, contentDescription = null, tint = ResidoBlue)
                },
                trailingIcon = trailingContent,
                singleLine = true
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = ResidoTextSecondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = value.ifBlank { "Not specified" },
                    fontSize = 14.sp,
                    color = ResidoDarkNavy,
                    modifier = Modifier.weight(1f)
                )
                if (trailingContent != null) {
                    trailingContent()
                }
            }
        }
    }
}
