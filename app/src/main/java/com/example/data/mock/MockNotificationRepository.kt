package com.example.data.mock

import com.example.data.models.NotificationItem
import com.example.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
