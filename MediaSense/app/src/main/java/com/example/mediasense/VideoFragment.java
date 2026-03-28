package com.example.mediasense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * VideoFragment — Day 1: Placeholder screen.
 *
 * Day 3: Full video player implementation will go here.
 * Responsibilities (Day 3+):
 *   - Accept a URL input from the user
 *   - Stream video using Media3 ExoPlayer + PlayerView
 *   - Play / Pause / Stop / Restart controls
 */
public class VideoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }
}