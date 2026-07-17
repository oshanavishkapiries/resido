package com.example.data.mock

import com.example.data.models.Property
import com.example.data.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockPropertyRepository : PropertyRepository {
    private val _properties = MutableStateFlow<List<Property>>(MockDataSeed.baseProperties)
    override val propertiesState: StateFlow<List<Property>> = _properties.asStateFlow()

    override fun getProperties(): List<Property> {
        return _properties.value
    }

    override fun getPropertyById(id: String): Property? {
        return _properties.value.find { it.id == id }
    }

    override fun addProperty(property: Property) {
        val updatedList = _properties.value.toMutableList()
        updatedList.add(0, property) // Add to the top
        _properties.value = updatedList
    }

    override fun updateProperty(property: Property) {
        val updatedList = _properties.value.map {
            if (it.id == property.id) property else it
        }
        _properties.value = updatedList
    }

    override fun deleteProperty(propertyId: String) {
        val updatedList = _properties.value.filter { it.id != propertyId }
        _properties.value = updatedList
    }

    override fun toggleFavorite(propertyId: String) {
        val updatedList = _properties.value.map {
            if (it.id == propertyId) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
        _properties.value = updatedList
    }
}
