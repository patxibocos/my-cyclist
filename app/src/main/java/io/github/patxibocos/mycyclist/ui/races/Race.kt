package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.ui.preview.racePreview
import io.github.patxibocos.mycyclist.ui.stages.StageScreen
import io.github.patxibocos.mycyclist.ui.util.isoFormat

@Preview
@Composable
internal fun RaceScreen(race: Race = racePreview, onStageSelected: (Stage) -> Unit = {}) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = race.name)
        if (race.stages.size == 1) {
            StageScreen(race.stages.first())
        } else {
            StagesList(race.stages, onStageSelected)
        }
    }
}

@Composable
private fun StagesList(stages: List<Stage>, onStageSelected: (Stage) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = stages, key = Stage::id, itemContent = { stage ->
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
        text = isoFormat(stage.startDateTime)
    )
}
