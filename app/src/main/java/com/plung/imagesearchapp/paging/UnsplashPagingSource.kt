/*
 * *
 *  * Created by bongo on 1/22/22, 10:05 PM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 12:53 AM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.plung.imagesearchapp.api.UnsplashApi
import com.plung.imagesearchapp.data.UnsplashPhoto
import com.plung.imagesearchapp.ui.gallery.GalleryViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

class UnsplashPagingSource(
    private val unsplashApi: UnsplashApi,
    private val query: String
) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX

        return try {
            val response = unsplashApi.searchPhotos(query, position, params.loadSize)
            val photos: List<UnsplashPhoto>? = response.body()?.results

            LoadResult.Page(
                data = photos!!,
                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? =
        state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
}