package com.example.bindechexclock.ui.stopwatch

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * This is used to prevent events from being re-triggered on configuration change
 * when the LiveData is re-observed.
 *
 * @param T The type of the content.
 */
class Event<T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     * Returns null if it has already been handled.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     * This is useful if you want to just peek at the content without consuming the event.
     */
    fun peekContent(): T = content
}
