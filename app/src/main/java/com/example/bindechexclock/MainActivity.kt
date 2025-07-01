package com.example.bindechexclock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bindechexclock.databinding.ActivityMainBinding // Import view binding class
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var timeManager: TimeManager // Injected by Hilt

    private var timeUpdateJob: Job? = null

    companion object {
        private const val UPDATE_INTERVAL_MS = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.navView // Access BottomNavigationView via binding

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_romans, R.id.navigation_stopwatch
            )
        )
        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun tickerFlow(period: Long) = flow {
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    override fun onStart() {
        super.onStart()
        // Start the periodic updates when the activity becomes visible
        timeUpdateJob?.cancel() // Cancel any existing job
        timeUpdateJob = tickerFlow(UPDATE_INTERVAL_MS)
            .onEach { timeManager.updateTime() }
            .launchIn(lifecycleScope)
    }

    override fun onStop() {
        super.onStop()
        // Stop the periodic updates when the activity is no longer visible
        timeUpdateJob?.cancel()
        timeUpdateJob = null
    }

    // No need for getTimeManager() method, fragments can access the public timeManager property.
}
