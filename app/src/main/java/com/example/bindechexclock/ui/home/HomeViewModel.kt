package com.example.bindechexclock.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    // Private MutableLiveData that can be modified within the ViewModel.
    private val _text = MutableLiveData<String>()

    // Public LiveData that is exposed to the UI (Fragment) for observation.
    // This is read-only from the Fragment's perspective.
    val text: LiveData<String> get() = _text

    // The original Java constructor initialized mText = new MutableLiveData<>()
    // In Kotlin, this is handled by the property initializer above.
    // The original also had a commented-out mText.setValue("This is home fragment").
    // We will keep it uninitialized or initialize with an empty string if preferred.
    // For now, it will be initialized by the first call to updateText.

    /**
     * Updates the text LiveData.
     * Called by HomeFragment when it receives updates from TimeManager.
     */
    fun updateText(newText: String) {
        _text.value = newText
    }
}
