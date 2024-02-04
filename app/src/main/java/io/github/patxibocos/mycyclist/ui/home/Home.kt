package io.github.patxibocos.mycyclist.ui.home

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

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
                .padding(it)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun BottomBar(
    navController: NavController,
    screenReselected: (Screen) -> Unit,
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val screens = remember { listOf(Screen.Races, Screen.Riders, Screen.Teams) }
        screens.forEach { screen ->
            val selected =
                currentDestination?.hierarchy?.any { it.route == screen.route } == true
            val currentRoute = currentDestination?.route
            val topScreenRoute = topScreenToLeafScreenRoute(screen)
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
                selected = selected,
                onClick = {
                    if (currentRoute == topScreenRoute) {
                        screenReselected(screen)
                    } else {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.findStartDestination().id)
                        }
                    }
                },
            )
        }
    }
}

private fun topScreenToLeafScreenRoute(screen: Screen): String {
    return when (screen) {
        Screen.Races -> LeafScreen.Races.createRoute(Screen.Races)
        Screen.Riders -> LeafScreen.Riders.createRoute(Screen.Riders)
        Screen.Teams -> LeafScreen.Teams.createRoute(Screen.Teams)
    }
}
