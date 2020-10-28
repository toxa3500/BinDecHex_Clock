package com.example.bindechexclock.ui.stopwatch;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    char presentationType = 'a';

    TimeManager timeManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        stopwatchViewModel = ViewModelProviders.of(this).get(StopwatchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        final TextView textView = root.findViewById(R.id.text_stopwatch);
        textView.setOnClickListener(v -> {
            switch (presentationType) {
                case 'h':
                    presentationType = 'r';
                    break;
                case 'd':
                    presentationType = 'h';
                    break;
                case 'b':
                    presentationType = 'd';
                    break;
                case 'a':
                    presentationType = 'b';
                    break;
                default:
                    presentationType = 'a';
            }
        });

        stopwatchViewModel.getText().observe(getViewLifecycleOwner(), s -> {
            if (startStopState) textView.setText(s);
        });

        textView.setText(R.string.start);

        final Button startStop = root.findViewById(R.id.buttonStartStop);
        startStop.setText(R.string.start);

        startStop.setOnClickListener(v -> {
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

        });

        final Button reset = root.findViewById(R.id.buttonReset);
        reset.setText(R.string.reset);

        reset.setOnClickListener(v -> {
            pauseValue = 0;
            textView.setText(getPresentationTimeByType(timeManager.getTimeStringFromInt(pauseValue)));
            startStopState = false;
            startStop.setText(R.string.start);
        });

        return root;


    }

    public void increaseCounterByOne(TimeManager timeManager) {
        this.timeManager = timeManager;
        counter++;
        stopwatchViewModel.setmText(getPresentationTimeByType(timeManager.getTimeStringFromInt(counter)));
    }

    public String getPresentationTimeByType(String[] s){
        switch (presentationType)
        {
            case 'b':
                return s[0];
            case 'd':
                return s[1];
            case 'h':
                return s[2];
            case 'r':
                return s[3];
            default:
                StringBuilder result = new StringBuilder();
                for (String res: s) {
                    result.append(res);
                }
                return result.toString();
        }
    }
}
