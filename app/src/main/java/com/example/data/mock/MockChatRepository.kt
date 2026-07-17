package com.example.data.mock

import com.example.data.models.Conversation
import com.example.data.models.Message
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockChatRepository : ChatRepository {
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    override val conversationsState: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    override val messagesState: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    init {
        val conv1Id = "conv_1"
        val conv2Id = "conv_2"

        val initialConversations = listOf(
            Conversation(
                id = conv1Id,
                propertyId = "prop_1",
                buyerId = "user_current",
                agentId = "agent_1",
                lastMessage = "Great, the apartment is available for viewing on Sunday at 10:00 AM. I have sent an approval request.",
                lastMessageTimestamp = "10:15 AM",
                unreadCount = 1
            ),
            Conversation(
                id = conv2Id,
                propertyId = "prop_3",
                buyerId = "user_current",
                agentId = "agent_2",
                lastMessage = "Is the price negotiable?",
                lastMessageTimestamp = "Yesterday",
                unreadCount = 0
            )
        )

        val initialMessages = mapOf(
            conv1Id to listOf(
                Message(
                    id = "m1_1",
                    conversationId = conv1Id,
                    senderId = "user_current",
                    text = "Hello Roshan, is this Luxury 3BR apartment still available?",
                    timestamp = "10:00 AM",
                    isRead = true
                ),
                Message(
                    id = "m1_2",
                    conversationId = conv1Id,
                    senderId = "agent_1",
                    text = "Hi Janaka, yes it is! Would you like to schedule a viewing?",
                    timestamp = "10:05 AM",
                    isRead = true
                ),
                Message(
                    id = "m1_3",
                    conversationId = conv1Id,
                    senderId = "user_current",
                    text = "Yes, Sunday morning would be perfect.",
                    timestamp = "10:10 AM",
                    isRead = true
                ),
                Message(
                    id = "m1_4",
                    conversationId = conv1Id,
                    senderId = "agent_1",
                    text = "Great, the apartment is available for viewing on Sunday at 10:00 AM. I have sent an approval request.",
                    timestamp = "10:15 AM",
                    isRead = false
                )
            ),
            conv2Id to listOf(
                Message(
                    id = "m2_1",
                    conversationId = conv2Id,
                    senderId = "agent_2",
                    text = "Hello Janaka, thank you for your interest in the Colonial Style Villa.",
                    timestamp = "Yesterday 2:00 PM",
                    isRead = true
                ),
                Message(
                    id = "m2_2",
                    conversationId = conv2Id,
                    senderId = "user_current",
                    text = "Is the price negotiable?",
                    timestamp = "Yesterday 2:30 PM",
                    isRead = true
                )
            )
        )

        _conversations.value = initialConversations
        _messages.value = initialMessages
    }

    override fun getConversations(): List<Conversation> = _conversations.value

    override fun getMessagesForConversation(conversationId: String): List<Message> {
        return _messages.value[conversationId] ?: emptyList()
    }

    override fun sendMessage(conversationId: String, senderId: String, text: String): Message {
        val messageId = "msg_${System.currentTimeMillis()}"
        val timestamp = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        val newMessage = Message(
            id = messageId,
            conversationId = conversationId,
            senderId = senderId,
            text = text,
            timestamp = timestamp,
            isRead = true
        )

        val currentMessagesMap = _messages.value.toMutableMap()
        val list = (currentMessagesMap[conversationId] ?: emptyList()).toMutableList()
        list.add(newMessage)
        currentMessagesMap[conversationId] = list
        _messages.value = currentMessagesMap

        val updatedConvs = _conversations.value.map { conv ->
            if (conv.id == conversationId) {
                conv.copy(
                    lastMessage = text,
                    lastMessageTimestamp = timestamp,
                    unreadCount = if (senderId == conv.buyerId) conv.unreadCount else conv.unreadCount + 1
                )
            } else {
                conv
            }
        }
        _conversations.value = updatedConvs

        return newMessage
    }

    override fun getOrCreateConversation(propertyId: String, buyerId: String, agentId: String): Conversation {
        val existing = _conversations.value.find { 
            it.propertyId == propertyId && it.buyerId == buyerId && it.agentId == agentId 
        }
        if (existing != null) return existing

        val newId = "conv_${System.currentTimeMillis()}"
        val newConv = Conversation(
            id = newId,
            propertyId = propertyId,
            buyerId = buyerId,
            agentId = agentId,
            lastMessage = "Conversation started about this property.",
            lastMessageTimestamp = "Just now",
            unreadCount = 0
        )

        val updatedConvs = _conversations.value.toMutableList()
        updatedConvs.add(0, newConv)
        _conversations.value = updatedConvs

        val currentMessagesMap = _messages.value.toMutableMap()
        currentMessagesMap[newId] = listOf(
            Message(
                id = "msg_init_${System.currentTimeMillis()}",
                conversationId = newId,
                senderId = agentId,
                text = "Hi! Thank you for inquiring about this listing. How can I help you?",
                timestamp = "Just now",
                isRead = true
            )
        )
        _messages.value = currentMessagesMap

        return newConv
    }

    override fun markAsRead(conversationId: String) {
        val currentMessagesMap = _messages.value.toMutableMap()
        val list = currentMessagesMap[conversationId]?.map {
            if (!it.isRead) it.copy(isRead = true) else it
        }
        if (list != null) {
            currentMessagesMap[conversationId] = list
            _messages.value = currentMessagesMap
        }

        val updatedConvs = _conversations.value.map { conv ->
            if (conv.id == conversationId) {
                conv.copy(unreadCount = 0)
            } else {
                conv
            }
        }
        _conversations.value = updatedConvs
    }
}
