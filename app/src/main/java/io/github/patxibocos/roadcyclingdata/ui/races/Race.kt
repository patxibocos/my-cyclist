package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Race
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Stage
import io.github.patxibocos.roadcyclingdata.ui.preview.racePreview
import io.github.patxibocos.roadcyclingdata.ui.stages.StageScreen
import io.github.patxibocos.roadcyclingdata.ui.util.isoDateFormat

@Preview
@Composable
internal fun RaceScreen(race: Race = racePreview, onStageSelected: (Stage) -> Unit = {}) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = race.name)
        if (race.stagesCount == 1) {
            StageScreen(race.stagesList.first())
        } else {
            StagesList(race.stagesList, onStageSelected)
        }
    }
}

@Composable
private fun StagesList(stages: List<Stage>, onStageSelected: (Stage) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = stages, key = Stage::getId, itemContent = { stage ->
            StageRow(stage, onStageSelected)
        })
    }
}

@Composable
private fun StageRow(stage: Stage, onStageSelected: (Stage) -> Unit) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStageSelected(stage) },
        text = isoDateFormat(stage.startDate)
    )
}
