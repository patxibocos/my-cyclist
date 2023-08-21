package io.github.patxibocos.mycyclist.ui.preview

import android.content.res.Configuration
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.patxibocos.mycyclist.ui.races.ClassificationType
import io.github.patxibocos.mycyclist.ui.races.RaceScreen
import io.github.patxibocos.mycyclist.ui.races.RaceViewState
import io.github.patxibocos.mycyclist.ui.races.Results
import io.github.patxibocos.mycyclist.ui.races.ResultsMode
import io.github.patxibocos.mycyclist.ui.riders.RiderScreen
import io.github.patxibocos.mycyclist.ui.riders.RiderViewState
import io.github.patxibocos.mycyclist.ui.teams.TeamScreen
import io.github.patxibocos.mycyclist.ui.teams.TeamViewState
import io.github.patxibocos.mycyclist.ui.theme.AppTheme

@Composable
private fun Preview(content: @Composable () -> Unit) {
    AppTheme {
        Surface(tonalElevation = 2.dp) {
            CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
                content()
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RiderPreview() {
    Preview {
        RiderScreen(
            riderViewState = RiderViewState(
                rider = riderPreview,
                team = teamPreview,
                currentParticipation = null,
                pastParticipations = emptyList(),
                futureParticipations = emptyList(),
                results = emptyList(),
            ),
            onTeamSelected = {},
            onRaceSelected = {},
            onStageSelected = { _, _ -> },
            onBackPressed = {},
            topBarProvider = {},
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TeamPreview() {
    Preview {
        TeamScreen(
            teamViewState = TeamViewState(
                team = teamPreview,
                riders = listOf(riderPreview),
            ),
            onRiderSelected = {},
            onBackPressed = {},
            topBarProvider = {},
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RacePreview() {
    Preview {
        RaceScreen(
            raceViewState = RaceViewState(
                race = racePreview,
                currentStageIndex = 0,
                resultsMode = ResultsMode.Stage,
                classificationType = ClassificationType.Time,
                stagesResults = mapOf(
                    racePreview.stages.first() to Results.RidersTimeResult(
                        emptyList(),
                    ),
                ),
            ),
            onRiderSelected = {},
            onTeamSelected = {},
            onResultsModeChanged = {},
            onClassificationTypeChanged = {},
            onStageSelected = {},
            onParticipationsClicked = {},
            onBackPressed = {},
            topBarProvider = {},
        )
    }
}
