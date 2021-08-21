package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RaceScreen(raceId: String) {
    Race(
        viewModel = hiltViewModel(),
        raceId = raceId
    )
}

@Composable
internal fun Race(viewModel: RaceViewModel, raceId: String) {
    val race = viewModel.getRace(raceId).collectAsState(null).value
    if (race != null) {
        Text(text = race.name)
    }
}
