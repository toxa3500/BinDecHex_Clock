package com.example.bindechexclock;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity; // Changed
import com.google.android.material.bottomnavigation.BottomNavigationView; // Changed

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    TimeManager timeManager = new TimeManager(); // Changed: Removed 'this'

    private Handler handler;
    private Runnable updateTimeRunnable;
    private static final long UPDATE_INTERVAL_MS = 1000; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_romans, R.id.navigation_stopwatch)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        handler = new Handler(Looper.getMainLooper());
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                timeManager.updateTime();
                handler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start the periodic updates when the activity becomes visible
        handler.post(updateTimeRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop the periodic updates when the activity is no longer visible
        handler.removeCallbacks(updateTimeRunnable);
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }
}