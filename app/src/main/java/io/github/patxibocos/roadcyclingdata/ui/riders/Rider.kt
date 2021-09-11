package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RiderScreen(riderId: String) {
    Rider(
        viewModel = hiltViewModel(),
        riderId = riderId
    )
}

@Composable
internal fun Rider(viewModel: RiderViewModel, riderId: String) {
    val riderOfTeam = viewModel.getRiderOfTeam(riderId).collectAsState(null).value
    if (riderOfTeam != null) {
        Column {
            Text(text = riderOfTeam.rider.lastName)
            Text(text = riderOfTeam.team.name)
        }
    }
}
