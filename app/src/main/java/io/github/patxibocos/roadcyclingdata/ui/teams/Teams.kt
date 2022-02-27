package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import io.github.patxibocos.roadcyclingdata.data.Team
import io.github.patxibocos.roadcyclingdata.data.TeamStatus
import io.github.patxibocos.roadcyclingdata.ui.preview.teamPreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
internal fun TeamsScreen(
    teams: List<Team> = listOf(teamPreview),
    onTeamSelected: (Team) -> Unit = {},
) {
    Column {
        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()
        TabRow(selectedTabIndex = pagerState.currentPage, indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                text = { Text("World Teams") }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                text = { Text("Pro Teams") }
            )
        }
        HorizontalPager(
            count = 2,
            state = pagerState,
        ) { page ->
            if (page == 0) {
                TeamsList(
                    teams = teams.filter { it.status == TeamStatus.WORLD_TEAM },
                    onTeamSelected,
                )
            } else {
                TeamsList(
                    teams = teams.filter { it.status == TeamStatus.PRO_TEAM },
                    onTeamSelected,
                )
            }
        }
    }
}

@Composable
internal fun TeamsList(
    teams: List<Team>,
    onTeamSelected: (Team) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(items = teams, key = Team::id) { team ->
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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTeamSelected(team) }
    ) {
        val (jerseyImage, abbreviation, name, bike) = createRefs()
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
        Image(
            modifier = Modifier
                .constrainAs(jerseyImage) {
                    start.linkTo(parent.start)
                }
                .padding(horizontal = 16.dp)
                .size(75.dp)
                .clip(CutCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .border(
                    2.dp,
                    MaterialTheme.colors.secondary,
                    CutCornerShape(topStart = 10.dp, topEnd = 10.dp)
                )
                .padding(horizontal = 2.dp),
            painter = rememberImagePainter(
                data = team.jersey,
            ),
            contentDescription = null,
        )
        Text(
            text = team.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .constrainAs(name) {
                    start.linkTo(jerseyImage.end)
                    top.linkTo(jerseyImage.top)
                }
                .padding(top = 10.dp)
        )
        Text(
            text = "\uD83D\uDEB4 ${team.bike}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .constrainAs(bike) {
                    start.linkTo(jerseyImage.end)
                    bottom.linkTo(jerseyImage.bottom)
                }
                .padding(bottom = 10.dp)
        )
    }
}
