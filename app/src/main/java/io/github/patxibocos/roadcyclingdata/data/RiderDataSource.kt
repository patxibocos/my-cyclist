package io.github.patxibocos.roadcyclingdata.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.patxibocos.roadcyclingdata.data.db.Rider
import io.github.patxibocos.roadcyclingdata.data.db.RiderDao

class RiderDataSource(
    private val ridersDao: RiderDao,
) : PagingSource<Int, Rider>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Rider> {
        val nextPage = params.key ?: 0
        val offset = nextPage * params.loadSize
        val riders = ridersDao.getRiders(limit = params.loadSize, offset = offset)

        return LoadResult.Page(
            data = riders,
            prevKey = if (nextPage == 0) null else nextPage - 1,
            nextKey = if (riders.isEmpty()) null else nextPage + 1,
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Rider>): Int? {
        return state.anchorPosition
    }

}