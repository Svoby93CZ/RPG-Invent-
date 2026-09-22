package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Cinzel — a titling face cut after Roman inscriptions. Carved-in-stone headings are what make
 * a fantasy UI read as one, so every panel title and screen heading uses it.
 *
 * Bundled as static instances pinned from the variable original, because minSdk is 24 and
 * variable fonts only arrived in API 26.
 */
val CinzelFamily = FontFamily(
    Font(R.font.cinzel_regular, FontWeight.Normal),
    Font(R.font.cinzel_bold, FontWeight.Bold)
)

/** EB Garamond — the book serif the item cards are set in, italic included for flavour text. */
val GaramondFamily = FontFamily(
    Font(R.font.eb_garamond_regular, FontWeight.Normal),
    Font(R.font.eb_garamond_semibold, FontWeight.SemiBold),
    Font(R.font.eb_garamond_semibold, FontWeight.Bold),
    Font(R.font.eb_garamond_italic, FontWeight.Normal, FontStyle.Italic)
)

/**
 * Three voices, on purpose:
 *  - Cinzel for headings, which sets the tone,
 *  - monospace for lists, counters and buttons, which keeps the pixel/terminal feel,
 *  - Garamond for item cards (see the Tooltip styles below), which reads like a page from a tome.
 */
val Typography = Typography(
    displayMedium = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        letterSpacing = 1.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp
    ),
    titleMedium = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.sp
    )
)

// --- Item card styles, after the reference tooltip ---

val TooltipTitleStyle = TextStyle(
    fontFamily = CinzelFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.5.sp
)

val TooltipHeadlineStyle = TextStyle(
    fontFamily = GaramondFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 23.sp
)

val TooltipRarityStyle = TextStyle(
    fontFamily = GaramondFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 17.sp,
    letterSpacing = 1.sp
)

val TooltipBodyStyle = TextStyle(
    fontFamily = GaramondFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp
)

val LoreStyle = TextStyle(
    fontFamily = GaramondFamily,
    fontStyle = FontStyle.Italic,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 19.sp
)
