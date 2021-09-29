package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import io.github.patxibocos.pcsscraper.protobuf.rider.RiderOuterClass.Rider
import io.github.patxibocos.roadcyclingdata.ui.preview.riderPreview
import io.github.patxibocos.roadcyclingdata.ui.util.CustomCircleCropTransformation
import io.github.patxibocos.roadcyclingdata.ui.util.getCountryEmoji

@Preview
@Composable
fun RidersScreen(
    riders: List<Rider> = listOf(riderPreview),
    onRiderSearched: (String) -> Unit = {},
    onRiderSelected: (Rider) -> Unit = {}
) {
    Column {
        var searchQuery by remember { mutableStateOf("") }
        TextField(modifier = Modifier.fillMaxWidth(), value = searchQuery, onValueChange = {
            searchQuery = it
            onRiderSearched(it)
        }, label = {
            Text("Search")
        })
        Spacer(modifier = Modifier.height(10.dp))
        RidersList(riders, onRiderSelected)
    }
}

@Composable
internal fun RidersList(riders: List<Rider>, onRiderSelected: (Rider) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(items = riders, key = Rider::getId) { rider ->
            RiderRow(rider, onRiderSelected)
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun RiderRow(
    rider: Rider,
    onRiderSelected: (Rider) -> Unit
) {
    Column(modifier = Modifier.clickable { onRiderSelected(rider) }) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Image(
            modifier = Modifier.size(75.dp),
            painter = rememberImagePainter(
                data = rider.photo,
                builder = { transformations(CustomCircleCropTransformation()) }
            ),
            contentDescription = null,
        )
        Box(
            modifier = Modifier
                .padding(end = 10.dp)
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
        ) {
            Text(
                text = "${rider.lastName.uppercase()} ${rider.firstName}",
                style = MaterialTheme.typography.body1,
            )
            Country(countryCode = rider.country, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}
}

@Composable
internal fun Country(countryCode: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "${getCountryEmoji(countryCode)} $countryCode",
        style = MaterialTheme.typography.body1,
    )
}
