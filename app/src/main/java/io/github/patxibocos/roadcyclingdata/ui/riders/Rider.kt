package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.team.TeamOuterClass.Team

@Composable
fun RiderScreen(riderId: String, onTeamSelected: (Team) -> Unit) {
    Rider(
        viewModel = hiltViewModel(),
        riderId = riderId,
        onTeamSelected = onTeamSelected
    )
}

@Composable
internal fun Rider(viewModel: RiderViewModel, riderId: String, onTeamSelected: (Team) -> Unit) {
    val riderOfTeam = viewModel.getRiderOfTeam(riderId).collectAsState(null).value
    if (riderOfTeam != null) {
        Column {
            Text(text = riderOfTeam.rider.lastName)
            Text(
                text = riderOfTeam.team.name,
                modifier = Modifier.clickable {
                    onTeamSelected(riderOfTeam.team)
                }
            )
        }
    }
}
