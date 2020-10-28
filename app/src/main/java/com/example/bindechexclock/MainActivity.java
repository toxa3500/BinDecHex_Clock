package com.example.bindechexclock;

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
import com.example.bindechexclock.ui.romans.RomansFragment;
import com.example.bindechexclock.ui.stopwatch.StopwatchFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TimeManager timeManager = new TimeManager(this);


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

    private void updateTime(){
        timeManager.getDate();
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        String prefix = getStrFromResource(R.string.time_in) + " ";
        if(navHostFragment != null && navHostFragment.getChildFragmentManager() != null) {
            List<Fragment> fragmentList = navHostFragment.getChildFragmentManager().getFragments();
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof HomeFragment) {
                    HomeFragment s = (HomeFragment) fragment;
                    s.homeViewModel.setmText(prefix + getStrFromResource(R.string.title_binary) + "\n" + timeManager.binary);
                    break;
                } else if (fragment instanceof DashboardFragment) {
                    DashboardFragment s = (DashboardFragment) fragment;
                    s.dashboardViewModel.setmText(prefix  + getStrFromResource(R.string.title_decimal) + "\n" + timeManager.decimal);
                    break;
                } else if (fragment instanceof NotificationsFragment) {
                    NotificationsFragment s = (NotificationsFragment) fragment;
                    s.notificationsViewModel.setmText(prefix + getStrFromResource(R.string.title_hex) + "\n" + timeManager.hex);
                    break;
                } else if (fragment instanceof RomansFragment) {
                    RomansFragment s = (RomansFragment) fragment;
                    s.romansViewModel.setmText(prefix + getStrFromResource(R.string.title_romans) + "\n" + timeManager.roman);
                    break;
                } else if (fragment instanceof StopwatchFragment) {
                    StopwatchFragment s = (StopwatchFragment) fragment;
                    s.increaseCounterByOne(timeManager);
                }
            }
        }
    }

    public String getStrFromResource(int r){
        return getResources().getString(r);
    }

}