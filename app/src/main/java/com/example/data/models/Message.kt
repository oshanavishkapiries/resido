package com.example.data.models

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isRead: Boolean
)
