package com.example.bindechexclock.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bindechexclock.MainActivity
import com.example.bindechexclock.R
import com.example.bindechexclock.TimeManager
import com.example.bindechexclock.databinding.FragmentDashboardBinding // Import view binding class

class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        dashboardViewModel.text.observe(viewLifecycleOwner) { s ->
            binding.textDashboard.text = s
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.let { mainActivity ->
            val timeManager: TimeManager = mainActivity.timeManager

            timeManager.decimalTimeLiveData.observe(viewLifecycleOwner) { decimalTimeValue ->
                val prefix = getString(R.string.time_in) + " " + getString(R.string.title_decimal) + "  ->  "
                // Use updateText from the Kotlin ViewModel
                dashboardViewModel.updateText(prefix + decimalTimeValue)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
