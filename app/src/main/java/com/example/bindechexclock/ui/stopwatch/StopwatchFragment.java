package com.example.bindechexclock.ui.stopwatch;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bindechexclock.R;
import com.example.bindechexclock.TimeManager;

public class StopwatchFragment extends Fragment {

    private StopwatchViewModel stopwatchViewModel;

    private boolean startStopState = false; // true if stopwatch/timer is running
    private int counter = 0; // Current stopwatch/timer time in seconds
    private int pauseValue = 0; // Stores counter value when paused (mainly for stopwatch)
    private char presentationType = 'd'; // Default to decimal: 'b'inary, 'd'ecimal, 'h'ex, 'r'oman, 'a'll
    private boolean isTimerMode = false; // false = Stopwatch, true = Timer

    private Handler stopwatchHandler;
    private Runnable stopwatchRunnable;
    private static final long UPDATE_INTERVAL_MS = 1000;

    private Button buttonPlus30, buttonMinus30, buttonModeToggle;
    private Button startStopButton; // Keep reference for text update
    private TextView textViewStopwatch;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopwatchHandler = new Handler(Looper.getMainLooper());
        stopwatchRunnable = new Runnable() {
            @Override
            public void run() {
                if (!startStopState) return;

                if (isTimerMode) { // Timer Mode
                    if (counter > 0) {
                        counter--;
                    }
                    if (counter <= 0) {
                        counter = 0;
                        startStopState = false; // Stop the timer
                        if (startStopButton != null) {
                            startStopButton.setText(R.string.start);
                        }
                        playAlarmSound();
                        updateStopwatchDisplay(); // Show 00:00:00
                        // Do not re-post runnable
                        return;
                    }
                } else { // Stopwatch Mode
                    counter++;
                }
                updateStopwatchDisplay();
                stopwatchHandler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        stopwatchViewModel = new ViewModelProvider(this).get(StopwatchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        textViewStopwatch = root.findViewById(R.id.text_stopwatch);
        startStopButton = root.findViewById(R.id.buttonStartStop);
        final Button resetButton = root.findViewById(R.id.buttonReset);

        buttonPlus30 = root.findViewById(R.id.button_plus_30);
        buttonMinus30 = root.findViewById(R.id.button_minus_30);
        buttonModeToggle = root.findViewById(R.id.button_mode_toggle);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateTitleAndModeButton(); // Set initial title and button text
        updateStopwatchDisplay();    // Initial display

        textViewStopwatch.setOnClickListener(v -> {
            switch (presentationType) {
                case 'h': presentationType = 'r'; break;
                case 'd': presentationType = 'h'; break;
                case 'b': presentationType = 'd'; break;
                case 'a': presentationType = 'b'; break;
                default: presentationType = 'd'; // Default to decimal if cycle is broken
            }
            updateStopwatchDisplay();
        });

        stopwatchViewModel.getText().observe(getViewLifecycleOwner(), s -> textViewStopwatch.setText(s));

        startStopButton.setOnClickListener(v -> {
            if (!startStopState) { // If stopped, and we click start
                if (isTimerMode && counter == 0) {
                    Toast.makeText(getContext(), "Add time to timer first", Toast.LENGTH_SHORT).show();
                    return;
                }
                startStopState = true;
                startStopButton.setText(R.string.stop);
                if (!isTimerMode && pauseValue != 0) { // Resuming stopwatch from pause
                    counter = pauseValue;
                }
                // For timer, counter is already set, or for stopwatch it's 0 or pauseValue
                pauseValue = 0; // Clear pauseValue once resumed/started
                stopwatchHandler.post(stopwatchRunnable);
            } else { // If running, and we click stop
                startStopState = false;
                startStopButton.setText(R.string.start);
                if (!isTimerMode) { // Store pauseValue only for stopwatch mode
                    pauseValue = counter;
                }
                // For timer mode, stopping means pausing at the current countdown value (stored in counter)
                stopwatchHandler.removeCallbacks(stopwatchRunnable);
            }
            updateStopwatchDisplay(); // Update display on start/stop to reflect current state immediately
        });

        resetButton.setOnClickListener(v -> {
            startStopState = false;
            stopwatchHandler.removeCallbacks(stopwatchRunnable);
            counter = 0;
            pauseValue = 0;
            if (isTimerMode) {
                // If in timer mode, reset should keep it in timer mode, ready for new input
            } else {
                // If in stopwatch mode, ensure it's fully reset to stopwatch defaults
                isTimerMode = false; // Should already be false, but to be sure
            }
            updateTitleAndModeButton(); // Ensure title and mode button are correct
            updateStopwatchDisplay();
            startStopButton.setText(R.string.start);
        });

        buttonPlus30.setOnClickListener(v -> {
            if (startStopState && isTimerMode) { // Don't add time to a running timer
                Toast.makeText(getContext(), "Stop timer to add time", Toast.LENGTH_SHORT).show();
                return;
            }
            counter += 30;
            if (isTimerMode && !startStopState) pauseValue = counter; // Update effective time if timer is paused/being set
            updateStopwatchDisplay();
        });

        buttonMinus30.setOnClickListener(v -> {
            if (startStopState && isTimerMode) { // Don't subtract time from a running timer
                Toast.makeText(getContext(), "Stop timer to subtract time", Toast.LENGTH_SHORT).show();
                return;
            }
            counter -= 30;
            if (counter < 0) counter = 0;
            if (isTimerMode && !startStopState) pauseValue = counter; // Update effective time if timer is paused/being set
            updateStopwatchDisplay();
        });

        buttonModeToggle.setOnClickListener(v -> {
            isTimerMode = !isTimerMode;
            startStopState = false; // Stop any active counting
            stopwatchHandler.removeCallbacks(stopwatchRunnable);
            counter = 0;
            pauseValue = 0;
            updateTitleAndModeButton();
            updateStopwatchDisplay();
            startStopButton.setText(R.string.start);
        });
    }

    private void updateTitleAndModeButton() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        }

        if (isTimerMode) {
            if (actionBar != null) actionBar.setTitle(R.string.action_bar_title_timer);
            buttonModeToggle.setText(R.string.stopwatch_mode_button);
        } else {
            if (actionBar != null) actionBar.setTitle(R.string.title_stopwatch);
            buttonModeToggle.setText(R.string.timer_mode_button);
        }
    }

