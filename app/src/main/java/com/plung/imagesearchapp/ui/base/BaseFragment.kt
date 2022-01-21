/*
 * *
 *  * Created by bongo on 1/22/22, 12:54 AM
 *  * Copyright (c) 2022. All rights reserved.
 *  * Last modified 1/21/22, 2:36 AM
 *  * email: scode43@gmail.com
 *
 */

package com.plung.imagesearchapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding


typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<V: ViewBinding>(
    private val inflate: Inflate<V>
) : Fragment() {

    private lateinit var _binding: V
    val binding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initObservers()
        _binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initDataState()
    }

    abstract fun initDataState()

    abstract fun initViews()

    abstract fun initObservers()

    fun toastThis(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
}