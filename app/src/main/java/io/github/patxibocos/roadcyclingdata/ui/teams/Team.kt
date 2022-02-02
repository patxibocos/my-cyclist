package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import io.github.patxibocos.pcsscraper.protobuf.RiderOuterClass.Rider
import io.github.patxibocos.roadcyclingdata.ui.data.TeamOfRiders
import io.github.patxibocos.roadcyclingdata.ui.preview.riderPreview
import io.github.patxibocos.roadcyclingdata.ui.preview.teamPreview

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
internal fun TeamScreen(
    teamOfRiders: TeamOfRiders = TeamOfRiders(
        teamPreview,
        listOf(riderPreview)
    ),
    onRiderSelected: (Rider) -> Unit = {}
) {
    Column {
        Text(text = teamOfRiders.team.name)
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            cells = GridCells.Adaptive(75.dp),
        ) {
            items(items = teamOfRiders.riders) { rider ->
                RiderRow(rider = rider, onRiderSelected = onRiderSelected)
            }
        }
    }
}

@Composable
private fun RiderRow(rider: Rider, onRiderSelected: (Rider) -> Unit) {
    Image(
        modifier = Modifier
            .clickable { onRiderSelected(rider) }
            .padding(5.dp)
            .requiredSize(75.dp)
            .clip(RoundedCornerShape(100)),
        painter = rememberImagePainter(
            data = rider.photo,
        ),
        alignment = Alignment.TopCenter,
        contentScale = ContentScale.Crop,
        contentDescription = null,
    )
}
