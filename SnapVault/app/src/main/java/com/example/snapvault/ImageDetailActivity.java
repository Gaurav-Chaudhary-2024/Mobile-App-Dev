package com.example.snapvault;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.database.Cursor;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ImageDetailActivity — Day 2: Full Image Details Screen
 *
 * Receives image URI from GalleryFragment via Intent.
 * Displays:
 *   - Full image using Glide
 *   - Metadata: name, path, size, date taken
 *   - Delete button with AlertDialog confirmation
 *   - After delete → finish() returns to GalleryFragment
 *
 * Key concepts from syllabus:
 * - Activity + Activity Lifecycle
 * - Receiving data from Intent extras
 * - AlertDialog for confirmation
 * - Snackbar for feedback
 * - finish() to go back
 */
public class ImageDetailActivity extends AppCompatActivity {

    // Key for Intent extra — must match what GalleryFragment sends
    public static final String EXTRA_IMAGE_PATH = "extra_image_path";

    // ---------------------------------------------------------------
    // UI
    // ---------------------------------------------------------------
    private ImageView      imgDetailFull;
    private TextView       txtImageName;
    private TextView       txtImagePath;
    private TextView       txtImageSize;
    private TextView       txtImageDate;
    private MaterialButton btnDelete;
    private MaterialToolbar toolbar;

    // ---------------------------------------------------------------
    // Data
    // ---------------------------------------------------------------
    private Uri    imageUri;
    private String imagePath;

