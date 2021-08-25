package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import io.github.patxibocos.roadcyclingdata.data.Team
import io.github.patxibocos.roadcyclingdata.ui.util.CustomCircleCropTransformation
import io.github.patxibocos.roadcyclingdata.ui.util.getCountryEmoji

@Composable
fun TeamsScreen(onTeamSelected: (Team) -> Unit) {
    Teams(
        viewModel = hiltViewModel(),
        onTeamSelected = onTeamSelected,
    )
}

@Composable
internal fun Teams(
    viewModel: TeamsViewModel,
    onTeamSelected: (Team) -> Unit
) {
    val teams by viewModel.getTeams().collectAsState(initial = emptyList())
    TeamsList(teams, onTeamSelected)
}

@Composable
internal fun TeamsList(teams: List<Team>, onTeamSelected: (Team) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        teams.groupBy { it.status }.forEach { (status, teams) ->
            item {
                Text(text = status.statusName)
            }
            items(items = teams, key = Team::id) { team ->
                TeamRow(team, onTeamSelected)
            }
        }
    }
}

@Composable
@Preview
internal fun TeamRow(
    team: Team = Team.Preview,
    onTeamSelected: (Team) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTeamSelected(team) }
    ) {
        Box {
            Text(
                modifier = Modifier.padding(start = 75.dp),
                text = getCountryEmoji(team.country),
                style = MaterialTheme.typography.h3,
            )
            Image(
                modifier = Modifier.size(75.dp),
                painter = rememberImagePainter(data = team.jersey, builder = {
                    transformations(CustomCircleCropTransformation())
                    crossfade(true)
                }),
                contentDescription = null,
            )
        }
        Text(
            text = team.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
