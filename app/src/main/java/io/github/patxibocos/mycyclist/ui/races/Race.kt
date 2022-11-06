/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.ui.preview.racePreview
import io.github.patxibocos.mycyclist.ui.util.SmallTopAppBar
import io.github.patxibocos.mycyclist.ui.util.isoFormat
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun RaceRoute(
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onBackPressed: () -> Unit = {},
    viewModel: RaceViewModel = hiltViewModel()
) {
    val raceViewState by viewModel.raceViewState.collectAsState()
    RaceScreen(
        raceViewState = raceViewState,
        onRiderSelected = onRiderSelected,
        onTeamSelected = onTeamSelected,
        onResultsModeChanged = viewModel::onResultsModeChanged,
        onStageSelected = viewModel::onStageSelected,
        onBackPressed = onBackPressed
    )
}

@Composable
private fun RaceScreen(
    raceViewState: RaceViewState = RaceViewState(
        racePreview,
        0,
        ResultsMode.StageResults,
        emptyMap()
    ),
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onResultsModeChanged: (ResultsMode) -> Unit,
    onStageSelected: (Int) -> Unit,
    onBackPressed: () -> Unit
) {
    Column {
        SmallTopAppBar(title = raceViewState.race?.name.toString(), onBackPressed)
        if (raceViewState.race != null) {
            if (raceViewState.race.stages.size == 1) {
                SingleStage(raceViewState.race.stages.first())
            } else {
                StagesList(
                    raceViewState.race.stages,
                    raceViewState.stageResults,
                    raceViewState.currentStageIndex,
                    raceViewState.resultsMode,
                    onRiderSelected,
                    onTeamSelected,
                    onResultsModeChanged,
                    onStageSelected
                )
            }
        }
    }
}

@Composable
private fun SingleStage(stage: Stage) {
    Text(text = isoFormat(stage.startDateTime))
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun StagesList(
    stages: List<Stage>,
    stageResults: Map<Stage, StageResults>,
    currentStageIndex: Int,
    resultsMode: ResultsMode,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onResultsModeChanged: (ResultsMode) -> Unit,
    onStageSelected: (Int) -> Unit
) {
    val pagerState = rememberPagerState(currentStageIndex)
    val coroutineScope = rememberCoroutineScope()
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        stages.forEachIndexed { index, _ ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    onStageSelected(index)
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                }
            ) {
                Text(text = "Stage ${index + 1}")
            }
        }
    }
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
        count = stages.size,
        verticalAlignment = Alignment.Top
    ) { page ->
        Stage(
            stage = stages[page],
            stageResults[stages[page]]!!,
            resultsMode,
            onRiderSelected = onRiderSelected,
            onTeamSelected = onTeamSelected,
            onResultsModeChanged = onResultsModeChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Stage(
    stage: Stage,
    stageResults: StageResults,
    resultsMode: ResultsMode,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onResultsModeChanged: (ResultsMode) -> Unit
) {
    Column {
        Text(text = isoFormat(stage.startDateTime))
        if (stage.departure.isNotEmpty() && stage.arrival.isNotEmpty()) {
            Text(text = "${stage.departure} - ${stage.arrival}")
        }
        if (stage.distance > 0) {
            Text(text = "${stage.distance} km")
        }
        if (stage.profileType != null) {
            Text(text = stage.profileType.toString())
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ElevatedFilterChip(
                selected = resultsMode == ResultsMode.StageResults,
                onClick = { onResultsModeChanged(ResultsMode.StageResults) },
                label = {
                    Text(text = ResultsMode.StageResults.toString())
                }
            )
            ElevatedFilterChip(
                selected = resultsMode == ResultsMode.GcResults,
                onClick = { onResultsModeChanged(ResultsMode.GcResults) },
                label = {
                    Text(text = ResultsMode.GcResults.toString())
                }
            )
        }
        val results = when (resultsMode) {
            ResultsMode.StageResults -> stageResults.result
            ResultsMode.GcResults -> stageResults.gcResult
        }
        if (results.isNotEmpty()) {
            ParticipantResults(results, onRiderSelected, onTeamSelected)
        } else {
            Text(text = "Results not available yet")
        }
    }
}

@Composable
private fun ParticipantResults(
    results: List<ParticipantResult>,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit
) {
    results.forEachIndexed { i, participantResult ->
        val duration = if (i == 0) {
            participantResult.time.seconds.toString()
        } else {
            "+${(participantResult.time - results.first().time).seconds}"
        }
        when (participantResult) {
            is ParticipantResult.RiderResult -> Text(
                text = "${i + 1}. ${participantResult.rider.fullName()} - $duration",
                modifier = Modifier
                    .fillMaxWidth().run {
                        if (participantResult.rider.id.isNotEmpty()) {
                            clickable { onRiderSelected(participantResult.rider) }
                        } else {
                            this
                        }
                    }
            )
            is ParticipantResult.TeamResult -> Text(
                text = "${i + 1}. ${participantResult.team.name} - $duration",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTeamSelected(participantResult.team) }
            )
        }
    }
}
