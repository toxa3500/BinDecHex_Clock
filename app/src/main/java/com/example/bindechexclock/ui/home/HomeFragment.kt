package com.example.bindechexclock.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Required for by viewModels()
import androidx.lifecycle.Observer // Still needed for Java LiveData interop if not using Flow
import com.example.bindechexclock.MainActivity
import com.example.bindechexclock.R
import com.example.bindechexclock.TimeManager

class HomeFragment : Fragment() {

    // Use the 'by viewModels()' Kotlin property delegate from fragment-ktx
    private val homeViewModel: HomeViewModel by viewModels()
    private var textViewHome: TextView? = null // To hold the TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        textViewHome = root.findViewById(R.id.text_home)

        // Observe LiveData from HomeViewModel
        // The 'text' LiveData is already exposed by the Kotlin HomeViewModel
        homeViewModel.text.observe(viewLifecycleOwner, Observer { s ->
            textViewHome?.text = s
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access TimeManager via MainActivity
        // Ensure activity is MainActivity and not null
        (activity as? MainActivity)?.let { mainActivity ->
            val timeManager: TimeManager = mainActivity.timeManager // Accessing Kotlin property

            // Observe LiveData from TimeManager
            // The binaryTimeLiveData is already exposed by the Kotlin TimeManager
            timeManager.binaryTimeLiveData.observe(viewLifecycleOwner, Observer { binaryTimeValue ->
                // getString requires a Context, which Fragment provides
                val prefix = getString(R.string.time_in) + " " + getString(R.string.title_binary) + "\n"
                homeViewModel.updateText(prefix + binaryTimeValue)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the reference to the view to prevent memory leaks
        textViewHome = null
    }
}
