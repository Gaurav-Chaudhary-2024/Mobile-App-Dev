package com.example.snapvault;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * GalleryFragment — Day 1: Placeholder
 *
 * Day 2: Full gallery implementation:
 * - Folder picker using ActivityResultLauncher
 * - Load all images from chosen folder
 * - Display in RecyclerView grid (3 columns)
 * - Click image → open ImageDetailActivity via Intent
 */
public class GalleryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }
}