    private void updateStopwatchDisplay() {
        int displayValue = counter;
        if (isTimerMode && !startStopState && pauseValue == 0) { // When setting timer, show current counter
             displayValue = counter;
        } else if (!startStopState && !isTimerMode && pauseValue != 0) { // When stopwatch paused
            displayValue = pauseValue;
        } else if (!startStopState && isTimerMode && pauseValue !=0) { // when timer paused
             displayValue = counter; // or pauseValue, depends on desired behavior for paused timer
        }


        String[] formattedTimes = TimeManager.formatDurationStrings(displayValue);
        stopwatchViewModel.setmText(getPresentationTimeByType(formattedTimes));
    }

    private void playAlarmSound() {
        if (getContext() == null) return;
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone r = RingtoneManager.getRingtone(getContext(), alarmSound);
            if (r != null) {
                r.play();
            }
        } catch (Exception e) {
            // Could log e.printStackTrace();
            Toast.makeText(getContext(), "Error playing alarm sound", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (startStopState) { // If it was running (either mode)
            stopwatchHandler.post(stopwatchRunnable);
        }
        updateTitleAndModeButton(); // Refresh title in case it was changed by another fragment/activity config change
        updateStopwatchDisplay(); // Refresh display
    }

    @Override
    public void onPause() {
        super.onPause();
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
        // startStopState, counter, pauseValue, isTimerMode, presentationType are preserved
    }

    private String getPresentationTimeByType(String[] s) {
        if (s == null || s.length < 4) return TimeManager.formatDurationStrings(0)[1]; // Default to 00:00:00 decimal
        switch (presentationType) {
            case 'b': return s[0];
            case 'd': return s[1];
            case 'h': return s[2];
            case 'r': return s[3];
            default: // 'a' for all
                return s[0] + "\n" + s[1] + "\n" + s[2] + "\n" + s[3];
        }
    }
}
