package com.example.bindechexclock.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    init {
        // Default text, if any, would be set here.
        // For this app, the Fragment usually sets the initial text.
    }

    fun updateText(newText: String) {
        _text.value = newText
    }
}
