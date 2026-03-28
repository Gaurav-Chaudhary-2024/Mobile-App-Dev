package com.example.mediasense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * AudioFragment — Day 1: Placeholder screen.
 *
 * Day 2: Full audio player implementation will go here.
 * Responsibilities (Day 2+):
 *   - Open an audio file from device storage
 *   - Play / Pause / Stop / Restart using Media3 ExoPlayer
 */
public class AudioFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the Day 1 placeholder layout
        return inflater.inflate(R.layout.fragment_audio, container, false);
    }
}