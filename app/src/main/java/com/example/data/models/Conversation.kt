package com.example.data.models

data class Conversation(
    val id: String,
    val propertyId: String,        // links back to the Property being discussed
    val buyerId: String,
    val agentId: String,
    val lastMessage: String,
    val lastMessageTimestamp: String,
    val unreadCount: Int
)
