package com.example

import com.example.utils.EquipmentSetCodec
import com.example.utils.EquipmentSetCodec.SlotAssignment
import org.junit.Assert.assertEquals
import org.junit.Test

class EquipmentSetCodecTest {

  private val layeredChest = listOf(
    SlotAssignment(7, "HRUDNIK", 0),
    SlotAssignment(8, "HRUDNIK", 1)
  )

  @Test
  fun `encode writes id slot and index`() {
    assertEquals("7:HRUDNIK:0,8:HRUDNIK:1", EquipmentSetCodec.encode(layeredChest))
  }

  @Test
  fun `a set survives a round trip with its slot indices`() {
    assertEquals(layeredChest, EquipmentSetCodec.decode(EquipmentSetCodec.encode(layeredChest)))
  }

  @Test
  fun `sets saved in the old id-only format still decode`() {
    assertEquals(
      listOf(
        SlotAssignment(7, "", EquipmentSetCodec.UNKNOWN_SLOT_INDEX),
        SlotAssignment(8, "", EquipmentSetCodec.UNKNOWN_SLOT_INDEX)
      ),
      EquipmentSetCodec.decode("7,8")
    )
  }

  @Test
  fun `entries without an item id are skipped`() {
    assertEquals(listOf(SlotAssignment(7, "HLAVA", 0)), EquipmentSetCodec.decode("7:HLAVA:0,,nonsense"))
  }
}
