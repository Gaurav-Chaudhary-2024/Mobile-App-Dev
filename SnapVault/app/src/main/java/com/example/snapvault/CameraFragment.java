package com.example.snapvault;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * CameraFragment — Day 1: Full Camera Implementation
 *
 * Features:
 * - Live camera preview using CameraX PreviewView
 * - Capture photo and save to MediaStore (device gallery)
 * - Choose save folder using folder picker
 * - Permission handling for Camera + Storage
 *
 * CameraX concepts:
 * - ProcessCameraProvider: manages camera lifecycle
 * - Preview: use case that shows live viewfinder
 * - ImageCapture: use case that captures still photos
 * - CameraSelector: choose front or back camera
 */
public class CameraFragment extends Fragment {

    // ---------------------------------------------------------------
    // UI
    // ---------------------------------------------------------------
    private PreviewView    cameraPreview;
    private MaterialButton btnCapture;
    private MaterialButton btnChooseFolder;
    private TextView       txtSaveFolder;
    private View           rootView;

    // ---------------------------------------------------------------
    // CameraX
    // ---------------------------------------------------------------
    private ImageCapture imageCapture;
    private Camera       camera;

    // ---------------------------------------------------------------
    // Save folder — default is Pictures/SnapVault
    // ---------------------------------------------------------------
    private String saveFolderName = "SnapVault";
    private Uri    saveFolderUri  = null;

    // ---------------------------------------------------------------
    // Permission Launcher
    // Requests both Camera + Storage permissions at once
    // ---------------------------------------------------------------
    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    permissions -> {
                        boolean cameraGranted = Boolean.TRUE.equals(
                                permissions.get(Manifest.permission.CAMERA));

                        if (cameraGranted) {
                            startCamera();
                        } else {
                            showSnackbar(getString(R.string.camera_permission_denied));
                        }
                    }
            );

    // ---------------------------------------------------------------
    // Folder Picker Launcher
    // Opens document tree picker for user to choose save folder
    // ---------------------------------------------------------------
    private final ActivityResultLauncher<Uri> folderPickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocumentTree(),
                    uri -> {
                        if (uri != null) {
                            saveFolderUri = uri;
                            // Extract folder name from URI for display
                            String path = uri.getPath();
                            if (path != null) {
                                String[] parts = path.split(":");
                                saveFolderName = parts.length > 1
                                        ? parts[parts.length - 1]
                                        : "Custom Folder";
                            }
                            txtSaveFolder.setText(saveFolderName);
                            showSnackbar("Save folder set to: " + saveFolderName);
                        }
                    }
            );

    // ---------------------------------------------------------------
    // Fragment Lifecycle
    // ---------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupListeners();
        checkPermissionsAndStartCamera();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // CameraX lifecycle is tied to fragment lifecycle
        // ProcessCameraProvider handles cleanup automatically
    }

    // ---------------------------------------------------------------
    // Setup
    // ---------------------------------------------------------------

    private void bindViews(View view) {
        cameraPreview   = view.findViewById(R.id.camera_preview);
        btnCapture      = view.findViewById(R.id.btn_capture);
        btnChooseFolder = view.findViewById(R.id.btn_choose_folder);
        txtSaveFolder   = view.findViewById(R.id.txt_save_folder);
    }

    private void setupListeners() {
        // Capture button
        btnCapture.setOnClickListener(v -> capturePhoto());

        // Choose folder button
        btnChooseFolder.setOnClickListener(v ->
                folderPickerLauncher.launch(null));
    }

    // ---------------------------------------------------------------
    // Permissions
    // ---------------------------------------------------------------

    /**
     * Checks required permissions and starts camera if granted.
     * Permission requirements differ by Android version:
     * - Android 13+: CAMERA only (no storage for saving to MediaStore)
     * - Android 9 and below: CAMERA + WRITE_EXTERNAL_STORAGE
     */
    private void checkPermissionsAndStartCamera() {
        if (hasCameraPermission()) {
            startCamera();
        } else {
            requestPermissions();
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // Android 9 and below needs write storage permission
            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });
        } else {
            // Android 10+ handles storage internally
            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA
            });
        }
    }

    // ---------------------------------------------------------------
    // CameraX Setup
    // ---------------------------------------------------------------

    /**
     * Initialises CameraX with two use cases:
     * 1. Preview  — shows live feed in PreviewView
     * 2. ImageCapture — enables photo capture
     *
     * ProcessCameraProvider.getInstance() is async —
     * we use addListener to get the result when ready.
     */
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider =
                        cameraProviderFuture.get();

                // Build Preview use case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(
                        cameraPreview.getSurfaceProvider());

                // Build ImageCapture use case
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(
                                ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                // Use back camera by default
                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_BACK_CAMERA;

                // Unbind any existing use cases before rebinding
                cameraProvider.unbindAll();

                // Bind both use cases to fragment lifecycle
                camera = cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        cameraSelector,
                        preview,
                        imageCapture
                );

            } catch (ExecutionException | InterruptedException e) {
                showSnackbar(getString(R.string.camera_not_available));
            }

        }, ContextCompat.getMainExecutor(requireContext()));
    }

    // ---------------------------------------------------------------
    // Capture Photo
    // ---------------------------------------------------------------

    /**
     * Captures a photo and saves it to MediaStore.
     *
     * MediaStore is Android's media database — saving here
     * makes the photo visible in the device gallery app
     * and works across all Android versions without
     * needing WRITE_EXTERNAL_STORAGE on Android 10+.
     *
     * File naming: SnapVault_20260328_143022.jpg
     */
    private void capturePhoto() {
        if (imageCapture == null) {
            showSnackbar(getString(R.string.camera_not_available));
            return;
        }

        // Generate unique file name with timestamp
        String timeStamp = new SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String fileName = "SnapVault_" + timeStamp + ".jpg";

        // ContentValues tells MediaStore how to store the file
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        // Set save path — use custom folder if chosen, else Pictures/SnapVault
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "Pictures/" + saveFolderName
            );
        }

        // Build output file options for MediaStore
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        requireContext().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build();

        // Take the picture
        imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(
                            @NonNull ImageCapture.OutputFileResults output) {
                        // Success — show save path to user
                        showSnackbar(getString(R.string.photo_saved)
                                + "Pictures/" + saveFolderName
                                + "/" + fileName);
                    }

                    @Override
                    public void onError(
                            @NonNull ImageCaptureException exception) {
                        // Failed — show error message
                        showSnackbar(getString(R.string.capture_failed)
                                + ": " + exception.getMessage());
                    }
                }
        );
    }

    // ---------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------

    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }
}