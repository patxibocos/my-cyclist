package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.roadcyclingdata.data.Race
import io.github.patxibocos.roadcyclingdata.data.Stage
import io.github.patxibocos.roadcyclingdata.ui.util.getCountryEmoji
import java.time.format.DateTimeFormatter

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
    ) {
        Text(
            text = "${getCountryEmoji(race.country)} ${race.name}",
            style = MaterialTheme.typography.h6,
        )
        Row {
            Card(border = BorderStroke(2.dp, Color.White)) {
                val (day, month) = race.startDate.format(DateTimeFormatter.ofPattern("dd MMM"))
                    .uppercase()
                    .split(" ")
                Column(modifier = Modifier.padding(horizontal = 5.dp)) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = month,
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
            Text(
                text = if (race.isSingleDay()) "Single day race" else "${race.stages.size} stages"
            )
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
