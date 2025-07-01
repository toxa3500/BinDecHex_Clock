package com.example.bindechexclock

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bindechexclock.databinding.ActivityMainBinding // Import view binding class

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val timeManager = TimeManager() // Public property, accessible by fragments

    private lateinit var handler: Handler
    private lateinit var updateTimeRunnable: Runnable

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

        handler = Handler(Looper.getMainLooper())
        updateTimeRunnable = object : Runnable {
            override fun run() {
                timeManager.updateTime()
                handler.postDelayed(this, UPDATE_INTERVAL_MS)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Start the periodic updates when the activity becomes visible
        handler.post(updateTimeRunnable)
    }

    override fun onStop() {
        super.onStop()
        // Stop the periodic updates when the activity is no longer visible
        handler.removeCallbacks(updateTimeRunnable)
    }

    // No need for getTimeManager() method, fragments can access the public timeManager property.
}
