package com.example.data.repository

import com.example.data.models.User

interface AuthRepository {
    fun login(email: String, password: String): Boolean
    fun register(user: User): Boolean
}
