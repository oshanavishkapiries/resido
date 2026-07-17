package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.models.Conversation
import com.example.data.models.Message
import com.example.data.ResidoServiceLocator
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, conversationId: String) {
    val context = LocalContext.current
    val chatRepository = ResidoServiceLocator.chatRepository
    val userRepository = ResidoServiceLocator.userRepository
    val propertyRepository = ResidoServiceLocator.propertyRepository

    val currentUser by userRepository.currentUserState.collectAsState()
    val conversations by chatRepository.conversationsState.collectAsState()
    val messagesMap by chatRepository.messagesState.collectAsState()

    val conversation = remember(conversations, conversationId) {
        conversations.find { it.id == conversationId }
    }

    // Mark conversation as read upon entering
    LaunchedEffect(conversationId) {
        chatRepository.markAsRead(conversationId)
    }

    if (conversation == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ResidoBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Error",
                    tint = Color.LightGray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Chat not found", color = ResidoDarkNavy, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = ResidoBlue)) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    val otherPartyId = if (currentUser?.id == conversation.buyerId) conversation.agentId else conversation.buyerId
    val otherUser = remember(otherPartyId) { userRepository.getUserById(otherPartyId) }
    val associatedProperty = remember(conversation.propertyId) { propertyRepository.getPropertyById(conversation.propertyId) }

    val otherPartyName = otherUser?.let { "${it.firstName} ${it.lastName}" } ?: "Resido Agent"
    val otherPartyPhoto = otherUser?.profileImageUrl
    
    val messages = messagesMap[conversationId] ?: emptyList()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to the latest message when opening the screen or when a new message is appended
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Column {
                // Top Bar: back button, other party's photo + name
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF08A)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!otherPartyPhoto.isNullOrBlank()) {
                                    AsyncImage(
                                        model = otherPartyPhoto,
                                        contentDescription = "Agent Photo",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = otherPartyName.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = ResidoDarkNavy,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = otherPartyName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResidoDarkNavy
                                )
                                Text(
                                    text = "Online",
                                    fontSize = 11.sp,
                                    color = Color(0xFF10B981), // green dot text
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
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
                    modifier = Modifier.shadow(1.dp)
                )

                // Small property reference chip/banner (tapping it navigates to that Property's Details screen)
                associatedProperty?.let { prop ->
                    val formattedPrice = if (prop.listingType == "RENT") {
                        "LKR ${String.format("%,.0f", prop.price * 300)}/mo"
                    } else {
                        "LKR ${String.format("%,.0f", prop.price * 300000)}"
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFF6FF)) // light soft blue background
                            .clickable {
                                navController.navigate("property_details/${prop.id}")
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Property thumbnail
                            Card(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(6.dp),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                AsyncImage(
                                    model = prop.imageUrls.firstOrNull() ?: "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?q=80&w=200",
                                    contentDescription = "Property Thumbnail",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = prop.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResidoDarkNavy,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = formattedPrice,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ResidoBlue
                                )
                            }
                        }

                        // Right pointing arrow or chip indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ResidoBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "View Property",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResidoBlue
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = ResidoBlue,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ResidoBackground)
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Message List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages) { message ->
                            val isOutgoing = message.senderId == currentUser?.id

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                Column(
                                    horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start
                                ) {
                                    // Bubble Container
                                    Box(
                                        modifier = Modifier
                                            .widthIn(max = 280.dp)
                                            .clip(
                                                RoundedCornerShape(
                                                    topStart = 16.dp,
                                                    topEnd = 16.dp,
                                                    bottomStart = if (isOutgoing) 16.dp else 4.dp,
                                                    bottomEnd = if (isOutgoing) 4.dp else 16.dp
                                                )
                                            )
                                            .background(if (isOutgoing) ResidoBlue else Color(0xFFE2E8F0))
                                            .padding(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = message.text,
                                            color = if (isOutgoing) Color.White else ResidoDarkNavy,
                                            fontSize = 14.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(3.dp))
                                    
                                    // Timestamp
                                    Text(
                                        text = message.timestamp,
                                        fontSize = 10.sp,
                                        color = ResidoTextSecondary,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Bottom input bar: text field + send button (paper plane icon)
                    Surface(
                        tonalElevation = 2.dp,
                        color = Color.White,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                placeholder = { Text("Type a message...", fontSize = 14.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input_field"),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ResidoBlue,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC)
                                ),
                                maxLines = 4,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Send
                                ),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (textInput.isNotBlank()) {
                                            chatRepository.sendMessage(
                                                conversationId = conversationId,
                                                senderId = currentUser?.id ?: "user_current",
                                                text = textInput.trim()
                                            )
                                            textInput = ""
                                        }
                                    }
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (textInput.isNotBlank()) {
                                        chatRepository.sendMessage(
                                            conversationId = conversationId,
                                            senderId = currentUser?.id ?: "user_current",
                                            text = textInput.trim()
                                        )
                                        textInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ResidoBlue)
                                    .testTag("chat_send_button"),
                                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
