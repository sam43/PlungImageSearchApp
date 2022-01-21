package com.plung.imagesearchapp.ui.details

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.plung.imagesearchapp.R
import com.plung.imagesearchapp.databinding.FragmentDetailsBinding
import com.plung.imagesearchapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class DetailsFragment : BaseFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate) {

    private val args by navArgs<DetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_transition)
    }

    override fun initDataState() {
        // Adding this work here because the initViews method is getting bigger (naming convention might hurt)
        if (args.photoUrl == null) return
        binding.apply {
            Glide.with(requireContext())
                .load(args.photoUrl)
                .error(R.drawable.ic_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //progressBar.isVisible = false
                        startPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //progressBar.isVisible = false
                        startPostponedEnterTransition()
                        return false
                    }
                })
                .into(imageView)
        }
    }

    override fun initViews() {
        postponeEnterTransition()
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.shared_transition
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun initObservers() {}
}