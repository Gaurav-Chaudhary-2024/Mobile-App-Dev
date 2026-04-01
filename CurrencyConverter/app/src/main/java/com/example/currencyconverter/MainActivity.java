package com.example.currencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.HashMap;
import java.util.Map;

/**
 * MainActivity.java
 * ─────────────────────────────────────────────────────────────────
 * Currency Converter screen.
 *
 * Concepts used (per syllabus):
 *   • Activity + Activity Lifecycle (onCreate, onResume)
 *   • ConstraintLayout with multiple Views (TextView, EditText,
 *     Spinner, Button, CardView)
 *   • Explicit Intent  → navigate to SettingsActivity
 *   • Toast            → validation messages
 *   • SharedPreferences → persist theme choice across sessions
 *   • AppCompatDelegate → apply Light / Dark theme at runtime
 * ─────────────────────────────────────────────────────────────────
 */
public class MainActivity extends AppCompatActivity {

    // ── UI components ────────────────────────────────────────────
    private EditText etAmount;
    private Spinner  spinnerFrom, spinnerTo;
    private Button   btnConvert, btnSettings;
    private TextView tvResult;

    // ── Currency data ────────────────────────────────────────────

    /**
     * The four supported currencies shown in both Spinners.
     * Order matches the spinner positions (0=INR, 1=USD, 2=JPY, 3=EUR).
     */
    private final String[] CURRENCIES = {"INR", "USD", "JPY", "EUR"};

    /**
     * Exchange rates expressed as: 1 USD = X units of each currency.
     * Using fixed/static rates suitable for a demo assignment.
     * (A production app would fetch live rates from an API.)
     *
     * Approximate rates as of early 2025:
     *   1 USD = 83.50  INR
     *   1 USD =  1.00  USD  (base)
     *   1 USD = 149.50 JPY
     *   1 USD =  0.92  EUR
     */
    private final Map<String, Double> RATE_VS_USD = new HashMap<>();

    // Tracks which spinner positions the user has chosen
    private int fromPos = 0; // default: INR
    private int toPos   = 1; // default: USD

    // SharedPreferences file and key names
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_DARK   = "dark_mode";

    // ── Activity Lifecycle ───────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*
         * IMPORTANT: applyTheme() must be called BEFORE super.onCreate()
         * and setContentView() so the correct theme is set before
         * any views are inflated.
         */
        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Populate exchange-rate map
        initRates();

        // Bind XML views to Java variables (taught in Activities lecture)
        etAmount    = findViewById(R.id.et_amount);
        spinnerFrom = findViewById(R.id.spinner_from);
        spinnerTo   = findViewById(R.id.spinner_to);
        btnConvert  = findViewById(R.id.btn_convert);
        btnSettings = findViewById(R.id.btn_settings);
        tvResult    = findViewById(R.id.tv_result);

        // Set up both Spinners with the currency list
        setupSpinners();

        // ── Convert button: perform currency conversion ──────────
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });

        /*
         * ── Settings button: Explicit Intent to SettingsActivity ──
         * (Explicit Intents taught in the Intents lecture)
         */
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * onResume() is called every time this Activity comes back to the
     * foreground — including after returning from SettingsActivity.
     *
     * We re-apply the theme here so if the user toggled Dark Mode in
     * Settings, the change is reflected immediately on this screen.
     * recreate() forces the Activity to re-inflate all views with the
     * correct theme colours.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Check if the saved preference differs from the current mode
        SharedPreferences prefs   = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean savedDark         = prefs.getBoolean(KEY_DARK, false);
        int     currentNightMode  = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean currentlyDark     = (currentNightMode
                == android.content.res.Configuration.UI_MODE_NIGHT_YES);

        // Recreate only if there is a mismatch (avoids infinite recreation)
        if (savedDark != currentlyDark) {
            applyTheme();
            recreate();
        }
    }

    // ── Helper Methods ───────────────────────────────────────────

    /**
     * Fills the RATE_VS_USD map.
     * All values represent: how many units of that currency equal 1 USD.
     */
    private void initRates() {
        RATE_VS_USD.put("USD",  1.0);
        RATE_VS_USD.put("INR", 83.5);
        RATE_VS_USD.put("JPY", 149.5);
        RATE_VS_USD.put("EUR",  0.92);
    }

    /**
     * Creates an ArrayAdapter from the CURRENCIES array and attaches
     * it to both Spinners.  Also registers item-selected listeners so
     * we always know the current from/to positions.
     *
     * ArrayAdapter + Spinner is taught in the Android UI Design lecture.
     */
    private void setupSpinners() {

        // Build adapter using the built-in simple_spinner_item layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CURRENCIES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Default: INR → USD
        spinnerFrom.setSelection(0);
        spinnerTo.setSelection(1);

        // Listener on "From" spinner
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                fromPos = position;
                tvResult.setText(getString(R.string.result_placeholder)); // clear stale result
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });

        // Listener on "To" spinner
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                toPos = position;
                tvResult.setText(getString(R.string.result_placeholder)); // clear stale result
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });
    }

    /**
     * Reads the amount entered by the user, validates it, then converts
     * from the chosen source currency to the chosen target currency.
     *
     * Conversion formula (two-step via USD as the base currency):
     *   Step 1 — source → USD : amountInUSD   = amount   / rate(sourceCurrency)
     *   Step 2 — USD → target : result         = amountInUSD * rate(targetCurrency)
     *
     * Example: 1000 INR → JPY
     *   amountInUSD = 1000 / 83.5  ≈ 11.976 USD
     *   result      = 11.976 * 149.5 ≈ 1790.31 JPY
     */
    private void performConversion() {

        // ── Input validation ─────────────────────────────────────
        String amountStr = etAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            // Toast — taught in Android UI Design lecture
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount < 0) {
            Toast.makeText(this, "Amount cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }

        // ── Conversion ───────────────────────────────────────────
        String fromCurrency = CURRENCIES[fromPos];
        String toCurrency   = CURRENCIES[toPos];

        double fromRate = RATE_VS_USD.get(fromCurrency); // e.g. INR → 83.5
        double toRate   = RATE_VS_USD.get(toCurrency);   // e.g. JPY → 149.5

        double amountInUSD = amount / fromRate;           // convert to USD first
        double result      = amountInUSD * toRate;        // then to target currency

        // ── Display result ───────────────────────────────────────
        // Format: "1,000.00 INR  =  11,934.93 JPY"
        String resultText = String.format(
                "%.2f %s  =  %.2f %s",
                amount, fromCurrency,
                result, toCurrency
        );
        tvResult.setText(resultText);
    }

    /**
     * Reads the dark_mode boolean from SharedPreferences and calls
     * AppCompatDelegate to set the global night mode accordingly.
     *
     * Must be called before super.onCreate() / setContentView().
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