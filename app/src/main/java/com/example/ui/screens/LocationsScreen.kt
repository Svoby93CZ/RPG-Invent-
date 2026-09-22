package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RPGViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LocationsScreen(
    viewModel: RPGViewModel,
    modifier: Modifier = Modifier
) {
    val locations by viewModel.locations.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val backpackItems by viewModel.backpackItems.collectAsState()
    val activeCap by viewModel.activeInventoryCapacity.collectAsState()

    var showCreateLocationDialog by remember { mutableStateOf(false) }
    var locationToEdit by remember { mutableStateOf<Location?>(null) }
    var locationToDelete by remember { mutableStateOf<Location?>(null) }
    
    var expandedLocationId by remember { mutableStateOf<Long?>(null) }
    var selectedItemForDetail by remember { mutableStateOf<Item?>(null) }
    
    // Backpack full warn dialog state
    var showBackpackFullWarn by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GothicDarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Screen Title Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ARCHIVNÍ MÍSTA & RESTRY",
                        color = GothicTextGold,
                        style = Typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Místa pro ukládání tvé nashromážděné výbavy.",
                        color = GothicTextMuted,
                        style = Typography.bodyLarge
                    )
                }
                
                IconButton(
                    onClick = { showCreateLocationDialog = true },
                    modifier = Modifier
                        .gothicBorder(GothicGold, 1.dp)
                        .testTag("add_location_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Vytvořit místo", tint = GothicGold)
                }
            }
        }

        // 2. Locations List
        if (locations.isEmpty()) {
            item {
                GothicPanel(borderColor = GothicBorderGray) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = GothicBorderGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nemáš založené žádné truhly ani stálá místa.\nKlikni na tlačítko '+' nahoře a stvoř novou lokaci.",
                                color = GothicTextMuted,
                                style = Typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            items(locations) { location ->
                val itemsInLocation = remember(allItems, location.id) {
                    allItems.filter { it.locationId == location.id }
                }
                val isExpanded = expandedLocationId == location.id
                
                GothicPanel(
                    borderColor = if (isExpanded) GothicGold else GothicBorderGray,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header Row: Expandable click, rename, delete
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedLocationId = if (isExpanded) null else location.id
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = GothicTextGold
                            )
                            Column {
                                Text(
                                    text = location.name.uppercase(),
                                    color = if (isExpanded) GothicTextGold else GothicTextSilver,
                                    style = Typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Uloženo věcí: ${itemsInLocation.size} z ${location.maxCapacity}",
                                    color = GothicTextMuted,
                                    style = Typography.bodyMedium,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Edit / Delete icons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { locationToEdit = location },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Upravit místo", tint = GothicGold, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                // Deleting a location destroys everything stored in it, so ask first.
                                onClick = { locationToDelete = location },
                                modifier = Modifier.size(36.dp).testTag("delete_location_${location.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Smazat místo", tint = GothicBloodRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Location Capacity progress bar
                    RPGProgressBar(
                        value = itemsInLocation.size.toFloat(),
                        maxValue = location.maxCapacity.toFloat(),
                        barColor = GothicGold
                    )

                    // Stored items grid inside (Collapsible with smooth animation)
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Text(
                                "ULOŽENÉ KLENOTY:",
                                color = GothicTextGold,
                                style = Typography.labelLarge,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            if (itemsInLocation.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(GothicLightSurface)
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Tato truhla je prázdná.\nPřesuň sem něco z aktivního batohu hrdiny.",
                                        color = GothicTextMuted,
                                        style = Typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    itemsInLocation.forEach { item ->
                                        ItemGridBox(item) {
                                            selectedItemForDetail = item
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- CREATE NEW LOCATION DIALOG ---
    if (showCreateLocationDialog) {
        var nameInput by remember { mutableStateOf("") }
        var capacityInput by remember { mutableStateOf("15") }

        AlertDialog(
            onDismissRequest = { showCreateLocationDialog = false },
            title = { Text("Založit nové útočiště", style = Typography.titleLarge, color = GothicTextGold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Název lokace (např. Chalupa)", color = GothicTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GothicTextSilver,
                            unfocusedTextColor = GothicTextSilver,
                            focusedBorderColor = GothicGold,
                            unfocusedBorderColor = GothicBorderGray
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("location_name_input_field"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = capacityInput,
                        onValueChange = { capacityInput = it },
                        label = { Text("Velikost truhly (kapacita)", color = GothicTextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GothicTextSilver,
                            unfocusedTextColor = GothicTextSilver,
                            focusedBorderColor = GothicGold,
                            unfocusedBorderColor = GothicBorderGray
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("location_capacity_input_field"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cap = capacityInput.toIntOrNull() ?: 15
                        if (nameInput.isNotBlank()) {
                            viewModel.createLocation(nameInput.trim(), cap)
                        }
                        showCreateLocationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GothicBloodRed),
                    enabled = nameInput.isNotBlank()
                ) {
                    Text("Založit", color = GothicTextSilver)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateLocationDialog = false }) {
                    Text("Zrušit", color = GothicTextMuted)
                }
            },
            containerColor = GothicDarkSurface,
            shape = GothicCardShape
        )
    }

    // --- EDIT LOCATION DIALOG ---
    locationToEdit?.let { location ->
        var nameInput by remember { mutableStateOf(location.name) }
        var capacityInput by remember { mutableStateOf(location.maxCapacity.toString()) }

        AlertDialog(
            onDismissRequest = { locationToEdit = null },
            title = { Text("Upravit skrýš", style = Typography.titleLarge, color = GothicTextGold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Název lokace", color = GothicTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GothicTextSilver,
                            unfocusedTextColor = GothicTextSilver,
                            focusedBorderColor = GothicGold,
                            unfocusedBorderColor = GothicBorderGray
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = capacityInput,
                        onValueChange = { capacityInput = it },
                        label = { Text("Kapacita truhly", color = GothicTextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GothicTextSilver,
                            unfocusedTextColor = GothicTextSilver,
                            focusedBorderColor = GothicGold,
                            unfocusedBorderColor = GothicBorderGray
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cap = capacityInput.toIntOrNull() ?: location.maxCapacity
                        if (nameInput.isNotBlank()) {
                            viewModel.updateLocation(location.copy(name = nameInput.trim(), maxCapacity = cap))
                        }
                        locationToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GothicBloodRed),
                    enabled = nameInput.isNotBlank()
                ) {
                    Text("Uložit změny", color = GothicTextSilver)
                }
            },
            dismissButton = {
                TextButton(onClick = { locationToEdit = null }) {
                    Text("Zrušit", color = GothicTextMuted)
                }
            },
            containerColor = GothicDarkSurface,
            shape = GothicCardShape
        )
    }

    // --- ITEM STASH TOOLTIP (RETRIEVING ACTION) ---
    selectedItemForDetail?.let { item ->
        val location = locations.find { it.id == item.locationId }
        ItemDetailDialog(
            item = item,
            onDismiss = { selectedItemForDetail = null },
            subtitle = location?.let { "Uloženo: ${it.name}" }
        ) {
            TooltipButton(
                text = "Vzít do batohu hrdiny",
                containerColor = GothicBloodRed,
                contentColor = GothicTextSilver,
                testTag = "retrieve_to_backpack_button"
            ) {
                if (backpackItems.size >= activeCap) {
                    showBackpackFullWarn = true
                } else {
                    // locationId = null puts the item back into the active inventory.
                    viewModel.moveItemToLocation(item, null)
                    selectedItemForDetail = null
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { selectedItemForDetail = null }) {
                    Text("Zavřít", color = GothicTextMuted)
                }
                IconButton(
                    onClick = {
                        viewModel.deleteItem(item)
                        selectedItemForDetail = null
                    },
                    modifier = Modifier.testTag("delete_stash_item_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Zničit věc",
                        tint = GothicBloodRed
                    )
                }
            }
        }
    }

    // --- DELETE LOCATION CONFIRMATION ---
    locationToDelete?.let { location ->
        val storedCount = allItems.count { it.locationId == location.id }
        AlertDialog(
            onDismissRequest = { locationToDelete = null },
            title = { Text("Zbořit ${location.name}?", style = Typography.titleLarge, color = GothicBloodRed) },
            text = {
                Text(
                    text = if (storedCount == 0) {
                        "Tato lokace je prázdná. Opravdu ji chceš smazat?"
                    } else {
                        "Spolu s lokací nenávratně zmizí i $storedCount uložených věcí. " +
                            "Pokud si je chceš nechat, nejdřív si je vezmi do batohu."
                    },
                    color = GothicTextSilver
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLocation(location)
                        locationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GothicBloodRed),
                    modifier = Modifier.testTag("confirm_delete_location_button")
                ) {
                    Text("Smazat navždy", color = GothicTextSilver)
                }
            },
            dismissButton = {
                TextButton(onClick = { locationToDelete = null }) {
                    Text("Zrušit", color = GothicTextMuted)
                }
            },
            containerColor = GothicDarkSurface,
            shape = GothicCardShape
        )
    }

    // --- ACTIVE BACKPACK FULL WARN DIALOG ---
    if (showBackpackFullWarn) {
        AlertDialog(
            onDismissRequest = { showBackpackFullWarn = false },
            title = { Text("Batoh je přeplněný!", style = Typography.titleLarge, color = GothicBloodRed) },
            text = {
                Text(
                    text = "Tvůj aktivní inventář nemá dostatek volného místa (Kapacita: $activeCap slots).\n\nAbys uvolnil místo v batohu, obleč si bundu/kalhoty s kapsami, nasaď si kožený batoh, nebo vyhoď nepoužívané krámy.",
                    color = GothicTextSilver
                )
            },
            confirmButton = {
                Button(
                    onClick = { showBackpackFullWarn = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GothicGold, contentColor = GothicDarkBackground)
                ) {
                    Text("Rozumím", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = GothicDarkSurface,
            shape = GothicCardShape
        )
    }
}
