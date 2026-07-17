package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneCountryCode: String,
    val phoneNumber: String,
    val dateOfBirth: String,
    val gender: String, // "Male" | "Female" | "Other"
    val profileImageUrl: String?,
    val activeRole: String // "BUYER" | "AGENT"
)

data class Property(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val listingType: String, // "BUY" | "RENT"
    val category: String, // "House" | "Apartment" | "Land" | "Commercial"
    val city: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val bedrooms: Int,
    val bathrooms: Int,
    val areaSqft: Double,
    val imageUrls: List<String>,
    val ownerId: String,
    val postedDate: String,
    val isFeatured: Boolean,
    val isFavorite: Boolean
)

data class Conversation(
    val id: String,
    val propertyId: String,        // links back to the Property being discussed
    val buyerId: String,
    val agentId: String,
    val lastMessage: String,
    val lastMessageTimestamp: String,
    val unreadCount: Int
)

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isRead: Boolean
)

interface UserRepository {
    val currentUserState: StateFlow<User?>
    fun getCurrentUser(): User?
    fun setCurrentUser(user: User?)
    fun updateCurrentUser(user: User)
    fun toggleUserRole()
    fun registerUser(user: User): Boolean
    fun getUserById(id: String): User?
}

interface PropertyRepository {
    val propertiesState: StateFlow<List<Property>>
    fun getProperties(): List<Property>
    fun getPropertyById(id: String): Property?
    fun addProperty(property: Property)
    fun updateProperty(property: Property)
    fun deleteProperty(propertyId: String)
    fun toggleFavorite(propertyId: String)
}

object MockDataLayer {

    val mockAgents = listOf(
        User(
            id = "agent_1",
            firstName = "Roshan",
            lastName = "Silva",
            email = "roshan@resido.lk",
            phoneCountryCode = "+94",
            phoneNumber = "779876543",
            dateOfBirth = "1985-11-20",
            gender = "Male",
            profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=200",
            activeRole = "AGENT"
        ),
        User(
            id = "agent_2",
            firstName = "Anura",
            lastName = "Fernando",
            email = "anura@resido.lk",
            phoneCountryCode = "+94",
            phoneNumber = "773334444",
            dateOfBirth = "1978-02-10",
            gender = "Male",
            profileImageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?q=80&w=200",
            activeRole = "AGENT"
        ),
        User(
            id = "agent_3",
            firstName = "Nisha",
            lastName = "Jayasuriya",
            email = "nisha@resido.lk",
            phoneCountryCode = "+94",
            phoneNumber = "772221111",
            dateOfBirth = "1992-07-24",
            gender = "Female",
            profileImageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=200",
            activeRole = "AGENT"
        )
    )

    val defaultCurrentUser = User(
        id = "user_current",
        firstName = "Janaka",
        lastName = "Perera",
        email = "janaka@resido.lk",
        phoneCountryCode = "+94",
        phoneNumber = "771234567",
        dateOfBirth = "1990-05-15",
        gender = "Male",
        profileImageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=200",
        activeRole = "BUYER"
    )

