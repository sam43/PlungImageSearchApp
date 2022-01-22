package com.plung.imagesearchapp.api

import androidx.room.Entity
import com.plung.imagesearchapp.data.UnsplashPhoto

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)