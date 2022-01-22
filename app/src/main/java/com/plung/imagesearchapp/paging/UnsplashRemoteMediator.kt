/*
 * *
 *  * Created by bongo on 1/22/22, 10:05 PM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 10:05 PM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.plung.imagesearchapp.api.UnsplashApi
import com.plung.imagesearchapp.data.UnsplashPhoto
import com.plung.imagesearchapp.data.UnsplashRepository
import com.plung.imagesearchapp.offline.PhotosDao
import com.plung.imagesearchapp.offline.UnsplashRemoteKey
import java.io.InvalidObjectException
import javax.inject.Inject

@ExperimentalPagingApi
class UnsplashRemoteMediator(
    private val api: UnsplashApi,
    private val photosDao: PhotosDao,
    private val query: String
    ): RemoteMediator<Int, UnsplashPhoto>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UnsplashPhoto>
    ): MediatorResult {
        val initialPage = 1
        return try {
            val page = when(loadType) {
                LoadType.APPEND -> {
                    val remoteKey = getLastRemoteKey(state) ?: throw InvalidObjectException("Invalid Object")
                    remoteKey.next ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.REFRESH -> {
                    val remoteKey = getClosestRemoteKeys(state)
                    remoteKey?.next?.minus(1) ?: initialPage
                }
            }
            // network request
            val response = api.searchPhotos(query, page, state.config.pageSize)
            val endOfPagination = response.body()?.results?.size!! < state.config.pageSize

            if (response.isSuccessful) {
                response.body()?.let {
                    // clear data
                    if (loadType == LoadType.REFRESH) {
                        photosDao.deleteAllPhotos()
                        photosDao.deleteAllRemoteKeys()
                    }

                    val prev = if (page == initialPage) null else page - 1
                    val next = if (endOfPagination) null else page + 1

                    val list = response.body()?.results?.map { model ->
                        UnsplashRemoteKey(id = model.id, prev, next)
                    }

                    // list of remote keys
                    if(list != null)
                        photosDao.insertAllRemoteKeys(list)

                    //insert into the roomDB
                    photosDao.insertPhotos(it.results)
                }

                MediatorResult.Success(endOfPagination)
            } else
                MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getClosestRemoteKeys(state: PagingState<Int, UnsplashPhoto>): UnsplashRemoteKey? =
        state.anchorPosition?.let {
            state.closestItemToPosition(it)?.let { model ->
                photosDao.fetchPhotosRemoteKey(model.id)
            }
        }

    private suspend fun getLastRemoteKey(state: PagingState<Int, UnsplashPhoto>): UnsplashRemoteKey? =
        state.anchorPosition?.let {
            state.lastItemOrNull()?.let { model ->
                photosDao.fetchPhotosRemoteKey(model.id)
            }
        }
}