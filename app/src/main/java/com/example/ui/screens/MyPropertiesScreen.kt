package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.data.models.Property
import com.example.data.ResidoServiceLocator
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPropertiesScreen(navController: NavController) {
    val context = LocalContext.current
    val propertyRepository = ResidoServiceLocator.propertyRepository
    val userRepository = ResidoServiceLocator.userRepository

    val properties by propertyRepository.propertiesState.collectAsState()
    val currentUser by userRepository.currentUserState.collectAsState()

    val myProperties = remember(properties, currentUser) {
        val currentUserId = currentUser?.id ?: "user_current"
        properties.filter { it.ownerId == currentUserId }
    }

    var propertyToDelete by remember { mutableStateOf<Property?>(null) }

    if (propertyToDelete != null) {
        AlertDialog(
            onDismissRequest = { propertyToDelete = null },
            title = { Text("Delete Listing?") },
            text = { Text("Are you sure you want to permanently delete \"${propertyToDelete?.title}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val prop = propertyToDelete
                        if (prop != null) {
                            propertyRepository.deleteProperty(prop.id)
                            Toast.makeText(context, "Listing deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                        propertyToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { propertyToDelete = null }) {
                    Text("Cancel", color = ResidoDarkNavy)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Posted Listings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ResidoDarkNavy
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ResidoDarkNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.shadow(2.dp)
            )
        },
        containerColor = ResidoBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (myProperties.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(ResidoBlue.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HomeWork,
                            contentDescription = "Empty My Properties",
                            tint = ResidoBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "No Listings Yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ResidoDarkNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You haven't published any real estate listings yet. Start posting to attract potential buyers and renters in Sri Lanka.",
                        fontSize = 14.sp,
                        color = ResidoTextSecondary,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = LocalTextStyle.current.copy(lineHeight = 20.sp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = { navController.navigate("add_property") },
                        colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .width(220.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            "Post Your First Property",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(myProperties, key = { it.id }) { property ->
                        MyPropertyItem(
                            property = property,
                            onClick = {
                                navController.navigate("property_details/${property.id}")
                            },
                            onEdit = {
                                navController.navigate("add_property?propertyId=${property.id}")
                            },
                            onDelete = {
                                propertyToDelete = property
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyPropertyItem(
    property: Property,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("my_property_item_${property.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(12.dp)
            ) {
                // Property image
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = property.imageUrls.firstOrNull(),
                        contentDescription = property.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (property.listingType == "RENT") ResidoAccent else ResidoBlue)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (property.listingType == "RENT") "RENT" else "BUY",
                            fontSize = 8.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Detail info
                Column(modifier = Modifier.weight(1f)) {
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
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Pin",
                            tint = ResidoBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${property.city}, Sri Lanka",
                            fontSize = 11.sp,
                            color = ResidoTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (property.listingType == "RENT") {
                            "LKR ${String.format("%,.0f", property.price * 300)}/mo"
                        } else {
                            "LKR ${String.format("%,.0f", property.price * 300000)}"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ResidoBlue
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Edit & Delete row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFCFE))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onEdit,
                    modifier = Modifier.testTag("my_property_edit_${property.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Listing",
                        tint = ResidoBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit", color = ResidoBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("my_property_delete_${property.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Listing",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
