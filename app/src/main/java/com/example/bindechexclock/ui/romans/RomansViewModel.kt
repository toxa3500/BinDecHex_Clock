package com.example.bindechexclock.ui.romans

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RomansViewModel : ViewModel() {

    // Private MutableLiveData that can be modified within the ViewModel.
    private val _text = MutableLiveData<String>()

    // Public LiveData that is exposed to the UI (Fragment) for observation.
    val text: LiveData<String> get() = _text

    // The original Java constructor initialized mText = new MutableLiveData<>()
    // In Kotlin, this is handled by the property initializer above.

    /**
     * Updates the text LiveData.
     * Called by RomansFragment when it receives updates from TimeManager.
     */
    fun updateText(newText: String) {
        _text.value = newText
    }
}
