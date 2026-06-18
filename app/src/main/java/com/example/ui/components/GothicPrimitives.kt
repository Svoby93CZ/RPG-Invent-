package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// --- GOTHYX CUSTOM BORDERS ---

fun Modifier.gothicBorder(
    borderColor: Color = GothicGold,
    borderWidth: Dp = 2.dp,
    padding: Dp = 4.dp
): Modifier = this
    .border(width = borderWidth, color = borderColor, shape = GothicCardShape)
    .padding(padding)

val GothicCardShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val notch = 12f // pixels to indent corners

    moveTo(notch, 0f)
    lineTo(w - notch, 0f)
    lineTo(w, notch)
    lineTo(w, h - notch)
    lineTo(w - notch, h)
    lineTo(notch, h)
    lineTo(0f, h - notch)
    lineTo(0f, notch)
    close()
}

@Composable
fun GothicPanel(
    modifier: Modifier = Modifier,
    borderColor: Color = GothicGold,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(GothicCardShape)
            .background(GothicDarkSurface)
            .border(2.dp, borderColor, GothicCardShape)
            .padding(12.dp)
    ) {
        if (title != null) {
            Text(
                text = title.uppercase(),
                color = GothicTextGold,
                style = Typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(borderColor)
                    .padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        content()
    }
}

// --- RPG PIXEL ART SPRITES ---

@Composable
fun PixelArtCharacter(
    modifier: Modifier = Modifier,
    modelName: String = "KNIGHT"
) {
    // Elegant breathing macro animation to animate the pixel character
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breatheY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheY"
    )

    // A very detailed 32x32 gothic warrior sprite matrix
    val knightSprite = listOf(
        "................................",
        ".............XXXXXX.............",
        "...........XXYYYYYYXX...........",
        "..........XXSSSSSSHSSXX.........",
        ".........XXSSSSSSSSHHHHX........",
        ".........XSSSSHHHHSSSSHHX.......",
        "........XXSSSKKKKKKSSSHHX.......",
        "........XXSSKCCCCCCKSSSHXX......",
        "........XXSSSKKKKKKSSSHHHX......",
        "........XXXSSSSHHHHSSHHHXX......",
        ".........XXXXHHHHHHHHXXXX.......",
        "........XXXSSXXXRRXXXSSXXX......",
        ".......XXXSSXXXRRYRXXXSSXXX.....",
        "......XXXSSSXXXRRYYRYXXSSSXXX...",
        "......XHHXSSXXXRRYYRRYYXSSXHHX..",
        "......XHHXSSXXXRRYRRRYYXSSXHHX..",
        "......XHHXHHXXRRRRYYRRYXXHHXHH..",
        ".......XXXXHHXBBRRRRBBXXHHXXXX..",
        "........XSSSSXBBBBBBBBXSSSSX....",
        "........XSSSSXHHHHHHHHXSSSSX....",
        ".........XXSXXHHHHHHHHXXSXX.....",
        "...........XXXHHHHHHHHXXX.......",
        "...........XHHHHH..HHHHHX.......",
        "...........XHHHH....HHHHX.......",
        "...........XHHHX....XHHHX.......",
        "...........XHHX......XHHX.......",
        "...........XHHX......XHHX.......",
        "...........XHHX......XHHX.......",
        "...........XHHX......XHHX.......",
        "...........XBBX......XBBX.......",
        "...........XXXX......XXXX.......",
        "................................"
    )

    val alchemistSprite = listOf(
        "................................",
        ".............XXXXXX.............",
        "...........XXDDDDDDXX...........",
        "..........XXDDDDMDDDDXX.........",
        ".........XXDDMMMMMMMDDXX........",
        ".........XDDMMMMMMMMMDDX........",
        "........XXDMMMMMYMMMMMDDXX......",
        "........XXDMMMYKKKYMMMMDXX......",
        "........XXDMMMKKWKKKMMMDXX......",
        "........XXDMMMKCCWKKKMMDXX......",
        ".........XXXXXMKKWKKMXXXX.......",
        "..........XXXDMMKKMMDXXX........",
        ".........XXXDDXXXXXXDDXXX.......",
        "........XXXDDXXXEEXXXDDXXX......",
        ".......XXXDDXXXEEEEXXXDDXXX.....",
        "......XXDDDDXXXEEREEEXXDDXDXX...",
        "......XDDDXXXEERREEREEXXXDDDDX..",
        "......XDDXXXEEERREEEREEEXXDDDX..",
        "......XDDXXXEEEEEEEEREEEXXDDDX..",
        "......XXDXXXXXBBERBBBBXXXXDXX...",
        "........XXXXXXXXBBBXXXXXXXX.....",
        "...........XXXXBBBBXXXX.........",
        "............XDDDDDDDDX..........",
        "...........XXDDDDDDDDXX.........",
        "...........XDDDDDDDDDDX.........",
        "..........XDDDD....DDDDX........",
        "..........XDDDX....XDDDX........",
        "..........XDDX......XDDX........",
        "..........XDDX......XDDX........",
        "..........XDDX......XDDX........",
        "..........XBBX......XBBX........",
        "..........XXXX......XXXX........"
    )

    val sprite = if (modelName == "ALCHEMIST") alchemistSprite else knightSprite

    Canvas(modifier = modifier) {
        val rows = sprite.size
        val cols = sprite[0].length
        val cellW = size.width / cols
        val cellH = size.height / rows

        // Shadows under character
        drawOval(
            color = Color(0x77000000),
            topLeft = Offset(size.width * 0.12f, size.height * 0.92f),
            size = Size(size.width * 0.76f, size.height * 0.08f)
        )

        for (r in 0 until rows) {
            val line = sprite[r]
            for (c in 0 until cols) {
                val char = line[c]
                if (char == '.') continue

                val color = when (char) {
                    'X' -> Color(0xFF09090B) // Outer frame / pitch black shading
                    'O' -> Color(0xFFFED7AA) // Skin tone
                    'N' -> Color(0xFFFDBA74) // Skin shadow
                    'W' -> Color(0xFFFFFFFF) // Bright white reflection / eyes
                    'Y' -> Color(0xFFFBBF24) // Gold crown / hilt / glowing components
                    'P' -> Color(0xFFF472B6) // Purple-pink mystical power
                    'G' -> Color(0xFF52525B) // Steel plate dark gray
                    'S' -> Color(0xFFD4D4D8) // Steel plate light silver reflection
                    'R' -> Color(0xFFB91C1C) // Blood Red robe / ruby potion
                    'F' -> Color(0xFFEF4444) // Shiny bright red tunic highlighting
                    'B' -> Color(0xFF78350F) // Wooden staff / leather belt brown
                    'D' -> Color(0xFF1E1B4B) // Dark Indigo deep shadow robes
                    'M' -> Color(0xFF8B5CF6) // Arcane Purple magic velvet wizard robes
                    'H' -> Color(0xFF27272A) // Heavy plate very dark anthracite
                    'E' -> Color(0xFF10B981) // Emerald green toxic fluid / lining
                    'C' -> Color(0xFF22D3EE) // Luminous cyan blue eyes / energy
                    'V' -> Color(0xFF047857) // Alchemist velvet green tunic
                    'K' -> Color(0xFF000000) // Deep void black
                    else -> Color.Transparent
                }

                if (color != Color.Transparent) {
                    // Slight vertical stretch animation for breathing
                    val currentBreathe = if (r < 6) 0f else breatheY // legs stay grounded, head/chest breathes
                    drawRect(
                        color = color,
                        topLeft = Offset(c * cellW, r * cellH - (currentBreathe * (rows - r) / rows)),
                        size = Size(cellW + 0.5f, cellH + 0.5f) // plus small overlap to avoid lines
                    )
                }
            }
        }
    }
}

