package io.github.patxibocos.roadcyclingdata.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Group
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.patxibocos.roadcyclingdata.ui.races.RaceScreen
import io.github.patxibocos.roadcyclingdata.ui.races.RaceViewModel
import io.github.patxibocos.roadcyclingdata.ui.races.RacesScreen
import io.github.patxibocos.roadcyclingdata.ui.races.RacesViewModel
import io.github.patxibocos.roadcyclingdata.ui.riders.RiderScreen
import io.github.patxibocos.roadcyclingdata.ui.riders.RiderViewModel
import io.github.patxibocos.roadcyclingdata.ui.riders.RidersScreen
import io.github.patxibocos.roadcyclingdata.ui.riders.RidersViewModel
import io.github.patxibocos.roadcyclingdata.ui.stages.StageScreen
import io.github.patxibocos.roadcyclingdata.ui.stages.StageViewModel
import io.github.patxibocos.roadcyclingdata.ui.teams.TeamScreen
import io.github.patxibocos.roadcyclingdata.ui.teams.TeamViewModel
import io.github.patxibocos.roadcyclingdata.ui.teams.TeamsScreen
import io.github.patxibocos.roadcyclingdata.ui.teams.TeamsViewModel

internal sealed class Screen(val route: String, val icon: ImageVector) {
    object Teams : Screen("teams", Icons.Outlined.Group)
    object Riders : Screen("riders", Icons.Outlined.Face)
    object Races : Screen("races", Icons.Outlined.Flag)
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
                val viewModel = hiltViewModel<TeamsViewModel>()
                val teams by viewModel.teams.collectAsState()
                TeamsScreen(
                    teams = teams,
                    onTeamSelected = {
                        navController.navigate(LeafScreen.Team.createRoute(Screen.Teams, it.id))
                    }
                )
            }
            composable(LeafScreen.Team.createRoute(Screen.Teams)) {
                it.arguments?.getString("teamId")?.let { teamId ->
                    val viewModel = hiltViewModel<TeamViewModel>()
                    viewModel.loadTeam(teamId)
                    val teamOfRiders by viewModel.teamOfRiders.collectAsState()
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
        navigation(
            startDestination = LeafScreen.Riders.createRoute(Screen.Riders),
            route = Screen.Riders.route
        ) {
            composable(LeafScreen.Riders.createRoute(Screen.Riders)) {
                val viewModel = hiltViewModel<RidersViewModel>()
                val riders by viewModel.riders.collectAsState()
                val query by viewModel.search.collectAsState()
                RidersScreen(
                    riders = riders,
                    searchQuery = query,
                    onRiderSearched = viewModel::onSearched,
                    onRiderSelected = {
                        navController.navigate(LeafScreen.Rider.createRoute(Screen.Riders, it.id))
                    }
                )
            }
            composable(LeafScreen.Rider.createRoute(Screen.Riders)) {
                it.arguments?.getString("riderId")?.let { riderId ->
                    val viewModel = hiltViewModel<RiderViewModel>()
                    viewModel.loadRider(riderId)
                    val riderOfTeam by viewModel.riderOfTeam.collectAsState()
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
        navigation(
            startDestination = LeafScreen.Races.createRoute(Screen.Races),
            route = Screen.Races.route
        ) {
            composable(LeafScreen.Races.createRoute(Screen.Races)) {
                val viewModel = hiltViewModel<RacesViewModel>()
                val races by viewModel.races.collectAsState()
                RacesScreen(
                    races = races,
                    onRaceSelected = {
                        navController.navigate(LeafScreen.Race.createRoute(Screen.Races, it.id))
                    }
                )
            }
            composable(LeafScreen.Race.createRoute(Screen.Races)) {
                it.arguments?.getString("raceId")?.let { raceId ->
                    val viewModel = hiltViewModel<RaceViewModel>()
                    viewModel.loadRace(raceId)
                    val race by viewModel.race.collectAsState()
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
                    val stage by viewModel.getStage(raceId, stageId).collectAsState(null)
                    StageScreen(stage)
                }
            }
        }
    }
}
