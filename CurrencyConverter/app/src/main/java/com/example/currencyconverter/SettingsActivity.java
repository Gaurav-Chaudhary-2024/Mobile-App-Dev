package com.example.currencyconverter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

/**
 * SettingsActivity.java
 * ─────────────────────────────────────────────────────────────────
 * Settings screen — lets the user toggle between Light and Dark mode.
 *
 * Concepts used (per syllabus):
 *   • Activity + Activity Lifecycle
 *   • ConstraintLayout with SwitchCompat, TextView, Button
 *   • SwitchCompat (taught in Android UI Design lecture)
 *   • SharedPreferences → persist the theme choice
 *   • AppCompatDelegate → apply the theme at runtime
 *   • Back navigation via finish() — standard Activity stack behaviour
 * ─────────────────────────────────────────────────────────────────
 */
public class SettingsActivity extends AppCompatActivity {

    // ── UI components ────────────────────────────────────────────
    private SwitchCompat switchTheme;
    private Button       btnBack;

    // SharedPreferences constants (same as MainActivity)
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_DARK   = "dark_mode";

    // Prevent the checked-change listener firing during initial setup
    private boolean isInitialising = true;

    // ── Activity Lifecycle ───────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply the correct theme before inflating the layout
        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Bind views
        switchTheme = findViewById(R.id.switch_theme);
        btnBack     = findViewById(R.id.btn_back);

        // Set the switch to match the currently saved preference
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark          = prefs.getBoolean(KEY_DARK, false);
        switchTheme.setChecked(isDark);

        isInitialising = false; // initial state set; now safe to listen

        // ── SwitchCompat listener ────────────────────────────────
        // OnCheckedChangeListener is taught in Android UI Design lecture
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Skip during programmatic setup to avoid unintended recreation
                if (isInitialising) return;

                // 1. Save the new preference
                SharedPreferences.Editor editor =
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(KEY_DARK, isChecked);
                editor.apply(); // apply() is async and safe on the main thread

                // 2. Apply the new night mode globally
                AppCompatDelegate.setDefaultNightMode(
                        isChecked
                                ? AppCompatDelegate.MODE_NIGHT_YES
                                : AppCompatDelegate.MODE_NIGHT_NO
                );

                // 3. Recreate this Activity so its own views re-render
                //    with the new theme colours immediately
                recreate();
            }
        });

        // ── Back button ──────────────────────────────────────────
        // finish() pops SettingsActivity off the back stack and
        // returns to MainActivity — standard Activity navigation.
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // returns to MainActivity
            }
        });
    }

    /**
     * Reads dark_mode from SharedPreferences and applies the
     * corresponding AppCompatDelegate night mode.
     * Must be called before super.onCreate().
     */
    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark          = prefs.getBoolean(KEY_DARK, false);

        AppCompatDelegate.setDefaultNightMode(
                isDark
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}