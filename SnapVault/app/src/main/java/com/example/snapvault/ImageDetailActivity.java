package com.example.snapvault;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * ImageDetailActivity — Day 1: Placeholder
 *
 * Day 3: Full implementation:
 * - Receive image URI via Intent extra
 * - Display full image using Glide
 * - Show metadata: name, path, size, date taken
 * - Delete button with AlertDialog confirmation
 * - After delete → finish() to return to GalleryFragment
 */
public class ImageDetailActivity extends AppCompatActivity {

    // Key for passing image path via Intent
    public static final String EXTRA_IMAGE_PATH = "extra_image_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
    }
}