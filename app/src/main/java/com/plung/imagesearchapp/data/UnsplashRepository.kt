/*
 * *
 *  * Created by bongo on 1/22/22, 12:54 AM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 12:23 AM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.plung.imagesearchapp.api.UnsplashApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(private val unsplashApi: UnsplashApi) {

    fun getSearchResults(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UnsplashPagingSource(unsplashApi, query) }
        ).liveData
}