    val baseProperties = listOf(
        Property(
            id = "prop_1",
            title = "Luxury 3BR Apartment in Colombo 3",
            description = "Stunning fully furnished apartment in the heart of Colombo with panoramic ocean views, modern kitchen, and 24/7 security.",
            price = 450000.0,
            listingType = "BUY",
            category = "Apartment",
            city = "Colombo",
            address = "Kollupitiya Road, Colombo 03",
            latitude = 6.9148,
            longitude = 79.8491,
            bedrooms = 3,
            bathrooms = 3,
            areaSqft = 1850.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?q=80&w=600",
                "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?q=80&w=600"
            ),
            ownerId = "agent_1",
            postedDate = "2026-07-01",
            isFeatured = true,
            isFavorite = false
        ),
        Property(
            id = "prop_2",
            title = "Modern 2BR Beachside Condo",
            description = "Relax in this peaceful condo located just footsteps from the famous Negombo Beach. Fully equipped kitchen, pool, and gym access.",
            price = 1500.0,
            listingType = "RENT",
            category = "Apartment",
            city = "Negombo",
            address = "Lewis Place, Negombo",
            latitude = 7.2211,
            longitude = 79.8407,
            bedrooms = 2,
            bathrooms = 2,
            areaSqft = 1100.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1493809842364-78817add7ffb?q=80&w=600"
            ),
            ownerId = "agent_1",
            postedDate = "2026-07-10",
            isFeatured = true,
            isFavorite = true
        ),
        Property(
            id = "prop_3",
            title = "Colonial Style Villa with Tea Garden View",
            description = "Nestled in the lush hills of Kandy, this beautiful colonial estate offers absolute privacy and scenic mountain and tea estate vistas.",
            price = 680000.0,
            listingType = "BUY",
            category = "House",
            city = "Kandy",
            address = "Hantana Road, Kandy",
            latitude = 7.2745,
            longitude = 80.6338,
            bedrooms = 4,
            bathrooms = 4,
            areaSqft = 3500.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1580587771525-78b9dba3b914?q=80&w=600"
            ),
            ownerId = "agent_2",
            postedDate = "2026-06-25",
            isFeatured = true,
            isFavorite = false
        ),
        Property(
            id = "prop_4",
            title = "Cozy 2BR House near Lake",
            description = "A warm, secure cottage situated in a friendly neighborhood near the Kurunegala Lake. Perfect for a small family.",
            price = 450.0,
            listingType = "RENT",
            category = "House",
            city = "Kurunegala",
            address = "Lake Round Road, Kurunegala",
            latitude = 7.4862,
            longitude = 80.3647,
            bedrooms = 2,
            bathrooms = 1,
            areaSqft = 1200.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1512915922686-57c11dde9b6b?q=80&w=600"
            ),
            ownerId = "agent_2",
            postedDate = "2026-07-14",
            isFeatured = false,
            isFavorite = false
        ),
        Property(
            id = "prop_5",
            title = "Commercial Building on Galle Road",
            description = "Excellent high-visibility corporate commercial building on the main Galle Road with spacious office units and underground parking.",
            price = 1500000.0,
            listingType = "BUY",
            category = "Commercial",
            city = "Colombo",
            address = "Galle Road, Colombo 04",
            latitude = 6.8902,
            longitude = 79.8553,
            bedrooms = 0,
            bathrooms = 6,
            areaSqft = 8500.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=600"
            ),
            ownerId = "agent_3",
            postedDate = "2026-06-15",
            isFeatured = true,
            isFavorite = false
        ),
        Property(
            id = "prop_6",
            title = "Beautiful Beachfront Land",
            description = "Ready-to-develop prime plot of tourist development land situated directly on the golden sandy beaches of Galle.",
            price = 950000.0,
            listingType = "BUY",
            category = "Land",
            city = "Galle",
            address = "Matara Road, Galle",
            latitude = 6.0367,
            longitude = 80.2170,
            bedrooms = 0,
            bathrooms = 0,
            areaSqft = 15000.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=600"
            ),
            ownerId = "agent_3",
            postedDate = "2026-07-05",
            isFeatured = true,
            isFavorite = false
        ),
        Property(
            id = "prop_7",
            title = "Elegant 4BR House in Jaffna",
            description = "Fully renovated traditional styled spacious home featuring polished concrete floors, tall columns, and a large front garden.",
            price = 320000.0,
            listingType = "BUY",
            category = "House",
            city = "Jaffna",
            address = "Palali Road, Jaffna",
            latitude = 9.6615,
            longitude = 80.0255,
            bedrooms = 4,
            bathrooms = 3,
            areaSqft = 2400.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?q=80&w=600"
            ),
            ownerId = "agent_1",
            postedDate = "2026-07-12",
            isFeatured = false,
            isFavorite = false
        ),
        Property(
            id = "prop_8",
            title = "Scenic Hill Country Bungalow",
            description = "Enjoy mountain mists and cool weather in this gorgeous colonial bungalow with wooden timber framing and a fireplace.",
            price = 450000.0,
            listingType = "BUY",
            category = "House",
            city = "Badulla",
            address = "Spring Valley Road, Badulla",
            latitude = 6.9934,
            longitude = 81.0550,
            bedrooms = 3,
            bathrooms = 2,
            areaSqft = 2100.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?q=80&w=600"
            ),
            ownerId = "agent_2",
            postedDate = "2026-07-08",
            isFeatured = true,
            isFavorite = false
        ),
        Property(
            id = "prop_9",
            title = "Modern Apartment near Ancient Lake",
            description = "Comfortable, fully air-conditioned apartment located near the historical Nuwara Wewa. Quiet, peaceful, and fully furnished.",
            price = 600.0,
            listingType = "RENT",
            category = "Apartment",
            city = "Anuradhapura",
            address = "Harischandra Mawatha, Anuradhapura",
            latitude = 8.3114,
            longitude = 80.4037,
            bedrooms = 2,
            bathrooms = 2,
            areaSqft = 950.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?q=80&w=600"
            ),
            ownerId = "agent_3",
            postedDate = "2026-07-02",
            isFeatured = false,
            isFavorite = false
        ),
        Property(
            id = "prop_10",
            title = "Spacious Commercial Office Space",
            description = "Open plan modern office space with fully partitioned conference rooms, direct elevator entry, server room and air conditioning.",
            price = 3500.0,
            listingType = "RENT",
            category = "Commercial",
            city = "Colombo",
            address = "Dharmapala Mawatha, Colombo 07",
            latitude = 6.9126,
            longitude = 79.8612,
            bedrooms = 0,
            bathrooms = 4,
            areaSqft = 4200.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1497366216548-37526070297c?q=80&w=600"
            ),
            ownerId = "agent_1",
            postedDate = "2026-07-09",
            isFeatured = false,
            isFavorite = false
        ),
        Property(
            id = "prop_11",
            title = "Charming 3BR Family House",
            description = "A beautiful double-story house situated in a peaceful gated community near Galle Fort. 5 minutes drive to surfing points.",
            price = 850.0,
            listingType = "RENT",
            category = "House",
            city = "Galle",
            address = "Dadalla Road, Galle",
            latitude = 6.0535,
            longitude = 80.1983,
            bedrooms = 3,
            bathrooms = 2,
            areaSqft = 1650.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?q=80&w=600"
            ),
            ownerId = "agent_2",
            postedDate = "2026-07-11",
            isFeatured = false,
            isFavorite = false
        ),
        Property(
            id = "prop_12",
            title = "Prime Agricultural Coconut Land",
            description = "Rich, highly fertile coconut land plot with a small storage building and direct gravel road accessibility, fully fenced.",
            price = 180000.0,
            listingType = "BUY",
            category = "Land",
            city = "Anuradhapura",
            address = "Mihintale Road, Anuradhapura",
            latitude = 8.3510,
            longitude = 80.5050,
            bedrooms = 0,
            bathrooms = 0,
            areaSqft = 43560.0, // 1 acre
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1500382017468-9049fed747ef?q=80&w=600"
            ),
            ownerId = "agent_3",
            postedDate = "2026-06-20",
            isFeatured = false,
            isFavorite = false
        ),
        Property(
            id = "prop_13",
            title = "Luxury Penthouse Suite",
            description = "Live in absolute luxury on the 32nd floor with 360-degree views of the Colombo Harbor and Beira Lake. Private jacuzzi included.",
            price = 6000.0,
            listingType = "RENT",
            category = "Apartment",
            city = "Colombo",
            address = "Sir James Peiris Mawatha, Colombo 02",
            latitude = 6.9200,
            longitude = 79.8510,
            bedrooms = 4,
            bathrooms = 5,
            areaSqft = 3200.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=600"
            ),
            ownerId = "agent_1",
            postedDate = "2026-07-15",
            isFeatured = true,
            isFavorite = false
        ),
        Property(
            id = "prop_14",
            title = "Eco-friendly Wooden Cabin",
            description = "A tranquil rustic retreat surrounding the lush forests of Kandy. Ideal for writers, nature lovers, or weekend getaways.",
            price = 300.0,
            listingType = "RENT",
            category = "House",
            city = "Kandy",
            address = "Aniwatta Road, Kandy",
            latitude = 7.2915,
            longitude = 80.6250,
            bedrooms = 1,
            bathrooms = 1,
            areaSqft = 650.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1449034446853-66c86144b0ad?q=80&w=600"
            ),
            ownerId = "agent_2",
            postedDate = "2026-07-06",
            isFeatured = false,
            isFavorite = false
        ),
        Property(
            id = "prop_15",
            title = "Industrial Warehouse Facility",
            description = "Heavy industrial spec high-roof warehouse located in Kurunegala Industrial Zone. Equipped with 3-phase electricity.",
            price = 550000.0,
            listingType = "BUY",
            category = "Commercial",
            city = "Kurunegala",
            address = "Dambulla Road, Kurunegala",
            latitude = 7.5020,
            longitude = 80.3750,
            bedrooms = 0,
            bathrooms = 2,
            areaSqft = 6000.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?q=80&w=600"
            ),
            ownerId = "agent_3",
            postedDate = "2026-06-30",
            isFeatured = false,
            isFavorite = false
        ),
        Property(
            id = "prop_16",
            title = "Budget Friendly 1BR Studio",
            description = "Compact studio apartment in a highly convenient residential neighborhood of Colombo. Close to supermarket and bus stop.",
            price = 280.0,
            listingType = "RENT",
            category = "Apartment",
            city = "Colombo",
            address = "Baseline Road, Colombo 09",
            latitude = 6.9270,
            longitude = 79.8820,
            bedrooms = 1,
            bathrooms = 1,
            areaSqft = 450.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=600"
            ),
            ownerId = "agent_1",
            postedDate = "2026-07-16",
            isFeatured = false,
            isFavorite = false
        ),
        Property(
            id = "prop_17",
            title = "Premium Residential Land plot",
            description = "Excellent 15 Perches high-ground residential build-ready block in an elegant, gated community area of Negombo.",
            price = 125000.0,
            listingType = "BUY",
            category = "Land",
            city = "Negombo",
            address = "Kiriwattuduwa Road, Negombo",
            latitude = 7.2150,
            longitude = 79.8550,
            bedrooms = 0,
            bathrooms = 0,
            areaSqft = 4080.0,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=600"
            ),
            ownerId = "agent_2",
            postedDate = "2026-07-03",
            isFeatured = false,
            isFavorite = false
        )
    )
}

