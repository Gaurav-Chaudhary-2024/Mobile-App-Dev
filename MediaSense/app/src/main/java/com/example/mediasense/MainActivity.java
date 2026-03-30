package com.example.mediasense;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity — hosts the Bottom Navigation and Top App Bar.
 *
 * Improvements in Final Day:
 * - Top App Bar shows current tab name
 * - Smooth fade transitions between fragments
 * - Fragment instances reused (not recreated on tab switch)
 */
public class MainActivity extends AppCompatActivity {

    // Fragment instances — created once, reused on tab switch
    private final AudioFragment   audioFragment   = new AudioFragment();
    private final VideoFragment   videoFragment   = new VideoFragment();
    private final SensorsFragment sensorsFragment = new SensorsFragment();

    // Currently shown fragment — tracked to avoid re-adding same fragment
    private Fragment activeFragment;

    // Top App Bar reference
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topAppBar = findViewById(R.id.top_app_bar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Load Audio tab as default on first launch
        if (savedInstanceState == null) {
            loadFragment(audioFragment, getString(R.string.tab_audio));
            bottomNav.setSelectedItemId(R.id.nav_audio);
        }

        // Bottom nav item selection listener
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_audio) {
                loadFragment(audioFragment, getString(R.string.tab_audio));
                return true;
            } else if (id == R.id.nav_video) {
                loadFragment(videoFragment, getString(R.string.tab_video));
                return true;
            } else if (id == R.id.nav_sensors) {
                loadFragment(sensorsFragment, getString(R.string.tab_sensors));
                return true;
            }
            return false;
        });
    }

    /**
     * Swaps the visible fragment with a smooth fade transition.
     * Updates the Top App Bar title to show current tab name.
     *
     * @param fragment  The fragment to show
     * @param title     The tab name to show in the app bar
     */
    private void loadFragment(Fragment fragment, String title) {
        // Don't reload if already showing this fragment
        if (fragment == activeFragment) return;

        // Update app bar title
        topAppBar.setTitle(title);

        // Perform fragment transaction with fade animations
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,   // enter animation
                        R.anim.fade_out   // exit animation
                )
                .replace(R.id.fragment_container, fragment);

        transaction.commit();
        activeFragment = fragment;
    }
}