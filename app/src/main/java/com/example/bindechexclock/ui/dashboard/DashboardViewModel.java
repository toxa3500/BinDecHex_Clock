package com.example.bindechexclock.ui.dashboard;

import androidx.lifecycle.LiveData; // Changed
import androidx.lifecycle.MutableLiveData; // Changed
import androidx.lifecycle.ViewModel; // Changed

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        // mText.setValue("This is dashboard fragment"); // Default text removed
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setmText(String s) {
        mText.setValue(s);
    }
}