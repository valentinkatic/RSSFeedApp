package com.katic.rssfeedapp.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.katic.rssfeedapp.R
import com.katic.rssfeedapp.appComponent
import com.katic.rssfeedapp.databinding.ActivityHomeBinding
import com.katic.rssfeedapp.utils.viewModelProvider

class HomeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FROM_NOTIFICATION = "FROM_NOTIFICATION"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    private val viewModel by viewModelProvider { appComponent.homeViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fromNotification = intent.getBooleanExtra(EXTRA_FROM_NOTIFICATION, false)
        viewModel.refreshFeed(showNotificationAfter = !fromNotification)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_container)

        binding.bottomNav.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.channelsScreen, R.id.favoritesScreen))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_container)
        return navController.navigateUp(appBarConfiguration)
    }
}