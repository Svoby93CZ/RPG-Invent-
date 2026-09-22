package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import com.example.ui.viewmodel.RPGViewModel

@Composable
fun MainAppContainer(
    viewModel: RPGViewModel,
    modifier: Modifier = Modifier
) {
    // rememberSaveable so a rotation or a trip through the background does not
    // dump the user back on the first tab.
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(GothicDarkBackground),
        bottomBar = {
            // Gothic Golden and Charcoal styled NavigationBar
            NavigationBar(
                containerColor = GothicDarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars) // safe navigation gestural area padding
                    .border(width = 1.dp, color = GothicGold.copy(alpha = 0.5f))
                    .testTag("gothic_bottom_nav_bar")
            ) {
                // Tab 1: Character (Hrdina / Postava)
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Hrdina",
                            tint = if (selectedTab == 0) GothicGold else GothicTextMuted
                        )
                    },
                    label = {
                        Text(
                            text = "Hrdina",
                            color = if (selectedTab == 0) GothicTextGold else GothicTextMuted,
                            style = Typography.labelLarge
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = GothicLightSurface,
                        selectedIconColor = GothicGold,
                        unselectedIconColor = GothicTextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_character")
                )

                // Tab 2: Backpack (Batoh / Inventář)
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Batoh",
                            tint = if (selectedTab == 1) GothicGold else GothicTextMuted
                        )
                    },
                    label = {
                        Text(
                            text = "Batoh",
                            color = if (selectedTab == 1) GothicTextGold else GothicTextMuted,
                            style = Typography.labelLarge
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = GothicLightSurface,
                        selectedIconColor = GothicGold,
                        unselectedIconColor = GothicTextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_backpack")
                )

                // Tab 3: Forge (Tvorba / Kovárna)
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Kovárna",
                            tint = if (selectedTab == 2) GothicGold else GothicTextMuted
                        )
                    },
                    label = {
                        Text(
                            text = "Kovárna",
                            color = if (selectedTab == 2) GothicTextGold else GothicTextMuted,
                            style = Typography.labelLarge
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = GothicLightSurface,
                        selectedIconColor = GothicGold,
                        unselectedIconColor = GothicTextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_create")
                )

                // Tab 4: Places (Místa / Archiv)
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Místa",
                            tint = if (selectedTab == 3) GothicGold else GothicTextMuted
                        )
                    },
                    label = {
                        Text(
                            text = "Místa",
                            color = if (selectedTab == 3) GothicTextGold else GothicTextMuted,
                            style = Typography.labelLarge
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = GothicLightSurface,
                        selectedIconColor = GothicGold,
                        unselectedIconColor = GothicTextMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_locations")
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing // respects notches automatically
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> CharacterScreen(viewModel = viewModel)
                1 -> BackpackScreen(viewModel = viewModel)
                2 -> ItemCreationScreen(viewModel = viewModel)
                3 -> LocationsScreen(viewModel = viewModel)
            }
        }
    }
}
