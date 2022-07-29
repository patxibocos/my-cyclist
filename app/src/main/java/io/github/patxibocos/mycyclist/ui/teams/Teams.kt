/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.github.patxibocos.mycyclist.R
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.data.TeamStatus
import io.github.patxibocos.mycyclist.ui.home.Screen
import io.github.patxibocos.mycyclist.ui.preview.teamPreview
import io.github.patxibocos.mycyclist.ui.util.CenterAlignedTopAppBar
import io.github.patxibocos.mycyclist.ui.util.RefreshableContent
import io.github.patxibocos.mycyclist.ui.util.rememberFlowWithLifecycle
import kotlinx.coroutines.launch

@Composable
internal fun TeamsRoute(
    onTeamSelected: (Team) -> Unit = {},
    reselectedScreen: State<Screen?> = mutableStateOf(null),
    onReselectedScreenConsumed: () -> Unit = {},
    viewModel: TeamsViewModel = hiltViewModel()
) {
    val teamsViewState by viewModel.teamsViewState.rememberFlowWithLifecycle()
    TeamsScreen(
        teamsViewState = teamsViewState,
        onTeamSelected = onTeamSelected,
        reselectedScreen = reselectedScreen,
        onReselectedScreenConsumed = onReselectedScreenConsumed
    )
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
private fun TeamsScreen(
    teamsViewState: TeamsViewState = TeamsViewState(listOf(teamPreview)),
    onTeamSelected: (Team) -> Unit = {},
    reselectedScreen: State<Screen?> = mutableStateOf(null),
    onReselectedScreenConsumed: () -> Unit = {}
) {
    val worldTeamsLazyGridState = rememberLazyGridState()
    val proTeamsLazyGridState = rememberLazyGridState()
    val pagerState = rememberPagerState()
    LaunchedEffect(key1 = reselectedScreen.value) {
        if (reselectedScreen.value == Screen.Teams) {
            if (pagerState.currentPage == 0) {
                worldTeamsLazyGridState.scrollToItem(0)
            } else {
                proTeamsLazyGridState.scrollToItem(0)
            }
            onReselectedScreenConsumed()
        }
    }
    Column {
        CenterAlignedTopAppBar(title = stringResource(R.string.teams_title))
        val coroutineScope = rememberCoroutineScope()
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                text = { Text(stringResource(R.string.teams_world)) }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                text = { Text(stringResource(R.string.teams_pro)) }
            )
        }
        RefreshableContent {
            HorizontalPager(
                count = 2,
                state = pagerState
            ) { page ->
                if (page == 0) {
                    TeamsList(
                        teams = teamsViewState.teams.filter { it.status == TeamStatus.WORLD_TEAM },
                        onTeamSelected = onTeamSelected,
                        lazyListState = worldTeamsLazyGridState
                    )
                } else {
                    TeamsList(
                        teams = teamsViewState.teams.filter { it.status == TeamStatus.PRO_TEAM },
                        onTeamSelected = onTeamSelected,
                        lazyListState = proTeamsLazyGridState
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamsList(
    teams: List<Team>,
    onTeamSelected: (Team) -> Unit,
    lazyListState: LazyGridState
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        state = lazyListState
    ) {
        items(teams) { team ->
            TeamRow(team, onTeamSelected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamRow(
    team: Team,
    onTeamSelected: (Team) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onTeamSelected(team) }
    ) {
        ConstraintLayout {
            val (jerseyImage, abbreviation, name, bike, background) = createRefs()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    .constrainAs(background) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
            Text(
                text = team.abbreviation,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.constrainAs(abbreviation) {
                    start.linkTo(jerseyImage.start)
                    end.linkTo(jerseyImage.end)
                    top.linkTo(jerseyImage.top)
                    bottom.linkTo(jerseyImage.bottom)
                }
            )
            AsyncImage(
                model = team.jersey,
                modifier = Modifier
                    .constrainAs(jerseyImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(all = 16.dp)
                    .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    .padding(2.dp)
                    .size(75.dp)
                    .clip(CircleShape),
                contentDescription = null
            )
            Text(
                text = team.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .constrainAs(name) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(jerseyImage.bottom)
                    }
                    .padding(bottom = 10.dp)
            )
            Text(
                text = "\uD83D\uDEB4 ${team.bike}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .constrainAs(bike) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(name.bottom)
                    }
            )
        }
    }
}
