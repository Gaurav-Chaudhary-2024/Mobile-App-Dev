package com.example.snapvault;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity — hosts Bottom Navigation and Top App Bar.
 *
 * Two tabs:
 * 1. Camera tab → CameraFragment
 * 2. Gallery tab → GalleryFragment
 *
 * Uses explicit fragment transactions (taught in class).
 * Fragment instances are reused — not recreated on tab switch.
 */
public class MainActivity extends AppCompatActivity {

    // Fragment instances — created once and reused
    private final CameraFragment  cameraFragment  = new CameraFragment();
    private final GalleryFragment galleryFragment = new GalleryFragment();

    // Track active fragment to avoid reloading same fragment
    private Fragment activeFragment;

    // Top App Bar
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topAppBar = findViewById(R.id.top_app_bar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Load Camera tab by default
        if (savedInstanceState == null) {
            loadFragment(cameraFragment, getString(R.string.tab_camera));
            bottomNav.setSelectedItemId(R.id.nav_camera);
        }

        // Tab selection listener
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_camera) {
                loadFragment(cameraFragment, getString(R.string.tab_camera));
                return true;
            } else if (id == R.id.nav_gallery) {
                loadFragment(galleryFragment, getString(R.string.tab_gallery));
                return true;
            }
            return false;
        });
    }

    /**
     * Replaces the fragment in fragment_container.
     * Updates app bar title to current tab name.
     *
     * @param fragment Fragment to show
     * @param title    Tab name for app bar
     */
    private void loadFragment(Fragment fragment, String title) {
        // Skip if already showing this fragment
        if (fragment == activeFragment) return;

        // Update app bar title
        topAppBar.setTitle(title);

        // Fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);
        transaction.commit();

        activeFragment = fragment;
    }
}