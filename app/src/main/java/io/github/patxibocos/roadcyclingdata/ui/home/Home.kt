package io.github.patxibocos.roadcyclingdata.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController

@Composable
fun Home() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) {
        AppNavigation(navController = navController)
    }
}

@Composable
fun BottomBar(navController: NavController) {
    val currentScreen by navController.currentScreenAsState()
    val screenReselected: MutableState<Screen> = remember { mutableStateOf(Screen.Teams) }
    AnimatedVisibility(
        visible = currentScreen != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        BottomNavigation {
            val screens = listOf(Screen.Teams, Screen.Riders, Screen.Races)
            screens.forEach { screen ->
                BottomNavigationItem(
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                    selected = currentScreen == screen,
                    onClick = {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                        screenReselected.value = screen
                    },
                    alwaysShowLabel = true,
                )
            }
        }
    }
}

@Stable
@Composable
private fun NavController.currentScreenAsState(): State<Screen?> {
    val selectedItem = remember { mutableStateOf<Screen?>(Screen.Teams) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem.value = when (destination.route) {
                LeafScreen.Teams.createRoute(Screen.Teams) -> Screen.Teams
                LeafScreen.Riders.createRoute(Screen.Riders) -> Screen.Riders
                LeafScreen.Races.createRoute(Screen.Races) -> Screen.Races
                else -> null
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}