class MockUserRepository : UserRepository {
    private val _currentUser = MutableStateFlow<User?>(MockDataLayer.defaultCurrentUser)
    override val currentUserState: StateFlow<User?> = _currentUser.asStateFlow()

    private val userDatabase = mutableMapOf<String, User>().apply {
        put(MockDataLayer.defaultCurrentUser.id, MockDataLayer.defaultCurrentUser)
        MockDataLayer.mockAgents.forEach { put(it.id, it) }
    }

    override fun getCurrentUser(): User? {
        return _currentUser.value
    }

    override fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    override fun updateCurrentUser(user: User) {
        userDatabase[user.id] = user
        _currentUser.value = user
    }

    override fun toggleUserRole() {
        val current = _currentUser.value ?: return
        val nextRole = if (current.activeRole == "BUYER") "AGENT" else "BUYER"
        val updated = current.copy(activeRole = nextRole)
        updateCurrentUser(updated)
    }

    override fun registerUser(user: User): Boolean {
        if (userDatabase.containsKey(user.email)) return false
        userDatabase[user.id] = user
        _currentUser.value = user
        return true
    }

    override fun getUserById(id: String): User? {
        return userDatabase[id]
    }
}

class MockPropertyRepository : PropertyRepository {
    private val _properties = MutableStateFlow<List<Property>>(MockDataLayer.baseProperties)
    override val propertiesState: StateFlow<List<Property>> = _properties.asStateFlow()

