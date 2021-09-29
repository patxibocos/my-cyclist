package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.pcsscraper.protobuf.team.TeamOuterClass.Team
import io.github.patxibocos.roadcyclingdata.ui.data.RiderOfTeam
import io.github.patxibocos.roadcyclingdata.ui.preview.riderPreview
import io.github.patxibocos.roadcyclingdata.ui.preview.teamPreview

@Preview
@Composable
fun RiderScreen(
    riderOfTeam: RiderOfTeam? = RiderOfTeam(riderPreview, teamPreview),
    onTeamSelected: (Team) -> Unit = {}
) {
    if (riderOfTeam != null) {
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
}
