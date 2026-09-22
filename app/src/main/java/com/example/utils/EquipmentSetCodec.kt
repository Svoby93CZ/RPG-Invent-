package com.example.utils

/**
 * An equipment set stores which item sits in which slot as a single string:
 * "12:HRUDNIK:0,13:HRUDNIK:1". Older sets only stored item ids ("12,13"), which is why
 * [decode] also understands that format and leaves the placement to the caller.
 */
object EquipmentSetCodec {

    const val UNKNOWN_SLOT_INDEX = -1

    data class SlotAssignment(
        val itemId: Long,
        val slotType: String,
        val slotIndex: Int
    )

    fun encode(assignments: List<SlotAssignment>): String =
        assignments.joinToString(",") { "${it.itemId}:${it.slotType}:${it.slotIndex}" }

    fun decode(encoded: String): List<SlotAssignment> = encoded.split(',').mapNotNull { entry ->
        val parts = entry.trim().split(':')
        val itemId = parts.getOrNull(0)?.trim()?.toLongOrNull() ?: return@mapNotNull null
        val slotType = parts.getOrNull(1)?.trim().orEmpty()
        val slotIndex = parts.getOrNull(2)?.trim()?.toIntOrNull() ?: UNKNOWN_SLOT_INDEX
        SlotAssignment(itemId, slotType, slotIndex)
    }
}