    override fun getProperties(): List<Property> {
        return _properties.value
    }

    override fun getPropertyById(id: String): Property? {
        return _properties.value.find { it.id == id }
    }

    override fun addProperty(property: Property) {
        val updatedList = _properties.value.toMutableList()
        updatedList.add(0, property) // Add to the top
        _properties.value = updatedList
    }

    override fun updateProperty(property: Property) {
        val updatedList = _properties.value.map {
            if (it.id == property.id) property else it
        }
        _properties.value = updatedList
    }

    override fun deleteProperty(propertyId: String) {
        val updatedList = _properties.value.filter { it.id != propertyId }
        _properties.value = updatedList
    }

    override fun toggleFavorite(propertyId: String) {
        val updatedList = _properties.value.map {
            if (it.id == propertyId) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
        _properties.value = updatedList
    }
}

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val isRead: Boolean,
    val type: String, // "APPROVED" | "NEW" | "DROP" | "MESSAGE"
    val relatedId: String? = null
)

interface NotificationRepository {
    val notificationsState: StateFlow<List<NotificationItem>>
    fun getNotifications(): List<NotificationItem>
    fun markAsRead(id: String)
    fun addNotification(notification: NotificationItem)
    fun clearAll()
}

interface ChatRepository {
    val conversationsState: StateFlow<List<Conversation>>
    val messagesState: StateFlow<Map<String, List<Message>>>
    fun getConversations(): List<Conversation>
    fun getMessagesForConversation(conversationId: String): List<Message>
    fun sendMessage(conversationId: String, senderId: String, text: String): Message
    fun getOrCreateConversation(propertyId: String, buyerId: String, agentId: String): Conversation
    fun markAsRead(conversationId: String)
}

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

