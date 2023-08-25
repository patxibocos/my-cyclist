package io.github.patxibocos.mycyclist.ui.home

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Home() {
    val navController by rememberUpdatedState(rememberNavController())
    val reselectedScreen: MutableState<Screen?> = remember { mutableStateOf(null) }
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            BottomBar(navController) { screen ->
                reselectedScreen.value = screen
            }
        },
    ) {
        AppNavigation(
            navController = navController,
            reselectedScreen = reselectedScreen,
            onReselectedScreenConsumed = {
                reselectedScreen.value = null
            },
            modifier = Modifier
                .consumeWindowInsets(it)
                .padding(it),
        )
    }
}

@Composable
private fun BottomBar(
    navController: NavController,
    screenReselected: (Screen) -> Unit,
) {
    val currentScreen by navController.currentScreenAsState()
    Surface(
        color = Color.Transparent,
    ) {
        NavigationBar(
            tonalElevation = 2.dp,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            val screens = remember { listOf(Screen.Races, Screen.Riders, Screen.Teams) }
            screens.forEach { screen ->
                val selected = currentScreen == screen
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selected) {
                                screen.selectedIcon
                            } else {
                                screen.unselectedIcon
                            },
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(screen.label)) },
                    selected = currentScreen == screen,
                    onClick = {
                        if (selected) {
                            screenReselected(screen)
                        }
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = false
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = false
                            }
                        }
                    },
                )
            }
        }
    }
}

@Stable
@Composable
private fun NavController.currentScreenAsState(): State<Screen?> {
    val selectedItem = remember { mutableStateOf<Screen?>(Screen.Teams) }

    fun findRootDestinationRoute(destination: NavDestination): String? {
        return destination.parent?.let { parent ->
            findRootDestinationRoute(parent)
        } ?: destination.route
    }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem.value = when (findRootDestinationRoute(destination)) {
                Screen.Teams.route -> Screen.Teams
                Screen.Riders.route -> Screen.Riders
                Screen.Races.route -> Screen.Races
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
