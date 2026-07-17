package com.example.data.mock

import com.example.data.models.User
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockUserRepository : UserRepository {
    private val _currentUser = MutableStateFlow<User?>(MockDataSeed.defaultCurrentUser)
    override val currentUserState: StateFlow<User?> = _currentUser.asStateFlow()

    private val userDatabase = mutableMapOf<String, User>().apply {
        put(MockDataSeed.defaultCurrentUser.id, MockDataSeed.defaultCurrentUser)
        MockDataSeed.mockAgents.forEach { put(it.id, it) }
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