    // ---------------------------------------------------------------
    // Activity Lifecycle
    // ---------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        bindViews();
        setupToolbar();
        receiveImageFromIntent();
    }

    // ---------------------------------------------------------------
    // Setup
    // ---------------------------------------------------------------

    private void bindViews() {
        imgDetailFull  = findViewById(R.id.img_detail_full);
        txtImageName   = findViewById(R.id.txt_image_name);
        txtImagePath   = findViewById(R.id.txt_image_path);
        txtImageSize   = findViewById(R.id.txt_image_size);
        txtImageDate   = findViewById(R.id.txt_image_date);
        btnDelete      = findViewById(R.id.btn_delete);
        toolbar        = findViewById(R.id.detail_toolbar);
    }

    /**
     * Sets up the toolbar with back navigation.
     * Back button → finish() → returns to GalleryFragment.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Receives the image URI from the Intent extra
     * sent by GalleryFragment.onImageClick().
     *
     * Then loads image and metadata.
     */
    private void receiveImageFromIntent() {
        String uriString = getIntent().getStringExtra(EXTRA_IMAGE_PATH);

        if (uriString == null) {
            showSnackbar("Error: No image provided");
            finish();
            return;
        }

        imageUri = Uri.parse(uriString);

        // Load full image into ImageView using Glide
        Glide.with(this)
                .load(imageUri)
                .fitCenter()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(imgDetailFull);

        // Load metadata from MediaStore
        loadImageMetadata();

        // Set up delete button
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    // ---------------------------------------------------------------
    // Metadata Loading
    // ---------------------------------------------------------------

    /**
     * Queries MediaStore to get image metadata:
     * - Display name (filename)
     * - File path
     * - File size
     * - Date taken
     *
     * ContentResolver queries are like SQL SELECT statements
     * on Android's media database.
     */
    private void loadImageMetadata() {
        String[] projection = {
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED
        };

        try (Cursor cursor = getContentResolver().query(
                imageUri,
                projection,
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {

                // Get column indices
                int nameCol  = cursor.getColumnIndex(
                        MediaStore.Images.Media.DISPLAY_NAME);
                int pathCol  = cursor.getColumnIndex(
                        MediaStore.Images.Media.DATA);
                int sizeCol  = cursor.getColumnIndex(
                        MediaStore.Images.Media.SIZE);
                int dateCol  = cursor.getColumnIndex(
                        MediaStore.Images.Media.DATE_TAKEN);
                int addedCol = cursor.getColumnIndex(
                        MediaStore.Images.Media.DATE_ADDED);

                // Extract values safely
                String name = nameCol >= 0
                        ? cursor.getString(nameCol) : "Unknown";
                imagePath   = pathCol >= 0
                        ? cursor.getString(pathCol) : "Unknown";
                long size   = sizeCol >= 0
                        ? cursor.getLong(sizeCol) : 0;
                long date   = dateCol >= 0
                        ? cursor.getLong(dateCol) : 0;

                // If date taken not available use date added
                if (date == 0 && addedCol >= 0) {
                    date = cursor.getLong(addedCol) * 1000L;
                }

                // Format size: bytes → KB or MB
                String sizeFormatted = formatFileSize(size);

                // Format date: milliseconds → readable string
                String dateFormatted = formatDate(date);

                // Update UI
                txtImageName.setText(name);
                txtImagePath.setText(imagePath != null
                        ? imagePath : imageUri.toString());
                txtImageSize.setText(sizeFormatted);
                txtImageDate.setText(dateFormatted);

            } else {
                // Fallback if cursor is empty
                txtImageName.setText("Unknown");
                txtImagePath.setText(imageUri.toString());
                txtImageSize.setText("Unknown");
                txtImageDate.setText("Unknown");
            }

        } catch (Exception e) {
            showSnackbar("Error loading metadata: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Delete Image
    // ---------------------------------------------------------------

    /**
     * Shows AlertDialog asking user to confirm deletion.
     *
     * AlertDialog is from the class syllabus:
     * "Dialogs, AlertDialog"
     *
     * Pattern:
     * AlertDialog.Builder → set title/message/buttons → show()
     */
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_confirm_title))
                .setMessage(getString(R.string.delete_confirm_message))
                .setPositiveButton(getString(R.string.delete_yes),
                        (dialog, which) -> deleteImage())
                .setNegativeButton(getString(R.string.delete_no),
                        (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_menu_delete)
                .show();
    }

    /**
     * Deletes the image from MediaStore.
     *
     * getContentResolver().delete() removes the image from
     * both MediaStore database AND the actual file on disk.
     *
     * After deletion:
     * - Show success Snackbar
     * - Call finish() to go back to GalleryFragment
     * - setResult(RESULT_OK) signals gallery to refresh
     */
    private void deleteImage() {
        try {
            int rowsDeleted = getContentResolver()
                    .delete(imageUri, null, null);

            if (rowsDeleted > 0) {
                // Image deleted successfully
                // Set result so GalleryFragment knows to refresh
                setResult(RESULT_OK);
                showSnackbar(getString(R.string.image_deleted));

                // Small delay to show snackbar before going back
                imgDetailFull.postDelayed(this::finish, 1000);
            } else {
                showSnackbar(getString(R.string.delete_failed));
            }

        } catch (SecurityException e) {
            // Android 10+ requires additional permission for deletion
            // Use MediaStore delete request for Android 10+
            showSnackbar(getString(R.string.delete_failed)
                    + ": " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------

    /**
     * Formats file size from bytes to human readable string.
     * Examples:
     *   500 bytes   → "500 B"
     *   1500 bytes  → "1.46 KB"
     *   2500000 bytes → "2.38 MB"
     */
    private String formatFileSize(long bytes) {
        if (bytes <= 0)  return "Unknown";
        if (bytes < 1024) return bytes + " B";
        float kb = bytes / 1024f;
        if (kb < 1024) return String.format(Locale.getDefault(),
                "%.2f KB", kb);
        float mb = kb / 1024f;
        return String.format(Locale.getDefault(), "%.2f MB", mb);
    }

    /**
     * Formats a timestamp in milliseconds to a readable date string.
     * Example: 1711619422000 → "28 Mar 2026, 14:30"
     */
    private String formatDate(long millis) {
        if (millis <= 0) return "Unknown";
        SimpleDateFormat sdf = new SimpleDateFormat(
                "dd MMM yyyy, HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    private void showSnackbar(String message) {
        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT
        ).show();
    }
}