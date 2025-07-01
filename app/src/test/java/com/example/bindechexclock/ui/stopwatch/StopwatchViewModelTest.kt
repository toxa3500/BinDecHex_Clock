package com.example.bindechexclock.ui.stopwatch

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.bindechexclock.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.junit.Assert.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StopwatchViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule() // For LiveData

    private lateinit var viewModel: StopwatchViewModel

    @Mock
    private lateinit var application: Application

    private val testDispatcher = UnconfinedTestDispatcher() // StandardTestDispatcher can also be used

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set main dispatcher for coroutines
        // Mock Application context calls
        `when`(application.getString(R.string.start)).thenReturn("Start")
        `when`(application.getString(R.string.stop)).thenReturn("Stop")
        `when`(application.getString(R.string.title_stopwatch)).thenReturn("Stopwatch")
        `when`(application.getString(R.string.action_bar_title_timer)).thenReturn("Timer")
        `when`(application.getString(R.string.stopwatch_mode_button)).thenReturn("Stopwatch Mode")
        `when`(application.getString(R.string.timer_mode_button)).thenReturn("Timer Mode")
        `when`(application.getString(R.string.time_in)).thenReturn("Time in")


        viewModel = StopwatchViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher
    }

    @Test
    fun `initial state is correct`() = runTest {
        assertEquals("00:00:00", viewModel.timeDisplay.value?.substringAfterLast("\n"))
        assertEquals("Start", viewModel.startStopButtonText.value)
        assertEquals("Stopwatch", viewModel.actionBarTitle.value)
        assertEquals("Timer Mode", viewModel.modeButtonText.value)
        assertFalse(viewModel.isRunning.value ?: true)
    }

    @Test
    fun `onStartStopClicked starts stopwatch`() = runTest {
        viewModel.onStartStopClicked()
        advanceTimeBy(1000L) // Advance coroutine time
        assertEquals("Stop", viewModel.startStopButtonText.value)
        assertTrue(viewModel.isRunning.value ?: false)
        assertEquals("00:00:01", viewModel.timeDisplay.value?.substringAfterLast("\n"))
    }

    @Test
    fun `onStartStopClicked stops stopwatch`() = runTest {
        viewModel.onStartStopClicked() // Start
        advanceTimeBy(2000L)
        viewModel.onStartStopClicked() // Stop
        assertEquals("Start", viewModel.startStopButtonText.value)
        assertFalse(viewModel.isRunning.value ?: true)
        assertEquals("00:00:02", viewModel.timeDisplay.value?.substringAfterLast("\n")) // Time should be paused

        advanceTimeBy(1000L) // Advance time further
         assertEquals("00:00:02", viewModel.timeDisplay.value?.substringAfterLast("\n")) // Time should not change
    }

    @Test
    fun `onResetClicked resets stopwatch`() = runTest {
        viewModel.onStartStopClicked() // Start
        advanceTimeBy(3000L)
        viewModel.onResetClicked()

        assertEquals("00:00:00", viewModel.timeDisplay.value?.substringAfterLast("\n"))
        assertEquals("Start", viewModel.startStopButtonText.value)
        assertFalse(viewModel.isRunning.value ?: true)
    }

    @Test
    fun `onModeToggleClicked switches to timer mode`() = runTest {
        viewModel.onModeToggleClicked()
        assertEquals("Timer", viewModel.actionBarTitle.value)
        assertEquals("Stopwatch Mode", viewModel.modeButtonText.value)
        assertEquals("00:00:00", viewModel.timeDisplay.value?.substringAfterLast("\n"))
        // Ensure it's not running after mode toggle
        assertFalse(viewModel.isRunning.value ?: true)
    }

    @Test
    fun `onPlus30Clicked adds 30 seconds in stopwatch mode`() = runTest {
        viewModel.onPlus30Clicked()
        assertEquals("00:00:30", viewModel.timeDisplay.value?.substringAfterLast("\n"))
    }

    @Test
    fun `onPlus30Clicked adds 30 seconds in timer mode when stopped`() = runTest {
        viewModel.onModeToggleClicked() // Switch to timer
        viewModel.onPlus30Clicked()
        assertEquals("00:00:30", viewModel.timeDisplay.value?.substringAfterLast("\n"))
    }

    @Test
    fun `onPlus30Clicked does not add time in timer mode when running`() = runTest {
        viewModel.onModeToggleClicked() // Switch to timer
        viewModel.onPlus30Clicked() // Add 30s (counter is 30)
        viewModel.onStartStopClicked() // Start timer
        advanceTimeBy(1000L) // counter becomes 29

        viewModel.onPlus30Clicked() // Try to add 30s while running

        assertEquals("Stop timer to adjust time", viewModel.toastMessageEvent.value?.getContentIfNotHandled())
        assertEquals("00:00:29", viewModel.timeDisplay.value?.substringAfterLast("\n")) // Time should not have changed from this action
    }

    @Test
    fun `onMinus30Clicked subtracts 30 seconds`() = runTest {
        viewModel.onPlus30Clicked() // Add 30s (counter is 30)
        viewModel.onPlus30Clicked() // Add 30s (counter is 60)
        viewModel.onMinus30Clicked()
        assertEquals("00:00:30", viewModel.timeDisplay.value?.substringAfterLast("\n"))
    }

    @Test
    fun `onMinus30Clicked does not go below zero`() = runTest {
        viewModel.onMinus30Clicked()
        assertEquals("00:00:00", viewModel.timeDisplay.value?.substringAfterLast("\n"))
    }

    @Test
    fun `onTimeDisplayClicked cycles presentation type`() = runTest {
        // Initial: Decimal (d)
        assertTrue(viewModel.timeDisplay.value?.startsWith("Time in Decimal:") ?: false)

        viewModel.onTimeDisplayClicked() // d -> h
        assertTrue(viewModel.timeDisplay.value?.startsWith("Time in Hex:") ?: false)

        viewModel.onTimeDisplayClicked() // h -> r
        assertTrue(viewModel.timeDisplay.value?.startsWith("Time in Roman:") ?: false)

        viewModel.onTimeDisplayClicked() // r -> b
        assertTrue(viewModel.timeDisplay.value?.startsWith("Time in Binary:") ?: false)

        viewModel.onTimeDisplayClicked() // b -> d
        assertTrue(viewModel.timeDisplay.value?.startsWith("Time in Decimal:") ?: false)
    }

    @Test
    fun `timer counts down and triggers alarm`() = runTest {
        viewModel.onModeToggleClicked() // Switch to Timer mode
        viewModel.onPlus30Clicked()    // Set timer to 30 seconds
        assertEquals("00:00:30", viewModel.timeDisplay.value?.substringAfterLast("\n"))

        viewModel.onStartStopClicked() // Start timer
        assertTrue(viewModel.isRunning.value ?: false)
        assertEquals("Stop", viewModel.startStopButtonText.value)

        advanceTimeBy(29000L) // Advance time by 29 seconds
        assertEquals("00:00:01", viewModel.timeDisplay.value?.substringAfterLast("\n"))
        assertTrue(viewModel.isRunning.value ?: false) // Still running

        advanceTimeBy(1000L) // Advance 1 more second to reach 0

        // Check LiveData values after coroutine finishes
        assertFalse(viewModel.isRunning.value ?: true) // Should be stopped
        assertEquals("Start", viewModel.startStopButtonText.value) // Button text should be Start
        assertEquals("00:00:00", viewModel.timeDisplay.value?.substringAfterLast("\n")) // Display should be 0
        assertNotNull(viewModel.playAlarmEvent.value?.getContentIfNotHandled()) // Alarm event should be triggered
    }

    @Test
    fun `startStop clicking in timer mode with 0 time shows toast`() = runTest {
        viewModel.onModeToggleClicked() // Switch to Timer mode
        // Counter is 0
        viewModel.onStartStopClicked()
        assertEquals("Add time to timer first", viewModel.toastMessageEvent.value?.getContentIfNotHandled())
        assertFalse(viewModel.isRunning.value ?: true) // Should not start
    }

    // Helper to advance coroutine time and run pending tasks
    private fun advanceTimeBy(delayTimeMillis: Long) {
        testDispatcher.scheduler.advanceTimeBy(delayTimeMillis)
        testDispatcher.scheduler.runCurrent() // Important for UnconfinedTestDispatcher to execute tasks scheduled for now
    }
}
