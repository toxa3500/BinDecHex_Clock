package com.example.bindechexclock.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider; // Changed
import androidx.annotation.Nullable; // Added for onViewCreated
import android.os.Bundle; // Added for onViewCreated

import com.example.bindechexclock.MainActivity;
import com.example.bindechexclock.R;
import com.example.bindechexclock.TimeManager;


public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel; // Changed to private

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class); // Changed
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            TimeManager timeManager = mainActivity.getTimeManager();

            timeManager.getDecimalTimeLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String decimalTimeValue) {
                    // The old TimeManager used "  ->  " for decimal, let's replicate that for consistency for now
                    // String prefix = getString(R.string.time_in) + " " + getString(R.string.title_decimal) + "\n";
                    String prefix = getString(R.string.time_in) + " " + getString(R.string.title_decimal) + "  ->  ";
                    dashboardViewModel.setmText(prefix + decimalTimeValue); // decimalTimeValue likely includes \n already
                }
            });
        }
    }
}