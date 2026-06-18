package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.GothicCardShape
import com.example.ui.theme.*
import com.example.ui.viewmodel.RPGViewModel
import com.example.utils.Pixelizer

@Composable
fun BackpackScreen(viewModel: RPGViewModel) {
    val backpackItems by viewModel.backpackItems.collectAsState()
    val activeCapacity by viewModel.activeInventoryCapacity.collectAsState()
    val activeWeight by viewModel.activeInventoryWeight.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GothicDarkBackground) // Same dark indigo as others
            .padding(16.dp)
    ) {
        // --- Header Section ---
        Text(
            text = "Batoh",
            style = Typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = GothicTextGold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = GothicDarkSurface),
            shape = GothicCardShape,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Kapacita Vybavení: $activeCapacity",
                    style = Typography.bodyMedium,
                    color = GothicTextSilver
                )
                Text(
                    text = "Celková Váha Věcí v Batohu: $activeWeight",
                    style = Typography.bodyMedium,
                    color = GothicTextSilver
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = GothicGold.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // --- Backpack Items List ---
        if (backpackItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Batoh je prázdný.\nZískej a vyrob nějaké předměty v Kovárně.",
                    style = Typography.bodyLarge,
                    color = GothicTextMuted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp) // space for bottom nav
            ) {
                items(backpackItems) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GothicDarkSurface),
                        shape = GothicCardShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pixel Art
                            if (item.pixelArtData != null) {
                                val bitmap = remember(item.pixelArtData) { Pixelizer.fromBase64(item.pixelArtData) }
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = item.name,
                                        modifier = Modifier.size(64.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                            }

                            // Info
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GothicTextGold
                                )
                                Text(
                                    text = item.description,
                                    style = Typography.bodySmall,
                                    color = GothicTextMuted
                                )
                                Text(
                                    text = "Váha: ${item.weight}",
                                    style = Typography.labelSmall,
                                    color = GothicTextSilver
                                )
                            }
                            
                            // Delete button
                            IconButton(onClick = { viewModel.deleteItem(item) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Zahodit",
                                    tint = GothicBloodRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
