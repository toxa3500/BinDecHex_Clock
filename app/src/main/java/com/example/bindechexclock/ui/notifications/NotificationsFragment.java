package com.example.bindechexclock.ui.notifications;

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

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel; // Changed to private

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class); // Changed
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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

            timeManager.getHexTimeLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String hexTimeValue) {
                    // The old TimeManager used "  ->  " for hex, let's replicate that.
                    // String prefix = getString(R.string.time_in) + " " + getString(R.string.title_hex) + "\n";
                    String prefix = getString(R.string.time_in) + " " + getString(R.string.title_hex) + "  ->  ";
                    notificationsViewModel.setmText(prefix + hexTimeValue); // hexTimeValue likely includes \n
                }
            });
        }
    }
}