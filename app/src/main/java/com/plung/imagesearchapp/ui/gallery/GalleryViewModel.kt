/*
 * *
 *  * Created by bongo on 1/22/22, 12:52 AM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 12:30 AM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.ui.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import com.plung.imagesearchapp.api.UnsplashApi
import com.plung.imagesearchapp.data.UnsplashRepository
import com.plung.imagesearchapp.offline.PhotosDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: UnsplashRepository,
    state: SavedStateHandle?
) : ViewModel() {
    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = "bangladesh" // i.e: cats, dogs, and so on
    }

    private val currentQuery = state?.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

    @ExperimentalPagingApi
    val photos = currentQuery?.switchMap { queryString ->
        repository.getSearchResults(queryString)
            .cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery?.value = query
    }
}