package com.example.data.models

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val isRead: Boolean,
    val type: String, // "APPROVED" | "NEW" | "DROP" | "MESSAGE"
    val relatedId: String? = null
)
