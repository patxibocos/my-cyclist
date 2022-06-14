package io.github.patxibocos.mycyclist.ui.stages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.ui.preview.stagePreview
import io.github.patxibocos.mycyclist.ui.util.isoFormat
import io.github.patxibocos.mycyclist.ui.util.rememberFlowWithLifecycle

@Composable
fun StageRoute(viewModel: StageViewModel = hiltViewModel()) {
    val stateViewState by viewModel.stateViewState.rememberFlowWithLifecycle(
        viewModel.viewModelScope,
        StageViewState.Empty
    )
    StageScreen(stateViewState)
}

@Preview
@Composable
fun StageScreen(stageViewState: StageViewState = StageViewState(stagePreview)) {
    if (stageViewState.stage != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = isoFormat(stageViewState.stage.startDateTime))
            Text(text = stageViewState.stage.distance.toString())
        }
    }
}
