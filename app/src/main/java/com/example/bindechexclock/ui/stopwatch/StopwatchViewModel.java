package com.example.bindechexclock.ui.stopwatch;

import androidx.lifecycle.LiveData; // Changed
import androidx.lifecycle.MutableLiveData; // Changed
import androidx.lifecycle.ViewModel; // Changed

public class StopwatchViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public StopwatchViewModel() {
        mText = new MutableLiveData<>();
        // mText.setValue("00:00:00"); // Initial display text, will be set by Fragment
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setmText(String s) {
        mText.setValue(s);
    }
}
