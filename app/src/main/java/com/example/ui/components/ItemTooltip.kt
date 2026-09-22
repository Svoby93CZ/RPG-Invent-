package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Item
import com.example.ui.theme.*
import com.example.utils.Pixelizer
import com.example.utils.StatUtils
import java.util.Locale

/** One place that decides how a weight is written, so every screen shows "1,20 kg" alike. */
fun formatWeight(kilograms: Double): String =
    String.format(Locale.getDefault(), "%.2f kg", kilograms)

/**
 * The item's own pixel art when it has one, otherwise the silhouette of the slot it belongs to
 * tinted in its rarity colour. Every screen draws item art through this so a missing icon never
 * degrades into a bare star.
 */
@Composable
fun ItemIcon(
    item: Item,
    modifier: Modifier = Modifier
) {
    val art = item.pixelArtData
    val bitmap = remember(art) { art?.let { Pixelizer.fromBase64(it) } }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = item.name,
            modifier = modifier,
            contentScale = ContentScale.Fit,
            // Nearest-neighbour keeps the blocks square instead of smudging them.
            filterQuality = FilterQuality.None
        )
    } else {
        PixelGlyph(
            rows = SlotGlyphs.forSlot(item.slotType),
            color = getRarityColor(item.rarity),
            modifier = modifier
        )
    }
}

/** A hairline that fades out to the right, the way the rules in a Baldur's Gate tooltip do. */
@Composable
private fun TooltipRule(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(color.copy(alpha = 0.7f), color.copy(alpha = 0.15f), Color.Transparent)
                )
            )
    )
}

@Composable
private fun StatRow(
    name: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = name,
            style = TooltipBodyStyle,
            color = GothicTextSilver,
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = value, style = TooltipBodyStyle, color = StatValue)
    }
}

/** An icon + label line, used for the properties a slot or a charge count adds to an item. */
@Composable
private fun PropertyRow(
    icon: ImageVector,
    text: String,
    tint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = TooltipBodyStyle, color = GothicTextSilver)
    }
}

/**
 * An item card laid out like the tooltips in Baldur's Gate III: a rarity-tinted header carrying
 * the name and the art, then the stat block, the properties, the italic flavour text, and a
 * closing strip with the slot and the weight. [actions] is appended above that strip.
 */
@Composable
fun ItemTooltipCard(
    item: Item,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable ColumnScope.() -> Unit = {}
) {
    val rarityColor = getRarityColor(item.rarity)
    val stats = remember(item.stats) { StatUtils.parse(item.stats) }

    Column(
        modifier = modifier
            .clip(GothicCardShape)
            .background(TooltipBody)
            .border(1.dp, rarityColor.copy(alpha = 0.55f), GothicCardShape)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Header: name, rarity, art. The rarity colour bleeds in from the left edge. ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TooltipHeader)
                .background(
                    Brush.horizontalGradient(listOf(rarityColor.copy(alpha = 0.22f), Color.Transparent))
                )
                .padding(start = 14.dp, end = 12.dp, top = 14.dp, bottom = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.name, style = TooltipTitleStyle, color = rarityColor)
                    Text(
                        text = getRarityCzechName(item.rarity).uppercase(),
                        style = TooltipRarityStyle,
                        color = rarityColor.copy(alpha = 0.75f)
                    )
                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        PropertyRow(icon = Icons.Default.Check, text = subtitle, tint = GothicGold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                ItemIcon(item = item, modifier = Modifier.size(58.dp))
            }
        }

        TooltipRule(rarityColor)

        // --- Stat block: the first stat reads as the headline, the rest as a list. ---
        if (stats.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val headline = stats.first()
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = headline.name, style = TooltipHeadlineStyle, color = GothicTextSilver)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = headline.value, style = TooltipHeadlineStyle, color = StatValue)
                }
                stats.drop(1).forEach { StatRow(name = it.name, value = it.value) }
            }
            TooltipRule(TooltipDivider)
        }

        // --- Properties the item grants beyond its plain stats. ---
        if (item.hasPockets || item.isConsumable) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TooltipSection)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.hasPockets) {
                    PropertyRow(
                        icon = Icons.Default.Star,
                        text = "Kapsy a popruhy: +${item.pocketSize} míst v batohu",
                        tint = GothicGold
                    )
                }
                if (item.isConsumable) {
                    PropertyRow(
                        icon = Icons.Default.Star,
                        text = "Spotřební předmět: zbývá ${item.charges} z ${item.maxCharges} použití",
                        tint = GothicGold
                    )
                }
            }
            TooltipRule(TooltipDivider)
        }

        // --- Flavour text, in the faded parchment italic the reference tooltip closes with. ---
        if (item.description.isNotEmpty()) {
            Text(
                text = item.description,
                style = LoreStyle,
                color = LoreParchment,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            )
        }

        // --- Screen-specific buttons. ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = actions
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- Closing strip: what it is on the left, what it weighs on the right. ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TooltipFooter)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getCzechSlotName(item.slotType),
                style = Typography.bodyMedium,
                color = GothicTextMuted
            )
            Text(
                text = formatWeight(item.weight),
                style = Typography.bodyMedium,
                color = GothicTextMuted
            )
        }
    }
}

/** The full-width action button the tooltip's action block is built from. */
@Composable
fun TooltipButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = GothicCardShape,
        modifier = modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Text(text = text, style = Typography.labelLarge)
    }
}

/** [ItemTooltipCard] shown as a modal, sized to the screen rather than to Material's defaults. */
@Composable
fun ItemDetailDialog(
    item: Item,
    onDismiss: () -> Unit,
    subtitle: String? = null,
    maxHeight: Dp = 640.dp,
    actions: @Composable ColumnScope.() -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ItemTooltipCard(
            item = item,
            subtitle = subtitle,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxWidth()
                .heightIn(max = maxHeight),
            actions = actions
        )
    }
}
