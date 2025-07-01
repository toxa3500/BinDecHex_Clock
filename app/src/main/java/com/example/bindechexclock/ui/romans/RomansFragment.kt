package com.example.bindechexclock.ui.romans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Required for by viewModels()
import androidx.lifecycle.Observer
import com.example.bindechexclock.MainActivity
import com.example.bindechexclock.R
import com.example.bindechexclock.TimeManager

class RomansFragment : Fragment() {

    // Use the 'by viewModels()' Kotlin property delegate from fragment-ktx
    private val romansViewModel: RomansViewModel by viewModels()
    private var textViewRomans: TextView? = null // To hold the TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_romans, container, false)
        textViewRomans = root.findViewById(R.id.text_romans)

        // Observe LiveData from RomansViewModel
        romansViewModel.text.observe(viewLifecycleOwner, Observer { s ->
            textViewRomans?.text = s
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access TimeManager via MainActivity
        (activity as? MainActivity)?.let { mainActivity ->
            val timeManager: TimeManager = mainActivity.timeManager // Accessing Kotlin property

            // Observe LiveData from TimeManager
            timeManager.romanTimeLiveData.observe(viewLifecycleOwner, Observer { romanTimeValue ->
                // getString requires a Context, which Fragment provides
                val prefix = getString(R.string.time_in) + " " + getString(R.string.title_romans) + "\n"
                romansViewModel.updateText(prefix + romanTimeValue)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the reference to the view to prevent memory leaks
        textViewRomans = null
    }
}
