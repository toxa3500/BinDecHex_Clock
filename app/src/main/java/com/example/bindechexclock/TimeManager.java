package com.example.bindechexclock;

import android.annotation.SuppressLint;

import java.util.Calendar;


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

    public String getTimeStringFromInt(int time) {
        int hours = time / 3600;
        int minutes = time % 3600 / 60;
        int seconds = time % 60;

        return getStrFromResource(R.string.title_binary) + "\n" +
                getTimeInString(hours, minutes, seconds, 2) + "\n" +
                getStrFromResource(R.string.title_decimal) + "  ->  " +
                getTimeInString(hours, minutes, seconds, 10) + "\n" +
                getStrFromResource(R.string.title_hex) + "  ->  " +
                getTimeInString(hours, minutes, seconds, 16) + "\n" +
                getStrFromResource(R.string.title_romans) + "\n" +
                getTimeInString(hours, minutes, seconds, 20);

    }

    public String getStrFromResource(int r){
        return mainActivity.getResources().getString(r);
    }

}
