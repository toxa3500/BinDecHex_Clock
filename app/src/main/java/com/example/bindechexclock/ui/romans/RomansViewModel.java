package com.example.bindechexclock.ui.romans;

import androidx.lifecycle.LiveData; // Changed
import androidx.lifecycle.MutableLiveData; // Changed
import androidx.lifecycle.ViewModel; // Changed

public class RomansViewModel extends ViewModel { // Corrected class declaration, was ViewModel{

    private MutableLiveData<String> mText;

    public RomansViewModel() {
        mText = new MutableLiveData<>();
        // mText.setValue("This is romans fragment"); // Default text removed
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setmText(String s) {
        mText.setValue(s);
    }
}
