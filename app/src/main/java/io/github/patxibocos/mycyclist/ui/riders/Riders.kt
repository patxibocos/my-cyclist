package io.github.patxibocos.mycyclist.ui.riders

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.patxibocos.mycyclist.R
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.ui.home.Screen
import io.github.patxibocos.mycyclist.ui.preview.riderPreview
import io.github.patxibocos.mycyclist.ui.util.getCountryEmoji

@Preview
@Composable
internal fun RidersScreen(
    uiRiders: UiState.UiRiders = UiState.UiRiders.RidersByLastName(
        mapOf(
            riderPreview.lastName.first() to listOf(
                riderPreview
            )
        )
    ),
    searchQuery: String = "",
    onRiderSearched: (String) -> Unit = {},
    onRiderSelected: (Rider) -> Unit = {},
    onSortingSelected: (Sorting) -> Unit = {},
    reselectedScreen: State<Screen?> = mutableStateOf(null),
    onReselectedScreenConsumed: () -> Unit = {},
) {
    Column {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery,
            onValueChange = onRiderSearched,
            label = {
                Text(stringResource(R.string.riders_search))
            }
        )
        val sortingOptionsVisible = remember { mutableStateOf(false) }
        Box {
            Button(onClick = { sortingOptionsVisible.value = true }) {
                Text(stringResource(R.string.riders_sort))
            }
            if (sortingOptionsVisible.value) {
                DropdownMenu(
                    expanded = sortingOptionsVisible.value,
                    onDismissRequest = { sortingOptionsVisible.value = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            sortingOptionsVisible.value = false
                            onSortingSelected(Sorting.LastName)
                        },
                        enabled = uiRiders.sorting != Sorting.LastName
                    ) { Text(stringResource(R.string.riders_sort_name)) }
                    DropdownMenuItem(
                        onClick = {
                            sortingOptionsVisible.value = false
                            onSortingSelected(Sorting.Team)
                        },
                        enabled = uiRiders.sorting != Sorting.Team
                    ) { Text(stringResource(R.string.riders_sort_team)) }
                    DropdownMenuItem(
                        onClick = {
                            sortingOptionsVisible.value = false
                            onSortingSelected(Sorting.Country)
                        },
                        enabled = uiRiders.sorting != Sorting.Country
                    ) { Text(stringResource(R.string.riders_sort_country)) }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        RidersList(uiRiders, onRiderSelected, reselectedScreen, onReselectedScreenConsumed)
        Spacer(modifier = Modifier.height(56.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun RidersList(
    uiRiders: UiState.UiRiders,
    onRiderSelected: (Rider) -> Unit,
    screenReselected: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(key1 = screenReselected.value) {
        if (screenReselected.value == Screen.Riders) {
            lazyListState.animateScrollToItem(0)
            onReselectedScreenConsumed()
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        state = lazyListState,
    ) {
        when (uiRiders) {
            is UiState.UiRiders.RidersByLastName -> {
                uiRiders.riders.forEach { (letter, riders) ->
                    stickyHeader {
                        Text(text = letter.toString())
                    }
                    items(riders, key = Rider::id) { rider ->
                        RiderRow(Modifier.animateItemPlacement(), rider, onRiderSelected)
                    }
                }
            }
            is UiState.UiRiders.RidersByTeam -> {
                uiRiders.riders.forEach { (team, riders) ->
                    stickyHeader {
                        Text(text = team.name)
                    }
                    items(riders, key = Rider::id) { rider ->
                        RiderRow(Modifier.animateItemPlacement(), rider, onRiderSelected)
                    }
                }
            }
            is UiState.UiRiders.RidersByCountry -> {
                uiRiders.riders.forEach { (country, riders) ->
                    stickyHeader {
                        Text(text = country)
                    }
                    items(riders, key = Rider::id) { rider ->
                        RiderRow(Modifier.animateItemPlacement(), rider, onRiderSelected)
                    }
                }
            }
        }
    }
}

@Composable
internal fun RiderRow(
    modifier: Modifier,
    rider: Rider,
    onRiderSelected: (Rider) -> Unit,
) {
    Column(modifier = modifier.clickable { onRiderSelected(rider) }) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = rider.photo,
                modifier = Modifier
                    .border(2.dp, MaterialTheme.colors.secondary, CircleShape)
                    .padding(2.dp)
                    .size(75.dp)
                    .clip(CircleShape),
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Crop,
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
