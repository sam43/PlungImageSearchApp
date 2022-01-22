/*
 * *
 *  * Created by bongo on 1/22/22, 9:25 PM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 9:25 PM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.data

import com.plung.imagesearchapp.api.Resource
import kotlinx.coroutines.flow.*


inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))

        try {
            saveFetchResult(fetch())
            query().map { Resource.Success(it) }
        } catch (throwable: Throwable) {
            query().map { Resource.Error(throwable, it) }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}