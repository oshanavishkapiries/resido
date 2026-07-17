package com.example.data

import com.example.data.repository.UserRepository
import com.example.data.repository.PropertyRepository
import com.example.data.repository.NotificationRepository
import com.example.data.repository.ChatRepository
import com.example.data.repository.AuthRepository
import com.example.data.mock.MockUserRepository
import com.example.data.mock.MockPropertyRepository
import com.example.data.mock.MockChatRepository
import com.example.data.mock.MockNotificationRepository
import com.example.data.mock.MockAuthRepository

/**
 * Central Service Locator and dependency injection point for all repositories.
 * 
 * To connect a real backend, create new implementations of the repository interfaces
 * (e.g., ApiPropertyRepository) under the repository/ package and swap them in below.
 * No screen or UI level code will need to change.
 */
object ResidoServiceLocator {
    val userRepository: UserRepository = MockUserRepository()
    val propertyRepository: PropertyRepository = MockPropertyRepository()
    val notificationRepository: NotificationRepository = MockNotificationRepository()
    val chatRepository: ChatRepository = MockChatRepository()
    val authRepository: AuthRepository = MockAuthRepository()
}
