package io.github.patxibocos.roadcyclingdata.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import io.github.patxibocos.roadcyclingdata.ui.races.RacesScreen
import io.github.patxibocos.roadcyclingdata.ui.riders.RidersScreen
import io.github.patxibocos.roadcyclingdata.ui.teams.TeamsScreen

internal sealed class Screen(val route: String) {
    object Teams : Screen("teams")
    object Riders : Screen("riders")
    object Races : Screen("races")
}

private const val HOME_ROUTE = "home"

@Composable
fun Home() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            BottomNavigation {
                val screens = listOf(Screen.Teams, Screen.Riders, Screen.Races)
                screens.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Face, contentDescription = null) },
                        label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        alwaysShowLabel = false,
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = HOME_ROUTE,
            Modifier.padding(innerPadding)
        ) {
            navigation(
                route = HOME_ROUTE,
                startDestination = Screen.Teams.route
            ) {
                composable(Screen.Teams.route) { TeamsScreen() }
                composable(Screen.Riders.route) { RidersScreen() }
                composable(Screen.Races.route) { RacesScreen() }
            }
        }
    }
}
