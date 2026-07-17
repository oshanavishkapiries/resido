package com.example.data.repository

import com.example.data.models.NotificationItem
import kotlinx.coroutines.flow.StateFlow

interface NotificationRepository {
    val notificationsState: StateFlow<List<NotificationItem>>
    fun getNotifications(): List<NotificationItem>
    fun markAsRead(id: String)
    fun addNotification(notification: NotificationItem)
    fun clearAll()
}
