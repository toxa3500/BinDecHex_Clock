package com.example.bindechexclock.ui.stopwatch;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
// Removed Handler, Looper imports
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
// Removed Observer import, specific LiveData observers will be used
import androidx.lifecycle.ViewModelProvider;

// R import is fine
// TimeManager import might not be needed directly if all formatting happens in ViewModel

public class StopwatchFragment extends Fragment {

    private StopwatchViewModel stopwatchViewModel;

    // Removed state variables: startStopState, counter, pauseValue, presentationType, isTimerMode
    // Removed Handler and Runnable: stopwatchHandler, stopwatchRunnable, UPDATE_INTERVAL_MS

    private Button buttonPlus30, buttonMinus30, buttonModeToggle;
    private Button startStopButton;
    private Button resetButton;
    private TextView textViewStopwatch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Handler and Runnable initialization removed
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        // ViewModelProvider.Factory might be needed if constructor takes arguments other than Application
        // For AndroidViewModel, this is usually handled automatically.
        stopwatchViewModel = new ViewModelProvider(this).get(StopwatchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        textViewStopwatch = root.findViewById(R.id.text_stopwatch);
        startStopButton = root.findViewById(R.id.buttonStartStop);
        resetButton = root.findViewById(R.id.buttonReset);
        buttonPlus30 = root.findViewById(R.id.button_plus_30);
        buttonMinus30 = root.findViewById(R.id.button_minus_30);
        buttonModeToggle = root.findViewById(R.id.button_mode_toggle);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObservers();
        setupClickListeners();
    }

    private void setupObservers() {
        stopwatchViewModel.timeDisplay.observe(getViewLifecycleOwner(), text -> {
            if (text != null) {
                textViewStopwatch.setText(text);
            }
        });

        stopwatchViewModel.startStopButtonText.observe(getViewLifecycleOwner(), text -> {
            if (text != null) {
                startStopButton.setText(text);
            }
        });

        stopwatchViewModel.modeButtonText.observe(getViewLifecycleOwner(), text -> {
            if (text != null) {
                buttonModeToggle.setText(text);
            }
        });

        stopwatchViewModel.actionBarTitle.observe(getViewLifecycleOwner(), title -> {
            if (title != null && getActivity() instanceof AppCompatActivity) {
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(title);
                }
            }
        });

        stopwatchViewModel.playAlarmEvent.observe(getViewLifecycleOwner(), event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                playAlarmSound();
            }
        });

        stopwatchViewModel.toastMessageEvent.observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                String message = event.getContentIfNotHandled();
                if (message != null && getContext() != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // No need to observe isRunning directly for UI changes if other LiveData cover it,
        // but it's available if needed for other logic.
    }

    private void setupClickListeners() {
        textViewStopwatch.setOnClickListener(v -> stopwatchViewModel.onTimeDisplayClicked());
        startStopButton.setOnClickListener(v -> stopwatchViewModel.onStartStopClicked());
        resetButton.setOnClickListener(v -> stopwatchViewModel.onResetClicked());
        buttonPlus30.setOnClickListener(v -> stopwatchViewModel.onPlus30Clicked());
        buttonPlus30.setOnLongClickListener(v -> {
            stopwatchViewModel.onPlus30LongClicked();
            return true; // Consume long click
        });
        buttonMinus30.setOnClickListener(v -> stopwatchViewModel.onMinus30Clicked());
        buttonMinus30.setOnLongClickListener(v -> {
            stopwatchViewModel.onMinus30LongClicked();
            return true; // Consume long click
        });
        buttonModeToggle.setOnClickListener(v -> stopwatchViewModel.onModeToggleClicked());
    }

    // updateTitleAndModeButton method removed (handled by ViewModel LiveData)
    // updateStopwatchDisplay method removed (handled by ViewModel LiveData)
    // getPresentationTimeByType method removed (handled by ViewModel)

    private void playAlarmSound() {
        if (getContext() == null) return;
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSound == null) {
                // Fallback to notification sound if alarm sound is not found/set
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            if (alarmSound != null) { // Ensure there is a sound URI
                Ringtone r = RingtoneManager.getRingtone(getContext(), alarmSound);
                if (r != null) {
                    r.play();
                } else {
                     Toast.makeText(getContext(), "Cannot play alarm: Ringtone not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                 Toast.makeText(getContext(), "Cannot play alarm: No default sound URI.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Log.e("StopwatchFragment", "Error playing alarm sound", e); // More detailed logging
            Toast.makeText(getContext(), "Error playing alarm sound", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        stopwatchViewModel.onFragmentResumed();
        // Removed direct UI updates, ViewModel handles refreshing its state and LiveData
    }

    @Override
    public void onPause() {
        super.onPause();
        stopwatchViewModel.onFragmentPaused();
        // Removed direct handler manipulation
    }
}
