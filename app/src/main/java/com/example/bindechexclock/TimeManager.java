package com.example.bindechexclock;

import android.annotation.SuppressLint;
// Import LiveData and MutableLiveData (assuming AndroidX)
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;

public class TimeManager {

    public static final String[] ROMANS_LOOKUP = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};

    private final MutableLiveData<String> binaryTimeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> decimalTimeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> hexTimeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> romanTimeLiveData = new MutableLiveData<>();

    // Public getters for the LiveData (Fragments/ViewModels will observe these)
    public LiveData<String> getBinaryTimeLiveData() { return binaryTimeLiveData; }
    public LiveData<String> getDecimalTimeLiveData() { return decimalTimeLiveData; }
    public LiveData<String> getHexTimeLiveData() { return hexTimeLiveData; }
    public LiveData<String> getRomanTimeLiveData() { return romanTimeLiveData; }

    public TimeManager() {
        // Initialize with current time or default values
        updateTime();
    }

    // Renamed from getDate to avoid confusion, this is the main update method
    public void updateTime() {
        int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int seconds = Calendar.getInstance().get(Calendar.SECOND);

        // PostValue is used if this method can be called from a background thread.
        // If always called from the main thread (as with the Handler in MainActivity),
        // setValue() can be used. Using postValue for safety.
        binaryTimeLiveData.postValue(formatTimePart(hours, 2) + "\n" + formatTimePart(minutes, 2) + "\n" + formatTimePart(seconds, 2));
        decimalTimeLiveData.postValue(formatTimePart(hours, 10) + ":" + formatTimePart(minutes, 10) + ":" + formatTimePart(seconds, 10));
        hexTimeLiveData.postValue(formatTimePart(hours, 16) + ":" + formatTimePart(minutes, 16) + ":" + formatTimePart(seconds, 16));
        romanTimeLiveData.postValue(intToRomans(hours) + "\n" + intToRomans(minutes) + "\n" + intToRomans(seconds));
    }

    // Renamed from getTimeInString to be more specific, and simplified.
    // This method now just formats a single integer part of the time.
    private String formatTimePart(int timePart, int radix) {
        if (radix == 2 || radix == 10 || radix == 16) {
            String value = Integer.toString(timePart, radix);
            if (radix == 10 || radix == 16) { // Pad decimal and hex for consistent two digits
                if (timePart < radix && timePart < 10 && value.length() < 2) { // for hex, 10-15 are single char A-F
                     if (timePart < 10) return "0" + value; // only pad if less than 10 for hex
                }
                 if (radix == 10 && value.length() < 2) { // Pad for decimal
                    return "0" + value;
                }
            }
            return value.toUpperCase(); // Hex values are typically uppercase
        }
        return "N/A"; // Should not happen with current usage
    }


    // Made static as it doesn't depend on instance state.
    public static String intToRomans(int i) {
        if (i < 0 || i > 99) { // Added basic validation
            // Or handle more gracefully, e.g., return "N/A" or throw exception
            return (i == 0) ? "0" : "??"; // Handle 0 explicitly if needed, or invalid for others
        }
        if (i == 0) {
            return "0"; // Common request for 0 seconds/minutes
        }
        return ROMANS_LOOKUP[(i % 100) / 10 + 10] + ROMANS_LOOKUP[i % 10];
    }

    /**
     * Formats a total number of seconds into H:M:S components for different radices.
     * This method is now static and does not rely on instance members like mainActivity.
     * It returns an array of strings: [binary, decimal, hex, roman].
     * The calling code (e.g., StopwatchViewModel) will be responsible for adding prefixes or labels.
     */
    public static String[] formatDurationStrings(int totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        String binaryStr = formatSingleUnit(hours, 2) + "\n" + formatSingleUnit(minutes, 2) + "\n" + formatSingleUnit(seconds, 2);
        String decimalStr = formatSingleUnit(hours, 10) + ":" + formatSingleUnit(minutes, 10) + ":" + formatSingleUnit(seconds, 10);
        String hexStr = formatSingleUnit(hours, 16) + ":" + formatSingleUnit(minutes, 16) + ":" + formatSingleUnit(seconds, 16);
        String romanStr = intToRomans(hours) + "\n" + intToRomans(minutes) + "\n" + intToRomans(seconds);

        return new String[]{binaryStr, decimalStr, hexStr, romanStr};
    }

    // Helper for formatDurationStrings, similar to formatTimePart but static.
    private static String formatSingleUnit(int unit, int radix) {
        // Simplified version for stopwatch display, may need padding logic like formatTimePart
        String value = Integer.toString(unit, radix);
        if ((radix == 10 || radix == 16) && unit < 10 && value.length() < 2) {
             if (radix == 10 || (radix == 16 && unit < 10)) { // Pad for decimal and hex single digits
                 return "0" + value;
             }
        }
        return value.toUpperCase();
    }
}
