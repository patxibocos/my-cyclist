package io.github.patxibocos.roadcyclingdata.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController

@Composable
fun Home() {
    val navController = rememberNavController()
    val currentScreen by navController.currentScreenAsState()
    val screenReselected: MutableState<Screen> = remember { mutableStateOf(Screen.Teams) }
    Scaffold(
        bottomBar = {
            val offset by animateDpAsState(
                if (currentScreen != null) 0.dp else 56.dp,
                animationSpec = tween(200, 0, LinearEasing)
            )
            BottomNavigation(modifier = Modifier.offset(y = offset)) {
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
