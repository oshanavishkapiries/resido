package com.example.data.mock

import com.example.data.models.User
import com.example.data.repository.AuthRepository

class MockAuthRepository : AuthRepository {
    private val credentialsDatabase = mutableMapOf<String, String>().apply {
        put("janaka@resido.lk", "password123")
    }

    override fun login(email: String, password: String): Boolean {
        return credentialsDatabase[email] == password
    }

    override fun register(user: User): Boolean {
        if (credentialsDatabase.containsKey(user.email)) return false
        credentialsDatabase[user.email] = "password123" // mock default password
        return true
    }
}
