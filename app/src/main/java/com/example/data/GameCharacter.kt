package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The hero. There is only ever one row, with [id] 1.
 *
 * The six attributes are what the hero is worth without any gear on; what their equipment adds
 * is computed from the items themselves, never stored here, so taking a ring off cannot leave a
 * stale bonus behind.
 *
 * The `defaultValue` on each attribute mirrors the `ALTER TABLE` in `MIGRATION_3_4`, so the
 * schema Room expects and the one actually in the database agree after the upgrade.
 */
@Entity(tableName = "characters")
data class GameCharacter(
    @PrimaryKey val id: Int = 1,
    val name: String = "Hrdina Reality",
    @ColumnInfo(defaultValue = "10") val strength: Int = DEFAULT_ABILITY_SCORE,
    @ColumnInfo(defaultValue = "10") val dexterity: Int = DEFAULT_ABILITY_SCORE,
    @ColumnInfo(defaultValue = "10") val constitution: Int = DEFAULT_ABILITY_SCORE,
    @ColumnInfo(defaultValue = "10") val intelligence: Int = DEFAULT_ABILITY_SCORE,
    @ColumnInfo(defaultValue = "10") val willpower: Int = DEFAULT_ABILITY_SCORE,
    @ColumnInfo(defaultValue = "10") val charisma: Int = DEFAULT_ABILITY_SCORE
) {
    fun baseScore(ability: Ability): Int = when (ability) {
        Ability.STRENGTH -> strength
        Ability.DEXTERITY -> dexterity
        Ability.CONSTITUTION -> constitution
        Ability.INTELLIGENCE -> intelligence
        Ability.WILLPOWER -> willpower
        Ability.CHARISMA -> charisma
    }

    /** Clamps to the allowed range, so a stuck "+" button cannot push a score out of bounds. */
    fun withScore(ability: Ability, score: Int): GameCharacter {
        val clamped = score.coerceIn(MIN_ABILITY_SCORE, MAX_ABILITY_SCORE)
        return when (ability) {
            Ability.STRENGTH -> copy(strength = clamped)
            Ability.DEXTERITY -> copy(dexterity = clamped)
            Ability.CONSTITUTION -> copy(constitution = clamped)
            Ability.INTELLIGENCE -> copy(intelligence = clamped)
            Ability.WILLPOWER -> copy(willpower = clamped)
            Ability.CHARISMA -> copy(charisma = clamped)
        }
    }

    fun baseScores(): Map<Ability, Int> = Ability.entries.associateWith { baseScore(it) }
}
