package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Stage
import io.github.patxibocos.roadcyclingdata.ui.stages.StageScreen
import io.github.patxibocos.roadcyclingdata.ui.util.isoDateFormat

@Composable
fun RaceScreen(raceId: String, onStageSelected: (Stage) -> Unit) {
    Race(
        viewModel = hiltViewModel(),
        raceId = raceId,
        onStageSelected = onStageSelected,
    )
}

@Composable
internal fun Race(viewModel: RaceViewModel, raceId: String, onStageSelected: (Stage) -> Unit) {
    val race = viewModel.getRace(raceId).collectAsState(null).value
    if (race != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = race.name)
            if (race.stagesCount == 1) {
                StageScreen(raceId = raceId, stageId = race.stagesList.first().id)
            } else {
                StagesList(race.stagesList, onStageSelected)
            }
        }
    }
}

@Composable
internal fun StagesList(stages: List<Stage>, onStageSelected: (Stage) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = stages, key = Stage::getId, itemContent = { stage ->
            StageRow(stage, onStageSelected)
        })
    }
}

@Composable
internal fun StageRow(stage: Stage, onStageSelected: (Stage) -> Unit) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStageSelected(stage) },
        text = isoDateFormat(stage.startDate)
    )
}
