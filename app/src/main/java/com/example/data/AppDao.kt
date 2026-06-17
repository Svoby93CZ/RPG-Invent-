package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- Character Queries ---
    @Query("SELECT * FROM characters WHERE id = 1")
    fun getCharacter(): Flow<GameCharacter?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: GameCharacter)

    // --- Location Queries ---
    @Query("SELECT * FROM locations ORDER BY name ASC")
    fun getAllLocations(): Flow<List<Location>>

    @Query("SELECT * FROM locations WHERE id = :id")
    fun getLocationById(id: Long): Flow<Location?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location): Long

    @Update
    suspend fun updateLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("DELETE FROM items WHERE locationId = :locationId")
    suspend fun deleteItemsInLocation(locationId: Long)

    @Transaction
    suspend fun deleteLocationWithItems(location: Location) {
        deleteItemsInLocation(location.id)
        deleteLocation(location)
    }

    // --- Item Queries ---
    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Long): Item?

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemByIdFlow(id: Long): Flow<Item?>

    @Query("SELECT * FROM items WHERE locationId = :locationId")
    fun getItemsByLocation(locationId: Long): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE locationId IS NULL AND equippedSlotIndex IS NOT NULL")
    fun getEquippedItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE locationId IS NULL AND equippedSlotIndex IS NULL")
    fun getBackpackItems(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)
}
