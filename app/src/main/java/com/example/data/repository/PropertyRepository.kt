package com.example.data.repository

import com.example.data.models.Property
import kotlinx.coroutines.flow.StateFlow

interface PropertyRepository {
    val propertiesState: StateFlow<List<Property>>
    fun getProperties(): List<Property>
    fun getPropertyById(id: String): Property?
    fun addProperty(property: Property)
    fun updateProperty(property: Property)
    fun deleteProperty(propertyId: String)
    fun toggleFavorite(propertyId: String)
}
