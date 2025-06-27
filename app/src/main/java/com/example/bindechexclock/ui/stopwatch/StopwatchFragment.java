package com.example.bindechexclock.ui.stopwatch;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.lifecycle.Observer; // Added for observing LiveData

import com.example.bindechexclock.R;
import com.example.bindechexclock.TimeManager;

public class StopwatchFragment extends Fragment {

    private StopwatchViewModel stopwatchViewModel;

    private boolean startStopState = false; // true if stopwatch is running
    private int counter = 0; // Current stopwatch time in seconds
    private int pauseValue = 0; // Stores counter value when paused
    private char presentationType = 'a'; // 'b'inary, 'd'ecimal, 'h'ex, 'r'oman, 'a'll

    private Handler stopwatchHandler;
    private Runnable stopwatchRunnable;
    private static final long UPDATE_INTERVAL_MS = 1000; // 1 second, can be adjusted for stopwatch precision

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopwatchHandler = new Handler(Looper.getMainLooper());
        stopwatchRunnable = new Runnable() {
            @Override
            public void run() {
                if (startStopState) {
                    counter++;
                    updateStopwatchDisplay();
                    stopwatchHandler.postDelayed(this, UPDATE_INTERVAL_MS);
                }
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        stopwatchViewModel = new ViewModelProvider(this).get(StopwatchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        final TextView textView = root.findViewById(R.id.text_stopwatch);
        final Button startStopButton = root.findViewById(R.id.buttonStartStop);
        final Button resetButton = root.findViewById(R.id.buttonReset);

        updateStopwatchDisplay(); // Initial display

        textView.setOnClickListener(v -> {
            switch (presentationType) {
                case 'h': presentationType = 'r'; break;
                case 'd': presentationType = 'h'; break;
                case 'b': presentationType = 'd'; break;
                case 'a': presentationType = 'b'; break;
                default: presentationType = 'a';
            }
            updateStopwatchDisplay(); // Update display with new presentation type
        });

        stopwatchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });

        startStopButton.setText(R.string.start);
        resetButton.setText(R.string.reset);

        startStopButton.setOnClickListener(v -> {
            if (!startStopState) { // If stopped, and we click start
                startStopState = true;
                startStopButton.setText(R.string.stop);
                if (pauseValue != 0) { // Resuming from pause
                    counter = pauseValue;
                }
                // counter is already set (either 0 or pauseValue)
                stopwatchHandler.post(stopwatchRunnable);
            } else { // If running, and we click stop
                startStopState = false;
                startStopButton.setText(R.string.start);
                pauseValue = counter;
                stopwatchHandler.removeCallbacks(stopwatchRunnable);
            }
        });

        resetButton.setOnClickListener(v -> {
            startStopState = false;
            stopwatchHandler.removeCallbacks(stopwatchRunnable);
            counter = 0;
            pauseValue = 0;
            updateStopwatchDisplay();
            startStopButton.setText(R.string.start);
        });

        return root;
    }

    private void updateStopwatchDisplay() {
        String[] formattedTimes = TimeManager.formatDurationStrings(startStopState ? counter : pauseValue);
        stopwatchViewModel.setmText(getPresentationTimeByType(formattedTimes));
    }

    @Override
    public void onResume() {
        super.onResume();
        // If stopwatch was running and fragment is resumed, restart the handler
        // This handles cases like screen rotation IF state is saved/restored properly
        // For now, let's assume if it was running, it should continue.
        if (startStopState) {
            stopwatchHandler.post(stopwatchRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop the handler when the fragment is not visible to save resources
        // but keep the state (counter, startStopState, pauseValue)
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
    }

    private String getPresentationTimeByType(String[] s) {
        if (s == null || s.length < 4) return "00:00:00"; // Basic safety
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
