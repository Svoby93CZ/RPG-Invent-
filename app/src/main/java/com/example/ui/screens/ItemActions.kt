package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.Item
import com.example.data.Location
import com.example.data.SlotType
import com.example.ui.components.TooltipButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.RPGViewModel

/**
 * Everything the hero can do with an item they are carrying: wear it, use it up, stash it in a
 * location, or destroy it. Shared by the character sheet and the backpack tab so an item offers
 * the same choices wherever it is tapped.
 */
@Composable
fun ColumnScope.BackpackItemActions(
    item: Item,
    locations: List<Location>,
    viewModel: RPGViewModel,
    onDismiss: () -> Unit
) {
    // A generic item has nowhere on the body to go, so it gets no equip button.
    if (item.slotType != SlotType.OBECNY.name) {
        TooltipButton(
            text = "Nasadit do slotu",
            containerColor = GothicBloodRed,
            contentColor = GothicTextSilver,
            testTag = "equip_item_button"
        ) {
            viewModel.equipItem(item, item.slotType, viewModel.firstFreeSlotIndex(item.slotType))
            onDismiss()
        }
    }

    if (item.isConsumable) {
        TooltipButton(
            text = "Použít předmět",
            containerColor = GothicGold,
            contentColor = GothicDarkBackground,
            testTag = "consume_item_button"
        ) {
            viewModel.consumeItem(item)
            onDismiss()
        }
    }

    if (locations.isNotEmpty()) {
        var showStoreDropdown by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            TooltipButton(
                text = "Uložit do truhly / místa...",
                containerColor = GothicLightSurface,
                contentColor = GothicTextGold,
                testTag = "store_item_button"
            ) {
                showStoreDropdown = true
            }
            DropdownMenu(
                expanded = showStoreDropdown,
                onDismissRequest = { showStoreDropdown = false },
                modifier = Modifier.background(GothicLightSurface)
            ) {
                locations.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(location.name, color = GothicTextSilver) },
                        onClick = {
                            viewModel.moveItemToLocation(item, location.id)
                            showStoreDropdown = false
                            onDismiss()
                        }
                    )
                }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onDismiss) {
            Text("Zavřít", color = GothicTextMuted)
        }
        IconButton(
            onClick = {
                viewModel.deleteItem(item)
                onDismiss()
            },
            modifier = Modifier.testTag("delete_item_button")
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Zničit předmět",
                tint = GothicBloodRed
            )
        }
    }
}
