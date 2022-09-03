package io.github.patxibocos.mycyclist.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
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
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .nestedScroll(nestedScrollConnection),
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
    screenReselected: (Screen) -> Unit
) {
    val currentScreen by navController.currentScreenAsState(onNavigatedToRootScreen = {
        showBottomBar.value = true
    })
    AnimatedVisibility(
        visible = showBottomBar.value && currentScreen != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Surface(
            modifier = Modifier.navigationBarsPadding(),
            color = Color.Transparent
        ) {
            NavigationBar(
                tonalElevation = 2.dp,
                containerColor = MaterialTheme.colorScheme.surface
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
                                contentDescription = null
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
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        }
                    )
                }
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
