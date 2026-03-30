package com.example.mediasense;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

/**
 * SensorsFragment — Final Day: Live Sensor Dashboard
 *
 * Displays live readings from 3 sensors:
 * 1. Accelerometer — X, Y, Z axis in m/s²
 * 2. Light         — ambient light in lux
 * 3. Proximity     — Near/Far + raw distance in cm
 *
 * Key concepts:
 * SensorManager  → system service to access device sensors
 * Sensor         → represents a physical hardware sensor
 * SensorEventListener → callback fired on every new reading
 *
 * Battery note:
 * MUST unregister listener in onPause() — sensors drain
 * battery significantly when continuously active!
 */
public class SensorsFragment extends Fragment implements SensorEventListener {

    // UI
    private TextView txtAccelX;
    private TextView txtAccelY;
    private TextView txtAccelZ;
    private TextView txtLightValue;
    private TextView txtProximityStatus;
    private TextView txtProximityRaw;
    private View     rootView;

    // Sensor API
    private SensorManager sensorManager;
    private Sensor        accelerometerSensor;
    private Sensor        lightSensor;
    private Sensor        proximitySensor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sensors, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        initSensors();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register listeners when fragment is visible
        registerSensorListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        // CRITICAL: Unregister to save battery when not visible
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private void bindViews(View view) {
        txtAccelX          = view.findViewById(R.id.txt_accel_x);
        txtAccelY          = view.findViewById(R.id.txt_accel_y);
        txtAccelZ          = view.findViewById(R.id.txt_accel_z);
        txtLightValue      = view.findViewById(R.id.txt_light_value);
        txtProximityStatus = view.findViewById(R.id.txt_proximity_status);
        txtProximityRaw    = view.findViewById(R.id.txt_proximity_raw);
    }

    /**
     * Initialises SensorManager and gets each sensor.
     * getDefaultSensor() returns null if sensor not present.
     */
    private void initSensors() {
        sensorManager = (SensorManager) requireActivity()
                .getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometerSensor = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            lightSensor         = sensorManager
                    .getDefaultSensor(Sensor.TYPE_LIGHT);
            proximitySensor     = sensorManager
                    .getDefaultSensor(Sensor.TYPE_PROXIMITY);

            // Inform user if any sensor is missing
            if (accelerometerSensor == null) {
                showSnackbar("Accelerometer not available on this device");
                txtAccelX.setText(getString(R.string.sensor_not_available));
            }
            if (lightSensor == null) {
                showSnackbar("Light sensor not available on this device");
                txtLightValue.setText(getString(R.string.sensor_not_available));
            }
            if (proximitySensor == null) {
                showSnackbar("Proximity sensor not available on this device");
                txtProximityStatus.setText(getString(R.string.sensor_not_available));
            }
        }
    }

    /**
     * Registers this fragment as a listener for all 3 sensors.
     * SENSOR_DELAY_UI = ~60ms update interval, ideal for UI display.
     */
    private void registerSensorListeners() {
        if (sensorManager == null) return;

        if (accelerometerSensor != null) {
            sensorManager.registerListener(
                    this, accelerometerSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }
        if (lightSensor != null) {
            sensorManager.registerListener(
                    this, lightSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }
        if (proximitySensor != null) {
            sensorManager.registerListener(
                    this, proximitySensor,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Called by Android automatically whenever a registered
     * sensor has a new value.
     *
     * event.sensor.getType() → which sensor fired
     * event.values[]         → the actual readings
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {

            case Sensor.TYPE_ACCELEROMETER:
                // X = left/right tilt
                // Y = forward/back tilt
                // Z = face up/down
                txtAccelX.setText(String.format(Locale.getDefault(),
                        "X: %.2f m/s²", event.values[0]));
                txtAccelY.setText(String.format(Locale.getDefault(),
                        "Y: %.2f m/s²", event.values[1]));
                txtAccelZ.setText(String.format(Locale.getDefault(),
                        "Z: %.2f m/s²", event.values[2]));
                break;

            case Sensor.TYPE_LIGHT:
                // values[0] = ambient light in lux
                txtLightValue.setText(String.format(Locale.getDefault(),
                        "%.1f lux", event.values[0]));
                break;

            case Sensor.TYPE_PROXIMITY:
                // values[0] = distance in cm
                // Most phones return 0 (Near) or max range (Far)
                float distance = event.values[0];
                float maxRange = proximitySensor.getMaximumRange();
                String status  = (distance < maxRange) ? "🔴 NEAR" : "🟢 FAR";
                txtProximityStatus.setText(status);
                txtProximityRaw.setText(String.format(Locale.getDefault(),
                        "%.1f cm", distance));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }

    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}