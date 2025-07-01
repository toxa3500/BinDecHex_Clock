package com.example.bindechexclock.ui.stopwatch;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bindechexclock.R;
// Import the Kotlin top-level functions.
// The actual import might be com.example.bindechexclock.TimeUtilsKt if TimeUtils.kt
// doesn't have @file:JvmName annotation. For simplicity, we'll assume direct import works
// or the correct Kotlin generated class name is used.
import static com.example.bindechexclock.TimeUtilsKt.*;
import com.example.bindechexclock.ui.stopwatch.Event; // Added import for the new Kotlin Event class


// Helper class for one-time events (already defined, assuming it's kept as is)
// class Event<T> { ... }

public class StopwatchViewModel extends AndroidViewModel {

    // LiveData fields (as previously defined)
    private final MutableLiveData<String> _timeDisplay = new MutableLiveData<>();
    public final LiveData<String> timeDisplay = _timeDisplay;

    private final MutableLiveData<String> _startStopButtonText = new MutableLiveData<>();
    public final LiveData<String> startStopButtonText = _startStopButtonText;

    private final MutableLiveData<String> _modeButtonText = new MutableLiveData<>();
    public final LiveData<String> modeButtonText = _modeButtonText;

    private final MutableLiveData<String> _actionBarTitle = new MutableLiveData<>();
    public final LiveData<String> actionBarTitle = _actionBarTitle;

    private final MutableLiveData<Event<Void>> _playAlarmEvent = new MutableLiveData<>();
    public final LiveData<Event<Void>> playAlarmEvent = _playAlarmEvent;

    private final MutableLiveData<Event<String>> _toastMessageEvent = new MutableLiveData<>();
    public final LiveData<Event<String>> toastMessageEvent = _toastMessageEvent;

    private final MutableLiveData<Boolean> _isRunning = new MutableLiveData<>(false);
    public final LiveData<Boolean> isRunning = _isRunning;

    // State variables
    private int counter = 0; // Current stopwatch/timer time in seconds
    private int pauseValue = 0; // Stores counter value when paused (mainly for stopwatch)
    private char presentationType = 'd'; // Default to decimal: 'b'inary, 'd'ecimal, 'h'ex, 'r'oman, 'a'll
    private boolean isTimerMode = false; // false = Stopwatch, true = Timer
    private boolean internalStartStopState = false; // Tracks if handler should be running

    private Handler stopwatchHandler;
    private Runnable stopwatchRunnable;
    private static final long UPDATE_INTERVAL_MS = 1000;

    private Application application;