// --- RPG QUALITY-OF-LIFE STATUS BARS ---

@Composable
fun RPGProgressBar(
    value: Float,
    maxValue: Float,
    modifier: Modifier = Modifier,
    barColor: Color = GothicBloodRed,
    label: String = ""
) {
    val progress = if (maxValue > 0f) (value / maxValue).coerceIn(0f, 1f) else 0f
    
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    color = GothicTextSilver,
                    style = Typography.bodyMedium
                )
                Text(
                    text = "${value.toInt()}/${maxValue.toInt()}",
                    color = GothicTextGold,
                    style = Typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(GothicLightSurface)
                .border(1.dp, GothicBorderGray)
                .padding(2.dp)
        ) {
            // Inner progress
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(barColor)
            )
            
            // Grid-ticks like RPG status bars
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = size.width / 10f
                for (i in 1..9) {
                    val x = i * step
                    drawLine(
                        color = Color(0x44000000),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 2f
                    )
                }
            }
        }
    }
}

// --- ITEM DETAILS HELPERS ---

fun getRarityColor(rarityName: String): Color {
    return when (rarityName.uppercase()) {
        "COMMON", "OBYCEJNY" -> ColorCommon
        "UNCOMMON", "NEOBYCEJNY" -> ColorUncommon
        "RARE", "VZACNY" -> ColorRare
        "UNIQUE", "UNIKATNI" -> ColorUnique
        "LEGENDARY", "LEGENDARNI" -> ColorLegendary
        else -> ColorCommon
    }
}

fun getRarityCzechName(rarityName: String): String {
    return when (rarityName.uppercase()) {
        "COMMON", "OBYCEJNY" -> "Obyčejný"
        "UNCOMMON", "NEOBYCEJNY" -> "Neobyčejný"
        "RARE", "VZACNY" -> "Vzácný"
        "UNIQUE", "UNIKATNI" -> "Unikátní"
        "LEGENDARY", "LEGENDARNI" -> "Legendární"
        else -> rarityName
    }
}

fun getCzechSlotName(slotType: String): String {
    return when (slotType.uppercase()) {
        "HLAVA" -> "Hlava"
        "KRK" -> "Krk"
        "HRUDNIK" -> "Hrudník"
        "PAS" -> "Pás"
        "NOHY" -> "Nohy"
        "CHODIDLA" -> "Chodidla"
        "PRST" -> "Prsty"
        "BATOH" -> "Batoh"
        "OBECNY" -> "Obecný předmět"
        else -> slotType
    }
}
