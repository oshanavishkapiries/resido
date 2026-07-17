package com.example.data.models

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
