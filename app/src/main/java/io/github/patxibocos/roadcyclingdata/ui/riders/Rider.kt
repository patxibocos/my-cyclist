package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.pcsscraper.protobuf.team.TeamOuterClass.Team
import io.github.patxibocos.roadcyclingdata.ui.data.RiderOfTeam
import io.github.patxibocos.roadcyclingdata.ui.preview.riderPreview
import io.github.patxibocos.roadcyclingdata.ui.preview.teamPreview
import kotlinx.coroutines.flow.Flow

@Composable
internal fun RiderScreen(
    riderOfTeamFlow: Flow<RiderOfTeam>,
    onTeamSelected: (Team) -> Unit
) {
    val riderOfTeam by riderOfTeamFlow.collectAsState(initial = null)
    riderOfTeam?.let {
        Rider(it, onTeamSelected)
    }
}

@Preview
@Composable
private fun Rider(
    riderOfTeam: RiderOfTeam = RiderOfTeam(riderPreview, teamPreview),
    onTeamSelected: (Team) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = riderOfTeam.rider.lastName)
        Text(
            text = riderOfTeam.team.name,
            modifier = Modifier.clickable {
                onTeamSelected(riderOfTeam.team)
            }
        )
    }
}
