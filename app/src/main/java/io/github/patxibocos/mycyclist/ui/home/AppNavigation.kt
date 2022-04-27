package io.github.patxibocos.mycyclist.ui.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.patxibocos.mycyclist.R
import io.github.patxibocos.mycyclist.ui.races.RaceScreen
import io.github.patxibocos.mycyclist.ui.races.RaceViewModel
import io.github.patxibocos.mycyclist.ui.races.RacesScreen
import io.github.patxibocos.mycyclist.ui.races.RacesViewModel
import io.github.patxibocos.mycyclist.ui.riders.RiderScreen
import io.github.patxibocos.mycyclist.ui.riders.RiderViewModel
import io.github.patxibocos.mycyclist.ui.riders.RidersScreen
import io.github.patxibocos.mycyclist.ui.riders.RidersViewModel
import io.github.patxibocos.mycyclist.ui.riders.UiState
import io.github.patxibocos.mycyclist.ui.stages.StageScreen
import io.github.patxibocos.mycyclist.ui.stages.StageViewModel
import io.github.patxibocos.mycyclist.ui.teams.TeamScreen
import io.github.patxibocos.mycyclist.ui.teams.TeamViewModel
import io.github.patxibocos.mycyclist.ui.teams.TeamsScreen
import io.github.patxibocos.mycyclist.ui.teams.TeamsViewModel
import io.github.patxibocos.mycyclist.ui.util.rememberFlowWithLifecycle

@Composable
internal fun AppNavigation(
    modifier: Modifier,
    navController: NavHostController,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit,
) {
    NavHost(
        navController,
        startDestination = Screen.Riders.route,
        modifier = modifier,
    ) {
        addTeamsNavigation(navController, reselectedScreen, onReselectedScreenConsumed)
        addRidersNavigation(navController, reselectedScreen, onReselectedScreenConsumed)
        addRacesNavigation(navController, reselectedScreen, onReselectedScreenConsumed)
    }
}

internal sealed class Screen(val route: String, @StringRes val label: Int, val icon: ImageVector) {
    object Teams : Screen("teams", R.string.screen_teams, Icons.Outlined.Group)
    object Riders : Screen("riders", R.string.screen_riders, Icons.Outlined.Face)
    object Races : Screen("races", R.string.screen_races, Icons.Outlined.Flag)
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
    onReselectedScreenConsumed: () -> Unit,
) {
    navigation(
        startDestination = LeafScreen.Teams.createRoute(Screen.Teams),
        route = Screen.Teams.route
    ) {
        composable(LeafScreen.Teams.createRoute(Screen.Teams)) {
            val viewModel = hiltViewModel<TeamsViewModel>()
            val teams by viewModel.teams.rememberFlowWithLifecycle(
                viewModel.viewModelScope,
                emptyList()
            )
            TeamsScreen(
                teams = teams,
                onTeamSelected = {
                    navController.navigate(LeafScreen.Team.createRoute(Screen.Teams, it.id))
                },
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed,
            )
        }
        composable(LeafScreen.Team.createRoute(Screen.Teams)) {
            it.arguments?.getString("teamId")?.let { teamId ->
                val viewModel = hiltViewModel<TeamViewModel>()
                LaunchedEffect(key1 = teamId) {
                    viewModel.loadTeam(teamId)
                }
                val teamOfRiders by viewModel.teamOfRiders.rememberFlowWithLifecycle(
                    viewModel.viewModelScope,
                    null
                )
                teamOfRiders?.let {
                    TeamScreen(
                        teamOfRiders = it,
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
    }
}

private fun NavGraphBuilder.addRidersNavigation(
    navController: NavController,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit,
) {
    navigation(
        startDestination = LeafScreen.Riders.createRoute(Screen.Riders),
        route = Screen.Riders.route
    ) {
        composable(LeafScreen.Riders.createRoute(Screen.Riders)) {
            val viewModel = hiltViewModel<RidersViewModel>()
            val uiState by viewModel.state.rememberFlowWithLifecycle(
                viewModel.viewModelScope,
                UiState.Empty
            )
            RidersScreen(
                uiRiders = uiState.riders,
                searchQuery = uiState.search,
                onRiderSearched = viewModel::onSearched,
                onRiderSelected = {
                    navController.navigate(LeafScreen.Rider.createRoute(Screen.Riders, it.id))
                },
                onSortingSelected = viewModel::onSorted,
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed,
            )
        }
        composable(LeafScreen.Rider.createRoute(Screen.Riders)) {
            it.arguments?.getString("riderId")?.let { riderId ->
                val viewModel = hiltViewModel<RiderViewModel>()
                LaunchedEffect(key1 = riderId) {
                    viewModel.loadRider(riderId)
                }
                val riderOfTeam by viewModel.riderOfTeam.rememberFlowWithLifecycle(
                    viewModel.viewModelScope,
                    null
                )
                riderOfTeam?.let {
                    RiderScreen(
                        riderOfTeam = it,
                        onTeamSelected = { team ->
                            navController.navigate(
                                LeafScreen.Team.createRoute(
                                    Screen.Teams,
                                    team.id
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun NavGraphBuilder.addRacesNavigation(
    navController: NavController,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit,
) {
    navigation(
        startDestination = LeafScreen.Races.createRoute(Screen.Races),
        route = Screen.Races.route
    ) {
        composable(LeafScreen.Races.createRoute(Screen.Races)) {
            val viewModel = hiltViewModel<RacesViewModel>()
            val races by viewModel.races.rememberFlowWithLifecycle(
                viewModel.viewModelScope,
                emptyList()
            )
            RacesScreen(
                races = races,
                onRaceSelected = {
                    navController.navigate(LeafScreen.Race.createRoute(Screen.Races, it.id))
                },
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed,
            )
        }
        composable(LeafScreen.Race.createRoute(Screen.Races)) {
            it.arguments?.getString("raceId")?.let { raceId ->
                val viewModel = hiltViewModel<RaceViewModel>()
                LaunchedEffect(key1 = raceId) {
                    viewModel.loadRace(raceId)
                }
                val race by viewModel.race.rememberFlowWithLifecycle(
                    viewModel.viewModelScope,
                    null
                )
                race?.let {
                    RaceScreen(
                        race = it,
                        onStageSelected = { stage ->
                            navController.navigate(
                                LeafScreen.Stage.createRoute(
                                    Screen.Races,
                                    raceId,
                                    stageId = stage.id,
                                )
                            )
                        }
                    )
                }
            }
        }
        composable(LeafScreen.Stage.createRoute(Screen.Races)) {
            val raceId = it.arguments?.getString("raceId")
            val stageId = it.arguments?.getString("stageId")
            if (raceId != null && stageId != null) {
                val viewModel = hiltViewModel<StageViewModel>()
                LaunchedEffect(key1 = raceId, key2 = stageId) {
                    viewModel.loadStage(raceId, stageId)
                }
                val stage by viewModel.stage.rememberFlowWithLifecycle(
                    viewModel.viewModelScope,
                    null
                )
                stage?.let {
                    StageScreen(it)
                }
            }
        }
    }
}
