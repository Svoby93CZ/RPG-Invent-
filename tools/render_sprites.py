#!/usr/bin/env python3
"""Renders the app's sprites to PNG files so they can be looked at without building the app.

Every sprite in this project is a list of strings in Kotlin source — one character per pixel —
drawn onto a Canvas at runtime. This script reads those same matrices straight out of the source
and rasterises them, so "edit the matrix, look at the result" is a two-second loop instead of a
full Android build.

    python3 tools/render_sprites.py [output_dir]
"""

import re
import sys
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
PRIMITIVES = ROOT / "app/src/main/java/com/example/ui/components/GothicPrimitives.kt"
GLYPHS = ROOT / "app/src/main/java/com/example/ui/components/SlotGlyphs.kt"

BACKGROUND = (15, 15, 16, 255)  # GothicDarkBackground
GLYPH_TINT = (63, 63, 70)       # GothicBorderGray, what an empty slot is drawn in


def read_matrix(source: str, name: str) -> list[str]:
    """Pulls `val <name> = listOf("...", "...")` out of Kotlin source."""
    match = re.search(rf"val\s+{name}\s*=\s*listOf\((.*?)\n\s*\)", source, re.S)
    if not match:
        raise SystemExit(f"matrix '{name}' not found")
    return re.findall(r'"([^"]*)"', match.group(1))


def read_palette(source: str) -> dict[str, tuple[int, int, int, int]]:
    """Pulls the `'X' -> Color(0xFF09090B)` lines out of the sprite's when-block."""
    palette = {}
    for symbol, argb in re.findall(r"'(.)'\s*->\s*Color\(0x([0-9A-Fa-f]{8})\)", source):
        value = int(argb, 16)
        palette[symbol] = (
            (value >> 16) & 0xFF, (value >> 8) & 0xFF, value & 0xFF, (value >> 24) & 0xFF
        )
    return palette


def render(rows: list[str], palette: dict, scale: int, background=BACKGROUND) -> Image.Image:
    columns = max(len(row) for row in rows)
    image = Image.new("RGBA", (columns, len(rows)), background)
    for y, row in enumerate(rows):
        for x, symbol in enumerate(row):
            color = palette.get(symbol)
            if color:
                image.putpixel((x, y), color)
    # Nearest neighbour, so the pixels stay square instead of being blurred.
    return image.resize((columns * scale, len(rows) * scale), Image.NEAREST)


def sheet(images: list[tuple[str, Image.Image]], columns: int, pad: int = 12) -> Image.Image:
    cell_w = max(img.width for _, img in images) + pad
    cell_h = max(img.height for _, img in images) + pad
    rows = (len(images) + columns - 1) // columns
    canvas = Image.new("RGBA", (cell_w * columns, cell_h * rows), BACKGROUND)
    for index, (_, img) in enumerate(images):
        x = (index % columns) * cell_w + pad // 2
        y = (index // columns) * cell_h + pad // 2
        canvas.paste(img, (x, y), img)
    return canvas


def main() -> None:
    out = Path(sys.argv[1]) if len(sys.argv) > 1 else ROOT / "build/sprite-preview"
    out.mkdir(parents=True, exist_ok=True)

    primitives = PRIMITIVES.read_text(encoding="utf-8")
    palette = read_palette(primitives)

    for name in ("knightSprite", "alchemistSprite"):
        image = render(read_matrix(primitives, name), palette, scale=12)
        path = out / f"{name}.png"
        image.save(path)
        print(f"{path}  ({image.width}x{image.height})")

    glyph_source = GLYPHS.read_text(encoding="utf-8")
    glyph_palette = {"X": (*GLYPH_TINT, 255), "o": (*GLYPH_TINT, 90)}
    names = re.findall(r"private val ([A-Z]+) = listOf\(", glyph_source)
    images = [(n, render(read_matrix(glyph_source, n), glyph_palette, scale=6)) for n in names]
    if images:
        path = out / "slot_glyphs.png"
        sheet(images, columns=5).save(path)
        print(f"{path}  ({', '.join(names)})")


if __name__ == "__main__":
    main()
