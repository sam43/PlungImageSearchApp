/*
 * *
 *  * Created by bongo on 1/22/22, 9:27 PM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 9:27 PM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.api

// This class is not using right now but can use it in future
sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : Resource<T>(data, throwable)
}