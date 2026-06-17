package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RPGViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ItemRepository

    // Raw Streams from Room
    val character: StateFlow<GameCharacter?>
    val locations: StateFlow<List<Location>>
    val equippedItems: StateFlow<List<Item>>
    val backpackItems: StateFlow<List<Item>>
    val allItems: StateFlow<List<Item>>

    // Computed Streams
    val activeInventoryCapacity: StateFlow<Int>
    val activeInventoryWeight: StateFlow<Double>

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = ItemRepository(database.appDao())

        character = repository.character
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        locations = repository.locations
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        equippedItems = repository.equippedItems
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        backpackItems = repository.backpackItems
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allItems = repository.allItems
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Calculate active carrying capacity of character based on pocket size of equipped items
        activeInventoryCapacity = repository.equippedItems
            .map { equipped ->
                equipped.filter { it.hasPockets }.sumOf { it.pocketSize }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

        // Calculate total weight of things currently in backpack
        activeInventoryWeight = repository.backpackItems
            .map { items ->
                items.sumOf { it.weight }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    }

    // --- CHARACTER OPERATIONS ---
    fun updateCharacterName(newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCharacter(GameCharacter(id = 1, name = newName))
        }
    }

    // --- LOCATION OPERATIONS ---
    fun createLocation(name: String, capacity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLocation(Location(name = name, maxCapacity = capacity))
        }
    }

    fun updateLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLocation(location)
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLocationWithItems(location)
        }
    }

    // --- ITEM OPERATIONS ---
    fun createItem(
        name: String,
        slotType: String,
        rarity: String,
        description: String,
        stats: String,
        weight: Double,
        isConsumable: Boolean,
        charges: Int,
        hasPockets: Boolean,
        pocketSize: Int,
        pixelArtData: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newItem = Item(
                name = name,
                slotType = slotType,
                rarity = rarity,
                description = description,
                stats = stats,
                weight = weight,
                isConsumable = isConsumable,
                charges = charges,
                maxCharges = charges,
                hasPockets = hasPockets,
                pocketSize = pocketSize,
                pixelArtData = pixelArtData,
                locationId = null, // initially in backpack
                equippedSlotIndex = null
            )
            repository.insertItem(newItem)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(item)
        }
    }

    fun equipItem(item: Item, slotType: String, index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.equipItem(item, slotType, index)
        }
    }

    fun unequipItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unequipItem(item)
        }
    }

    fun consumeItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.consumeItem(item)
        }
    }

    fun moveItemToLocation(item: Item, locationId: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.moveItemToLocation(item, locationId)
        }
    }
}
