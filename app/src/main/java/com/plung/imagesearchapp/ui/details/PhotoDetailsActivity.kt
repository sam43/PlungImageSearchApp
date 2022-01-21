/*
 * *
 *  * Created by bongo on 1/22/22, 12:54 AM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/21/22, 7:50 AM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.ui.details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.plung.imagesearchapp.R
import com.plung.imagesearchapp.databinding.ActivityDetailsBinding
import com.plung.imagesearchapp.ui.utils.hideSystemUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoDetailsActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var photoUrl: String

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        hideSystemUI()
        retrieveIntent()
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        binding.apply {
            Glide.with(this@PhotoDetailsActivity)
                .load(photoUrl)
                .error(R.drawable.ic_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        startPostponedEnterTransition()
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        startPostponedEnterTransition()
                        progressBar.isVisible = false
                        return false
                    }
                })
                .into(imageView)
        }
    }

    private fun retrieveIntent() {
        photoUrl = if (intent.getStringExtra("photo_url") != null) intent.getStringExtra("photo_url").toString() else ""
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}