package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TeamScreen(teamId: String) {
    Team(
        viewModel = hiltViewModel(),
        teamId = teamId
    )
}

@Composable
internal fun Team(viewModel: TeamViewModel, teamId: String) {
    val team = viewModel.getTeam(teamId).collectAsState(null).value
    if (team != null) {
        Text(text = team.name)
    }
}
