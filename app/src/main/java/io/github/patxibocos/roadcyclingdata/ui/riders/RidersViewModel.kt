package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.RiderSource
import io.github.patxibocos.roadcyclingdata.data.db.Rider
import io.github.patxibocos.roadcyclingdata.data.db.RiderDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RidersViewModel @Inject constructor(private val riderDao: RiderDao) : ViewModel() {

    lateinit var riders: Flow<PagingData<Rider>>

    init {
        reloadRiders()
    }

    private fun reloadRiders(query: String = "") {
        this.riders = Pager(PagingConfig(pageSize = 20)) {
            RiderSource(riderDao, query)
        }.flow
    }

    fun onSearched(query: String) {
        reloadRiders(query.trim())
    }

}