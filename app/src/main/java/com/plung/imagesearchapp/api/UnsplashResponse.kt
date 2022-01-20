package com.plung.imagesearchapp.api

import com.plung.imagesearchapp.data.UnsplashPhoto

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)