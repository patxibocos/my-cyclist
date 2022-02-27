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
import androidx.compose.runtime.rememberUpdatedState
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
    val navController by rememberUpdatedState(newValue = rememberNavController())
    val reselectedScreen: MutableState<Screen?> = remember { mutableStateOf(null) }
    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        bottomBar = {
            BottomBar(navController, showBottomBar) { screen ->
                reselectedScreen.value = screen
            }
        }
    ) {
        AppNavigation(
            navController = navController,
            reselectedScreen = reselectedScreen,
            onReselectedScreenConsumed = {
                reselectedScreen.value = null
            }
        )
    }
}

@Composable
private fun BottomBar(
    navController: NavController,
    showBottomBar: MutableState<Boolean>,
    screenReselected: (Screen) -> Unit,
) {
    val currentScreen by navController.currentScreenAsState(onNavigatedToRootScreen = {
        showBottomBar.value = true
    })
    AnimatedVisibility(
        visible = showBottomBar.value && currentScreen != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        BottomNavigation {
            val screens = remember { listOf(Screen.Teams, Screen.Riders, Screen.Races) }
            screens.forEach { screen ->
                BottomNavigationItem(
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                    selected = currentScreen == screen,
                    onClick = {
                        if (screen == currentScreen) {
                            screenReselected(screen)
                        }
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    },
                    alwaysShowLabel = true,
                )
            }
        }
    }
}

@Stable
@Composable
private fun NavController.currentScreenAsState(onNavigatedToRootScreen: () -> Unit): State<Screen?> {
    val selectedItem = remember { mutableStateOf<Screen?>(Screen.Teams) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem.value = when (destination.route) {
                LeafScreen.Teams.createRoute(Screen.Teams) -> Screen.Teams
                LeafScreen.Riders.createRoute(Screen.Riders) -> Screen.Riders
                LeafScreen.Races.createRoute(Screen.Races) -> Screen.Races
                else -> null
            }
            if (selectedItem.value != null) {
                onNavigatedToRootScreen()
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}
