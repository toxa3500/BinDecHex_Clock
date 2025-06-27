package com.example.bindechexclock.ui.home;

import androidx.lifecycle.LiveData; // Changed
import androidx.lifecycle.MutableLiveData; // Changed
import androidx.lifecycle.ViewModel; // Changed

// import com.example.bindechexclock.MainActivity; // Unused import removed

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    // TODO: This ViewModel will be populated by observing TimeManager's LiveData
    // from the HomeFragment. This setmText might still be useful if HomeFragment
    // does some formatting before updating the ViewModel's LiveData.

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        // mText.setValue("This is home fragment"); // Default text removed, will be set by TimeManager
    }

    public LiveData<String> getText() {
        return mText;
    }

    // This method will be called by HomeFragment when it receives updates
    // from TimeManager's LiveData.
    public void setmText(String s) {
        mText.setValue(s);
    }
}