package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.Item
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RPGViewModel

@Composable
fun BackpackScreen(viewModel: RPGViewModel) {
    val backpackItems by viewModel.backpackItems.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val activeCapacity by viewModel.activeInventoryCapacity.collectAsState()
    val activeWeight by viewModel.activeInventoryWeight.collectAsState()

    var selectedItem by remember { mutableStateOf<Item?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GothicDarkBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "BATOH",
            style = Typography.displayMedium,
            color = GothicTextGold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        GothicPanel(borderColor = GothicBloodRed, modifier = Modifier.fillMaxWidth()) {
            RPGProgressBar(
                value = backpackItems.size.toFloat(),
                maxValue = activeCapacity.toFloat(),
                barColor = GothicBloodRed,
                label = "Obsazená místa"
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Nesená váha: ${formatWeight(activeWeight)}",
                style = Typography.bodyMedium,
                color = GothicTextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (backpackItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Batoh je prázdný.\nVykovej si něco v Kovárně nebo si dojdi\npro věci do svých truhel.",
                    style = Typography.bodyLarge,
                    color = GothicTextMuted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(backpackItems, key = { it.id }) { item ->
                    BackpackRow(item = item) { selectedItem = item }
                }
            }
        }
    }

    selectedItem?.let { item ->
        ItemDetailDialog(item = item, onDismiss = { selectedItem = null }) {
            BackpackItemActions(
                item = item,
                locations = locations,
                viewModel = viewModel,
                onDismiss = { selectedItem = null }
            )
        }
    }
}

/** One line of the backpack list: art, name in its rarity colour, flavour text and weight. */
@Composable
private fun BackpackRow(
    item: Item,
    onClick: () -> Unit
) {
    val rarityColor = getRarityColor(item.rarity)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(GothicCardShape)
            .background(GothicDarkSurface)
            .border(1.dp, rarityColor.copy(alpha = 0.6f), GothicCardShape)
            .clickable { onClick() }
            .testTag("backpack_item_${item.id}")
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(GothicLightSurface)
                .border(1.dp, rarityColor.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            ItemIcon(item = item, modifier = Modifier.fillMaxSize().padding(4.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = rarityColor
            )
            if (item.description.isNotEmpty()) {
                Text(
                    text = item.description,
                    style = LoreStyle,
                    color = LoreParchment,
                    maxLines = 2
                )
            }
            Text(
                text = formatWeight(item.weight),
                style = Typography.bodyMedium,
                color = GothicTextMuted,
                fontStyle = FontStyle.Italic
            )
        }

        if (item.isConsumable) {
            Text(
                text = "${item.charges}x",
                style = Typography.labelLarge,
                color = GothicTextGold
            )
        }
    }
}
