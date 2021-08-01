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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.patxibocos.roadcyclingdata.ui.riders.RidersScreen
import io.github.patxibocos.roadcyclingdata.ui.teams.TeamsScreen

internal sealed class Screen(val route: String) {
    object Teams : Screen("teams")
    object Riders : Screen("riders")
}

@Composable
fun Home() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(Screen.Teams, Screen.Riders)
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Face, contentDescription = null) },
                        label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Teams.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Teams.route) { TeamsScreen() }
            composable(Screen.Riders.route) { RidersScreen() }
        }
    }
}
