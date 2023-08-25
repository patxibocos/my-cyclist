/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.riders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.ui.data.Result
import io.github.patxibocos.mycyclist.ui.util.SmallTopAppBar

@Composable
internal fun RiderRoute(
    onTeamSelected: (Team) -> Unit,
    onRaceSelected: (Race) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: RiderViewModel = hiltViewModel(),
) {
    val riderViewState by viewModel.riderViewState.collectAsState()
    RiderScreen(
        riderViewState = riderViewState,
        onTeamSelected = onTeamSelected,
        onRaceSelected = onRaceSelected,
        onStageSelected = onStageSelected,
        onBackPressed = onBackPressed,
    )
}

@Composable
internal fun RiderScreen(
    riderViewState: RiderViewState,
    onTeamSelected: (Team) -> Unit,
    onRaceSelected: (Race) -> Unit,
    onStageSelected: (Race, Stage) -> Unit,
    onBackPressed: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SmallTopAppBar(
            title = { Text(text = riderViewState.rider?.lastName.toString()) },
            onBackPressed,
        )
        if (riderViewState.rider != null && riderViewState.team != null) {
            Text(text = riderViewState.rider.lastName)
            if (riderViewState.rider.uciRankingPosition > 0) {
                Text(text = "UCI Ranking: ${riderViewState.rider.uciRankingPosition}")
            }
            Text(
                text = riderViewState.team.name,
                modifier = Modifier.clickable {
                    onTeamSelected(riderViewState.team)
                },
            )
            riderViewState.currentParticipation?.let { currentParticipation ->
                Text(
                    text = "Currently running ${currentParticipation.race.name}",
                    modifier = Modifier.clickable {
                        onRaceSelected(riderViewState.currentParticipation.race)
                    },
                )
            }
            riderViewState.results.forEach { lastResult ->
                when (lastResult) {
                    is Result.RaceResult -> Text(
                        text = "${lastResult.position} on ${lastResult.race.name}",
                        modifier = Modifier.clickable {
                            onRaceSelected(lastResult.race)
                        },
                    )

                    is Result.StageResult -> Text(
                        text = "${lastResult.position} on stage ${lastResult.stageNumber} of ${lastResult.race.name}",
                        modifier = Modifier.clickable {
                            onStageSelected(lastResult.race, lastResult.stage)
                        },
                    )
                }
            }
        }
    }
}