class MockNotificationRepository : NotificationRepository {
    private val _notifications = MutableStateFlow<List<NotificationItem>>(
        listOf(
            NotificationItem(
                id = "1",
                title = "Viewing Approved",
                body = "Your request to view 'Luxury 3BR Apartment' was accepted for Sunday, Jul 19 at 10:00 AM.",
                time = "2 hrs ago",
                isRead = false,
                type = "APPROVED",
                relatedId = "prop_1"
            ),
            NotificationItem(
                id = "2",
                title = "New Property in Negombo!",
                body = "A beautiful 'Modern 2BR Beachside Condo' just got listed in your preferred neighborhood.",
                time = "5 hrs ago",
                isRead = false,
                type = "NEW",
                relatedId = "prop_2"
            ),
            NotificationItem(
                id = "3",
                title = "Price Drop Alert",
                body = "The property 'Premium Villa in Kandy' has dropped its price by LKR 2,500,000!",
                time = "1 day ago",
                isRead = true,
                type = "DROP",
                relatedId = "prop_3"
            )
        )
    )
    override val notificationsState: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    override fun getNotifications(): List<NotificationItem> = _notifications.value

    override fun markAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    override fun addNotification(notification: NotificationItem) {
        val current = _notifications.value.toMutableList()
        current.add(0, notification)
        _notifications.value = current
    }

    override fun clearAll() {
        _notifications.value = emptyList()
    }
}

object ResidoServiceLocator {
    val userRepository: UserRepository = MockUserRepository()
    val propertyRepository: PropertyRepository = MockPropertyRepository()
    val notificationRepository: NotificationRepository = MockNotificationRepository()
    val chatRepository: ChatRepository = MockChatRepository()
}

