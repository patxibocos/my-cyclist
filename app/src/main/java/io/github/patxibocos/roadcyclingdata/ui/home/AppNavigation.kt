package io.github.patxibocos.roadcyclingdata.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.patxibocos.roadcyclingdata.ui.races.RaceScreen
import io.github.patxibocos.roadcyclingdata.ui.races.RacesScreen
import io.github.patxibocos.roadcyclingdata.ui.riders.RiderScreen
import io.github.patxibocos.roadcyclingdata.ui.riders.RidersScreen
import io.github.patxibocos.roadcyclingdata.ui.teams.TeamScreen
import io.github.patxibocos.roadcyclingdata.ui.teams.TeamsScreen

internal sealed class Screen(val route: String, val icon: ImageVector) {
    object Teams : Screen("teams", Icons.Outlined.Group)
    object Riders : Screen("riders", Icons.Outlined.Face)
    object Races : Screen("races", Icons.Outlined.Flag)
}

private sealed class LeafScreen(
    private val route: String
) {
    fun createRoute(root: Screen) = "${root.route}/$route"

    object Teams : LeafScreen("teams")
    object Riders : LeafScreen("riders")
    object Races : LeafScreen("races")

    object Team : LeafScreen("team/{teamId}") {
        fun createRoute(root: Screen, teamId: String): String {
            return "${root.route}/team/$teamId"
        }
    }

    object Rider : LeafScreen("rider/{riderId}") {
        fun createRoute(root: Screen, riderId: String): String {
            return "${root.route}/rider/$riderId"
        }
    }

    object Race : LeafScreen("race/{raceId}") {
        fun createRoute(root: Screen, raceId: String): String {
            return "${root.route}/race/$raceId"
        }
    }
}

@Composable
internal fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController,
        startDestination = Screen.Teams.route,
        modifier = modifier,
    ) {
        navigation(
            startDestination = LeafScreen.Teams.createRoute(Screen.Teams),
            route = Screen.Teams.route
        ) {
            composable(LeafScreen.Teams.createRoute(Screen.Teams)) {
                TeamsScreen(onTeamSelected = {
                    navController.navigate(LeafScreen.Team.createRoute(Screen.Teams, it.id))
                })
            }
            composable(LeafScreen.Team.createRoute(Screen.Teams)) {
                val teamId = it.arguments?.getString("teamId")
                if (teamId != null) {
                    TeamScreen(teamId)
                }
            }
        }
        navigation(
            startDestination = LeafScreen.Riders.createRoute(Screen.Riders),
            route = Screen.Riders.route
        ) {
            composable(LeafScreen.Riders.createRoute(Screen.Riders)) {
                RidersScreen(onRiderSelected = {
                    navController.navigate(LeafScreen.Rider.createRoute(Screen.Riders, it.id))
                })
            }
            composable(LeafScreen.Rider.createRoute(Screen.Riders)) {
                val riderId = it.arguments?.getString("riderId")
                if (riderId != null) {
                    RiderScreen(riderId)
                }
            }
        }
        navigation(
            startDestination = LeafScreen.Races.createRoute(Screen.Races),
            route = Screen.Races.route
        ) {
            composable(LeafScreen.Races.createRoute(Screen.Races)) {
                RacesScreen(onRaceSelected = {
                    navController.navigate(LeafScreen.Race.createRoute(Screen.Races, it.id))
                })
            }
            composable(LeafScreen.Race.createRoute(Screen.Races)) {
                val raceId = it.arguments?.getString("raceId")
                if (raceId != null) {
                    RaceScreen(raceId)
                }
            }
        }
    }
}
