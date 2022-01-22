/*
 * *
 *  * Created by bongo on 1/22/22, 10:24 PM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 10:24 PM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.offline

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UnsplashRemoteKey(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prev: Int?,
    val next: Int?
)