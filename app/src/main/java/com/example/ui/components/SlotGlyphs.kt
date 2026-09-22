package com.example.ui.components

/**
 * Silhouettes drawn for empty equipment slots, one per [com.example.data.SlotType].
 * Every glyph is a 16x16 grid: 'X' is the outline, 'o' the filled body, '.' stays empty.
 * They are rendered by [PixelGlyph], which tints both shades from a single colour.
 */
object SlotGlyphs {

    private val HLAVA = listOf(
        "................",
        ".....XXXXXX.....",
        "...XXoooooXXX...",
        "..XoooooooooX...",
        "..XooooooooooX..",
        "..XooooooooooX..",
        "..XXXooXXooXXX..",
        "..XooooooooooX..",
        "..XooooooooooX..",
        "..XoooooooooX...",
        "...XooooooooX...",
        "...XooooooooX...",
        "....XXoooXXX....",
        "......XXXX......",
        "................",
        "................"
    )

    private val KRK = listOf(
        "................",
        "...XX......XX...",
        "....XX....XX....",
        ".....XX..XX.....",
        "......XXXX......",
        ".......XX.......",
        "......XXXX......",
        ".....XXooXX.....",
        "....XXooooXX....",
        "....XoooooX.....",
        "....XXooooXX....",
        ".....XXooXX.....",
        "......XXXX......",
        "................",
        "................",
        "................"
    )

    private val HRUDNIK = listOf(
        "................",
        "..XX........XX..",
        ".XXXX......XXXX.",
        ".XooXXXXXXXXooX.",
        ".XoooooooooooX..",
        ".XoooooooooooX..",
        "..XooooooooooX..",
        "..XooooooooooX..",
        "..XooooooooooX..",
        "..XooooooooooX..",
        "..XoooooooooX...",
        "...XoooooooX....",
        "...XXXXXXXXX....",
        "................",
        "................",
        "................"
    )

    private val PAS = listOf(
        "................",
        "................",
        "................",
        "................",
        "XXXXXXXXXXXXXXXX",
        "XooooooooooooooX",
        "XooXXXXXXXXooooX",
        "XooXooooooXooooX",
        "XooXooooooXooooX",
        "XooXXXXXXXXooooX",
        "XooooooooooooooX",
        "XXXXXXXXXXXXXXXX",
        "................",
        "................",
        "................",
        "................"
    )

    private val NOHY = listOf(
        "................",
        "..XXXXXXXXXXXX..",
        "..XoooooooooooX.",
        "..XoooooooooooX.",
        "..XoooooooooooX.",
        "..XoooooXXoooooX",
        "..XooooXXXooooX.",
        "..XooooX.XooooX.",
        "..XooooX.XooooX.",
        "..XooooX.XooooX.",
        "..XooooX.XooooX.",
        "..XXXXX..XXXXX..",
        "................",
        "................",
        "................",
        "................"
    )

    private val CHODIDLA = listOf(
        "................",
        "....XXXXXX......",
        "....XooooX......",
        "....XooooX......",
        "....XooooX......",
        "....XooooX......",
        "....XooooXX.....",
        "....XoooooXX....",
        "....XoooooooX...",
        "....XooooooooX..",
        "...XXooooooooXX.",
        "..XXoooooooooXX.",
        "..XXXXXXXXXXXXX.",
        "................",
        "................",
        "................"
    )

    private val PRST = listOf(
        "................",
        "......XXXX......",
        ".....XooooX.....",
        ".....XooooX.....",
        "......XXXX......",
        "....XXXXXXXX....",
        "...XXooooooXX...",
        "..XXoooooooXX...",
        "..XXoo....ooXX..",
        "..XXo......oXX..",
        "..XXo......oXX..",
        "...XXo....oXX...",
        "...XXoooooXX....",
        "....XXXXXX......",
        "................",
        "................"
    )

    private val BATOH = listOf(
        "................",
        ".....XXXXXX.....",
        "....XooooooX....",
        "...XXooooooXX...",
        "..XXooooooooXX..",
        "..XooooooooooX..",
        "..XooXXXXXXooX..",
        "..XooXooooXooX..",
        "..XooXXXXXXooX..",
        "..XooooooooooX..",
        "..XoooXXXXoooX..",
        "..XooooooooooX..",
        "...XXXXXXXXXX...",
        "................",
        "................",
        "................"
    )

    private val OBECNY = listOf(
        "................",
        ".......XX.......",
        "......XooX......",
        ".....XXooXX.....",
        "....XXooooXX....",
        "...XXoooooXXX...",
        "..XXooooooooXX..",
        "..XoooooooooX...",
        "..XoooooooooX...",
        "..XooooooooooX..",
        "..XooooooooooX..",
        "...XoooooooX....",
        "....XXXXXXX.....",
        "................",
        "................",
        "................"
    )

    private val BY_SLOT = mapOf(
        "HLAVA" to HLAVA,
        "KRK" to KRK,
        "HRUDNIK" to HRUDNIK,
        "PAS" to PAS,
        "NOHY" to NOHY,
        "CHODIDLA" to CHODIDLA,
        "PRST" to PRST,
        "BATOH" to BATOH,
        "OBECNY" to OBECNY
    )

    /** Falls back to the generic pouch for a slot type this version does not know. */
    fun forSlot(slotType: String): List<String> = BY_SLOT[slotType.uppercase()] ?: OBECNY
}
