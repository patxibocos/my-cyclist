package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patxibocos.roadcyclingdata.data.Race
import io.github.patxibocos.roadcyclingdata.data.RaceMoment
import io.github.patxibocos.roadcyclingdata.data.getMoment
import io.github.patxibocos.roadcyclingdata.ui.home.Screen
import io.github.patxibocos.roadcyclingdata.ui.preview.racePreview
import io.github.patxibocos.roadcyclingdata.ui.util.ddMMMFormat
import io.github.patxibocos.roadcyclingdata.ui.util.getCountryEmoji

@Preview
@Composable
internal fun RacesScreen(
    races: List<Race> = listOf(racePreview),
    onRaceSelected: (Race) -> Unit = {},
    reselectedScreen: State<Screen?> = mutableStateOf(null),
    onReselectedScreenConsumed: () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(key1 = reselectedScreen.value) {
        if (reselectedScreen.value == Screen.Races) {
            lazyListState.animateScrollToItem(0)
            onReselectedScreenConsumed()
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(items = races, key = Race::id, itemContent = { race ->
            RaceRow(race, onRaceSelected)
        })
        item {
            Spacer(modifier = Modifier.height(56.dp))
        }
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
            .clickable { onRaceSelected(race) }.run {
                if (race.getMoment() == RaceMoment.Past) alpha(.7f) else this
            }
    ) {
        Text(
            text = "${getCountryEmoji(race.country)} ${race.name}",
            style = MaterialTheme.typography.h6,
        )
        Row {
            Card(
                border = BorderStroke(2.dp, Color.White),
                modifier = Modifier.padding(end = 10.dp)
            ) {
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
                text = if (race.stages.size == 1) "Single day race" else "${race.stages.size} stages"
            )
        }
    }
}
