/*
 * *
 *  * Created by bongo on 1/22/22, 12:52 AM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/22/22, 12:41 AM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.plung.imagesearchapp.databinding.FragmentGalleryBinding
import com.plung.imagesearchapp.databinding.ItemUnsplashPhotoBinding
import com.plung.imagesearchapp.di.Constants
import com.plung.imagesearchapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.plung.imagesearchapp.R
import com.plung.imagesearchapp.data.UnsplashPhoto
import com.plung.imagesearchapp.ui.details.PhotoDetailsActivity
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.plung.imagesearchapp.offline.AppDB
import com.plung.imagesearchapp.paging.UnsplashPhotoLoadStateAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@AndroidEntryPoint
class GalleryFragment : BaseFragment<FragmentGalleryBinding>(FragmentGalleryBinding::inflate) {

    @Inject
    lateinit var appDB: AppDB

    private lateinit var adapter: UnsplashPhotoAdapter
    private val viewModel: GalleryViewModel by viewModels()
    private var spanCount: Int = 2
    private var shouldBackPressEnable: Boolean = true
    private val callback: OnBackPressedCallback =
        object : OnBackPressedCallback(shouldBackPressEnable /* enabled by default */) {
            override fun handleOnBackPressed() {
                if (shouldBackPressEnable) {
                    findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentSelf())
                    shouldBackPressEnable = false
                } else super.isEnabled()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun initViews() {
        postponeEnterTransition()
        adapter = UnsplashPhotoAdapter { itemView, photo ->
            navigateToDetailsPage(itemView, photo)
        }
        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager =
                GridLayoutManager(requireContext(), spanCount)
            recyclerView.itemAnimator = null
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() }
            )
            recyclerView.viewTreeObserver
                ?.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
            buttonRetry.setOnClickListener { adapter.retry() }
        }
        setHasOptionsMenu(true)
    }

    private fun navigateToDetailsPage(itemView: ItemUnsplashPhotoBinding, photo: UnsplashPhoto) {
        val intent = Intent(requireContext(), PhotoDetailsActivity::class.java)
        intent.putExtra("photo_url", photo.urls?.regular)
        val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(), Pair(itemView.imageView, "image_big")
        )
        startActivity(intent, options.toBundle())
    }

    override fun initDataState() {
        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.pager?.collectLatest {
                adapter.submitData(it)
            }
        }
/*        viewModel.pager?.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_photos, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                onCLickSearchAction(item)
                true
            }
            R.id.action_span_count_change -> {
                showDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateGridLayoutManager(spanCount: Int) {
        if (spanCount > 5) return
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        adapter.notifyItemRangeChanged(Constants.INITIAL, adapter.itemCount)
    }

    private fun onCLickSearchAction(searchItem: MenuItem) {
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerView.scrollToPosition(Constants.INITIAL)
                    viewModel.searchPhotos(query.trim())
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun showDialog() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog)
        builder.setTitle("Set Span Count")
        val input = EditText(requireContext())
        input.hint = "5 (no more than 5)"
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, _ ->
            updateGridLayoutManager(Integer.valueOf(input.text.toString()))
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}