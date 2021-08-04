package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.roadcyclingdata.data.Race
import io.github.patxibocos.roadcyclingdata.data.Stage

@Composable
fun RacesScreen() {
    Races(viewModel = hiltViewModel())
}

@Composable
internal fun Races(viewModel: RacesViewModel) {
    val races by viewModel.races.collectAsState()
    val selectedRace by viewModel.selectedRiderIndex.collectAsState()
    RacesList(races, selectedRace, viewModel::onRaceSelected)
}

@Composable
internal fun RacesList(races: List<Race>, selectedRace: Int, onRaceSelected: (Race) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(items = races, key = { _, race -> race.id }, itemContent = { index, race ->
            RaceRow(race, selectedRace == index, onRaceSelected)
        })
    }
}

@Composable
@Preview
internal fun RaceRow(
    race: Race = Race.Preview,
    selected: Boolean = true,
    onRaceSelected: (Race) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onRaceSelected(race) }
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
    ) {
        Text(
            text = race.name,
            style = MaterialTheme.typography.h6,
        )
        Text(
            text = "${race.startDate} - ${race.endDate}",
            style = MaterialTheme.typography.body1,
        )
        Text(
            text = if (race.stages.isEmpty()) "Single day race" else "${race.stages.size} stages"
        )
        if (selected) {
            RaceStages(race.stages)
        }
    }
}

@Composable
internal fun RaceStages(stages: List<Stage>) {
    stages.forEachIndexed { index, stage ->
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Stage ${index + 1}", modifier = Modifier.padding(end = 10.dp))
            Text(text = "${stage.distance} km", modifier = Modifier.padding(end = 10.dp))
            Text(text = "${stage.departure} - ${stage.arrival}")
        }
    }
}
