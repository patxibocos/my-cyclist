package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import io.github.patxibocos.roadcyclingdata.Country
import io.github.patxibocos.roadcyclingdata.data.Team
import io.github.patxibocos.roadcyclingdata.getEmoji

@Composable
fun TeamsScreen() {
    Teams(
        viewModel = hiltViewModel(),
    )
}

@Composable
internal fun Teams(viewModel: TeamsViewModel) {
    TeamsList(viewModel.getTeams().collectAsState(initial = emptyList()).value)
}

@Composable
internal fun TeamsList(teams: List<Team>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(teams) { team ->
            TeamRow(team)
        }
    }
}

@Composable
@Preview
internal fun TeamRow(team: Team = Team.Preview) {
    Row {
        Box {
            Text(
                modifier = Modifier.padding(start = 75.dp),
                text = getEmoji(Country(team.country)),
                style = MaterialTheme.typography.h3,
            )
            Image(
                modifier = Modifier.size(100.dp),
                painter = rememberImagePainter(team.jersey),
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