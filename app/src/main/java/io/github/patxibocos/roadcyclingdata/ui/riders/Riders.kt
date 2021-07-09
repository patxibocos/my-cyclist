package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.roadcyclingdata.data.db.Rider

@Composable
fun RidersScreen() {
    Riders(
        viewModel = hiltViewModel(),
    )
}

@Composable
internal fun Riders(
    viewModel: RidersViewModel
) {
    Column {
        var searchQuery by remember { mutableStateOf("") }
        TextField(modifier = Modifier.fillMaxWidth(), value = searchQuery, onValueChange = {
            searchQuery = it
            viewModel.onSearched(it)
        }, label = {
            Text("Search")
        })
        val riders by viewModel.riders.collectAsState()
        RidersList(riders)
    }
}

@Composable
internal fun RidersList(riders: List<Rider>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(riders) { rider ->
            RiderRow(rider)
        }
    }
}

@Composable
internal fun RiderRow(rider: Rider) {
    Card(
        shape = RoundedCornerShape(3.dp),
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
        elevation = 10.dp,
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = "${rider.firstName} ${rider.lastName}",
            style = MaterialTheme.typography.body1,
        )
    }
}