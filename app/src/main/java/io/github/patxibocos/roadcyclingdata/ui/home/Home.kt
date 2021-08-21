package io.github.patxibocos.roadcyclingdata.ui.home

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController

@Composable
fun Home() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val currentSelectedItem by navController.currentScreenAsState()
            BottomNavigation {
                val screens = listOf(Screen.Teams, Screen.Riders, Screen.Races)
                screens.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                        selected = currentSelectedItem == screen,
                        onClick = {
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        },
                        alwaysShowLabel = false,
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier
                .fillMaxHeight()
                .padding(innerPadding)
        )
    }
}

@Stable
@Composable
private fun NavController.currentScreenAsState(): State<Screen> {
    val selectedItem = remember { mutableStateOf<Screen>(Screen.Teams) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == Screen.Teams.route } -> {
                    selectedItem.value = Screen.Teams
                }
                destination.hierarchy.any { it.route == Screen.Riders.route } -> {
                    selectedItem.value = Screen.Riders
                }
                destination.hierarchy.any { it.route == Screen.Races.route } -> {
                    selectedItem.value = Screen.Races
                }
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}
