package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment_sets")
data class EquipmentSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val itemIds: String // Comma-separated list of item ids
)
