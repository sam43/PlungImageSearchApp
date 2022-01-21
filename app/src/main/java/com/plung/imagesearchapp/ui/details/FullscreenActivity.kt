package com.plung.imagesearchapp.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.plung.imagesearchapp.R
import com.plung.imagesearchapp.databinding.ActivityFullscreenBinding
import com.plung.imagesearchapp.ui.utils.hideSystemUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullscreenActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val args: FullscreenActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        hideSystemUI()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.app_navigation) as NavHostFragment
        navController = navHostFragment.findNavController()
        navController.setGraph(R.navigation.detail_nav_graph, args.toBundle())
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}