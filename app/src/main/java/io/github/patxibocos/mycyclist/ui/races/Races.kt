/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.mycyclist.R
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.areResultsAvailable
import io.github.patxibocos.mycyclist.ui.home.Screen
import io.github.patxibocos.mycyclist.ui.util.CenterAlignedTopAppBar
import io.github.patxibocos.mycyclist.ui.util.RefreshableContent
import io.github.patxibocos.mycyclist.ui.util.ddMMMFormat
import io.github.patxibocos.mycyclist.ui.util.formatTime
import io.github.patxibocos.mycyclist.ui.util.getCountryEmoji
import io.github.patxibocos.mycyclist.ui.util.rememberFlowWithLifecycle

@Composable
internal fun RacesRoute(
    onRaceSelected: (Race) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit,
    viewModel: RacesViewModel = hiltViewModel()
) {
    val racesViewState by viewModel.racesViewState.rememberFlowWithLifecycle()
    RacesScreen(
        racesViewState = racesViewState,
        onRaceSelected = onRaceSelected,
        onStageSelected = onStageSelected,
        reselectedScreen = reselectedScreen,
        onReselectedScreenConsumed = onReselectedScreenConsumed
    )
}

@Composable
private fun RacesScreen(
    racesViewState: RacesViewState,
    onRaceSelected: (Race) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(key1 = reselectedScreen.value) {
        if (reselectedScreen.value == Screen.Races) {
            lazyListState.scrollToItem(0)
            onReselectedScreenConsumed()
        }
    }
    Column {
        CenterAlignedTopAppBar(title = stringResource(R.string.races_title))
        Surface {
            RefreshableContent {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    state = lazyListState
                ) {
                    when (racesViewState) {
                        RacesViewState.EmptyViewState -> {}
                        is RacesViewState.SeasonEndedViewState -> {
                            seasonEnded(racesViewState.pastRaces, onRaceSelected)
                        }
                        is RacesViewState.SeasonInProgressViewState -> {
                            seasonInProgress(
                                racesViewState.pastRaces,
                                racesViewState.todayStages,
                                racesViewState.futureRaces,
                                onRaceSelected,
                                onStageSelected
                            )
                        }
                        is RacesViewState.SeasonNotStartedViewState -> {
                            seasonNotStarted(racesViewState.futureRaces, onRaceSelected)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.seasonInProgress(
    pastRaces: List<Race>,
    todayStages: List<TodayStage>,
    futureRaces: List<Race>,
    onRaceSelected: (Race) -> Unit,
    onStageSelected: (Race, Stage) -> Unit
) {
    item {
        Text(
            text = "Today",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp)
                .fillMaxWidth()
        )
    }
    if (todayStages.isEmpty()) {
        item {
            Text("No races today, see next races below")
        }
    }
    items(todayStages) { todayStage ->
        when (todayStage) {
            is TodayStage.MultiStageRace -> TodayMultiStageRaceStage(
                todayStage.race,
                todayStage.stage,
                todayStage.stageNumber,
                onStageSelected
            )
            is TodayStage.RestDay -> TodayRestDayStage(todayStage.race, onRaceSelected)
            is TodayStage.SingleDayRace -> TodaySingleDayRaceStage(
                todayStage.race,
                todayStage.stage,
                onRaceSelected
            )
        }
    }
    if (futureRaces.isNotEmpty()) {
        stickyHeader {
            Text(
                text = "Future races",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
        items(
            items = futureRaces,
            key = Race::id,
            itemContent = { race ->
                RaceRow(race, onRaceSelected)
            }
        )
    }
    if (pastRaces.isNotEmpty()) {
        stickyHeader {
            Text(
                text = "Past races",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
        items(
            items = pastRaces,
            key = Race::id,
            itemContent = { race ->
                RaceRow(race, onRaceSelected)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodayMultiStageRaceStage(
    race: Race,
    stage: Stage,
    stageNumber: Int,
    onStageSelected: (Race, Stage) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onStageSelected(race, stage)
            }
    ) {
        Text("${race.name} - Stage $stageNumber")
        Text("🏳 ${stage.departure} - ${stage.arrival} 🏁")
        Text(formatTime(stage.startDateTime))
        if (stage.areResultsAvailable()) {
            // Show a button to go to results
            Button(onClick = { onStageSelected(race, stage) }) {
                Text("See results")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodayRestDayStage(race: Race, onRaceSelected: (Race) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRaceSelected(race) }
    ) {
        Text("Rest day - ${race.name}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodaySingleDayRaceStage(race: Race, stage: Stage, onRaceSelected: (Race) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRaceSelected(race) }
    ) {
        Text(text = race.name)
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.seasonEnded(pastRaces: List<Race>, onRaceSelected: (Race) -> Unit) {
    item {
        Text(text = "Season has ended")
    }
    stickyHeader {
        Text(text = "Past races")
    }
    items(pastRaces) { pastRace ->
        RaceRow(race = pastRace, onRaceSelected = onRaceSelected)
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.seasonNotStarted(
    futureRaces: List<Race>,
    onRaceSelected: (Race) -> Unit
) {
    item {
        Text(text = "Season has not started")
    }
    stickyHeader {
        Text(text = "Future races")
    }
    items(futureRaces) { pastRace ->
        RaceRow(race = pastRace, onRaceSelected = onRaceSelected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            style = MaterialTheme.typography.bodyMedium
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
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = month,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Text(
                text = LocalContext.current.resources.getQuantityString(
                    R.plurals.races_stages,
                    race.stages.size,
                    race.stages.size
                )
            )
        }
    }
}
