package com.example.data

import com.example.utils.StatUtils

/** A fresh hero is average at everything. */
const val DEFAULT_ABILITY_SCORE = 10
const val MIN_ABILITY_SCORE = 1
const val MAX_ABILITY_SCORE = 30

/**
 * The six attributes a hero is described by.
 *
 * [statName] is how the attribute is written inside an item's stats string, so a pair of boots
 * carrying "Obratnost:+2" feeds straight into [DEXTERITY]. Note that the match is exact: an item
 * with "Odolnost vůči zimě:Dobrá" describes a property of the coat, not the hero's Odolnost.
 */
enum class Ability(val statName: String, val abbreviation: String) {
    STRENGTH("Síla", "SIL"),
    DEXTERITY("Obratnost", "OBR"),
    CONSTITUTION("Odolnost", "ODO"),
    INTELLIGENCE("Inteligence", "INT"),
    WILLPOWER("Vůle", "VŮL"),
    CHARISMA("Charisma", "CHA")
}

/**
 * One attribute of the hero as it currently stands: what they are worth on their own ([base])
 * and what their gear adds on top ([bonus]).
 */
data class AbilityScore(
    val ability: Ability,
    val base: Int,
    val bonus: Int
) {
    val total: Int get() = base + bonus

    /** Average (10) is +0 and every two points shift the modifier by one, as in Baldur's Gate. */
    val modifier: Int get() = (total - DEFAULT_ABILITY_SCORE).floorDiv(2)

    val modifierLabel: String get() = if (modifier >= 0) "+$modifier" else modifier.toString()
}

object AbilityCalculator {

    private val BY_STAT_NAME = Ability.entries.associateBy { it.statName.lowercase() }

    /**
     * Adds up what the equipped items contribute to each attribute. Stats that name no attribute
     * and attribute stats with a purely textual value are ignored.
     */
    fun bonuses(rawStats: List<String>): Map<Ability, Int> {
        val totals = mutableMapOf<Ability, Int>()
        rawStats.forEach { raw ->
            StatUtils.parse(raw).forEach { (name, value) ->
                val ability = BY_STAT_NAME[name.lowercase()] ?: return@forEach
                val number = StatUtils.numericValue(value) ?: return@forEach
                totals[ability] = (totals[ability] ?: 0) + number.toInt()
            }
        }
        return totals
    }

    /** The full sheet, in a fixed order, so the UI never has to deal with a missing attribute. */
    fun scores(baseScores: Map<Ability, Int>, rawStats: List<String>): List<AbilityScore> {
        val bonuses = bonuses(rawStats)
        return Ability.entries.map { ability ->
            AbilityScore(
                ability = ability,
                base = baseScores[ability] ?: DEFAULT_ABILITY_SCORE,
                bonus = bonuses[ability] ?: 0
            )
        }
    }
}
