/*
 * *
 *  * Created by bongo on 1/22/22, 12:54 AM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 12:23 AM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.plung.imagesearchapp.api.UnsplashApi
import com.plung.imagesearchapp.offline.PhotosDao
import com.plung.imagesearchapp.paging.UnsplashPagingSource
import com.plung.imagesearchapp.paging.UnsplashRemoteMediator
import com.plung.imagesearchapp.ui.gallery.GalleryViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(private val unsplashApi: UnsplashApi) {
    @ExperimentalPagingApi
    fun getSearchResults(query: String): LiveData<PagingData<UnsplashPhoto>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                5,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UnsplashPagingSource(unsplashApi, query) }
        ).liveData
}