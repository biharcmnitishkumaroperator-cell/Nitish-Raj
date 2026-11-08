package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SmartButton
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AIModelsScreen
import com.example.ui.screens.ActionDeckScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.OrbCustomizationScreen
import com.example.ui.screens.VoiceAssistantScreen
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class NavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    VOICE("voice", "MYRA", Icons.Filled.Mic, Icons.Outlined.Mic),
    ACTIONS("actions", "Actions", Icons.Filled.SmartButton, Icons.Outlined.SmartButton),
    MODELS("models", "Models", Icons.Filled.Psychology, Icons.Outlined.Psychology),
    STYLE("style", "Style Orb", Icons.Filled.ColorLens, Icons.Outlined.ColorLens),
    MEMORY("memory", "Memory", Icons.Filled.Tune, Icons.Outlined.Tune)
}

@Composable
fun MainScreen(viewModel: MyraViewModel) {
    var currentTab by rememberSaveable { mutableStateOf(NavDestination.VOICE) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepSpace,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavDestination.values().forEach { destination ->
                    val isSelected = currentTab == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = destination },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.label
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepSpace,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = NeonCyan
                        ),
                        modifier = Modifier.testTag("nav_item_${destination.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DeepSpace)
        ) {
            Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    NavDestination.VOICE -> VoiceAssistantScreen(
                        viewModel = viewModel,
                        onNavigateToModels = { currentTab = NavDestination.MODELS },
                        onNavigateToAccount = { currentTab = NavDestination.MEMORY }
                    )
                    NavDestination.ACTIONS -> ActionDeckScreen(viewModel = viewModel)
                    NavDestination.MODELS -> AIModelsScreen(viewModel = viewModel)
                    NavDestination.STYLE -> OrbCustomizationScreen(viewModel = viewModel)
                    NavDestination.MEMORY -> MemoryScreen(viewModel = viewModel)
                }
            }
        }
    }
}
