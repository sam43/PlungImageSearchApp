/*
 * *
 *  * Created by bongo on 1/22/22, 10:52 PM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 10:52 PM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.offline

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.plung.imagesearchapp.data.UnsplashPhotoUrls

class DbTypeConverter {

    @TypeConverter
    fun urlsToString(source: UnsplashPhotoUrls): String {
        return Gson().toJson(source)
    }

    @TypeConverter
    fun String.stringToUrls(): UnsplashPhotoUrls {
        return Gson().fromJson(this, UnsplashPhotoUrls::class.java)
    }
/*
    @TypeConverter
    fun <T> sourceToString(source: T): String {
        return gson.toJson(source)
    }

    @TypeConverter
    inline fun <reified T> String.stringToSource(): T {
        return gson.fromJson(this, T::class.java)
    }*/
}