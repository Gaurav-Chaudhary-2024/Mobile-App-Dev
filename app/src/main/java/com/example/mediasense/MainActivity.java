package com.example.mediasense;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // ------------------------------------------------------------------
    // We create the fragments once and reuse them on tab switches.
    // This avoids re-creating the UI every time the user taps a tab.
    // ------------------------------------------------------------------
    private final AudioFragment   audioFragment   = new AudioFragment();
    private final VideoFragment   videoFragment   = new VideoFragment();
    private final SensorsFragment sensorsFragment = new SensorsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Load the Audio tab by default when the app first opens
        if (savedInstanceState == null) {
            loadFragment(audioFragment);
        }

        // Handle tab selections
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_audio) {
                return loadFragment(audioFragment);
            } else if (id == R.id.nav_video) {
                return loadFragment(videoFragment);
            } else if (id == R.id.nav_sensors) {
                return loadFragment(sensorsFragment);
            }

            return false;
        });
    }

    /**
     * Swaps the fragment shown in fragment_container.
     *
     * @param fragment The Fragment to display.
     * @return true — tells BottomNavigationView the item was handled.
     */
    private boolean loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return true;
    }
}