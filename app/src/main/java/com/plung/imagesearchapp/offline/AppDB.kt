/*
 * *
 *  * Created by bongo on 1/22/22, 8:16 PM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 8:16 PM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.offline

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.plung.imagesearchapp.data.UnsplashPhoto

@Database(entities = [UnsplashPhoto::class, UnsplashRemoteKey::class], version = 1, exportSchema = false)
@TypeConverters(DbTypeConverter::class)
abstract class AppDB: RoomDatabase() {
    abstract fun photosDao(): PhotosDao
}