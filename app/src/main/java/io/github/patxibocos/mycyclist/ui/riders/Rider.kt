package io.github.patxibocos.mycyclist.ui.riders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.ui.data.Result
import io.github.patxibocos.mycyclist.ui.preview.riderPreview
import io.github.patxibocos.mycyclist.ui.preview.teamPreview
import io.github.patxibocos.mycyclist.ui.util.SmallTopAppBar
import io.github.patxibocos.mycyclist.ui.util.rememberFlowWithLifecycle

@Composable
internal fun RiderRoute(
    onTeamSelected: (Team) -> Unit = {},
    onBackPressed: () -> Unit = {},
    viewModel: RiderViewModel = hiltViewModel()
) {
    val riderViewState by viewModel.riderViewState.rememberFlowWithLifecycle(
        viewModel.viewModelScope,
        RiderViewState.Empty
    )
    RiderScreen(
        riderViewState = riderViewState,
        onTeamSelected = onTeamSelected,
        onBackPressed = onBackPressed
    )
}

@Preview
@Composable
internal fun RiderScreen(
    riderViewState: RiderViewState = RiderViewState(riderPreview, teamPreview),
    onTeamSelected: (Team) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    Column {
        SmallTopAppBar(title = riderViewState.rider?.lastName.toString(), onBackPressed)
        if (riderViewState.rider != null && riderViewState.team != null) {
            Text(text = riderViewState.rider.lastName)
            Text(
                text = riderViewState.team.name,
                modifier = Modifier.clickable {
                    onTeamSelected(riderViewState.team)
                }
            )
            riderViewState.currentParticipation?.let { currentParticipation ->
                Text(text = "Currently running ${currentParticipation.race.name}")
            }
            riderViewState.results.forEach { lastResult ->
                when (lastResult) {
                    is Result.RaceResult -> Text(text = "${lastResult.position} on ${lastResult.race.name}")
                    is Result.StageResult -> Text(text = "${lastResult.position} on stage ${lastResult.stageNumber} of ${lastResult.race.name}")
                }
            }
        }
    }
}
