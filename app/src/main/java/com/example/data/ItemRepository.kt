package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ItemRepository(private val appDao: AppDao) {

    val character: Flow<GameCharacter?> = appDao.getCharacter()
    val locations: Flow<List<Location>> = appDao.getAllLocations()
    val allItems: Flow<List<Item>> = appDao.getAllItems()
    val equippedItems: Flow<List<Item>> = appDao.getEquippedItems()
    val backpackItems: Flow<List<Item>> = appDao.getBackpackItems()

    fun getItemsByLocation(locationId: Long): Flow<List<Item>> = appDao.getItemsByLocation(locationId)
    fun getItemByIdFlow(id: Long): Flow<Item?> = appDao.getItemByIdFlow(id)

    suspend fun insertCharacter(character: GameCharacter) {
        appDao.insertCharacter(character)
    }

    suspend fun insertLocation(location: Location): Long = appDao.insertLocation(location)
    suspend fun updateLocation(location: Location) = appDao.updateLocation(location)
    suspend fun deleteLocationWithItems(location: Location) = appDao.deleteLocationWithItems(location)

    suspend fun insertItem(item: Item): Long = appDao.insertItem(item)
    suspend fun updateItem(item: Item) = appDao.updateItem(item)
    suspend fun deleteItem(item: Item) = appDao.deleteItem(item)

    suspend fun consumeItem(item: Item) {
        if (!item.isConsumable) return
        if (item.charges > 1) {
            appDao.updateItem(item.copy(charges = item.charges - 1))
        } else {
            appDao.deleteItem(item)
        }
    }

    suspend fun equipItem(item: Item, targetSlotType: String, slotIndex: Int) {
        // Find if there is an item already equipped in that precise slot and index
        val currentEquipped = appDao.getEquippedItems().firstOrNull() ?: emptyList()
        val alreadyEquippedInSlot = currentEquipped.find {
            it.slotType == targetSlotType && it.equippedSlotIndex == slotIndex
        }

        // Unequip current item to active backpack (locationId = null, equippedSlotIndex = null)
        alreadyEquippedInSlot?.let {
            appDao.updateItem(it.copy(equippedSlotIndex = null, locationId = null))
        }

        // Equip new item
        appDao.updateItem(item.copy(
            slotType = targetSlotType,
            locationId = null,
            equippedSlotIndex = slotIndex
        ))
    }

    suspend fun unequipItem(item: Item) {
        appDao.updateItem(item.copy(
            equippedSlotIndex = null,
            locationId = null
        ))
    }

    suspend fun moveItemToLocation(item: Item, locationId: Long?) {
        // If it's currently equipped, unequip it first
        appDao.updateItem(item.copy(
            equippedSlotIndex = null,
            locationId = locationId
        ))
    }

    val equipmentSets: Flow<List<EquipmentSet>> = appDao.getAllEquipmentSets()
    val skills: Flow<List<Skill>> = appDao.getAllSkills()

    suspend fun insertSkill(skill: Skill): Long = appDao.insertSkill(skill)
    suspend fun deleteSkill(skill: Skill) = appDao.deleteSkill(skill)

    suspend fun insertEquipmentSet(equipmentSet: EquipmentSet): Long = appDao.insertEquipmentSet(equipmentSet)
    
    suspend fun deleteEquipmentSet(equipmentSet: EquipmentSet) = appDao.deleteEquipmentSet(equipmentSet)

    suspend fun loadEquipmentSet(equipmentSet: EquipmentSet) {
        val itemIds = equipmentSet.itemIds.split(",").mapNotNull { it.toLongOrNull() }
        
        // Unequip current items
        val currentEquipped = appDao.getEquippedItems().firstOrNull() ?: emptyList()
        currentEquipped.forEach {
            appDao.updateItem(it.copy(equippedSlotIndex = null, locationId = null))
        }

        // Equip saved items to index 0 of their respective slotType
        itemIds.forEach { itemId ->
            appDao.getItemById(itemId)?.let { item ->
                appDao.updateItem(item.copy(locationId = null, equippedSlotIndex = 0))
            }
        }
    }
}
