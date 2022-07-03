/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.stages

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.ui.preview.racePreview
import io.github.patxibocos.mycyclist.ui.preview.stagePreview
import io.github.patxibocos.mycyclist.ui.util.SmallTopAppBar
import io.github.patxibocos.mycyclist.ui.util.isoFormat
import io.github.patxibocos.mycyclist.ui.util.rememberFlowWithLifecycle

@Composable
fun StageRoute(onBackPressed: () -> Unit = {}, viewModel: StageViewModel = hiltViewModel()) {
    val stateViewState by viewModel.stageViewState.rememberFlowWithLifecycle(
        viewModel.viewModelScope,
        StageViewState.Empty
    )
    StageScreen(stateViewState, onBackPressed)
}

@Preview
@Composable
private fun StageScreen(
    stageViewState: StageViewState = StageViewState(racePreview, stagePreview),
    onBackPressed: () -> Unit = {}
) {
    Column {
        SmallTopAppBar(
            title = "${stageViewState.race?.name} - ${stageViewState.stageNumber}",
            onBackPressed
        )
        if (stageViewState.stage != null) {
            Text(text = isoFormat(stageViewState.stage.startDateTime))
            Text(text = stageViewState.stage.distance.toString())
            stageViewState.ridersResult.forEachIndexed { i, riderResult ->
                Text("${i + 1}. ${riderResult.rider.fullName()}")
            }
        }
    }
}
