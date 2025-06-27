package com.example.bindechexclock.ui.notifications;

import androidx.lifecycle.LiveData; // Changed
import androidx.lifecycle.MutableLiveData; // Changed
import androidx.lifecycle.ViewModel; // Changed

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        // mText.setValue("This is notifications fragment"); // Default text removed
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setmText(String s) {
        mText.setValue(s);
    }
}