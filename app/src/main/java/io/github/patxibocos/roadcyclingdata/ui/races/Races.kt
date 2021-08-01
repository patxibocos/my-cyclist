package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import io.github.patxibocos.roadcyclingdata.data.Race

@Composable
fun RacesScreen() {
    Races(viewModel = hiltViewModel())
}

@Composable
internal fun Races(viewModel: RacesViewModel) {
    RacesList(viewModel.getRaces().collectAsState(initial = emptyList()).value)
}

@Composable
internal fun RacesList(races: List<Race>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(races, key = { it.id }) { race -> RaceRow(race) }
    }
}

@Composable
@Preview
internal fun RaceRow(race: Race = Race.Preview) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = race.name,
            style = MaterialTheme.typography.h6,
        )
        Text(
            text = "${race.startDate} - ${race.endDate}",
            style = MaterialTheme.typography.body1,
        )
    }
}
