/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.ui.util.SmallTopAppBar
import io.github.patxibocos.mycyclist.ui.util.isoFormat
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun RaceRoute(
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onParticipationsClicked: (Race) -> Unit,
    onBackPressed: () -> Unit = {},
    topBarProvider: (@Composable () -> Unit) -> Unit,
    viewModel: RaceViewModel = hiltViewModel(),
) {
    val raceViewState by viewModel.raceViewState.collectAsState()
    RaceScreen(
        raceViewState = raceViewState,
        onRiderSelected = onRiderSelected,
        onTeamSelected = onTeamSelected,
        onResultsModeChanged = viewModel::onResultsModeChanged,
        onClassificationTypeChanged = viewModel::onClassificationTypeChanged,
        onStageSelected = viewModel::onStageSelected,
        onParticipationsClicked = onParticipationsClicked,
        onBackPressed = onBackPressed,
        topBarProvider = topBarProvider,
    )
}

@Composable
internal fun RaceScreen(
    raceViewState: RaceViewState,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onResultsModeChanged: (ResultsMode) -> Unit,
    onClassificationTypeChanged: (ClassificationType) -> Unit,
    onStageSelected: (Int) -> Unit,
    onParticipationsClicked: (Race) -> Unit,
    onBackPressed: () -> Unit,
    topBarProvider: (@Composable () -> Unit) -> Unit,
) {
    topBarProvider {
        SmallTopAppBar(title = { Text(text = raceViewState.race?.name.toString()) }, onBackPressed)
    }
    Column {
        if (raceViewState.race != null) {
            Button(onClick = { onParticipationsClicked(raceViewState.race) }) {
                Text(text = "Participants")
            }
            if (raceViewState.race.stages.size == 1) {
                val stage = raceViewState.race.stages.first()
                SingleStage(
                    stage,
                    raceViewState.stagesResults[stage]!!,
                    onRiderSelected,
                    onTeamSelected,
                )
            } else {
                StagesList(
                    stages = raceViewState.race.stages,
                    stagesResults = raceViewState.stagesResults,
                    currentStageIndex = raceViewState.currentStageIndex,
                    resultsMode = raceViewState.resultsMode,
                    classificationType = raceViewState.classificationType,
                    onRiderSelected = onRiderSelected,
                    onTeamSelected = onTeamSelected,
                    onResultsModeChanged = onResultsModeChanged,
                    onClassificationTypeChanged = onClassificationTypeChanged,
                    onStageSelected = onStageSelected,
                )
            }
        }
    }
}

@Composable
private fun SingleStage(
    stage: Stage,
    results: Results,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
) {
    StageData(stage)
    Results(results, onRiderSelected, onTeamSelected)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StagesList(
    stages: List<Stage>,
    stagesResults: Map<Stage, Results>,
    currentStageIndex: Int,
    resultsMode: ResultsMode,
    classificationType: ClassificationType,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onResultsModeChanged: (ResultsMode) -> Unit,
    onClassificationTypeChanged: (ClassificationType) -> Unit,
    onStageSelected: (Int) -> Unit,
) {
    val pagerState =
        rememberPagerState(initialPage = currentStageIndex, pageCount = { stages.size })
    val coroutineScope = rememberCoroutineScope()
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        stages.forEachIndexed { index, _ ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    onStageSelected(index)
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
            ) {
                Text(text = "Stage ${index + 1}")
            }
        }
    }
    HorizontalPager(
        modifier = Modifier.fillMaxWidth(),
        state = pagerState,
        verticalAlignment = Alignment.Top,
    ) { page ->
        val stage = stages[page]
        Stage(
            stage = stage,
            stageResults = stagesResults[stage]!!,
            resultsMode = resultsMode,
            classificationType = classificationType,
            onResultsModeChanged = onResultsModeChanged,
            onClassificationTypeChanged = onClassificationTypeChanged,
            onRiderSelected = onRiderSelected,
            onTeamSelected = onTeamSelected,
        )
    }
}

@Composable
private fun StageData(stage: Stage) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Stage(
    stage: Stage,
    stageResults: Results,
    resultsMode: ResultsMode,
    classificationType: ClassificationType,
    onResultsModeChanged: (ResultsMode) -> Unit,
    onClassificationTypeChanged: (ClassificationType) -> Unit,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        StageData(stage)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ElevatedFilterChip(
                selected = resultsMode == ResultsMode.Stage,
                onClick = { onResultsModeChanged(ResultsMode.Stage) },
                label = {
                    Text(text = ResultsMode.Stage.toString())
                },
            )
            ElevatedFilterChip(
                selected = resultsMode == ResultsMode.General,
                onClick = { onResultsModeChanged(ResultsMode.General) },
                label = {
                    Text(text = ResultsMode.General.toString())
                },
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ClassificationType.values().forEach {
                ElevatedFilterChip(
                    selected = classificationType == it,
                    onClick = { onClassificationTypeChanged(it) },
                    label = {
                        Text(text = it.toString())
                    },
                )
            }
        }
        Results(stageResults, onRiderSelected, onTeamSelected)
    }
}

@Composable
private fun Results(
    results: Results,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
    ) {
        when (results) {
            is Results.RidersPointResult -> RidersPointResult(results, onRiderSelected)
            is Results.RidersPointsPerPlaceResult -> RidersPointsPerPlaceResult(
                results,
                onRiderSelected,
            )

            is Results.RidersTimeResult -> RidersTimeResult(results, onRiderSelected)
            is Results.TeamsTimeResult -> TeamsTimeResult(results, onTeamSelected)
        }
    }
}

@Composable
private fun RidersPointResult(
    results: Results.RidersPointResult,
    onRiderSelected: (Rider) -> Unit,
) {
    results.riders.forEachIndexed { i, (rider, points) ->
        Text(
            text = "${i + 1}. ${rider.fullName()} - $points",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRiderSelected(rider) },
        )
    }
}

@Composable
private fun RidersPointsPerPlaceResult(
    results: Results.RidersPointsPerPlaceResult,
    onRiderSelected: (Rider) -> Unit,
) {
    results.perPlaceResult.forEach { (place, riders) ->
        Text(text = "${place.name} - ${place.distance}")
        riders.forEachIndexed { i, (rider, points) ->
            Text(
                text = "${i + 1}. ${rider.fullName()} - $points",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRiderSelected(rider) },
            )
        }
        Divider(thickness = 8.dp)
    }
}

@Composable
private fun RidersTimeResult(
    results: Results.RidersTimeResult,
    onRiderSelected: (Rider) -> Unit,
) {
    results.riders.forEachIndexed { i, (rider, time) ->
        val duration = if (i == 0) {
            time.seconds.toString()
        } else {
            "+${(time - results.riders.first().time).seconds}"
        }
        Text(
            text = "${i + 1}. ${rider.fullName()} - $duration",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRiderSelected(rider) },
        )
    }
}

@Composable
private fun TeamsTimeResult(
    results: Results.TeamsTimeResult,
    onTeamSelected: (Team) -> Unit,
) {
    results.teams.forEachIndexed { i, (team, time) ->
        val duration = if (i == 0) {
            time.seconds.toString()
        } else {
            "+${(time - results.teams.first().time).seconds}"
        }
        Text(
            text = "${i + 1}. ${team.name} - $duration",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTeamSelected(team) },
        )
    }
}
