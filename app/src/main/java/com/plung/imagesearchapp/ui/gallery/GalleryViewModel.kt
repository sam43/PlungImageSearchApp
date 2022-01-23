/*
 * *
 *  * Created by bongo on 1/22/22, 12:52 AM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 12:30 AM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.ui.gallery

import androidx.lifecycle.*
import androidx.paging.*
import com.plung.imagesearchapp.api.UnsplashApi
import com.plung.imagesearchapp.data.UnsplashPhoto
import com.plung.imagesearchapp.data.UnsplashRepository
import com.plung.imagesearchapp.offline.PhotosDao
import com.plung.imagesearchapp.paging.UnsplashRemoteMediator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: UnsplashRepository,
    state: SavedStateHandle?
) : ViewModel() {

    @Inject
    lateinit var api: UnsplashApi

    @Inject
    lateinit var photosDao: PhotosDao

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = "bangladesh" // i.e: cats, dogs, and so on
    }

    private val currentQuery = state?.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

/*
    val photos = currentQuery?.switchMap { queryString ->
        repository.getSearchResults(queryString)
            .cachedIn(viewModelScope)
    }
*/

val pager = currentQuery?.switchMap { queryString ->
    Pager(
        PagingConfig(
            pageSize = 20
        ),
        remoteMediator = UnsplashRemoteMediator(api, photosDao, queryString)
    ) {
        photosDao.fetchPhotos()
    }.liveData.cachedIn(viewModelScope)
}

    // Experimental
    val pagerFlow = currentQuery?.asFlow()?.flatMapLatest { queryString ->
        Pager(
            PagingConfig(
                pageSize = 20
            ),
            remoteMediator = UnsplashRemoteMediator(api, photosDao, queryString)
        ) {
            photosDao.fetchPhotos()
        }.flow
    }

    fun searchPhotos(query: String) {
        currentQuery?.value = query
    }
}