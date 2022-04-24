package io.github.patxibocos.mycyclist.ui.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import io.github.patxibocos.mycyclist.R
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.data.TeamStatus
import io.github.patxibocos.mycyclist.ui.home.Screen
import io.github.patxibocos.mycyclist.ui.preview.teamPreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
internal fun TeamsScreen(
    teams: List<Team> = listOf(teamPreview),
    onTeamSelected: (Team) -> Unit = {},
    reselectedScreen: State<Screen?> = mutableStateOf(null),
    onReselectedScreenConsumed: () -> Unit = {},
) {
    val worldTeamsLazyGridState = rememberLazyGridState()
    val proTeamsLazyGridState = rememberLazyGridState()
    val pagerState = rememberPagerState()
    LaunchedEffect(key1 = reselectedScreen.value) {
        if (reselectedScreen.value == Screen.Teams) {
            if (pagerState.currentPage == 0) {
                worldTeamsLazyGridState.animateScrollToItem(0)
            } else {
                proTeamsLazyGridState.animateScrollToItem(0)
            }
            onReselectedScreenConsumed()
        }
    }
    Column {
        val coroutineScope = rememberCoroutineScope()
        TabRow(selectedTabIndex = pagerState.currentPage, indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }) {
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
        HorizontalPager(
            count = 2,
            state = pagerState,
        ) { page ->
            if (page == 0) {
                TeamsList(
                    teams = teams.filter { it.status == TeamStatus.WORLD_TEAM },
                    onTeamSelected = onTeamSelected,
                    lazyListState = worldTeamsLazyGridState,
                )
            } else {
                TeamsList(
                    teams = teams.filter { it.status == TeamStatus.PRO_TEAM },
                    onTeamSelected = onTeamSelected,
                    lazyListState = proTeamsLazyGridState,
                )
            }
        }
    }
}

@Composable
internal fun TeamsList(
    teams: List<Team>,
    onTeamSelected: (Team) -> Unit,
    lazyListState: LazyGridState,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        state = lazyListState,
    ) {
        items(teams) { team ->
            TeamRow(team, onTeamSelected)
        }
        item {
            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}

@Composable
internal fun TeamRow(
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
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
                    .constrainAs(background) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
            Text(
                text = team.abbreviation,
                style = MaterialTheme.typography.h5,
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
                    .border(2.dp, MaterialTheme.colors.secondary, CircleShape)
                    .padding(2.dp)
                    .size(75.dp)
                    .clip(CircleShape),
                contentDescription = null,
            )
            Text(
                text = team.name,
                style = MaterialTheme.typography.body1,
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
                style = MaterialTheme.typography.body1,
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
