package com.example.data.repository

import com.example.data.models.Conversation
import com.example.data.models.Message
import kotlinx.coroutines.flow.StateFlow

interface ChatRepository {
    val conversationsState: StateFlow<List<Conversation>>
    val messagesState: StateFlow<Map<String, List<Message>>>
    fun getConversations(): List<Conversation>
    fun getMessagesForConversation(conversationId: String): List<Message>
    fun sendMessage(conversationId: String, senderId: String, text: String): Message
    fun getOrCreateConversation(propertyId: String, buyerId: String, agentId: String): Conversation
    fun markAsRead(conversationId: String)
}
