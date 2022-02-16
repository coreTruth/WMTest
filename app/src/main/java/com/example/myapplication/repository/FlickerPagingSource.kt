package com.example.myapplication.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.data.Photo
import com.example.myapplication.network.ApiInterface

class FlickerPagingSource (
    private val backend: ApiInterface,
    private val query: String
) : PagingSource<Int, Photo>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Photo> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val response = backend.searchPhotos(text = query, page = nextPageNumber)
            var nextKey: Int? = null
            response?.photos?.let {
                if (it.page < it.pages)
                    nextKey = it.page + 1
            }
            return LoadResult.Page(
                data = response?.photos?.photo ?: emptyList(),
                prevKey = null, // Only paging forward.
                nextKey = nextKey
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}