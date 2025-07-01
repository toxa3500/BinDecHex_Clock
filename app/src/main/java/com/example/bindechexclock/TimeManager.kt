package com.example.bindechexclock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.Calendar

class TimeManager {

    private val _binaryTimeLiveData = MutableLiveData<String>()
    val binaryTimeLiveData: LiveData<String> get() = _binaryTimeLiveData

    private val _decimalTimeLiveData = MutableLiveData<String>()
    val decimalTimeLiveData: LiveData<String> get() = _decimalTimeLiveData

    private val _hexTimeLiveData = MutableLiveData<String>()
    val hexTimeLiveData: LiveData<String> get() = _hexTimeLiveData

    private val _romanTimeLiveData = MutableLiveData<String>()
    val romanTimeLiveData: LiveData<String> get() = _romanTimeLiveData

    init {
        // Call updateTime() once to initialize LiveData with current time
        updateTime()
    }

    fun updateTime() {
        val calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)

        // Assuming formatTimePart will be converted and available in this class
        // and intToRomans is available from TimeUtils.kt
        _binaryTimeLiveData.postValue("${formatTimePart(hours, 2)}\n${formatTimePart(minutes, 2)}\n${formatTimePart(seconds, 2)}")
        _decimalTimeLiveData.postValue("${formatTimePart(hours, 10)}:${formatTimePart(minutes, 10)}:${formatTimePart(seconds, 10)}")
        _hexTimeLiveData.postValue("${formatTimePart(hours, 16)}:${formatTimePart(minutes, 16)}:${formatTimePart(seconds, 16)}")
        _romanTimeLiveData.postValue("${intToRomans(hours)}\n${intToRomans(minutes)}\n${intToRomans(seconds)}")
    }

    private fun formatTimePart(timePart: Int, radix: Int): String {
        val value = timePart.toString(radix)
        return when (radix) {
            2 -> value // Binary doesn't need uppercase typically, but consistency is fine.
            10 -> if (timePart < 10) "0$value" else value
            16 -> if (timePart < 10) "0$value" else value.uppercase() // Pad 0-9 for hex, A-F are single char. Ensure uppercase.
            else -> "N/A" // Should ideally not be reached if radix is always 2, 10, or 16.
        }
    }
}
