package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class GameCharacter(
    @PrimaryKey val id: Int = 1,
    val name: String = "Hrdina Reality"
)
