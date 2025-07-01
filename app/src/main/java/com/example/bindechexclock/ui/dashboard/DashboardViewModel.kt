package com.example.bindechexclock.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    init {
        // If there was a default value like "This is dashboard fragment"
        // it would be set here, e.g., _text.value = "This is dashboard fragment"
        // For now, it's initialized by DashboardFragment.
    }

    fun updateText(newText: String) {
        _text.value = newText
    }
}
