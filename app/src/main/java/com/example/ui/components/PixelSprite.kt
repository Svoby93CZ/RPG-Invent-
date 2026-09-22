package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

/**
 * Draws a character matrix as solid square pixels — the cheap way to get crisp sprite art
 * without shipping bitmaps. A '.' leaves the pixel empty, every other character is looked up
 * in [palette] and anything missing from it is skipped.
 *
 * Cells are drawn half a pixel oversized so neighbouring blocks never show a hairline seam
 * when the sprite is scaled to a size that is not a whole multiple of the grid.
 */
@Composable
fun PixelSprite(
    rows: List<String>,
    palette: Map<Char, Color>,
    modifier: Modifier = Modifier
) {
    if (rows.isEmpty()) return
    val columns = rows.maxOf { it.length }
    if (columns == 0) return

    Canvas(modifier = modifier) {
        val cellWidth = size.width / columns
        val cellHeight = size.height / rows.size
        rows.forEachIndexed { y, row ->
            row.forEachIndexed { x, symbol ->
                val color = palette[symbol] ?: return@forEachIndexed
                drawRect(
                    color = color,
                    topLeft = Offset(x * cellWidth, y * cellHeight),
                    size = Size(cellWidth + 0.5f, cellHeight + 0.5f)
                )
            }
        }
    }
}

/**
 * Renders a two-tone glyph from [SlotGlyphs]: the outline in [color], the body in a faded
 * version of it, so one colour is enough to restyle the whole sprite.
 */
@Composable
fun PixelGlyph(
    rows: List<String>,
    color: Color,
    modifier: Modifier = Modifier
) {
    PixelSprite(
        rows = rows,
        palette = mapOf('X' to color, 'o' to color.copy(alpha = color.alpha * 0.35f)),
        modifier = modifier
    )
}
