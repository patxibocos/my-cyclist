package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.isAvailable
import io.github.patxibocos.mycyclist.ui.preview.racePreview
import io.github.patxibocos.mycyclist.ui.util.isoFormat
import io.github.patxibocos.mycyclist.ui.util.rememberFlowWithLifecycle

@Composable
internal fun RaceRoute(
    onStageSelected: (Race, Stage) -> Unit = { _, _ -> },
    onBackPressed: () -> Unit = {},
    viewModel: RaceViewModel = hiltViewModel()
) {
    val raceViewState by viewModel.raceViewState.rememberFlowWithLifecycle(
        viewModel.viewModelScope,
        RaceViewState.Empty
    )
    RaceScreen(
        raceViewState = raceViewState,
        onStageSelected = onStageSelected,
        onBackPressed = onBackPressed
    )
}

@Preview
@Composable
internal fun RaceScreen(
    raceViewState: RaceViewState = RaceViewState(racePreview),
    onStageSelected: (Race, Stage) -> Unit = { _, _ -> },
    onBackPressed: () -> Unit = {}
) {
    Column {
        SmallTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            ),
            title = {
                Text(text = raceViewState.race?.name.toString())
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Filled.ArrowBack, null)
                }
            }
        )
        if (raceViewState.race != null) {
            if (raceViewState.race.stages.size == 1) {
                SingleStage(raceViewState.race.stages.first())
            } else {
                StagesList(raceViewState.race.stages) { stage ->
                    onStageSelected(raceViewState.race, stage)
                }
            }
        }
    }
}

@Composable
private fun SingleStage(stage: Stage) {
    Text(text = isoFormat(stage.startDateTime))
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
    Row {
        Text(
            modifier = Modifier.clickable { onStageSelected(stage) },
            text = isoFormat(stage.startDateTime)
        )
        if (stage.result.isAvailable()) {
            Icon(Icons.Outlined.Check, null)
        }
    }
}
