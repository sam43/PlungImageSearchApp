package com.plung.imagesearchapp.di

import com.plung.imagesearchapp.BuildConfig

object Constants {
    // Utils
    const val INITIAL: Int = 0

    // DI helper
    const val TAG: String = "HiltClass"
    const val DATABASE_NAME: String = "unsplash_db"
    const val READ_TIMEOUT = 30
    const val WRITE_TIMEOUT = 30
    const val CONNECTION_TIMEOUT = 10
    const val CACHE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB
}