package com.plung.imagesearchapp.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "photos")
@Parcelize
data class UnsplashPhoto(
    @PrimaryKey(autoGenerate = false) val id: String,
    var description: String? = null,
    @Embedded
    var urls: UnsplashPhotoUrls? = null
) : Parcelable {
    @Parcelize
    data class UnsplashPhotoUrls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String
    ) : Parcelable
}