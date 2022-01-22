/*
 * *
 *  * Created by bongo on 1/22/22, 8:16 PM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 8:16 PM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.offline

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.plung.imagesearchapp.data.UnsplashPhoto

@Dao
interface PhotosDao {

    @Query("SELECT * FROM photos")
    fun fetchPhotos(): PagingSource<Int, UnsplashPhoto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<UnsplashPhoto>)

    @Query("DELETE FROM photos")
    suspend fun deleteAllPhotos()

    // Paging + roomDB cache

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(list: List<UnsplashRemoteKey>)

    @Query("SELECT * FROM UnsplashRemoteKey WHERE id = :id")
    suspend fun fetchPhotosRemoteKey(id: String): UnsplashRemoteKey?

    @Query("DELETE FROM UnsplashRemoteKey")
    suspend fun deleteAllRemoteKeys()
}