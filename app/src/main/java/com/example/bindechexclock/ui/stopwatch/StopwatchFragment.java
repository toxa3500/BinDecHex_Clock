package com.example.bindechexclock.ui.stopwatch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bindechexclock.R;
import com.example.bindechexclock.TimeManager;

public class StopwatchFragment extends Fragment {

    public StopwatchViewModel stopwatchViewModel;

    boolean startStopState = false;
    public int counter = 0;
    int pauseValue = 0;

    TimeManager timeManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        stopwatchViewModel = ViewModelProviders.of(this).get(StopwatchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        final TextView textView = root.findViewById(R.id.text_stopwatch);

        stopwatchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (startStopState) textView.setText(s);
            }
        });

        textView.setText(R.string.start);

        final Button startStop = root.findViewById(R.id.buttonStartStop);
        startStop.setText(R.string.start);

        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startStopState) {
                    if (pauseValue != 0) {
                        counter = pauseValue;
                    } else {
                        counter = 0;
                    }
                    startStopState = true;
                    startStop.setText(R.string.stop);

                } else {
                    startStopState = false;
                    startStop.setText(R.string.start);
                    pauseValue = counter;

                }

            }
        });

        final Button reset = root.findViewById(R.id.buttonReset);
        reset.setText(R.string.reset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseValue = 0;
                textView.setText(timeManager.getTimeStringFromInt(pauseValue));
                startStopState = false;
                startStop.setText(R.string.start);
            }
        });

        return root;


    }

    public void increaseCounterByOne(TimeManager timeManager) {
        this.timeManager = timeManager;
        counter++;
        stopwatchViewModel.setmText(timeManager.getTimeStringFromInt(counter));
    }
}
