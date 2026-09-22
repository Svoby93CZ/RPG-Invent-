package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.utils.EquipmentSetCodec
import com.example.utils.StatUtils
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

    val equipmentSets: StateFlow<List<EquipmentSet>>
    val skills: StateFlow<List<Skill>>

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

        equipmentSets = repository.equipmentSets
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        skills = repository.skills
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // What the hero can carry: their own hands and pockets, plus every bag and coat worn.
        activeInventoryCapacity = repository.equippedItems
            .map { equipped ->
                BASE_INVENTORY_CAPACITY + equipped.filter { it.hasPockets }.sumOf { it.pocketSize }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                BASE_INVENTORY_CAPACITY
            )

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
                // A decimal comma typed into a value would otherwise split the stat in half.
                stats = StatUtils.sanitize(stats),
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

    /**
     * Where a piece lands when the user just taps "equip": the first free index of its slot
     * type, or the last one when every layer is already taken — that is the one being replaced.
     */
    fun firstFreeSlotIndex(slotType: String): Int {
        val maxSlots = SlotType.entries.find { it.name == slotType }?.maxSlots ?: 0
        if (maxSlots == 0) return 0
        val occupied = equippedItems.value
            .filter { it.slotType == slotType }
            .mapNotNull { it.equippedSlotIndex }
            .toSet()
        return (0 until maxSlots).firstOrNull { it !in occupied } ?: (maxSlots - 1)
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

    // --- EQUIPMENT SET OPERATIONS ---
    fun deleteEquipmentSet(set: EquipmentSet) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEquipmentSet(set)
        }
    }

    fun loadEquipmentSet(set: EquipmentSet) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadEquipmentSet(set)
        }
    }

    fun saveCurrentEquipmentAsSet(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Record which slot each piece sits in, otherwise layered outfits cannot be restored.
            val assignments = equippedItems.value.mapNotNull { item ->
                item.equippedSlotIndex?.let {
                    EquipmentSetCodec.SlotAssignment(item.id, item.slotType, it)
                }
            }
            repository.insertEquipmentSet(
                EquipmentSet(name = name, itemIds = EquipmentSetCodec.encode(assignments))
            )
        }
    }

    // --- SKILL OPERATIONS ---
    fun insertSkill(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSkill(Skill(name = name))
        }
    }

    fun deleteSkill(skill: Skill) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSkill(skill)
        }
    }
}
