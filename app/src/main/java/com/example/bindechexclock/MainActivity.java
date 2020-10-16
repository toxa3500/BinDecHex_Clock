package com.example.bindechexclock;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bindechexclock.ui.dashboard.DashboardFragment;
import com.example.bindechexclock.ui.home.HomeFragment;
import com.example.bindechexclock.ui.notifications.NotificationsFragment;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String[] toxa16 = {"0", "1", "2", "3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTime();
                            }
                        });
                    }
                } catch (InterruptedException ignored) {
                }
            }
        };

        t.start();

    }


    @SuppressLint("SimpleDateFormat")
    private void getDate(){
        int hours =  Calendar.getInstance().getTime().getHours();
        int minutes = Calendar.getInstance().getTime().getMinutes();
        int seconds = Calendar.getInstance().getTime().getSeconds();

        toxa16[0] = "Time in binary:" + "\n"
                + Integer.toString(hours, 2) + "\n"
                + Integer.toString(minutes, 2) + "\n"
                + Integer.toString(seconds, 2) + "\n";

        toxa16[1] =  "Time in decimal:" + "\n"
                + Integer.toString(hours, 10) + ":"
                + Integer.toString(minutes, 10) + ":"
                + Integer.toString(seconds, 10) + "\n";

        toxa16[2] =  "Time in hex:" + "\n"
                + Integer.toString(hours, 16) + ":"
                + Integer.toString(minutes, 16) + ":"
                + Integer.toString(seconds, 16) + "\n";

    }

    private void updateTime(){
        getDate();
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if(navHostFragment != null && navHostFragment.getChildFragmentManager() != null) {
            List<Fragment> fragmentList = navHostFragment.getChildFragmentManager().getFragments();
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof HomeFragment) {
                    HomeFragment s = (HomeFragment) fragment;
                    s.homeViewModel.setmText(toxa16[0]);
                    break;
                } else if (fragment instanceof DashboardFragment) {
                    DashboardFragment s = (DashboardFragment) fragment;
                    s.dashboardViewModel.setmText(toxa16[1]);
                    break;
                } else if (fragment instanceof NotificationsFragment) {
                    NotificationsFragment s = (NotificationsFragment) fragment;
                    s.notificationsViewModel.setmText(toxa16[2]);
                    break;
                }
            }
        }
    }

}