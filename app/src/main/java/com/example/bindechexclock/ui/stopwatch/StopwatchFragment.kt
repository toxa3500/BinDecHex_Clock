package com.example.bindechexclock.ui.stopwatch

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bindechexclock.R
import com.example.bindechexclock.databinding.FragmentStopwatchBinding // Import view binding class

class StopwatchFragment : Fragment() {

    private val stopwatchViewModel: StopwatchViewModel by viewModels()

    // Declare binding variable - nullable because it's only valid between onCreateView and onDestroyView
    private var _binding: FragmentStopwatchBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!! // Non-null assertion operator for convenience

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStopwatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        stopwatchViewModel.timeDisplay.observe(viewLifecycleOwner) { text ->
            binding.textStopwatch.text = text
        }

        stopwatchViewModel.startStopButtonText.observe(viewLifecycleOwner) { text ->
            binding.buttonStartStop.text = text
        }

        stopwatchViewModel.modeButtonText.observe(viewLifecycleOwner) { text ->
            binding.buttonModeToggle.text = text
        }

        stopwatchViewModel.actionBarTitle.observe(viewLifecycleOwner) { title ->
            (activity as? AppCompatActivity)?.supportActionBar?.title = title
        }

        stopwatchViewModel.playAlarmEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                playAlarmSound()
            }
        }

        stopwatchViewModel.toastMessageEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                context?.let { ctx ->
                    Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.textStopwatch.setOnClickListener { stopwatchViewModel.onTimeDisplayClicked() }
        binding.buttonStartStop.setOnClickListener { stopwatchViewModel.onStartStopClicked() }
        binding.buttonReset.setOnClickListener { stopwatchViewModel.onResetClicked() }

        binding.buttonPlus30.setOnClickListener { stopwatchViewModel.onPlus30Clicked() }
        binding.buttonPlus30.setOnLongClickListener {
            stopwatchViewModel.onPlus30LongClicked()
            true // Consume long click
        }

        binding.buttonMinus30.setOnClickListener { stopwatchViewModel.onMinus30Clicked() }
        binding.buttonMinus30.setOnLongClickListener {
            stopwatchViewModel.onMinus30LongClicked()
            true // Consume long click
        }
        binding.buttonModeToggle.setOnClickListener { stopwatchViewModel.onModeToggleClicked() }
    }

    private fun playAlarmSound() {
        context?.let { ctx ->
            try {
                var alarmSoundUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                if (alarmSoundUri == null) {
                    alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                }

                alarmSoundUri?.let { uri ->
                    val ringtone: Ringtone? = RingtoneManager.getRingtone(ctx, uri)
                    ringtone?.play() ?: Toast.makeText(ctx, "Cannot play alarm: Ringtone not found.", Toast.LENGTH_SHORT).show()
                } ?: Toast.makeText(ctx, "Cannot play alarm: No default sound URI.", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(ctx, "Error playing alarm sound", Toast.LENGTH_SHORT).show()
                // Consider logging the exception e.g. Log.e("StopwatchFragment", "Error playing alarm", e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stopwatchViewModel.onFragmentResumed()
    }

    override fun onPause() {
        super.onPause()
        stopwatchViewModel.onFragmentPaused()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important to clear the binding reference
    }
}
