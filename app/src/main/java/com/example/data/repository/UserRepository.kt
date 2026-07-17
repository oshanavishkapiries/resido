package com.example.data.repository

import com.example.data.models.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUserState: StateFlow<User?>
    fun getCurrentUser(): User?
    fun setCurrentUser(user: User?)
    fun updateCurrentUser(user: User)
    fun toggleUserRole()
    fun registerUser(user: User): Boolean
    fun getUserById(id: String): User?
}
