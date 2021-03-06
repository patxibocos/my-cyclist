package io.github.patxibocos.mycyclist.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.patxibocos.mycyclist.R
import io.github.patxibocos.mycyclist.ui.races.RaceRoute
import io.github.patxibocos.mycyclist.ui.races.RacesRoute
import io.github.patxibocos.mycyclist.ui.riders.RiderRoute
import io.github.patxibocos.mycyclist.ui.riders.RidersRoute
import io.github.patxibocos.mycyclist.ui.stages.StageRoute
import io.github.patxibocos.mycyclist.ui.teams.TeamRoute
import io.github.patxibocos.mycyclist.ui.teams.TeamsRoute

@Composable
internal fun AppNavigation(
    navController: NavHostController,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit
) {
    NavHost(
        modifier = Modifier.systemBarsPadding(),
        navController = navController,
        startDestination = Screen.Riders.route
    ) {
        addTeamsNavigation(navController, reselectedScreen, onReselectedScreenConsumed)
        addRidersNavigation(navController, reselectedScreen, onReselectedScreenConsumed)
        addRacesNavigation(navController, reselectedScreen, onReselectedScreenConsumed)
    }
}

internal sealed class Screen(
    val route: String,
    @StringRes val label: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Teams : Screen("teams", R.string.screen_teams, Icons.Outlined.Group, Icons.Filled.Group)
    object Riders : Screen("riders", R.string.screen_riders, Icons.Outlined.Face, Icons.Filled.Face)
    object Races : Screen("races", R.string.screen_races, Icons.Outlined.Flag, Icons.Filled.Flag)
}

internal sealed class LeafScreen(
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

    object Stage : LeafScreen("race/{raceId}/stage/{stageId}") {
        fun createRoute(root: Screen, raceId: String, stageId: String): String {
            return "${root.route}/race/$raceId/stage/$stageId"
        }
    }
}

private fun NavGraphBuilder.addTeamsNavigation(
    navController: NavController,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit
) {
    navigation(
        startDestination = LeafScreen.Teams.createRoute(Screen.Teams),
        route = Screen.Teams.route
    ) {
        composable(LeafScreen.Teams.createRoute(Screen.Teams)) {
            TeamsRoute(
                onTeamSelected = {
                    navController.navigate(LeafScreen.Team.createRoute(Screen.Teams, it.id))
                },
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed
            )
        }
        composable(LeafScreen.Team.createRoute(Screen.Teams)) {
            TeamRoute(
                onRiderSelected = { rider ->
                    navController.navigate(
                        LeafScreen.Rider.createRoute(
                            Screen.Riders,
                            rider.id
                        )
                    )
                },
                onBackPressed = { navController.navigateUp() }
            )
        }
    }
}

private fun NavGraphBuilder.addRidersNavigation(
    navController: NavController,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit
) {
    navigation(
        startDestination = LeafScreen.Riders.createRoute(Screen.Riders),
        route = Screen.Riders.route
    ) {
        composable(LeafScreen.Riders.createRoute(Screen.Riders)) {
            RidersRoute(
                onRiderSelected = {
                    navController.navigate(LeafScreen.Rider.createRoute(Screen.Riders, it.id))
                },
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed
            )
        }
        composable(LeafScreen.Rider.createRoute(Screen.Riders)) {
            RiderRoute(
                onTeamSelected = { team ->
                    navController.navigate(
                        LeafScreen.Team.createRoute(
                            Screen.Teams,
                            team.id
                        )
                    )
                },
                onRaceSelected = { race ->
                    navController.navigate(
                        LeafScreen.Race.createRoute(
                            Screen.Races,
                            race.id
                        )
                    )
                },
                onStageSelected = { race, stage ->
                    navController.navigate(
                        LeafScreen.Stage.createRoute(
                            Screen.Races,
                            race.id,
                            stage.id
                        )
                    )
                },
                onBackPressed = { navController.navigateUp() }
            )
        }
    }
}

private fun NavGraphBuilder.addRacesNavigation(
    navController: NavController,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit
) {
    navigation(
        startDestination = LeafScreen.Races.createRoute(Screen.Races),
        route = Screen.Races.route
    ) {
        composable(LeafScreen.Races.createRoute(Screen.Races)) {
            RacesRoute(
                onRaceSelected = {
                    navController.navigate(LeafScreen.Race.createRoute(Screen.Races, it.id))
                },
                onStageSelected = { race, stage ->
                    navController.navigate(
                        LeafScreen.Stage.createRoute(
                            Screen.Races,
                            race.id,
                            stage.id
                        )
                    )
                },
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed
            )
        }
        composable(LeafScreen.Race.createRoute(Screen.Races)) {
            RaceRoute(
                onStageSelected = { race, stage ->
                    navController.navigate(
                        LeafScreen.Stage.createRoute(
                            Screen.Races,
                            race.id,
                            stage.id
                        )
                    )
                },
                onBackPressed = { navController.navigateUp() }
            )
        }
        composable(LeafScreen.Stage.createRoute(Screen.Races)) {
            StageRoute(
                onBackPressed = { navController.navigateUp() },
                onRiderSelected = { rider ->
                    navController.navigate(
                        LeafScreen.Rider.createRoute(
                            Screen.Riders,
                            rider.id
                        )
                    )
                }
            )
        }
    }
}
