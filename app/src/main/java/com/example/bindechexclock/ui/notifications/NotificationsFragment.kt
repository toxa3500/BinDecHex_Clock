package com.example.bindechexclock.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bindechexclock.MainActivity
import com.example.bindechexclock.R
import com.example.bindechexclock.TimeManager
import com.example.bindechexclock.databinding.FragmentNotificationsBinding // Import view binding class

class NotificationsFragment : Fragment() {

    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        notificationsViewModel.text.observe(viewLifecycleOwner) { s ->
            binding.textNotifications.text = s
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.let { mainActivity ->
            val timeManager: TimeManager = mainActivity.timeManager

            timeManager.hexTimeLiveData.observe(viewLifecycleOwner) { hexTimeValue ->
                val prefix = getString(R.string.time_in) + " " + getString(R.string.title_hex) + "  ->  "
                // Use updateText from the Kotlin ViewModel
                notificationsViewModel.updateText(prefix + hexTimeValue)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
