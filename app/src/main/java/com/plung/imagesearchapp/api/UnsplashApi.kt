package com.plung.imagesearchapp.api

import com.plung.imagesearchapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashApi {
    // API taken from https://unsplash.com/documentation#search-photos
    @Headers("Accept-Version: v1", "Authorization: Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}")
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): UnsplashResponse
}