package com.example.bindechexclock.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bindechexclock.MainActivity;
import com.example.bindechexclock.R;
import com.example.bindechexclock.TimeManager;
// Note: The import for com.example.bindechexclock.ui.home.HomeViewModel will resolve to the Kotlin class
// once the Java version is deleted and the project rebuilds.

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // ViewModelProvider will correctly get the Kotlin HomeViewModel
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        // homeViewModel.getText() will call the 'text' LiveData property's getter in Kotlin
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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

            timeManager.getBinaryTimeLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String binaryTimeValue) {
                    String prefix = getString(R.string.time_in) + " " + getString(R.string.title_binary) + "\n";
                    // Call the renamed method in the Kotlin ViewModel
                    homeViewModel.updateText(prefix + binaryTimeValue);
                }
            });
        }
    }
}