package com.example.bindechexclock;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;

import com.example.bindechexclock.ui.dashboard.DashboardFragment;
import com.example.bindechexclock.ui.home.HomeFragment;
import com.example.bindechexclock.ui.notifications.NotificationsFragment;
import com.example.bindechexclock.ui.romans.RomansFragment;
import com.example.bindechexclock.ui.stopwatch.StopwatchFragment;

import java.util.Calendar;
import java.util.List;


public class TimeManager {

    public static String[] romans = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};

    public String binary;
    public String decimal;
    public String hex;
    public String roman;
    MainActivity mainActivity;

    public TimeManager(MainActivity mainActivity){
        binary = "";
        decimal = "";
        hex = "";
        roman = "";
        this.mainActivity = mainActivity;

    }


    @SuppressLint("SimpleDateFormat")
    public void getDate(){
        int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int seconds = Calendar.getInstance().get(Calendar.SECOND);


        binary = getTimeInString(hours, minutes, seconds, 2);
        decimal = getTimeInString(hours, minutes, seconds, 10);
        hex = getTimeInString(hours, minutes, seconds, 16);
        roman = getTimeInString(hours, minutes, seconds, 20);

    }

    private String getTimeInString (int hours, int minutes, int seconds, int radix) {
        switch (radix)
        {
//            return time in binary
            case 2:
                return Integer.toString(hours, 2) + "\n"
                        + Integer.toString(minutes, 2) + "\n"
                        + Integer.toString(seconds, 2) + "\n";
//            return time in decimal
            case 10:
                return  Integer.toString(hours, 10) + ":"
                        + Integer.toString(minutes, 10) + ":"
                        + Integer.toString(seconds, 10) + "\n";
//            return time in hex
            case 16:
                return Integer.toString(hours, 16) + ":"
                        + Integer.toString(minutes, 16) + ":"
                        + Integer.toString(seconds, 16) + "\n";
//            return time in roman
            case 20:
                return intToRomans(hours) + "\n"
                        + intToRomans(minutes) + "\n"
                        + intToRomans(seconds) + "\n";
            default:
                return "null";
        }
    }

    private String intToRomans(int i) {
        // can convert int into romans from 0 to 99
        if (i == 0) {
            return "0";
        }
        return romans[(i % 100) / 10 + 10] + romans[i % 10];
    }

    public String[] getTimeStringFromInt(int time) {
        int hours = time / 3600;
        int minutes = time % 3600 / 60;
        int seconds = time % 60;

        return new String[]{getStrFromResource(R.string.title_binary) + "\n" +
                getTimeInString(hours, minutes, seconds, 2) + "\n",
                getStrFromResource(R.string.title_decimal) + "  ->  " +
                getTimeInString(hours, minutes, seconds, 10) + "\n",
                getStrFromResource(R.string.title_hex) + "  ->  " +
                getTimeInString(hours, minutes, seconds, 16) + "\n",
                getStrFromResource(R.string.title_romans) + "\n" +
                getTimeInString(hours, minutes, seconds, 20)};

    }

    public String getStrFromResource(int r){
        return mainActivity.getResources().getString(r);
    }

    public void updateTime(){
        this.getDate();
        Fragment navHostFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        String prefix = getStrFromResource(R.string.time_in) + " ";
        if(navHostFragment != null && navHostFragment.getChildFragmentManager() != null) {
            List<Fragment> fragmentList = navHostFragment.getChildFragmentManager().getFragments();
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof HomeFragment) {
                    HomeFragment s = (HomeFragment) fragment;
                    s.homeViewModel.setmText(prefix + getStrFromResource(R.string.title_binary) + "\n" + this.binary);
                    break;
                } else if (fragment instanceof DashboardFragment) {
                    DashboardFragment s = (DashboardFragment) fragment;
                    s.dashboardViewModel.setmText(prefix  + getStrFromResource(R.string.title_decimal) + "\n" + this.decimal);
                    break;
                } else if (fragment instanceof NotificationsFragment) {
                    NotificationsFragment s = (NotificationsFragment) fragment;
                    s.notificationsViewModel.setmText(prefix + getStrFromResource(R.string.title_hex) + "\n" + this.hex);
                    break;
                } else if (fragment instanceof RomansFragment) {
                    RomansFragment s = (RomansFragment) fragment;
                    s.romansViewModel.setmText(prefix + getStrFromResource(R.string.title_romans) + "\n" + this.roman);
                    break;
                } else if (fragment instanceof StopwatchFragment) {
                    StopwatchFragment s = (StopwatchFragment) fragment;
                    s.increaseCounterByOne(this);
                }
            }
        }
    }

}
