package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.Property
import com.example.data.ResidoServiceLocator
import com.example.ui.theme.ResidoAccent
import com.example.ui.theme.ResidoBackground
import com.example.ui.theme.ResidoBlue
import com.example.ui.theme.ResidoDarkNavy
import com.example.ui.theme.ResidoTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(navController: NavController, propertyId: String?) {
    val context = LocalContext.current
    val propertyRepository = ResidoServiceLocator.propertyRepository
    val userRepository = ResidoServiceLocator.userRepository

    val properties by propertyRepository.propertiesState.collectAsState()
    val property = properties.find { it.id == propertyId }

    if (property == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(ResidoBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Error", tint = Color.LightGray, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Property not found", color = ResidoDarkNavy, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue)) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    var isFavorite by remember { mutableStateOf(property.isFavorite) }
    var descriptionExpanded by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val formattedPrice = if (property.listingType == "RENT") {
        "LKR ${String.format("%,.0f", property.price * 300)}/mo"
    } else {
        "LKR ${String.format("%,.0f", property.price * 300000)}"
    }

    val mockAgent = ResidoServiceLocator.userRepository.getCurrentUser()?.copy(
        id = "agent_1",
        firstName = "Roshan",
        lastName = "Silva",
        activeRole = "AGENT",
        profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=200"
    )
    val actualAgent = ResidoServiceLocator.userRepository.getUserById(property.ownerId) ?: mockAgent

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property Details", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ResidoDarkNavy) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ResidoDarkNavy)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            propertyRepository.toggleFavorite(property.id)
                            isFavorite = !isFavorite
                            Toast.makeText(context, if (isFavorite) "Added to Favorites" else "Removed from Favorites", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else ResidoDarkNavy
                        )
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
        ) {
            // Main Property Image Gallery
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                AsyncImage(
                    model = property.imageUrls.firstOrNull(),
                    contentDescription = property.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Listing Type Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (property.listingType == "RENT") ResidoAccent else ResidoBlue)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (property.listingType == "RENT") "FOR RENT" else "FOR SALE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }

                // Category Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ResidoDarkNavy.copy(alpha = 0.8f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = property.category.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Title and Price Block
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = property.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ResidoDarkNavy,
                            lineHeight = 28.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = ResidoBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${property.address}, ${property.city}",
                                fontSize = 14.sp,
                                color = ResidoTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price display with elegant background card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Price", fontSize = 12.sp, color = ResidoTextSecondary, fontWeight = FontWeight.Medium)
                            Text(
                                text = formattedPrice,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = ResidoBlue
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ResidoBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = "Verified", tint = ResidoBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verified List", fontSize = 12.sp, color = ResidoBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Key specifications Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Bed
                    SpecCard(
                        icon = Icons.Default.Bed,
                        value = "${property.bedrooms} Beds",
                        label = "Bedrooms",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    // Bath
                    SpecCard(
                        icon = Icons.Default.Bathtub,
                        value = "${property.bathrooms} Baths",
                        label = "Bathrooms",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    // Area
                    SpecCard(
                        icon = Icons.Default.SquareFoot,
                        value = "${property.areaSqft.toInt()} Sqft",
                        label = "Build Area",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Property Description
                Text("Description", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .clickable { descriptionExpanded = !descriptionExpanded }
                ) {
                    Text(
                        text = property.description,
                        fontSize = 14.sp,
                        color = ResidoTextSecondary,
                        lineHeight = 22.sp,
                        maxLines = if (descriptionExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (descriptionExpanded) "Read Less" else "Read More...",
                        color = ResidoBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Agent Information
                Text("Listing Agent", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ResidoBlue.copy(alpha = 0.15f))
                                .border(1.5.dp, ResidoBlue, CircleShape)
                        ) {
                            AsyncImage(
                                model = actualAgent?.profileImageUrl ?: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=200",
                                contentDescription = "Agent",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${actualAgent?.firstName ?: "Roshan"} ${actualAgent?.lastName ?: "Silva"}",
                                fontWeight = FontWeight.Bold,
                                color = ResidoDarkNavy,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Certified Resido Partner",
                                fontSize = 12.sp,
                                color = ResidoTextSecondary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Calling ${actualAgent?.firstName} at +94 ${actualAgent?.phoneNumber ?: "779876543"}...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ResidoBlue.copy(alpha = 0.12f))
                            ) {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = "Call Agent", tint = ResidoBlue, modifier = Modifier.size(20.dp))
                            }

                            IconButton(
                                onClick = {
                                    val currentUserObj = userRepository.getCurrentUser()
                                    if (currentUserObj == null) {
                                        Toast.makeText(context, "Please sign in to chat with the agent", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val agentId = actualAgent?.id ?: "agent_1"
                                        val conversation = ResidoServiceLocator.chatRepository.getOrCreateConversation(
                                            propertyId = property.id,
                                            buyerId = currentUserObj.id,
                                            agentId = agentId
                                        )
                                        navController.navigate("chat/${conversation.id}")
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ResidoAccent.copy(alpha = 0.12f))
                            ) {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat Agent", tint = ResidoAccent, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Beautiful Book Viewings Scheduler
                Text("Schedule a Viewing", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy)
                Spacer(modifier = Modifier.height(12.dp))

                val dates = listOf(
                    "Today" to "Jul 17",
                    "Tomorrow" to "Jul 18",
                    "Sunday" to "Jul 19",
                    "Monday" to "Jul 20",
                    "Tuesday" to "Jul 21"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dates) { item ->
                        val isSelected = selectedDate == item.second
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ResidoBlue else Color.White)
                                .border(1.dp, if (isSelected) ResidoBlue else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable { selectedDate = item.second }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = item.first,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else ResidoTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.second,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color.White else ResidoDarkNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (selectedDate.isEmpty()) {
                            Toast.makeText(context, "Please select a viewing date first", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Viewing requested for $selectedDate successfully!", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(6.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue)
                ) {
                    Text("Request Viewing Session", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SpecCard(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(84.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = ResidoBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ResidoDarkNavy, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = label, fontSize = 10.sp, color = ResidoTextSecondary)
        }
    }
}
