package io.github.patxibocos.mycyclist.ui.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
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
import io.github.patxibocos.mycyclist.ui.races.RaceViewState
import io.github.patxibocos.mycyclist.ui.races.RacesScreen
import io.github.patxibocos.mycyclist.ui.races.RacesViewModel
import io.github.patxibocos.mycyclist.ui.races.RacesViewState
import io.github.patxibocos.mycyclist.ui.riders.RiderScreen
import io.github.patxibocos.mycyclist.ui.riders.RiderViewModel
import io.github.patxibocos.mycyclist.ui.riders.RiderViewState
import io.github.patxibocos.mycyclist.ui.riders.RidersScreen
import io.github.patxibocos.mycyclist.ui.riders.RidersViewModel
import io.github.patxibocos.mycyclist.ui.riders.RidersViewState
import io.github.patxibocos.mycyclist.ui.stages.StageScreen
import io.github.patxibocos.mycyclist.ui.stages.StageViewModel
import io.github.patxibocos.mycyclist.ui.stages.StageViewState
import io.github.patxibocos.mycyclist.ui.teams.TeamScreen
import io.github.patxibocos.mycyclist.ui.teams.TeamViewModel
import io.github.patxibocos.mycyclist.ui.teams.TeamViewState
import io.github.patxibocos.mycyclist.ui.teams.TeamsScreen
import io.github.patxibocos.mycyclist.ui.teams.TeamsViewModel
import io.github.patxibocos.mycyclist.ui.teams.TeamsViewState
import io.github.patxibocos.mycyclist.ui.util.rememberFlowWithLifecycle

@Composable
internal fun AppNavigation(
    navController: NavHostController,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Riders.route,
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
    val selectedIcon: ImageVector,
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
    onReselectedScreenConsumed: () -> Unit,
) {
    navigation(
        startDestination = LeafScreen.Teams.createRoute(Screen.Teams),
        route = Screen.Teams.route
    ) {
        composable(LeafScreen.Teams.createRoute(Screen.Teams)) {
            val viewModel = hiltViewModel<TeamsViewModel>()
            val teamsViewState by viewModel.teamsViewState.rememberFlowWithLifecycle(
                viewModel.viewModelScope,
                TeamsViewState.Empty
            )
            TeamsScreen(
                teamsViewState = teamsViewState,
                onTeamSelected = {
                    navController.navigate(LeafScreen.Team.createRoute(Screen.Teams, it.id))
                },
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed,
            )
        }
        composable(LeafScreen.Team.createRoute(Screen.Teams)) {
            val viewModel = hiltViewModel<TeamViewModel>()
            val teamViewState by viewModel.teamViewState.rememberFlowWithLifecycle(
                viewModel.viewModelScope,
                TeamViewState.Empty
            )
            TeamScreen(
                teamViewState = teamViewState,
                onRiderSelected = { rider ->
                    navController.navigate(
                        LeafScreen.Rider.createRoute(
                            Screen.Riders,
                            rider.id
                        )
                    )
                },
                onBackPressed = { navController.navigateUp() },
            )
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
            val ridersViewState by viewModel.state.rememberFlowWithLifecycle(
                viewModel.viewModelScope,
                RidersViewState.Empty
            )
            RidersScreen(
                riders = ridersViewState.riders,
                showSearch = ridersViewState.searching,
                searchQuery = ridersViewState.search,
                onRiderSearched = viewModel::onSearched,
                onRiderSelected = {
                    navController.navigate(LeafScreen.Rider.createRoute(Screen.Riders, it.id))
                },
                onSortingSelected = viewModel::onSorted,
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed,
                onToggled = viewModel::onToggled,
            )
        }
        composable(LeafScreen.Rider.createRoute(Screen.Riders)) {
            val viewModel = hiltViewModel<RiderViewModel>()
            val riderViewState by viewModel.riderViewState.rememberFlowWithLifecycle(
                viewModel.viewModelScope,
                RiderViewState.Empty
            )
            RiderScreen(
                riderViewState = riderViewState,
                onTeamSelected = { team ->
                    navController.navigate(
                        LeafScreen.Team.createRoute(
                            Screen.Teams,
                            team.id
                        )
                    )
                },
                onBackPressed = { navController.navigateUp() },
            )
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
            val racesViewState by viewModel.racesViewState.rememberFlowWithLifecycle(
                viewModel.viewModelScope,
                RacesViewState.Empty
            )
            RacesScreen(
                racesViewState = racesViewState,
                onRaceSelected = {
                    navController.navigate(LeafScreen.Race.createRoute(Screen.Races, it.id))
                },
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed,
            )
        }
        composable(LeafScreen.Race.createRoute(Screen.Races)) {
            val viewModel = hiltViewModel<RaceViewModel>()
            val raceViewState by viewModel.raceViewState.rememberFlowWithLifecycle(
                viewModel.viewModelScope,
                RaceViewState.Empty
            )
            RaceScreen(
                raceViewState = raceViewState,
                onStageSelected = { race, stage ->
                    navController.navigate(
                        LeafScreen.Stage.createRoute(
                            Screen.Races,
                            race.id,
                            stage.id,
                        )
                    )
                },
                onBackPressed = { navController.navigateUp() },
            )
        }
        composable(LeafScreen.Stage.createRoute(Screen.Races)) {
            val viewModel = hiltViewModel<StageViewModel>()
            val stateViewState by viewModel.stateViewState.rememberFlowWithLifecycle(
                viewModel.viewModelScope,
                StageViewState.Empty
            )
            StageScreen(stageViewState = stateViewState)
        }
    }
}
