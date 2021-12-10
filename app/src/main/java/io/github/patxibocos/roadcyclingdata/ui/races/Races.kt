package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import io.github.patxibocos.pcsscraper.protobuf.RaceOuterClass.Race
import io.github.patxibocos.roadcyclingdata.ui.preview.racePreview
import io.github.patxibocos.roadcyclingdata.ui.util.ddMMMFormat
import io.github.patxibocos.roadcyclingdata.ui.util.getCountryEmoji

@Composable
internal fun RacesScreen(racesLiveData: LiveData<List<Race>>, onRaceSelected: (Race) -> Unit) {
    val races by racesLiveData.observeAsState(initial = emptyList())
    Races(races, onRaceSelected)
}

@Preview
@Composable
private fun Races(races: List<Race> = listOf(racePreview), onRaceSelected: (Race) -> Unit = {}) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = races, key = Race::getId, itemContent = { race ->
            RaceRow(race, onRaceSelected)
        })
    }
}

@Composable
private fun RaceRow(
    race: Race,
    onRaceSelected: (Race) -> Unit
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
                val (day, month) = ddMMMFormat(race.startDate)
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
                text = if (race.stagesCount == 1) "Single day race" else "${race.stagesCount} stages"
            )
        }
    }
}
