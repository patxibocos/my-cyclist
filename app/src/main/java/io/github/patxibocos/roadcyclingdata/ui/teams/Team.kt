package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.rider.RiderOuterClass.Rider

@Composable
fun TeamScreen(teamId: String) {
    Team(
        viewModel = hiltViewModel(),
        teamId = teamId
    )
}

@Composable
internal fun Team(viewModel: TeamViewModel, teamId: String) {
    val teamOfRiders = viewModel.getTeamOfRiders(teamId).collectAsState(null).value
    if (teamOfRiders != null) {
        Column {
            Text(text = teamOfRiders.team.name)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = teamOfRiders.riders, key = Rider::getId, itemContent = { rider ->
                    Text(text = rider.lastName)
                })
            }
        }
    }
}
