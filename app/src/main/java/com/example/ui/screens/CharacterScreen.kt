package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.compose.material.icons.filled.List
import com.example.utils.StatUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterScreen(
    viewModel: RPGViewModel,
    modifier: Modifier = Modifier
) {
    val characterState by viewModel.character.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val equippedItems by viewModel.equippedItems.collectAsState()
    val backpackItems by viewModel.backpackItems.collectAsState()
    val equipmentSets by viewModel.equipmentSets.collectAsState()
    val skills by viewModel.skills.collectAsState()
    
    val activeCap by viewModel.activeInventoryCapacity.collectAsState()
    val activeWeight by viewModel.activeInventoryWeight.collectAsState()
    val abilityScores by viewModel.abilityScores.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showSetsDialog by remember { mutableStateOf(false) }
    var showAddSkillDialog by remember { mutableStateOf(false) }
    var showAbilitiesDialog by remember { mutableStateOf(false) }
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
                }

                // Switch avatar button
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = { showSetsDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GothicLightSurface,
                                contentColor = GothicTextGold
                            ),
                            modifier = Modifier.gothicBorder(GothicGold, 1.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
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
                    }
                    // Character basic HP/Mana UI detail
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.width(40.dp).height(4.dp).background(GothicBloodRed))
                        Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color(0xFF3B82F6)))
                    }
                }
            }
        }

        // 2. Attributes
        item {
            AbilityPanel(
                scores = abilityScores,
                onEditClick = { showAbilitiesDialog = true },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 3. Equipment Slots Layout
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
                            
                            // Bonuses of the same name add up instead of the last item
                            // equipped silently overwriting every earlier one.
                            val combinedStats = remember(equippedItems) {
                                StatUtils.merge(equippedItems.map { it.stats })
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

        // 3. Skills (Zkušenosti) List
        item {
            GothicPanel(title = "ZKUŠENOSTI A DOVEDNOSTI") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (skills.isEmpty()) {
                        Text(
                            text = "Zatím nemáš žádné zkušenosti.",
                            color = GothicTextMuted,
                            style = Typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        skills.forEach { skill ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(GothicLightSurface, GothicCardShape)
                                    .gothicBorder(GothicBorderGray, 1.dp)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = skill.name,
                                    color = GothicTextSilver,
                                    style = Typography.bodyLarge
                                )
                                IconButton(
                                    onClick = { viewModel.deleteSkill(skill) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Smazat",
                                        tint = GothicBloodRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { showAddSkillDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GothicDarkSurface, contentColor = GothicTextGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .gothicBorder(GothicGold, 1.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Přidat zkušenost")
                    }
                }
            }
        }

        // 4. Backpack (Inventory) Header with capacity Meter
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
                        text = "Základ ($BASE_INVENTORY_CAPACITY) + kapsy",
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

    // --- ATTRIBUTES DIALOG ---
    if (showAbilitiesDialog) {
        AbilityEditDialog(
            scores = abilityScores,
            onDismiss = { showAbilitiesDialog = false },
            onChange = { ability, score -> viewModel.updateAbilityScore(ability, score) }
        )
    }

    // --- ADD SKILL DIALOG ---
    if (showAddSkillDialog) {
        var skillNameInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddSkillDialog = false },
            title = { Text("Nová zkušenost", style = Typography.titleLarge, color = GothicTextGold) },
            text = {
                OutlinedTextField(
                    value = skillNameInput,
                    onValueChange = { skillNameInput = it },
                    label = { Text("Např. Řidičák, Vyjednávání...", color = GothicTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GothicTextSilver,
                        unfocusedTextColor = GothicTextSilver,
                        focusedBorderColor = GothicGold,
                        unfocusedBorderColor = GothicBorderGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (skillNameInput.isNotBlank()) {
                            viewModel.insertSkill(skillNameInput.trim())
                        }
                        showAddSkillDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GothicGold, contentColor = GothicDarkBackground)
                ) {
                    Text("Přidat", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSkillDialog = false }) {
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

    // --- ITEM DETAIL TOOLTIP (BACKPACK ITEM CLICKED) ---
    selectedItemForDetail?.let { item ->
        ItemDetailDialog(item = item, onDismiss = { selectedItemForDetail = null }) {
            BackpackItemActions(
                item = item,
                locations = locations,
                viewModel = viewModel,
                onDismiss = { selectedItemForDetail = null }
            )
        }
    }

    // --- SETS MANAGEMENT DIALOG ---
    if (showSetsDialog) {
        var newSetNameInput by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showSetsDialog = false },
            title = { Text("Správa výzbroje (Sety)", style = Typography.titleLarge, color = GothicTextGold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Save new set
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newSetNameInput,
                            onValueChange = { newSetNameInput = it },
                            label = { Text("Název nového setu", color = GothicTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GothicTextSilver,
                                unfocusedTextColor = GothicTextSilver,
                                focusedBorderColor = GothicGold,
                                unfocusedBorderColor = GothicBorderGray
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (newSetNameInput.isNotBlank()) {
                                    viewModel.saveCurrentEquipmentAsSet(newSetNameInput.trim())
                                    newSetNameInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GothicGold, contentColor = GothicDarkBackground)
                        ) {
                            Text("Uložit", fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = GothicBorderGray)

                    // List existing sets
                    if (equipmentSets.isEmpty()) {
                        Text("Nemáš uložené žádné sety.", color = GothicTextMuted, style = Typography.bodyMedium)
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.heightIn(max = 250.dp)
                        ) {
                            items(equipmentSets) { setDef ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().gothicBorder(GothicGold, 1.dp),
                                    colors = CardDefaults.cardColors(containerColor = GothicLightSurface)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(setDef.name, style = Typography.titleMedium, color = GothicTextGold, fontWeight = FontWeight.Bold)
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    viewModel.loadEquipmentSet(setDef)
                                                    showSetsDialog = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = GothicDarkSurface, contentColor = GothicTextGold),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp).gothicBorder(GothicGold, 1.dp)
                                            ) {
                                                Text("Obléct", fontSize = 12.sp)
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteEquipmentSet(setDef) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Smazat set", tint = GothicBloodRed)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSetsDialog = false }) {
                    Text("Zavřít", color = GothicTextMuted)
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
                .clickable { onClick() }
                .testTag("slot_${slotType}_$index"),
            contentAlignment = Alignment.Center
        ) {
            if (equippedItem == null) {
                // An empty slot shows the silhouette of what belongs in it, so the six chest
                // and leg layers can be told apart at a glance.
                PixelGlyph(
                    rows = SlotGlyphs.forSlot(slotType),
                    color = GothicBorderGray,
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                )
            } else {
                ItemIcon(
                    item = equippedItem,
                    modifier = Modifier.fillMaxSize().padding(4.dp)
                )
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
        ItemIcon(item = item, modifier = Modifier.fillMaxSize().padding(5.dp))

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