    public StopwatchViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        initHandler();
        updateUIComponentsText();
        updateStopwatchDisplay(); // Initial display
    }

    private void initHandler() {
        stopwatchHandler = new Handler(Looper.getMainLooper());
        stopwatchRunnable = new Runnable() {
            @Override
            public void run() {
                if (!internalStartStopState) return;

                if (isTimerMode) { // Timer Mode
                    if (counter > 0) {
                        counter--;
                    }
                    if (counter <= 0) {
                        counter = 0;
                        internalStartStopState = false; // Stop the timer
                        _isRunning.postValue(false);
                        _startStopButtonText.postValue(application.getString(R.string.start));
                        _playAlarmEvent.postValue(new Event<>(null)); // Signal alarm
                        updateStopwatchDisplay(); // Show 00:00:00
                        return; // Do not re-post runnable
                    }
                } else { // Stopwatch Mode
                    counter++;
                }
                updateStopwatchDisplay();
                stopwatchHandler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        };
    }

    private void updateUIComponentsText() {
        if (isTimerMode) {
            _actionBarTitle.setValue(application.getString(R.string.action_bar_title_timer));
            _modeButtonText.setValue(application.getString(R.string.stopwatch_mode_button));
        } else {
            _actionBarTitle.setValue(application.getString(R.string.title_stopwatch));
            _modeButtonText.setValue(application.getString(R.string.timer_mode_button));
        }
        _startStopButtonText.setValue(internalStartStopState ? application.getString(R.string.stop) : application.getString(R.string.start));
    }

    private void updateStopwatchDisplay() {
        int displayValue = counter;
        // Logic from fragment's updateStopwatchDisplay for determining displayValue
        if (isTimerMode && !internalStartStopState && pauseValue == 0) {
             displayValue = counter;
        } else if (!internalStartStopState && !isTimerMode && pauseValue != 0) {
            displayValue = pauseValue;
        } else if (!internalStartStopState && isTimerMode && pauseValue !=0) { // This condition might need review based on desired pause behavior for timer
             displayValue = counter;
        }

        // Use the Kotlin top-level function formatDurationStrings
        String[] formattedTimes = formatDurationStrings(displayValue);
        _timeDisplay.setValue(getPresentationTimeByType(formattedTimes));
    }

    private String getPresentationTimeByType(String[] s) {
        // Use the Kotlin top-level function formatDurationStrings for default if s is invalid
        if (s == null || s.length < 4) return formatDurationStrings(0)[1]; // Default
        switch (presentationType) {
            case 'b': return application.getString(R.string.time_in) + " Binary:\n" + s[0];
            case 'd': return application.getString(R.string.time_in) + " Decimal:\n" + s[1];
            case 'h': return application.getString(R.string.time_in) + " Hex:\n" + s[2];
            case 'r': return application.getString(R.string.time_in) + " Roman:\n" + s[3];
            default: // 'a' for all - though not explicitly handled by original fragment text click, so we'll stick to single types
                return s[0] + "\n" + s[1] + "\n" + s[2] + "\n" + s[3];
        }
    }

    // Public methods to be called by the Fragment
    public void onStartStopClicked() {
        if (!internalStartStopState) { // If stopped, and we click start
            if (isTimerMode && counter == 0) {
                _toastMessageEvent.setValue(new Event<>("Add time to timer first"));
                return;
            }
            internalStartStopState = true;
            if (!isTimerMode && pauseValue != 0) {
                counter = pauseValue;
            }
            pauseValue = 0;
            stopwatchHandler.post(stopwatchRunnable);
        } else { // If running, and we click stop
            internalStartStopState = false;
            if (!isTimerMode) {
                pauseValue = counter;
            }
            stopwatchHandler.removeCallbacks(stopwatchRunnable);
        }
        _isRunning.setValue(internalStartStopState);
        _startStopButtonText.setValue(internalStartStopState ? application.getString(R.string.stop) : application.getString(R.string.start));
        updateStopwatchDisplay(); // Update display immediately
    }

    public void onResetClicked() {
        internalStartStopState = false;
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
        counter = 0;
        pauseValue = 0;
        // isTimerMode remains unchanged on reset, user explicitly toggles mode
        updateUIComponentsText();
        updateStopwatchDisplay();
        _isRunning.setValue(false);
    }

    public void onPlus30Clicked() {
        adjustTime(30, false);
    }

    public void onPlus30LongClicked() {
        adjustTime(180, true);
        _toastMessageEvent.setValue(new Event<>("+ 3 minutes"));
    }

    public void onMinus30Clicked() {
        adjustTime(-30, false);
    }

    public void onMinus30LongClicked() {
        adjustTime(-180, true);
        _toastMessageEvent.setValue(new Event<>("- 3 minutes"));
    }

    private void adjustTime(int seconds, boolean isLongClick) {
        if (internalStartStopState && isTimerMode) {
            _toastMessageEvent.setValue(new Event<>("Stop timer to adjust time"));
            return;
        }
        counter += seconds;
        if (counter < 0) counter = 0;

        if (isTimerMode && !internalStartStopState) {
            pauseValue = 0; // Timer value is directly in counter when not running
        }
        updateStopwatchDisplay();
    }

    public void onModeToggleClicked() {
        isTimerMode = !isTimerMode;
        internalStartStopState = false; // Stop any active counting
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
        counter = 0;
        pauseValue = 0;
        updateUIComponentsText();
        updateStopwatchDisplay();
        _isRunning.setValue(false);
    }

    public void onTimeDisplayClicked() {
        switch (presentationType) {
            case 'h': presentationType = 'r'; break;
            case 'd': presentationType = 'h'; break;
            case 'b': presentationType = 'd'; break;
            // case 'a': presentationType = 'b'; break; // 'a' was not fully implemented for click cycle
            default: presentationType = 'b'; // Cycle to binary after Roman
        }
        updateStopwatchDisplay();
    }

    public void onFragmentResumed() {
        if (internalStartStopState) {
            stopwatchHandler.post(stopwatchRunnable);
        }
        updateUIComponentsText(); // Refresh titles/button text
        updateStopwatchDisplay(); // Refresh display
    }

    public void onFragmentPaused() {
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopwatchHandler.removeCallbacks(stopwatchRunnable); // Clean up handler
    }

    // Methods to expose LiveData (already public final)

    // Remove old mText methods if they are fully replaced
    // public LiveData<String> getText() { return _timeDisplay; }
    // public void setmText(String s) { _timeDisplay.setValue(s); }
}
