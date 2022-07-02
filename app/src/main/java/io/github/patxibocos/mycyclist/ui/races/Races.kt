package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.R
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.RaceMoment
import io.github.patxibocos.mycyclist.data.getMoment
import io.github.patxibocos.mycyclist.ui.home.Screen
import io.github.patxibocos.mycyclist.ui.preview.racePreview
import io.github.patxibocos.mycyclist.ui.util.ddMMMFormat
import io.github.patxibocos.mycyclist.ui.util.getCountryEmoji
import io.github.patxibocos.mycyclist.ui.util.rememberFlowWithLifecycle

@Composable
internal fun RacesRoute(
    onRaceSelected: (Race) -> Unit = {},
    reselectedScreen: State<Screen?> = mutableStateOf(null),
    onReselectedScreenConsumed: () -> Unit = {},
    viewModel: RacesViewModel = hiltViewModel()
) {
    val racesViewState by viewModel.racesViewState.rememberFlowWithLifecycle(
        viewModel.viewModelScope,
        RacesViewState.Empty
    )
    RacesScreen(
        racesViewState = racesViewState,
        onRaceSelected = onRaceSelected,
        reselectedScreen = reselectedScreen,
        onReselectedScreenConsumed = onReselectedScreenConsumed
    )
}

@Preview
@Composable
internal fun RacesScreen(
    racesViewState: RacesViewState = RacesViewState(listOf(racePreview)),
    onRaceSelected: (Race) -> Unit = {},
    reselectedScreen: State<Screen?> = mutableStateOf(null),
    onReselectedScreenConsumed: () -> Unit = {}
) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(key1 = reselectedScreen.value) {
        if (reselectedScreen.value == Screen.Races) {
            lazyListState.scrollToItem(0)
            onReselectedScreenConsumed()
        }
    }
    Column {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            ),
            title = {
                Text(text = stringResource(R.string.races_title))
            }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(items = racesViewState.races, key = Race::id, itemContent = { race ->
                RaceRow(race, onRaceSelected)
            })
        }
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
            .clickable { onRaceSelected(race) }.run {
                if (race.getMoment() == RaceMoment.Past) alpha(.7f) else this
            }
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
