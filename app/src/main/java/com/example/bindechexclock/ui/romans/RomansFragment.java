package com.example.bindechexclock.ui.romans;

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

public class RomansFragment extends Fragment {

    private RomansViewModel romansViewModel; // Changed to private

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        romansViewModel =
                new ViewModelProvider(this).get(RomansViewModel.class); // Changed
        View root = inflater.inflate(R.layout.fragment_romans, container, false);
        final TextView textView = root.findViewById(R.id.text_romans);
        romansViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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

            timeManager.getRomanTimeLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String romanTimeValue) {
                    String prefix = getString(R.string.time_in) + " " + getString(R.string.title_romans) + "\n";
                    romansViewModel.setmText(prefix + romanTimeValue);
                }
            });
        }
    }
}
