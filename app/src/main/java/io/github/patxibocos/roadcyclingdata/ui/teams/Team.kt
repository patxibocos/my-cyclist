package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.pcsscraper.protobuf.rider.RiderOuterClass.Rider
import io.github.patxibocos.roadcyclingdata.ui.data.TeamOfRiders
import io.github.patxibocos.roadcyclingdata.ui.preview.riderPreview
import io.github.patxibocos.roadcyclingdata.ui.preview.teamPreview

@Preview
@Composable
fun TeamScreen(
    teamOfRiders: TeamOfRiders? = TeamOfRiders(
        teamPreview,
        riders = listOf(riderPreview)
    ),
    onRiderSelected: (Rider) -> Unit = {}
) {
    if (teamOfRiders != null) {
        Column {
            Text(text = teamOfRiders.team.name)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = teamOfRiders.riders, key = Rider::getId, itemContent = { rider ->
                    Text(
                        text = rider.lastName,
                        modifier = Modifier.clickable {
                            onRiderSelected(rider)
                        }
                    )
                })
            }
        }
    }
}
