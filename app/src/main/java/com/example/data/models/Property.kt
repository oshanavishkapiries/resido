package com.example.data.models

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
