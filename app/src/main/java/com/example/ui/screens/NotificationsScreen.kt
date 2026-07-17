package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.NotificationItem
import com.example.data.ResidoServiceLocator
import com.example.ui.theme.ResidoBackground
import com.example.ui.theme.ResidoBlue
import com.example.ui.theme.ResidoDarkNavy
import com.example.ui.theme.ResidoTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val notificationRepository = ResidoServiceLocator.notificationRepository
    val notifications by notificationRepository.notificationsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ResidoDarkNavy) },
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
                .padding(16.dp)
        ) {
            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "None", tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("All caught up!", color = ResidoTextSecondary, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("No notifications yet", color = ResidoTextSecondary.copy(alpha = 0.8f), fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(notifications, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    notificationRepository.markAsRead(item.id)
                                    if (item.relatedId != null) {
                                        if (item.type == "MESSAGE") {
                                            navController.navigate("chat/${item.relatedId}")
                                        } else {
                                            navController.navigate("property_details/${item.relatedId}")
                                        }
                                    }
                                }
                                .testTag("notification_item_${item.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.isRead) Color.White else Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (item.isRead) 1.dp else 3.dp),
                            border = if (!item.isRead) ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (item.type) {
                                                "APPROVED" -> Color(0xFFE8F5E9)
                                                "NEW" -> ResidoBlue.copy(alpha = 0.12f)
                                                "MESSAGE" -> Color(0xFFFEF08A)
                                                else -> Color(0xFFFFF3E0)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (item.type) {
                                            "APPROVED" -> Icons.Default.Star
                                            "MESSAGE" -> Icons.Default.Chat
                                            else -> Icons.Default.NotificationsActive
                                        },
                                        contentDescription = "Notification Icon",
                                        tint = when (item.type) {
                                            "APPROVED" -> Color(0xFF4CAF50)
                                            "NEW" -> ResidoBlue
                                            "MESSAGE" -> ResidoBlue
                                            else -> Color(0xFFFF9800)
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = item.title,
                                                fontWeight = if (item.isRead) FontWeight.Bold else FontWeight.ExtraBold,
                                                color = ResidoDarkNavy,
                                                fontSize = 15.sp
                                            )
                                            if (!item.isRead) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(Color.Red)
                                                )
                                            }
                                        }
                                        Text(
                                            text = item.time,
                                            fontSize = 11.sp,
                                            color = ResidoTextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = item.body,
                                        fontSize = 13.sp,
                                        color = if (item.isRead) ResidoTextSecondary else ResidoDarkNavy.copy(alpha = 0.85f),
                                        lineHeight = 18.sp,
                                        fontWeight = if (item.isRead) FontWeight.Normal else FontWeight.Medium
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

