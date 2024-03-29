package io.github.patxibocos.mycyclist.ui.home

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import io.github.patxibocos.mycyclist.R
import io.github.patxibocos.mycyclist.ui.races.RaceParticipationsRoute
import io.github.patxibocos.mycyclist.ui.races.RaceRoute
import io.github.patxibocos.mycyclist.ui.races.RacesRoute
import io.github.patxibocos.mycyclist.ui.riders.RiderRoute
import io.github.patxibocos.mycyclist.ui.riders.RidersRoute
import io.github.patxibocos.mycyclist.ui.teams.TeamRoute
import io.github.patxibocos.mycyclist.ui.teams.TeamsRoute

private const val ROOT_SCREENS_ANIMATION_SPEED = 100
private const val LEAF_SCREENS_ANIMATION_SPEED = 300

@Composable
internal fun AppNavigation(
    navController: NavHostController,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit,
    modifier: Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Races.route,
    ) {
        addTeamsNavigation(
            navController,
            reselectedScreen,
            onReselectedScreenConsumed,
        )
        addRidersNavigation(
            navController,
            reselectedScreen,
            onReselectedScreenConsumed,
        )
        addRacesNavigation(
            navController,
            reselectedScreen,
            onReselectedScreenConsumed,
        )
    }
}

internal sealed class Screen(
    val route: String,
    @StringRes val label: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
) {
    data object Teams :
        Screen("teams", R.string.screen_teams, Icons.Outlined.Group, Icons.Filled.Group)

    data object Riders :
        Screen("riders", R.string.screen_riders, Icons.Outlined.Face, Icons.Filled.Face)

    data object Races :
        Screen("races", R.string.screen_races, Icons.Outlined.Flag, Icons.Filled.Flag)
}

internal sealed class LeafScreen(
    private val route: String,
) {
    fun createRoute(root: Screen) = "${root.route}/$route"

    data object Teams : LeafScreen("teams")
    data object Riders : LeafScreen("riders")
    data object Races : LeafScreen("races")

    data object Team : LeafScreen("team/{teamId}") {
        fun createRoute(root: Screen, teamId: String): String {
            return "${root.route}/team/$teamId"
        }
    }

    data object Rider : LeafScreen("rider/{riderId}") {
        fun createRoute(root: Screen, riderId: String): String {
            return "${root.route}/rider/$riderId"
        }
    }

    data object Race : LeafScreen("race/{raceId}?stage={stageId}") {
        fun createRoute(root: Screen, raceId: String, stageId: String? = null): String {
            return "${root.route}/race/$raceId" + (stageId?.let { "?stage=$it" } ?: "")
        }
    }

    data object RaceParticipations : LeafScreen("race/{raceId}/participations") {
        fun createRoute(root: Screen, raceId: String): String {
            return "${root.route}/race/$raceId/participations"
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
        route = Screen.Teams.route,
    ) {
        composableWithAnimations(LeafScreen.Teams.createRoute(Screen.Teams)) {
            TeamsRoute(
                onTeamSelected = {
                    navController.navigate(LeafScreen.Team.createRoute(Screen.Teams, it.id))
                },
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed,
            )
        }
        composableWithAnimations(LeafScreen.Team.createRoute(Screen.Teams)) {
            TeamRoute(
                onRiderSelected = { rider ->
                    navController.navigate(
                        LeafScreen.Rider.createRoute(
                            Screen.Riders,
                            rider.id,
                        ),
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
        route = Screen.Riders.route,
    ) {
        composableWithAnimations(LeafScreen.Riders.createRoute(Screen.Riders)) {
            RidersRoute(
                onRiderSelected = {
                    navController.navigate(LeafScreen.Rider.createRoute(Screen.Riders, it.id))
                },
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed,
            )
        }
        composableWithAnimations(LeafScreen.Rider.createRoute(Screen.Riders)) {
            RiderRoute(
                onTeamSelected = { team ->
                    navController.navigate(
                        LeafScreen.Team.createRoute(
                            Screen.Teams,
                            team.id,
                        ),
                    )
                },
                onRaceSelected = { race ->
                    navController.navigate(
                        LeafScreen.Race.createRoute(
                            Screen.Races,
                            race.id,
                        ),
                    )
                },
                onStageSelected = { race, stage ->
                    navController.navigate(
                        LeafScreen.Race.createRoute(
                            Screen.Races,
                            race.id,
                            stage.id,
                        ),
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
        route = Screen.Races.route,
    ) {
        composableWithAnimations(route = LeafScreen.Races.createRoute(Screen.Races)) {
            RacesRoute(
                onRaceSelected = {
                    navController.navigate(LeafScreen.Race.createRoute(Screen.Races, it.id))
                },
                onStageSelected = { race, stage ->
                    navController.navigate(
                        LeafScreen.Race.createRoute(
                            Screen.Races,
                            race.id,
                            stage.id,
                        ),
                    )
                },
                reselectedScreen = reselectedScreen,
                onReselectedScreenConsumed = onReselectedScreenConsumed,
            )
        }
        composableWithAnimations(
            route = LeafScreen.Race.createRoute(Screen.Races),
            arguments = listOf(navArgument("stageId") { nullable = true }),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "mycyclist://${LeafScreen.Race.createRoute(Screen.Races)}"
                },
            ),
        ) {
            RaceRoute(
                onRiderSelected = { rider ->
                    navController.navigate(
                        LeafScreen.Rider.createRoute(
                            Screen.Riders,
                            rider.id,
                        ),
                    )
                },
                onTeamSelected = { team ->
                    navController.navigate(
                        LeafScreen.Team.createRoute(
                            Screen.Teams,
                            team.id,
                        ),
                    )
                },
                onParticipationsClicked = { race ->
                    navController.navigate(
                        LeafScreen.RaceParticipations.createRoute(
                            Screen.Races,
                            race.id,
                        ),
                    )
                },
                onBackPressed = { navController.navigateUp() },
            )
        }
        composableWithAnimations(
            LeafScreen.RaceParticipations.createRoute(Screen.Races),
        ) {
            RaceParticipationsRoute(
                onRiderSelected = { rider ->
                    navController.navigate(
                        LeafScreen.Rider.createRoute(
                            Screen.Riders,
                            rider.id,
                        ),
                    )
                },
                onTeamSelected = { team ->
                    navController.navigate(
                        LeafScreen.Team.createRoute(
                            Screen.Teams,
                            team.id,
                        ),
                    )
                },
                onBackPressed = { navController.navigateUp() },
            )
        }
    }
}

private fun NavGraphBuilder.composableWithAnimations(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = {
            if (areRootDestinations(
                    this.initialState.destination.route,
                    this.targetState.destination.route,
                )
            ) {
                fadeIn(animationSpec = tween(ROOT_SCREENS_ANIMATION_SPEED))
            } else {
                fadeIn(animationSpec = tween(LEAF_SCREENS_ANIMATION_SPEED))
            }
        },
        exitTransition = {
            if (areRootDestinations(
                    this.initialState.destination.route,
                    this.targetState.destination.route,
                )
            ) {
                fadeOut(animationSpec = tween(ROOT_SCREENS_ANIMATION_SPEED))
            } else {
                fadeOut(animationSpec = tween(LEAF_SCREENS_ANIMATION_SPEED))
            }
        },
        content = content,
    )
}

private fun areRootDestinations(vararg routes: String?): Boolean {
    return routes.all {
        it in listOf(
            LeafScreen.Races.createRoute(Screen.Races),
            LeafScreen.Riders.createRoute(Screen.Riders),
            LeafScreen.Teams.createRoute(Screen.Teams),
        )
    }
}
