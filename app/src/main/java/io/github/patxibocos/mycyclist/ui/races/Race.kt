package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.ui.preview.racePreview
import io.github.patxibocos.mycyclist.ui.stages.StageScreen
import io.github.patxibocos.mycyclist.ui.stages.StageViewState
import io.github.patxibocos.mycyclist.ui.util.isoFormat
import io.github.patxibocos.mycyclist.ui.util.rememberFlowWithLifecycle

@Composable
internal fun RaceRoute(
    onStageSelected: (Race, Stage) -> Unit = { _, _ -> },
    onBackPressed: () -> Unit = {},
    viewModel: RaceViewModel = hiltViewModel(),
) {
    val raceViewState by viewModel.raceViewState.rememberFlowWithLifecycle(
        viewModel.viewModelScope,
        RaceViewState.Empty
    )
    RaceScreen(
        raceViewState = raceViewState,
        onStageSelected = onStageSelected,
        onBackPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun RaceScreen(
    raceViewState: RaceViewState = RaceViewState(racePreview),
    onStageSelected: (Race, Stage) -> Unit = { _, _ -> },
    onBackPressed: () -> Unit = {},
) {
    Scaffold(topBar = {
        SmallTopAppBar(
            title = {
                Text(text = raceViewState.race?.name.toString())
            }, navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Filled.ArrowBack, null)
                }
            }
        )
    }) {
        if (raceViewState.race != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Text(text = raceViewState.race.name)
                if (raceViewState.race.stages.size == 1) {
                    StageScreen(StageViewState(raceViewState.race.stages.first()))
                } else {
                    StagesList(raceViewState.race.stages) { stage ->
                        onStageSelected(raceViewState.race, stage)
                    }
                }
            }
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
