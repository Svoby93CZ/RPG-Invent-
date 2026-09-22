package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RPGViewModel
import com.example.utils.Pixelizer
import com.example.utils.toWeightOrNull
import java.io.InputStream

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemCreationScreen(
    viewModel: RPGViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val backpackItems by viewModel.backpackItems.collectAsState()
    val activeCapacity by viewModel.activeInventoryCapacity.collectAsState()
    val freeSlots = (activeCapacity - backpackItems.size).coerceAtLeast(0)

    // Form inputs
    var name by remember { mutableStateOf("") }
    var selectedSlot by remember { mutableStateOf(SlotType.OBECNY) }
    var selectedRarity by remember { mutableStateOf(ItemRarity.COMMON) }
    var description by remember { mutableStateOf("") }
    var stats by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("0.5") }
    
    var isConsumable by remember { mutableStateOf(false) }
    var chargesInput by remember { mutableStateOf("3") }
    
    var hasPockets by remember { mutableStateOf(false) }
    var pocketSizeInput by remember { mutableStateOf("4") }

    // Pixelator state
    var selectedPixelArtBase64 by remember { mutableStateOf<String?>(null) }
    var showSuccessfullyCreatedMessage by remember { mutableStateOf(false) }

    // Predefined icon templates (custom-built retro grids). Each one rasterises a 16x16 matrix
    // and base64-encodes it, so they are built once instead of on every recomposition.
    val templates = remember {
        listOf(
            TemplateIcon("👑 Koruna", "HLAVA", drawCrownData()),
            TemplateIcon("🧣 Šála", "KRK", drawNecklaceData()),
            TemplateIcon("🛡️ Zbroj", "HRUDNIK", drawArmorData()),
            TemplateIcon("🎒 Batoh", "BATOH", drawBagData()),
            TemplateIcon("💍 Prsten", "PRST", drawRingData()),
            TemplateIcon("🧪 Elixír", "OBECNY", drawElixirData()),
            TemplateIcon("🥾 Bota", "CHODIDLA", drawBootsData()),
            TemplateIcon("🗡️ Meč", "OBECNY", drawSwordData())
        )
    }

    // Gallery selector launcher to pixelate device images
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                originalBitmap?.let { bitmap ->
                    // Run our core 128x128 gothic pixelator!
                    val pixelatedBitmap = Pixelizer.pixelize(bitmap, 128)
                    selectedPixelArtBase64 = Pixelizer.toBase64(pixelatedBitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GothicDarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Title
        item {
            Text(
                text = "KOVÁRNA PŘEDMĚTŮ",
                color = GothicTextGold,
                style = Typography.displayMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Zde ukovte nové zbraně, oděvy nebo stvořte magické lektvary.",
                color = GothicTextMuted,
                style = Typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Volné místo v batohu: $freeSlots z $activeCapacity",
                color = if (freeSlots > 0) GothicTextGold else GothicBloodRed,
                style = Typography.bodyMedium
            )
        }

        // Core Form Inputs
        item {
            GothicPanel(borderColor = GothicGold, title = "ZÁKLADNÍ VLASTNOSTI") {
                // Item Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Jméno předmětu", color = GothicTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GothicTextSilver,
                        unfocusedTextColor = GothicTextSilver,
                        focusedBorderColor = GothicGold,
                        unfocusedBorderColor = GothicBorderGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_name_input_field"),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                // Rarity Dropdown selector (Gothic styled Grid of Buttons instead of annoying traditional Dropdowns)
                Text("Vzácnost předmětu:", color = GothicTextGold, style = Typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ItemRarity.values().forEach { rarity ->
                        val isSelected = selectedRarity == rarity
                        Box(
                            modifier = Modifier
                                .clip(GothicCardShape)
                                .background(if (isSelected) getRarityColor(rarity.name) else GothicLightSurface)
                                .border(1.dp, getRarityColor(rarity.name), GothicCardShape)
                                .clickable { selectedRarity = rarity }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("rarity_chip_${rarity.name}")
                        ) {
                            Text(
                                text = rarity.displayName.split(" ")[0],
                                color = if (isSelected) GothicDarkBackground else getRarityColor(rarity.name),
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Slot Dropdown Selector (Grid selection buttons)
                Text("Cílový slot těla:", color = GothicTextGold, style = Typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SlotType.values().forEach { slot ->
                        val isSelected = selectedSlot == slot
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) GothicBloodRed else GothicLightSurface)
                                .border(1.dp, if (isSelected) GothicGold else GothicBorderGray, RoundedCornerShape(4.dp))
                                .clickable { selectedSlot = slot }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("slot_chip_${slot.name}")
                        ) {
                            Text(
                                text = slot.displayName,
                                color = if (isSelected) GothicTextSilver else GothicTextMuted,
                                style = Typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedSlot.description,
                    color = GothicTextMuted,
                    style = Typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp
                )
            }
        }

        // Extra attributes (Weight, Stats, Description)
        item {
            GothicPanel(borderColor = GothicBorderGray, title = "POPIS A STATISTIKY") {
                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Popis předmětu (bude šedou kurzívou)", color = GothicTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GothicTextSilver,
                        unfocusedTextColor = GothicTextSilver,
                        focusedBorderColor = GothicGold,
                        unfocusedBorderColor = GothicBorderGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_description_input_field"),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Predefined list of convenient Czech stats buttons to help the user input them instantly!
                Text("Přidat statistiky:", color = GothicTextGold, style = Typography.labelLarge)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // The attribute chips are generated from Ability, so a chip always writes
                    // a name the character sheet actually recognises as a bonus.
                    val quickStatsList = remember {
                        Ability.entries.map { "${it.statName}:+1" } + listOf(
                            "Odolnost vůči zimě:Dobrá",
                            "Odolnost vůči zimě:Skvělá",
                            "Ochrana:+3",
                            "Kapacita:+10",
                            "Štěstí:+15",
                            "Energie:+10"
                        )
                    }
                    quickStatsList.forEach { qStat ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(2.dp))
                                .background(GothicLightSurface)
                                .border(1.dp, GothicBorderGray, RoundedCornerShape(2.dp))
                                .clickable {
                                    val current = stats.trim()
                                    stats = if (current.isEmpty()) {
                                        qStat
                                    } else {
                                        "$current,$qStat"
                                    }
                                }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(qStat, color = GothicTextSilver, fontSize = 9.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Stats Text Field
                OutlinedTextField(
                    value = stats,
                    onValueChange = { stats = it },
                    label = { Text("Statistiky (např: Síla:+5,Odolnost:Skvělá)", color = GothicTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GothicTextSilver,
                        unfocusedTextColor = GothicTextSilver,
                        focusedBorderColor = GothicGold,
                        unfocusedBorderColor = GothicBorderGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_stats_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Weight (Numeric)
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Hmotnost / Váha (kg)", color = GothicTextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GothicTextSilver,
                        unfocusedTextColor = GothicTextSilver,
                        focusedBorderColor = GothicGold,
                        unfocusedBorderColor = GothicBorderGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_weight_input_field"),
                    singleLine = true
                )
            }
        }

        // Consumables & Caps modification
        item {
            GothicPanel(borderColor = GothicBloodRed, title = "MODIFIKÁTORY INVENTÁŘE / SPOTŘEBY") {
                // 1. Checkbox for carrying pockets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hasPockets,
                        onCheckedChange = { hasPockets = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GothicBloodRed,
                            checkmarkColor = GothicTextSilver
                        ),
                        modifier = Modifier.testTag("item_has_pockets_checkbox")
                    )
                    Column {
                        Text("Zvětšuje kapacitu aktivního batohu", color = GothicTextSilver, style = Typography.bodyLarge)
                        Text("Tento kabát nebo batoh obsahuje další kapsy a popruhy.", color = GothicTextMuted, style = Typography.bodyMedium)
                    }
                }
                
                if (hasPockets) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pocketSizeInput,
                        onValueChange = { pocketSizeInput = it },
                        label = { Text("Kapacita kapes (počet přidaných slotů)", color = GothicTextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GothicTextSilver,
                            unfocusedTextColor = GothicTextSilver,
                            focusedBorderColor = GothicGold,
                            unfocusedBorderColor = GothicBorderGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_pocket_size_input_field"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Checkbox for Spotřební (Consumable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isConsumable,
                        onCheckedChange = { isConsumable = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GothicBloodRed,
                            checkmarkColor = GothicTextSilver
                        ),
                        modifier = Modifier.testTag("item_is_consumable_checkbox")
                    )
                    Column {
                        Text("Spotřební předmět (lektvary, jídlo...)", color = GothicTextSilver, style = Typography.bodyLarge)
                        Text("Předmět lze použít omezený početkrát a po spotřebování zmizí.", color = GothicTextMuted, style = Typography.bodyMedium)
                    }
                }

                if (isConsumable) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = chargesInput,
                        onValueChange = { chargesInput = it },
                        label = { Text("Počet použití předmětu", color = GothicTextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GothicTextSilver,
                            unfocusedTextColor = GothicTextSilver,
                            focusedBorderColor = GothicGold,
                            unfocusedBorderColor = GothicBorderGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_charges_input_field"),
                        singleLine = true
                    )
                }
            }
        }

        // Custom Visual / Image generator / Pixelizer
        item {
            GothicPanel(borderColor = GothicGold, title = "GOTICKÁ GRAFIKA & PIXELART") {
                Text(
                    text = "Každý obrázek se převede do autentického pixelartu o velikosti 128x128 pixelů.",
                    color = GothicTextMuted,
                    style = Typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Preview Card
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(GothicCardShape)
                            .background(GothicLightSurface)
                            .border(2.dp, getRarityColor(selectedRarity.name), GothicCardShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedPixelArtBase64 != null) {
                            val bitmap = remember(selectedPixelArtBase64) { Pixelizer.fromBase64(selectedPixelArtBase64!!) }
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Pixelart náhled",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit,
                                    filterQuality = androidx.compose.ui.graphics.FilterQuality.None // blocky pixels! Excellent!
                                )
                            }
                        } else {
                            Text(
                                "BEZ\nIKONY",
                                color = GothicTextMuted,
                                style = Typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Source Actions
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = GothicLightSurface, contentColor = GothicTextGold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .gothicBorder(GothicGold, 1.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Vybrat z Galerie", style = Typography.labelLarge)
                        }

                        if (selectedPixelArtBase64 != null) {
                            Button(
                                onClick = { selectedPixelArtBase64 = null },
                                colors = ButtonDefaults.buttonColors(containerColor = GothicBloodRed),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Odstranit ikonu", color = GothicTextSilver)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Presets templates grid
                Text("Použít rychlou předlohu / šablonu ikon:", color = GothicTextGold, style = Typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    templates.forEach { template ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GothicLightSurface)
                                .border(1.dp, GothicBorderGray, RoundedCornerShape(4.dp))
                                .clickable {
                                    selectedPixelArtBase64 = template.pixelBase64
                                    // Autofill compatible slots for convenience
                                    val matchedSlotType = SlotType.values().find { it.name == template.slotsIntoSlotName }
                                    matchedSlotType?.let { selectedSlot = it }
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(template.name, color = GothicTextSilver, style = Typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // Action Create Item Button
        item {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val weight = weightInput.toWeightOrNull() ?: 0.5
                        val charges = if (isConsumable) (chargesInput.toIntOrNull() ?: 3) else 0
                        val pockets = if (hasPockets) (pocketSizeInput.toIntOrNull() ?: 4) else 0

                        viewModel.createItem(
                            name = name.trim(),
                            slotType = selectedSlot.name,
                            rarity = selectedRarity.name,
                            description = description.trim(),
                            stats = stats.trim(),
                            weight = weight,
                            isConsumable = isConsumable,
                            charges = charges,
                            hasPockets = hasPockets,
                            pocketSize = pockets,
                            pixelArtData = selectedPixelArtBase64
                        )

                        // Clear inputs
                        name = ""
                        description = ""
                        stats = ""
                        weightInput = "0.5"
                        isConsumable = false
                        hasPockets = false
                        selectedPixelArtBase64 = null
                        showSuccessfullyCreatedMessage = true
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GothicBloodRed,
                    disabledContainerColor = GothicLightSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .gothicBorder(GothicGold, 2.dp)
                    .testTag("forge_item_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = GothicTextSilver)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "UKOVAT PŘEDMĚT DO BATOHU",
                    style = Typography.titleMedium,
                    color = if (name.isNotBlank()) GothicTextGold else GothicTextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // --- SUCCESS NOTIFICATION SNACKBAR / DIALOG ---
    if (showSuccessfullyCreatedMessage) {
        AlertDialog(
            onDismissRequest = { showSuccessfullyCreatedMessage = false },
            title = { Text("Předmět vykován!", style = Typography.titleLarge, color = GothicTextGold) },
            text = { Text("Nově stvořený předmět byl bezpečně uložen do tvého hrdinského batohu.", color = GothicTextSilver) },
            confirmButton = {
                Button(
                    onClick = { showSuccessfullyCreatedMessage = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GothicBloodRed)
                ) {
                    Text("Výborně!", color = GothicTextSilver)
                }
            },
            containerColor = GothicDarkSurface,
            shape = GothicCardShape
        )
    }
}

// Data class to guide preset template pixel art icons
data class TemplateIcon(
    val name: String,
    val slotsIntoSlotName: String,
    val pixelBase64: String
)

// --- MEMORY SPRITE ENCODER FUNCTIONS ---
// These generate real grid representations and encode them back to Base64 so we have high-quality pixelart presets!

private fun drawFromMatrix(
    matrix: List<String>,
    colorMap: Map<Char, Int>
): String {
    val bitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
    for (y in 0 until 16) {
        val row = matrix.getOrNull(y) ?: "................"
        for (x in 0 until 16) {
            val char = row.getOrNull(x) ?: '.'
            val color = colorMap[char] ?: android.graphics.Color.TRANSPARENT
            bitmap.setPixel(x, y, color)
        }
    }
    val scaled = Bitmap.createScaledBitmap(bitmap, 128, 128, false)
    return Pixelizer.toBase64(scaled)
}

fun drawCrownData(): String {
    val colors = mapOf(
        'X' to android.graphics.Color.rgb(17, 24, 39),
        'G' to android.graphics.Color.rgb(217, 119, 6),
        'H' to android.graphics.Color.rgb(251, 191, 36),
        'S' to android.graphics.Color.rgb(120, 53, 4),
        'V' to android.graphics.Color.rgb(109, 40, 217),
        'R' to android.graphics.Color.rgb(220, 38, 38),
        'W' to android.graphics.Color.rgb(255, 255, 255)
    )
    val matrix = listOf(
        "................",
        "....X.X..X.X....",
        "....XWX..XWX....",
        "...XHXHXHXHXH...",
        "..XHHHHHHHHHXX..",
        "..XHHVVVVVVHHX..",
        "..XVVVVVVVVVVX..",
        ".XXVVVVVVVVVVXX.",
        "XGGGGGGGGGGGGGGX",
        "XGGGRRGGRRGGRGGX",
        "XGHHGGGGGGGGHHGX",
        "XGSSGSSGSSGSSGGX",
        "XGGGGGGGGGGGGGGX",
        "XGGGGGGGGGGGGGGX",
        ".XXXXXXXXXXXXXX.",
        "................"
    )
    return drawFromMatrix(matrix, colors)
}

fun drawNecklaceData(): String {
    val colors = mapOf(
        'X' to android.graphics.Color.rgb(17, 24, 39),
        'C' to android.graphics.Color.rgb(209, 213, 219),
        'S' to android.graphics.Color.rgb(107, 114, 128),
        'B' to android.graphics.Color.rgb(14, 165, 233),
        'D' to android.graphics.Color.rgb(3, 105, 161),
        'W' to android.graphics.Color.rgb(255, 255, 255),
        'Y' to android.graphics.Color.rgb(245, 158, 11)
    )
    val matrix = listOf(
        "......XXXX......",
        "....XXCCCCXX....",
        "...XCC....CCX...",
        "..XC........CX..",
        ".XC..........CX.",
        ".XC..........CX.",
        "XCC..........CCX",
        "XC............CX",
        "X....XXXXXX....X",
        "....XYYYYYYX....",
        "....XYWBBBYX....",
        "....XYBBDDBYX...",
        ".....XYBBDYX....",
        "......XYDYX.....",
        ".......XYX......",
        "........X......."
    )
    return drawFromMatrix(matrix, colors)
}

fun drawArmorData(): String {
    val colors = mapOf(
        'X' to android.graphics.Color.rgb(17, 24, 39),
        'S' to android.graphics.Color.rgb(228, 228, 231),
        'I' to android.graphics.Color.rgb(161, 161, 170),
        'D' to android.graphics.Color.rgb(82, 82, 91),
        'Y' to android.graphics.Color.rgb(245, 158, 11)
    )
    val matrix = listOf(
        "................",
        "....XX....XX....",
        "...XSSX..XSSX...",
        "..XSSSSXXSSSSX..",
        ".XSSSSSSISSSSX..",
        ".XSSSSIIDISSSX..",
        ".XSSSIIDIDISSX..",
        "XSSSIIDIDIDISSSX",
        "XSSYYIDIDIDYYSSX",
        "XSYYIIDIDIDIIYSX",
        "XSSIIIDIDIDIIISX",
        "XSSIIIDIDIDIIISX",
        "XSSDIDIDIDIDDDSX",
        ".XDIDIDIDIDIDDX.",
        "..XXXXXXXXXXXX..",
        "................"
    )
    return drawFromMatrix(matrix, colors)
}

fun drawBagData(): String {
    val colors = mapOf(
        'X' to android.graphics.Color.rgb(24, 24, 27),
        'L' to android.graphics.Color.rgb(217, 119, 6),
        'B' to android.graphics.Color.rgb(146, 64, 14),
        'D' to android.graphics.Color.rgb(120, 53, 4),
        'Y' to android.graphics.Color.rgb(234, 179, 8),
        'G' to android.graphics.Color.rgb(156, 163, 175)
    )
    val matrix = listOf(
        "................",
        ".....XXXXXX.....",
        "....XLLLLLLX....",
        "...XGGLLLLGGX...",
        "..XGGBBBBBBGGX..",
        "..XLLBBBBBBLLX..",
        ".XLLLBBBBBBLLLX.",
        ".XLLXBBXXBBXLLX.",
        ".XLLXYYXXYYXLLX.",
        ".XLLXBBXXBBXLLX.",
        ".XLLLBBBBBBLLLX.",
        ".XLLDDBBDDDDLDX.",
        "..XDDDDDDDDDDX..",
        "...XDDDDDDDDX...",
        "....XXXXXXXX....",
        "................"
    )
    return drawFromMatrix(matrix, colors)
}

fun drawRingData(): String {
    val colors = mapOf(
        'X' to android.graphics.Color.rgb(17, 24, 39),
        'Y' to android.graphics.Color.rgb(217, 119, 6),
        'H' to android.graphics.Color.rgb(251, 191, 36),
        'S' to android.graphics.Color.rgb(120, 53, 4),
        'E' to android.graphics.Color.rgb(16, 185, 129),
        'W' to android.graphics.Color.rgb(255, 255, 255)
    )
    val matrix = listOf(
        "................",
        "......XXXX......",
        ".....XEWWEX.....",
        "....XEEEEEEX....",
        "....XEEEEEEX....",
        ".....XEEXEX.....",
        "....XYHHHHX.....",
        "...XYHHHHHHSX...",
        "..XYHHXXSSHSYX..",
        "..XYHXX..XXSYX..",
        "..XYHXX..XXSYX..",
        "..XYHXX..XXSYX..",
        "...XYHXXSSSYX...",
        "....XYSSSSYX....",
        ".....XXXXXX.....",
        "................"
    )
    return drawFromMatrix(matrix, colors)
}

fun drawElixirData(): String {
    val colors = mapOf(
        'X' to android.graphics.Color.rgb(24, 24, 27),
        'C' to android.graphics.Color.rgb(146, 64, 14),
        'A' to android.graphics.Color.rgb(236, 254, 255),
        'G' to android.graphics.Color.rgb(161, 161, 170),
        'R' to android.graphics.Color.rgb(153, 27, 27),
        'F' to android.graphics.Color.rgb(239, 68, 68),
        'W' to android.graphics.Color.rgb(255, 255, 255)
    )
    val matrix = listOf(
        "................",
        "......XXXX......",
        "......XCCX......",
        ".....XXGGXX.....",
        ".....XGAAX......",
        ".....XGAAX......",
        "....XXGAAXX.....",
        "...XGGGGGGGGX...",
        "..XGGAAAAAAGGX..",
        ".XGAAAAAAWWWWGX.",
        ".XGFFRRRAAAWFFX.",
        "XGFFFRRRFFRAAAX.",
        "XGFFRRRRFRRRAAAX",
        "XGRRRRRRRRRAAAAX",
        ".XXXXXXXXXXXXXX.",
        "................"
    )
    return drawFromMatrix(matrix, colors)
}

fun drawBootsData(): String {
    val colors = mapOf(
        'X' to android.graphics.Color.rgb(17, 24, 39),
        'P' to android.graphics.Color.rgb(228, 228, 231),
        'I' to android.graphics.Color.rgb(113, 113, 122),
        'D' to android.graphics.Color.rgb(63, 63, 70),
        'B' to android.graphics.Color.rgb(120, 53, 4)
    )
    val matrix = listOf(
        "................",
        ".....XXXXXX.....",
        "....XPPIIIDX....",
        "....XPPIDIDX....",
        ".....XPIIDX.....",
        ".....XPIDDX.....",
        ".....XPIIDX.....",
        ".....XPIIDX.....",
        "....XXPIIDDXX...",
        "...XPPPIIDDDDX..",
        "..XPPPPPIIDDDDX.",
        ".XPPPPPPPIIDDDDX",
        "XPPPPPPPPIIDDDDX",
        "XBBBBBBBBBBBBBBX",
        ".XXXXXXXXXXXXXX.",
        "................"
    )
    return drawFromMatrix(matrix, colors)
}

fun drawSwordData(): String {
    val colors = mapOf(
        'X' to android.graphics.Color.rgb(17, 24, 39),
        'S' to android.graphics.Color.rgb(244, 244, 245),
        'I' to android.graphics.Color.rgb(161, 161, 170),
        'H' to android.graphics.Color.rgb(82, 82, 91),
        'Y' to android.graphics.Color.rgb(217, 119, 6),
        'P' to android.graphics.Color.rgb(251, 191, 36),
        'R' to android.graphics.Color.rgb(153, 27, 27)
    )
    val matrix = listOf(
        "..............SX",
        ".............SIX",
        "............SIHX",
        "...........SIHX.",
        "..........SIHX..",
        ".........SIHX...",
        "........SIHX....",
        ".......SIHX.....",
        "......SIHX......",
        ".....SIHX.......",
        "....XYYX........",
        "...YRRRY........",
        "..XYRRY.........",
        ".XYYXX..........",
        "XPPX............",
        "XX.............."
    )
    return drawFromMatrix(matrix, colors)
}
