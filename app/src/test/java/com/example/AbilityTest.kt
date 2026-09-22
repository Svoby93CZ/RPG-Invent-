package com.example

import com.example.data.Ability
import com.example.data.AbilityCalculator
import com.example.data.AbilityScore
import com.example.data.GameCharacter
import com.example.data.MAX_ABILITY_SCORE
import com.example.data.MIN_ABILITY_SCORE
import org.junit.Assert.assertEquals
import org.junit.Test

class AbilityTest {

  @Test
  fun `an equipped item raises the attribute it names`() {
    assertEquals(mapOf(Ability.STRENGTH to 3), AbilityCalculator.bonuses(listOf("Síla:+3")))
  }

  @Test
  fun `bonuses from several items add up`() {
    assertEquals(mapOf(Ability.STRENGTH to 5), AbilityCalculator.bonuses(listOf("Síla:+3", "Síla:+2")))
  }

  @Test
  fun `a negative bonus is subtracted`() {
    assertEquals(
      mapOf(Ability.DEXTERITY to -2),
      AbilityCalculator.bonuses(listOf("Obratnost:+4", "Obratnost:-6"))
    )
  }

  @Test
  fun `matching an attribute name ignores case`() {
    assertEquals(mapOf(Ability.STRENGTH to 1), AbilityCalculator.bonuses(listOf("síla:+1")))
  }

  @Test
  fun `stats that name no attribute are left alone`() {
    // "Odolnost vůči zimě" describes the coat, not the hero's Odolnost.
    assertEquals(
      emptyMap<Ability, Int>(),
      AbilityCalculator.bonuses(listOf("Odolnost vůči zimě:Dobrá,Kapacita:+12"))
    )
  }

  @Test
  fun `an attribute with a textual value grants nothing`() {
    assertEquals(emptyMap<Ability, Int>(), AbilityCalculator.bonuses(listOf("Síla:veliká")))
  }

  @Test
  fun `the sheet always lists every attribute in a fixed order`() {
    val sheet = AbilityCalculator.scores(emptyMap(), emptyList())
    assertEquals(Ability.entries.toList(), sheet.map { it.ability })
  }

  @Test
  fun `an attribute the character has no score for falls back to average`() {
    val sheet = AbilityCalculator.scores(mapOf(Ability.STRENGTH to 14), emptyList())
    assertEquals(10, sheet.first { it.ability == Ability.WILLPOWER }.base)
  }

  @Test
  fun `the total is the character's own score plus what the gear adds`() {
    val sheet = AbilityCalculator.scores(mapOf(Ability.STRENGTH to 14), listOf("Síla:+2"))
    val strength = sheet.first { it.ability == Ability.STRENGTH }
    assertEquals(14, strength.base)
    assertEquals(2, strength.bonus)
    assertEquals(16, strength.total)
  }

  @Test
  fun `the modifier rounds towards minus infinity, not towards zero`() {
    assertEquals(0, AbilityScore(Ability.STRENGTH, 10, 0).modifier)
    assertEquals(-1, AbilityScore(Ability.STRENGTH, 9, 0).modifier)
    assertEquals(-1, AbilityScore(Ability.STRENGTH, 8, 0).modifier)
    assertEquals(-2, AbilityScore(Ability.STRENGTH, 7, 0).modifier)
    assertEquals(-5, AbilityScore(Ability.STRENGTH, 1, 0).modifier)
    assertEquals(3, AbilityScore(Ability.STRENGTH, 16, 0).modifier)
  }

  @Test
  fun `the modifier label always carries its sign`() {
    assertEquals("+3", AbilityScore(Ability.STRENGTH, 16, 0).modifierLabel)
    assertEquals("+0", AbilityScore(Ability.STRENGTH, 10, 0).modifierLabel)
    assertEquals("-2", AbilityScore(Ability.STRENGTH, 7, 0).modifierLabel)
  }

  @Test
  fun `setting a score keeps it inside the allowed range`() {
    val hero = GameCharacter()
    assertEquals(MAX_ABILITY_SCORE, hero.withScore(Ability.CHARISMA, 999).charisma)
    assertEquals(MIN_ABILITY_SCORE, hero.withScore(Ability.CHARISMA, -5).charisma)
    assertEquals(17, hero.withScore(Ability.CHARISMA, 17).charisma)
  }

  @Test
  fun `setting one score leaves the others alone`() {
    val hero = GameCharacter().withScore(Ability.STRENGTH, 18)
    assertEquals(18, hero.strength)
    assertEquals(10, hero.dexterity)
    assertEquals("Hrdina Reality", hero.name)
  }

  @Test
  fun `baseScores reports every attribute`() {
    val hero = GameCharacter().withScore(Ability.WILLPOWER, 13)
    assertEquals(Ability.entries.toSet(), hero.baseScores().keys)
    assertEquals(13, hero.baseScores()[Ability.WILLPOWER])
  }
}
