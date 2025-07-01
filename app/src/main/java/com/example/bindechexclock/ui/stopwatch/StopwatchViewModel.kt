package com.example.bindechexclock.ui.stopwatch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bindechexclock.R
import com.example.bindechexclock.formatDurationStrings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Event class should be defined in Kotlin or imported if already Kotlin
// For this step, we assume 'Event.kt' exists in this package or is imported.
// e.g., import com.example.bindechexclock.Event

class StopwatchViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData fields
    private val _timeDisplay = MutableLiveData<String>()
    val timeDisplay: LiveData<String> get() = _timeDisplay

    private val _startStopButtonText = MutableLiveData<String>()
    val startStopButtonText: LiveData<String> get() = _startStopButtonText

    private val _modeButtonText = MutableLiveData<String>()
    val modeButtonText: LiveData<String> get() = _modeButtonText

    private val _actionBarTitle = MutableLiveData<String>()
    val actionBarTitle: LiveData<String> get() = _actionBarTitle

    private val _playAlarmEvent = MutableLiveData<Event<Unit>>() // Unit for Void
    val playAlarmEvent: LiveData<Event<Unit>> get() = _playAlarmEvent

    private val _toastMessageEvent = MutableLiveData<Event<String>>()
    val toastMessageEvent: LiveData<Event<String>> get() = _toastMessageEvent

    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> get() = _isRunning

    // State variables
    private var counter: Int = 0 // Current stopwatch/timer time in seconds
    private var pauseValue: Int = 0 // Stores counter value when paused (mainly for stopwatch)
    private var presentationType: Char = 'd' // Default to decimal: 'b'inary, 'd'ecimal, 'h'ex, 'r'oman
    private var isTimerMode: Boolean = false // false = Stopwatch, true = Timer
    private var internalStartStopState: Boolean = false // Tracks if the timer logic should be running

    private var timerJob: Job? = null

    companion object {
        private const val UPDATE_INTERVAL_MS = 1000L // Kotlin Long
    }

    init {
        // initHandler() removed
        updateUIComponentsText()
        updateStopwatchDisplay() // Initial display
    }

    // initHandler() and stopwatchRunnable removed

    private fun startTimerUpdates() {
        if (timerJob?.isActive == true) {
            return // Already running
        }
        internalStartStopState = true
        timerJob = viewModelScope.launch {
            while (internalStartStopState) {
                delay(UPDATE_INTERVAL_MS)
                if (!internalStartStopState) break // Re-check state after delay before processing

                if (isTimerMode) { // Timer Mode
                    if (counter > 0) {
                        counter--
                    }
                    if (counter <= 0) {
                        counter = 0
                        internalStartStopState = false // Stop the timer
                        _isRunning.postValue(false)
                        _startStopButtonText.postValue(getApplication<Application>().getString(R.string.start))
                        _playAlarmEvent.postValue(Event(Unit)) // Signal alarm
                        updateStopwatchDisplay() // Show 00:00:00
                        break // Exit loop
                    }
                } else { // Stopwatch Mode
                    counter++
                }
                updateStopwatchDisplay()
            }
        }
    }

    private fun stopTimerUpdates() {
        internalStartStopState = false
        timerJob?.cancel()
        timerJob = null
    }

    private fun updateUIComponentsText() {
        val app = getApplication<Application>()
        if (isTimerMode) {
            _actionBarTitle.value = app.getString(R.string.action_bar_title_timer)
            _modeButtonText.value = app.getString(R.string.stopwatch_mode_button)
        } else {
            _actionBarTitle.value = app.getString(R.string.title_stopwatch)
            _modeButtonText.value = app.getString(R.string.timer_mode_button)
        }
        _startStopButtonText.value = if (internalStartStopState) app.getString(R.string.stop) else app.getString(R.string.start)
    }

    private fun updateStopwatchDisplay() {
        var displayValue = counter
        // Logic from fragment's updateStopwatchDisplay for determining displayValue
        if (isTimerMode && !internalStartStopState && pauseValue == 0) {
             displayValue = counter
        } else if (!internalStartStopState && !isTimerMode && pauseValue != 0) {
            displayValue = pauseValue
        } else if (!internalStartStopState && isTimerMode && pauseValue !=0) { // This condition might need review based on desired pause behavior for timer
             displayValue = counter
        }

        val formattedTimes = formatDurationStrings(displayValue) // from TimeUtils.kt
        _timeDisplay.value = getPresentationTimeByType(formattedTimes)
    }

    private fun getPresentationTimeByType(s: Array<String>): String {
        val app = getApplication<Application>()
        // Use the Kotlin top-level function formatDurationStrings for default if s is invalid
        if (s.isEmpty() || s.size < 4) return formatDurationStrings(0)[1] // Default to decimal part of 00:00:00
        return when (presentationType) {
            'b' -> app.getString(R.string.time_in) + " Binary:\n" + s[0]
            'd' -> app.getString(R.string.time_in) + " Decimal:\n" + s[1]
            'h' -> app.getString(R.string.time_in) + " Hex:\n" + s[2]
            'r' -> app.getString(R.string.time_in) + " Roman:\n" + s[3]
            else -> s[0] + "\n" + s[1] + "\n" + s[2] + "\n" + s[3] // Should not happen with current cycle
        }
    }

    // Public methods to be called by the Fragment
    fun onStartStopClicked() {
        val app = getApplication<Application>()
        if (!internalStartStopState) { // If stopped, and we click start
            if (isTimerMode && counter == 0) {
                _toastMessageEvent.value = Event("Add time to timer first")
                return
            }
            // internalStartStopState will be set by startTimerUpdates
            if (!isTimerMode && pauseValue != 0) {
                counter = pauseValue
            }
            pauseValue = 0
            startTimerUpdates()
        } else { // If running, and we click stop
            // internalStartStopState will be set by stopTimerUpdates
            if (!isTimerMode) {
                pauseValue = counter
            }
            stopTimerUpdates()
        }
        _isRunning.value = internalStartStopState // This should reflect the change from start/stopTimerUpdates
        _startStopButtonText.value = if (internalStartStopState) app.getString(R.string.stop) else app.getString(R.string.start)
        updateStopwatchDisplay() // Update display immediately
    }

    fun onResetClicked() {
        stopTimerUpdates()
        counter = 0
        pauseValue = 0
        // isTimerMode remains unchanged on reset, user explicitly toggles mode
        updateUIComponentsText()
        updateStopwatchDisplay()
        _isRunning.value = false
    }

    fun onPlus30Clicked() {
        adjustTime(30, false)
    }

    fun onPlus30LongClicked() {
        adjustTime(180, true)
        _toastMessageEvent.value = Event("+ 3 minutes")
    }

    fun onMinus30Clicked() {
        adjustTime(-30, false)
    }

    fun onMinus30LongClicked() {
        adjustTime(-180, true)
        _toastMessageEvent.value = Event("- 3 minutes")
    }

    private fun adjustTime(seconds: Int, isLongClick: Boolean) {
        if (internalStartStopState && isTimerMode) {
            _toastMessageEvent.value = Event("Stop timer to adjust time")
            return
        }
        counter += seconds
        if (counter < 0) counter = 0

        if (isTimerMode && !internalStartStopState) {
            pauseValue = 0 // Timer value is directly in counter when not running
        }
        updateStopwatchDisplay()
    }

    fun onModeToggleClicked() {
        isTimerMode = !isTimerMode
        stopTimerUpdates()
        counter = 0
        pauseValue = 0
        updateUIComponentsText()
        updateStopwatchDisplay()
        _isRunning.value = false
    }

    fun onTimeDisplayClicked() {
        presentationType = when (presentationType) {
            'h' -> 'r'
            'd' -> 'h'
            'b' -> 'd'
            else -> 'b' // Cycle to binary after Roman (or any other case)
        }
        updateStopwatchDisplay()
    }

    fun onFragmentResumed() {
        if (internalStartStopState) { // If it was supposed to be running
            startTimerUpdates()
        }
        updateUIComponentsText() // Refresh titles/button text
        updateStopwatchDisplay() // Refresh display
    }

    fun onFragmentPaused() {
        // Decide if we want to truly stop the timer or just UI updates.
        // Current logic implies stopping the timer job.
        // If internalStartStopState is true, it means it was running.
        // We don't change internalStartStopState here, so onResume can pick it up.
        timerJob?.cancel() // Cancel the job, but keep internalStartStopState
    }

    override fun onCleared() {
        super.onCleared()
        stopTimerUpdates() // Clean up coroutine
    }
}
