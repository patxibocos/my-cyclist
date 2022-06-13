package io.github.patxibocos.mycyclist.ui.riders

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
    riders: RidersViewState.Riders = RidersViewState.Riders.ByLastName(
        mapOf(
            riderPreview.lastName.first() to listOf(
                riderPreview
            )
        )
    ),
    showSearch: Boolean = false,
    searchQuery: String = "",
    onRiderSearched: (String) -> Unit = {},
    onRiderSelected: (Rider) -> Unit = {},
    onSortingSelected: (Sorting) -> Unit = {},
    reselectedScreen: State<Screen?> = mutableStateOf(null),
    onReselectedScreenConsumed: () -> Unit = {},
    onToggled: () -> Unit = {},
) {
    Column {
        val focusManager = LocalFocusManager.current
        TopAppBar(
            riders.sorting,
            searchQuery,
            showSearch,
            focusManager,
            onSortingSelected,
            onRiderSearched,
            onToggled = onToggled,
        )
        Spacer(modifier = Modifier.height(10.dp))
        RidersList(
            ridersState = riders,
            onRiderSelected = {
                focusManager.clearFocus()
                onRiderSelected(it)
            },
            screenReselected = reselectedScreen,
            onReselectedScreenConsumed = onReselectedScreenConsumed
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TopAppBar(
    sorting: Sorting,
    searchQuery: String,
    showSearch: Boolean,
    focusManager: FocusManager,
    onSortingSelected: (Sorting) -> Unit,
    onSearched: (String) -> Unit,
    onToggled: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var showKeyboard by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = {
            AnimatedContent(showSearch) {
                if (it) {
                    var searchFieldValue by remember { mutableStateOf(TextFieldValue(searchQuery)) }
                    TextField(
                        value = searchFieldValue,
                        onValueChange = {
                            searchFieldValue = it
                            onSearched(it.text)
                        },
                        placeholder = {
                            Text(stringResource(R.string.riders_search))
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Words,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search,
                        ),
                        keyboardActions = KeyboardActions(onSearch = {
                            focusManager.clearFocus()
                        }),
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                    if (showKeyboard) {
                        showKeyboard = false
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    }
                } else {
                    LaunchedEffect(Unit) {
                        focusManager.clearFocus()
                    }
                    Text(text = stringResource(R.string.riders_title))
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                if (!showSearch) {
                    showKeyboard = true
                }
                onToggled()
            }) {
                val icon = if (showSearch) Icons.Outlined.Close else Icons.Outlined.Search
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        actions = {
            Box {
                var sortingOptionsVisible by remember { mutableStateOf(false) }
                IconButton(onClick = { sortingOptionsVisible = true }) {
                    Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
                }
                SortingMenu(
                    expanded = sortingOptionsVisible,
                    selectedSorting = sorting,
                    onSortingSelected = { sorting ->
                        sortingOptionsVisible = false
                        onSortingSelected(sorting)
                    },
                    onDismissed = { sortingOptionsVisible = false }
                )
            }
        },
    )
}

@Composable
private fun SortingMenu(
    expanded: Boolean,
    selectedSorting: Sorting,
    onSortingSelected: (Sorting) -> Unit,
    onDismissed: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissed
    ) {
        DropdownMenuItem(
            onClick = {
                onSortingSelected(Sorting.LastName)
            },
            enabled = selectedSorting != Sorting.LastName,
            text = {
                Text(stringResource(R.string.riders_sort_name))
            }
        )
        DropdownMenuItem(
            onClick = {
                onSortingSelected(Sorting.Team)
            },
            enabled = selectedSorting != Sorting.Team,
            text = {
                Text(stringResource(R.string.riders_sort_team))
            }
        )
        DropdownMenuItem(
            onClick = {
                onSortingSelected(Sorting.Country)
            },
            enabled = selectedSorting != Sorting.Country,
            text = {
                Text(stringResource(R.string.riders_sort_country))
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun RidersList(
    ridersState: RidersViewState.Riders,
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
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        state = lazyListState,
    ) {
        when (ridersState) {
            is RidersViewState.Riders.ByLastName -> {
                ridersState.riders.forEach { (letter, riders) ->
                    stickyHeader {
                        Text(text = letter.toString())
                    }
                    items(riders, key = Rider::id) { rider ->
                        RiderRow(rider, onRiderSelected)
                    }
                }
            }
            is RidersViewState.Riders.ByTeam -> {
                ridersState.riders.forEach { (team, riders) ->
                    stickyHeader {
                        Text(text = team.name)
                    }
                    items(riders, key = Rider::id) { rider ->
                        RiderRow(rider, onRiderSelected)
                    }
                }
            }
            is RidersViewState.Riders.ByCountry -> {
                ridersState.riders.forEach { (country, riders) ->
                    stickyHeader {
                        Text(text = country)
                    }
                    items(riders, key = Rider::id) { rider ->
                        RiderRow(rider, onRiderSelected)
                    }
                }
            }
        }
    }
}

@Composable
internal fun RiderRow(
    rider: Rider,
    onRiderSelected: (Rider) -> Unit,
) {
    Column(modifier = Modifier.clickable { onRiderSelected(rider) }) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = rider.photo,
                modifier = Modifier
                    .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
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
                    style = MaterialTheme.typography.bodyLarge,
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
        style = MaterialTheme.typography.bodyLarge,
    )
}
