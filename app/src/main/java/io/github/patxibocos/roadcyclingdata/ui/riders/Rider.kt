package io.github.patxibocos.roadcyclingdata.ui.riders

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
    val rider = viewModel.getRider(riderId).collectAsState(null).value
    if (rider != null) {
        Text(text = rider.lastName)
    }
}
