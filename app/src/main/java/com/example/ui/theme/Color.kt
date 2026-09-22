package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Gothic RPG Palette
val GothicBloodRed = Color(0xFF991B1B) // Crimson line, primary accents
val GothicGold = Color(0xFFC5A059) // Accents, frames, titles
val GothicDarkBackground = Color(0xFF0F0F10) // Dark slate-obsidian void
val GothicDarkSurface = Color(0xFF1A1A1C) // Dark granite chamber cards
val GothicLightSurface = Color(0xFF252529) // Lighter stone for items and inputs
val GothicBorderGray = Color(0xFF3F3F46) // Muted metal border
val GothicTextGold = Color(0xFFE2C284) // Glowing gold text
val GothicTextSilver = Color(0xFFE4E4E7) // Clean soft silver text
val GothicTextMuted = Color(0xFF8E8E93) // Haunted gray/italics for descriptions

// RPG item quality colors
val ColorCommon = Color(0xFFFFFFFF)     // Common (White)
val ColorUncommon = Color(0xFF4ADE80)   // Uncommon (Green)
val ColorRare = Color(0xFF60A5FA)       // Rare (Blue)
val ColorUnique = Color(0xFFC084FC)     // Unique (Purple)
val ColorLegendary = Color(0xFFF97316)  // Legendary (Orange)

// --- Item tooltip palette (Baldur's Gate III inspired layering) ---
// The tooltip is built from four stacked bands that get darker towards the bottom,
// so the item name reads first and the weight/slot footer reads last.
val TooltipHeader = Color(0xFF2B2B31)   // band behind the item name and its icon
val TooltipBody = Color(0xFF1F1F24)     // stat block
val TooltipSection = Color(0xFF26262C)  // properties / description band
val TooltipFooter = Color(0xFF131316)   // weight and slot footer strip
val TooltipDivider = Color(0xFF44444E)  // hairline between bands
val LoreParchment = Color(0xFF938B68)   // italic flavour text, faded parchment ink
val StatValue = Color(0xFFD8C79A)       // numbers on the right-hand side of a stat row
