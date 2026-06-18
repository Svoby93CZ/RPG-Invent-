package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RPGViewModel
import com.example.utils.Pixelizer

@Composable
fun CharacterScreen(
    viewModel: RPGViewModel,
    modifier: Modifier = Modifier
) {
    val characterState by viewModel.character.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val equippedItems by viewModel.equippedItems.collectAsState()
    val backpackItems by viewModel.backpackItems.collectAsState()
    val activeCap by viewModel.activeInventoryCapacity.collectAsState()
    val activeWeight by viewModel.activeInventoryWeight.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var selectedItemForDetail by remember { mutableStateOf<Item?>(null) }
    var selectSlotToEquip by remember { mutableStateOf<Pair<String, Int>?>(null) } // slotType, slotIndex
    var classType by remember { mutableStateOf("KNIGHT") } // KNIGHT or ALCHEMIST

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GothicDarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Character Name & Class Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = characterState?.name ?: "Hrdina Reality",
                            color = GothicTextGold,
                            style = Typography.displayMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { showEditNameDialog = true },
                            modifier = Modifier.testTag("edit_char_name_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Upravit jméno",
                                tint = GothicGold
                            )
                        }
                    }
                    Text(
                        text = if (classType == "KNIGHT") "Rytíř Osudu" else "Alchymista Stínů",
                        color = GothicTextMuted,
                        style = Typography.bodyLarge,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LvL. 12",
                            color = GothicGold,
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Box(modifier = Modifier.width(120.dp).height(6.dp).background(GothicDarkSurface, RoundedCornerShape(3.dp))) {
                            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.65f).background(GothicGold, RoundedCornerShape(3.dp)))
                        }
                    }
                }

                // Switch avatar button
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { classType = if (classType == "KNIGHT") "ALCHEMIST" else "KNIGHT" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GothicLightSurface,
                            contentColor = GothicTextGold
                        ),
                        modifier = Modifier
                            .gothicBorder(GothicGold, 1.dp)
                            .testTag("switch_class_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Avatar", style = Typography.labelLarge)
                    }
                    // Character basic HP/Mana UI detail
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.width(40.dp).height(4.dp).background(GothicBloodRed))
                        Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color(0xFF3B82F6)))
                    }
                }
            }
        }

        // 2. Equipment Slots Layout
        item {
            GothicPanel(
                borderColor = GothicGold,
                title = "EQUIPMENT / VÝZBROJ"
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT COLUMN: Hlava, Krk, Hrudník 1-3, Pás
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EquipmentSlotCard("HLAVA", 0, "Hlava (Čepice)", equippedItems) {
                            selectSlotToEquip = Pair("HLAVA", 0)
                        }
                        EquipmentSlotCard("KRK", 0, "Krk (Náhrdelník)", equippedItems) {
                            selectSlotToEquip = Pair("KRK", 0)
                        }
                        EquipmentSlotCard("HRUDNIK", 0, "Hrudník L1", equippedItems) {
                            selectSlotToEquip = Pair("HRUDNIK", 0)
                        }
                        EquipmentSlotCard("HRUDNIK", 1, "Hrudník L2", equippedItems) {
                            selectSlotToEquip = Pair("HRUDNIK", 1)
                        }
                        EquipmentSlotCard("HRUDNIK", 2, "Hrudník L3", equippedItems) {
                            selectSlotToEquip = Pair("HRUDNIK", 2)
                        }
                        EquipmentSlotCard("PAS", 0, "Pás (Opasek)", equippedItems) {
                            selectSlotToEquip = Pair("PAS", 0)
                        }
                    }

                    // MIDDLE COLUMN: Animated Character Representation + Stats Summary
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        PixelArtCharacter(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            modelName = classType
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Summary Box
                        GothicPanel(
                            borderColor = GothicBorderGray,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "STATISTIKY",
                                color = GothicTextGold,
                                style = Typography.labelLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            val combinedStats = remember(equippedItems) {
                                val map = mutableMapOf<String, String>()
                                equippedItems.forEach { item ->
                                    if (item.stats.isNotEmpty()) {
                                        item.stats.split(",").forEach {
                                            val parts = it.split(":")
                                            if (parts.size == 2) {
                                                val k = parts[0].trim()
                                                val v = parts[1].trim()
                                                map[k] = v
                                            }
                                        }
                                    }
                                }
                                map
                            }

                            if (combinedStats.isEmpty()) {
                                Text(
                                    "Žádné bonusy z výbavy",
                                    color = GothicTextMuted,
                                    style = Typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                combinedStats.forEach { (name, value) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(name, color = GothicTextSilver, style = Typography.bodyMedium, fontSize = 10.sp)
                                        Text(value, color = GothicTextGold, style = Typography.bodyMedium, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // RIGHT COLUMN: Nohy 1-3, Chodidla 1-2
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EquipmentSlotCard("NOHY", 0, "Nohy L1", equippedItems) {
                            selectSlotToEquip = Pair("NOHY", 0)
                        }
                        EquipmentSlotCard("NOHY", 1, "Nohy L2", equippedItems) {
                            selectSlotToEquip = Pair("NOHY", 1)
                        }
                        EquipmentSlotCard("NOHY", 2, "Nohy L3", equippedItems) {
                            selectSlotToEquip = Pair("NOHY", 2)
                        }
                        EquipmentSlotCard("CHODIDLA", 0, "Socks (Chodidla L1)", equippedItems) {
                            selectSlotToEquip = Pair("CHODIDLA", 0)
                        }
                        EquipmentSlotCard("CHODIDLA", 1, "Boots (Chodidla L2)", equippedItems) {
                            selectSlotToEquip = Pair("CHODIDLA", 1)
                        }
                        EquipmentSlotCard("BATOH", 0, "Batoh/Taška", equippedItems) {
                            selectSlotToEquip = Pair("BATOH", 0)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // FINGER RINGS (Horizontal Panel)
                Text(
                    text = "Prsteny",
                    style = Typography.titleMedium,
                    color = GothicTextGold,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 0..3) {
                        EquipmentSlotCard("PRST", i, "Prst ${i+1}", equippedItems) {
                            selectSlotToEquip = Pair("PRST", i)
                        }
                    }
                }
            }
        }

        // 3. Backpack (Inventory) Header with capacity Meter
        item {
            GothicPanel(borderColor = GothicBloodRed) {
                RPGProgressBar(
                    value = backpackItems.size.toFloat(),
                    maxValue = activeCap.toFloat(),
                    barColor = GothicBloodRed,
                    label = "Aktivní Inventář (V batohu a kapsách)"
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Váha: ${String.format("%.2f", activeWeight)} kg",
                        color = GothicTextMuted,
                        style = Typography.bodyMedium
                    )
                    Text(
                        text = "Základní kapacita (0) + kapsy",
                        color = GothicTextMuted,
                        style = Typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        // 4. Grid of Backpack Items
        item {
            GothicPanel(title = "BATOH / KAPSY") {
                if (backpackItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tvůj batoh zeje prázdnotou.\nVytvoř nebo vezmi si věci z truhly.",
                            color = GothicTextMuted,
                            style = Typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        backpackItems.forEach { item ->
                            ItemGridBox(item) {
                                selectedItemForDetail = item
                            }
                        }
                    }
                }
            }
        }
    }

    // --- RENAME CHARACTER DIALOG ---
    if (showEditNameDialog) {
        var currentNameInput by remember { mutableStateOf(characterState?.name ?: "Hrdina Reality") }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Změna jména postavy", style = Typography.titleLarge, color = GothicTextGold) },
            text = {
                OutlinedTextField(
                    value = currentNameInput,
                    onValueChange = { currentNameInput = it },
                    label = { Text("Jméno postavy", color = GothicTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GothicTextSilver,
                        unfocusedTextColor = GothicTextSilver,
                        focusedBorderColor = GothicGold,
                        unfocusedBorderColor = GothicBorderGray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("character_name_input_field")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentNameInput.isNotBlank()) {
                            viewModel.updateCharacterName(currentNameInput.trim())
                        }
                        showEditNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GothicBloodRed)
                ) {
                    Text("Uložit", color = GothicTextSilver)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Zrušit", color = GothicTextMuted)
                }
            },
            containerColor = GothicDarkSurface,
            shape = GothicCardShape
        )
    }

    // --- SLOT CLICKED / ITEM COMPATIBLE SELECTION DIALOG ---
    selectSlotToEquip?.let { (slotType, index) ->
        val currentlyInSlot = equippedItems.find { it.slotType == slotType && it.equippedSlotIndex == index }
        val compatibleItems = backpackItems.filter { it.slotType == slotType }

        AlertDialog(
            onDismissRequest = { selectSlotToEquip = null },
            title = {
                Text(
                    text = "${getCzechSlotName(slotType)} [Slot ${index + 1}]",
                    style = Typography.titleLarge,
                    color = GothicTextGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (currentlyInSlot != null) {
                        Text("Aktuálně vybaveno:", color = GothicTextGold, style = Typography.labelLarge)
                        Card(
                            modifier = Modifier.fillMaxWidth().gothicBorder(getRarityColor(currentlyInSlot.rarity), 1.dp),
                            colors = CardDefaults.cardColors(containerColor = GothicLightSurface)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(currentlyInSlot.name, style = Typography.titleMedium, color = getRarityColor(currentlyInSlot.rarity))
                                if (currentlyInSlot.description.isNotEmpty()) {
                                    Text(currentlyInSlot.description, style = Typography.bodyMedium, fontStyle = FontStyle.Italic, color = GothicTextMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = {
                                        viewModel.unequipItem(currentlyInSlot)
                                        selectSlotToEquip = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GothicBloodRed),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Sundat z hrdiny", color = GothicTextSilver)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text("Vybrat ze tvůj batoh:", color = GothicTextGold, style = Typography.labelLarge)
                    if (compatibleItems.isEmpty()) {
                        Text(
                            text = "V batohu nemáš žádný předmět typu: ${getCzechSlotName(slotType)}",
                            style = Typography.bodyMedium,
                            color = GothicTextMuted
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.heightIn(max = 200.dp)
                        ) {
                            items(compatibleItems) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GothicLightSurface)
                                        .border(1.dp, getRarityColor(item.rarity), RoundedCornerShape(4.dp))
                                        .clickable {
                                            viewModel.equipItem(item, slotType, index)
                                            selectSlotToEquip = null
                                        }
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.name, color = getRarityColor(item.rarity), style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        if (item.stats.isNotEmpty()) {
                                            Text(item.stats, color = GothicTextGold, style = Typography.bodyMedium, fontSize = 10.sp)
                                        }
                                    }
                                    Text("Nasadit", color = GothicTextGold, style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectSlotToEquip = null }) {
                    Text("Zavřít", color = GothicTextMuted)
                }
            },
            containerColor = GothicDarkSurface,
            shape = GothicCardShape
        )
    }

    // --- ITEM DETAIL DIALOG (BACKPACK ITEM CLICKED) ---
    selectedItemForDetail?.let { item ->
        AlertDialog(
            onDismissRequest = { selectedItemForDetail = null },
            title = {
                Column {
                    Text(
                        text = item.name,
                        style = Typography.titleLarge,
                        color = getRarityColor(item.rarity)
                    )
                    Text(
                        text = getRarityCzechName(item.rarity),
                        color = getRarityColor(item.rarity).copy(alpha = 0.8f),
                        style = Typography.bodyMedium
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Item Icon Pixelart Preview if available
                    item.pixelArtData?.let { base64 ->
                        val bitmap = remember(base64) { Pixelizer.fromBase64(base64) }
                        bitmap?.let {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .background(GothicLightSurface)
                                    .border(2.dp, getRarityColor(item.rarity), GothicCardShape)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit,
                                    filterQuality = androidx.compose.ui.graphics.FilterQuality.None // maintains pixelation blocks
                                )
                            }
                        }
                    }

                    if (item.description.isNotEmpty()) {
                        Text(
                            text = item.description,
                            color = GothicTextMuted,
                            style = Typography.bodyLarge,
                            fontStyle = FontStyle.Italic
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Základní typ: ${getCzechSlotName(item.slotType)}", color = GothicTextSilver, style = Typography.bodyMedium)
                    Text("Hmotnost: ${item.weight} kg", color = GothicTextSilver, style = Typography.bodyMedium)
                    
                    if (item.stats.isNotEmpty()) {
                        Text("Vlastnosti:", color = GothicTextGold, style = Typography.labelLarge)
                        item.stats.split(",").forEach { stat ->
                            Text("• $stat", color = GothicTextSilver, style = Typography.bodyMedium)
                        }
                    }

                    if (item.hasPockets) {
                        Text("Zvětšuje inventář o: +${item.pocketSize} kapes", color = GothicTextGold, style = Typography.bodyMedium)
                    }

                    if (item.isConsumable) {
                        Text("Použití: ${item.charges} / ${item.maxCharges} zbývá", color = GothicTextGold, style = Typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Akce předmětu:", color = GothicTextGold, style = Typography.labelLarge)

                    // 1. Equip Button (if equipable)
                    if (item.slotType != "OBECNY") {
                        Button(
                            onClick = {
                                // For items with multiple slots (like HRUDNIK: 3 layers, PRST: 4 rings etc), auto-find first free or index 0
                                val targetSlotType = item.slotType
                                val maxSlots = SlotType.valueOf(targetSlotType).maxSlots
                                
                                // find empty index or use 0
                                var indexToEquip = 0
                                for (i in 0 until maxSlots) {
                                    val isOccupied = equippedItems.any { it.slotType == targetSlotType && it.equippedSlotIndex == i }
                                    if (!isOccupied) {
                                        indexToEquip = i
                                        break
                                    }
                                }
                                viewModel.equipItem(item, targetSlotType, indexToEquip)
                                selectedItemForDetail = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GothicBloodRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Nasadit do slotu", color = GothicTextSilver)
                        }
                    }

                    // 2. Consume Button (if consumable)
                    if (item.isConsumable) {
                        Button(
                            onClick = {
                                viewModel.consumeItem(item)
                                selectedItemForDetail = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GothicGold, contentColor = GothicDarkBackground),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Použít předmět", fontWeight = FontWeight.Bold)
                        }
                    }

                    // 3. Store in Location Button
                    if (locations.isNotEmpty()) {
                        var showStoreDropdown by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { showStoreDropdown = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GothicLightSurface, contentColor = GothicTextSilver),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Uložit do truhly / místa...", color = GothicTextGold)
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
                                            selectedItemForDetail = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                // Delete / Destroy Button
                IconButton(
                    onClick = {
                        viewModel.deleteItem(item)
                        selectedItemForDetail = null
                    },
                    modifier = Modifier.testTag("delete_item_button")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Zničit předmět", tint = GothicBloodRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemForDetail = null }) {
                    Text("Zavřít", color = GothicTextWithMuted)
                }
            },
            containerColor = GothicDarkSurface,
            shape = GothicCardShape
        )
    }
}

@Composable
fun EquipmentSlotCard(
    slotType: String,
    index: Int,
    label: String,
    equippedList: List<Item>,
    onClick: () -> Unit
) {
    val equippedItem = equippedList.find { it.slotType == slotType && it.equippedSlotIndex == index }
    val rarityColor = if (equippedItem != null) getRarityColor(equippedItem.rarity) else GothicBorderGray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(62.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (equippedItem != null) GothicLightSurface else GothicDarkSurface)
                .border(if (equippedItem != null) 2.dp else 1.dp, rarityColor, RoundedCornerShape(4.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (equippedItem == null) {
                // Empty slot indicator
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = label,
                    tint = GothicBorderGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                // Equipped Item Icon
                if (equippedItem.pixelArtData != null) {
                    val bitmap = remember(equippedItem.pixelArtData) { Pixelizer.fromBase64(equippedItem.pixelArtData) }
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = equippedItem.name,
                            modifier = Modifier.fillMaxSize().padding(2.dp),
                            contentScale = ContentScale.Fit,
                            filterQuality = androidx.compose.ui.graphics.FilterQuality.None
                        )
                    }
                } else {
                    // Fallback
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = equippedItem.name,
                        tint = rarityColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Text(
            text = label,
            color = if (equippedItem != null) GothicTextGold else GothicTextMuted,
            fontSize = 7.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            lineHeight = 9.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun ItemGridBox(
    item: Item,
    onClick: () -> Unit
) {
    val rarityColor = getRarityColor(item.rarity)

    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(GothicLightSurface)
            .border(2.dp, rarityColor, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .testTag("item_${item.id}"),
        contentAlignment = Alignment.Center
    ) {
        if (item.pixelArtData != null) {
            val bitmap = remember(item.pixelArtData) { Pixelizer.fromBase64(item.pixelArtData) }
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    contentScale = ContentScale.Fit,
                    filterQuality = androidx.compose.ui.graphics.FilterQuality.None
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = item.name,
                tint = rarityColor,
                modifier = Modifier.size(28.dp)
            )
        }

        // Pocket indicator small symbol
        if (item.hasPockets) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(2.dp)
                    .background(GothicBloodRed, RoundedCornerShape(2.dp))
                    .padding(horizontal = 2.dp)
            ) {
                Text(
                    text = "+${item.pocketSize}",
                    color = GothicTextSilver,
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Charges/Consumable indicator small symbol
        if (item.isConsumable) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp)
                    .background(GothicGold, RoundedCornerShape(2.dp))
                    .padding(horizontal = 2.dp)
            ) {
                Text(
                    text = "${item.charges}x",
                    color = GothicDarkBackground,
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Simple FlowRow helper if experimental Compose FlowRow isn't configured
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Custom layout mapping layout rows
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        var yPosition = 0
        var xPosition = 0
        var rowHeight = 0
        val layoutWidth = constraints.maxWidth
        
        // Compute heights
        val coordinates = mutableListOf<Triple<androidx.compose.ui.layout.Placeable, Int, Int>>()
        
        placeables.forEach { placeable ->
            if (xPosition + placeable.width > layoutWidth) {
                xPosition = 0
                yPosition += rowHeight + 8.dp.roundToPx()
                rowHeight = 0
            }
            coordinates.add(Triple(placeable, xPosition, yPosition))
            xPosition += placeable.width + 8.dp.roundToPx()
            rowHeight = Math.max(rowHeight, placeable.height)
        }
        
        val totalHeight = yPosition + rowHeight
        layout(layoutWidth, totalHeight) {
            coordinates.forEach { (placeable, x, y) ->
                placeable.placeRelative(x, y)
            }
        }
    }
}
val GothicTextWithMuted = Color(0xFFA1A1AA)
