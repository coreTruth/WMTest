package com.example.myapplication.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.myapplication.data.Photo
import com.example.myapplication.network.ApiInterface
import kotlinx.coroutines.flow.Flow

class FlickerRepository(private val service: ApiInterface) {
    @OptIn(ExperimentalPagingApi::class)
    fun getSearchResultStream(query: String): Flow<PagingData<Photo>> {
        return Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
            )
        ) {
            FlickerPagingSource(service, query)
        }.flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 25
    }
}