package io.github.patxibocos.roadcyclingdata.ui.stages

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun StageScreen(raceId: String, stageId: String) {
    Stage(
        viewModel = hiltViewModel(),
        raceId = raceId,
        stageId = stageId,
    )
}

@Composable
internal fun Stage(viewModel: StageViewModel, raceId: String, stageId: String) {
    val stage = viewModel.getStage(raceId, stageId).collectAsState(null).value
    if (stage != null) {
        Text(text = stage.distance.toString())
    }
}
