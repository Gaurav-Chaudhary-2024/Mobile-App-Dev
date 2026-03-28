package com.example.mediasense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * SensorsFragment — Day 1: Placeholder screen.
 *
 * Day 4: Live sensor dashboard will go here.
 * Responsibilities (Day 4+):
 *   - Register SensorEventListener for:
 *       1. Accelerometer (TYPE_ACCELEROMETER)
 *       2. Light        (TYPE_LIGHT)
 *       3. Proximity    (TYPE_PROXIMITY)
 *   - Display live updating values for each sensor
 *   - Unregister listener onPause to save battery
 */
public class SensorsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }
}