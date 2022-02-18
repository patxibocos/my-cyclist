package io.github.patxibocos.roadcyclingdata.ui.home

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController

@Composable
fun Home() {
    val showBottomBar = remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override suspend fun onPreFling(available: Velocity): Velocity {
                if (available.y > 0) {
                    showBottomBar.value = true
                } else if (available.y < 0) {
                    showBottomBar.value = false
                }
                return super.onPreFling(available)
            }
        }
    }
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        bottomBar = {
            BottomBar(navController, showBottomBar.value)
        }
    ) {
        BackHandler {
            navController.popBackStack()
            showBottomBar.value = true
        }
        AppNavigation(navController = navController)
    }
}

@Composable
fun BottomBar(navController: NavController, showBottomBar: Boolean) {
    val currentScreen by navController.currentScreenAsState()
    val screenReselected: MutableState<Screen> = remember { mutableStateOf(Screen.Teams) }
    AnimatedVisibility(
        visible = showBottomBar && currentScreen != null,
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
