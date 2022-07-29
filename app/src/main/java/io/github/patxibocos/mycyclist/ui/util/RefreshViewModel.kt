package io.github.patxibocos.mycyclist.ui.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class RefreshViewModel @Inject constructor(
    val dataRepository: DataRepository
) : ViewModel() {

    private val _refreshing = MutableStateFlow(false)
    val state = _refreshing.asStateFlow()

    fun onRefreshed() {
        viewModelScope.launch {
            _refreshing.value = true
            delay(500 - measureTimeMillis { dataRepository.refresh() })
            _refreshing.value = false
        }
    }
}
