package com.example

import com.example.utils.StatUtils
import com.example.utils.StatUtils.Stat
import com.example.utils.toWeightOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StatUtilsTest {

  @Test
  fun `parse splits name and value`() {
    assertEquals(listOf(Stat("Síla", "+5"), Stat("Ochrana", "+3")), StatUtils.parse("Síla:+5,Ochrana:+3"))
  }

  @Test
  fun `parse drops fragments without a name or a value`() {
    assertEquals(listOf(Stat("Síla", "+5")), StatUtils.parse("Síla:+5,rozbité,:prázdné,Ochrana:"))
  }

  @Test
  fun `merge adds up numeric bonuses instead of overwriting them`() {
    assertEquals(listOf(Stat("Ochrana", "+6")), StatUtils.merge(listOf("Ochrana:+3", "Ochrana:+3")))
  }

  @Test
  fun `merge keeps the unit of the first occurrence`() {
    assertEquals(listOf(Stat("Energie", "+5 body")), StatUtils.merge(listOf("Energie:+2 body", "Energie:+3 body")))
  }

  @Test
  fun `merge leaves an unsigned value unsigned`() {
    assertEquals(listOf(Stat("Trvání", "1 hodina")), StatUtils.merge(listOf("Trvání:1 hodina")))
  }

  @Test
  fun `merge joins distinct textual values and drops duplicates`() {
    assertEquals(listOf(Stat("Odolnost", "Dobrá / Skvělá")), StatUtils.merge(listOf("Odolnost:Dobrá", "Odolnost:Skvělá")))
    assertEquals(listOf(Stat("Odolnost", "Dobrá")), StatUtils.merge(listOf("Odolnost:Dobrá", "Odolnost:Dobrá")))
  }

  @Test
  fun `merge combines a numeric and a textual value of the same stat`() {
    assertEquals(listOf(Stat("Ochrana", "+2 / Slabá")), StatUtils.merge(listOf("Ochrana:+2", "Ochrana:Slabá")))
  }

  @Test
  fun `merge subtracts negative bonuses`() {
    assertEquals(listOf(Stat("Rychlost", "+3")), StatUtils.merge(listOf("Rychlost:+5", "Rychlost:-2")))
  }

  @Test
  fun `merge keeps the order in which stats first appeared`() {
    assertEquals(listOf("B", "A"), StatUtils.merge(listOf("B:+1", "A:+1", "B:+1")).map { it.name })
  }

  @Test
  fun `sanitize rewrites a decimal comma that would otherwise split the value`() {
    assertEquals("Váha:+0.5", StatUtils.sanitize("Váha:+0,5"))
    assertEquals(listOf(Stat("Váha", "+1")), StatUtils.merge(listOf("Váha:+0.5", "Váha:+0.5")))
  }

  @Test
  fun `sanitize leaves the separator between two stats alone`() {
    assertEquals("Síla:+5,Ochrana:+3", StatUtils.sanitize("Síla:+5,Ochrana:+3"))
  }

  @Test
  fun `weight accepts both a dot and a comma`() {
    assertEquals(0.5, "0.5".toWeightOrNull()!!, 0.0001)
    assertEquals(0.5, "0,5".toWeightOrNull()!!, 0.0001)
    assertNull("abc".toWeightOrNull())
    assertNull("  ".toWeightOrNull())
  }
}
