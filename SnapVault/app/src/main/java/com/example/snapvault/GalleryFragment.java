package com.example.snapvault;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * GalleryFragment — Day 2: Full Gallery Implementation
 */
public class GalleryFragment extends Fragment
        implements ImageAdapter.OnImageClickListener {

    private static final String PREFS_NAME = "SnapVaultPrefs";
    private static final String KEY_GALLERY_URI = "gallery_folder_uri";

    private MaterialButton  btnChooseFolder;
    private TextView        txtSelectedFolder;
    private RecyclerView    recyclerGallery;
    private LinearLayout    layoutEmptyState;
    private TextView        txtEmptyState;
    private View            rootView;

    private ImageAdapter    imageAdapter;
    private List<Uri>       imageUriList = new ArrayList<>();

    private final ActivityResultLauncher<Uri> folderPickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocumentTree(),
                    folderUri -> {
                        if (folderUri != null) {
                            try {
                                requireContext().getContentResolver().takePersistableUriPermission(
                                        folderUri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );
                                saveFolderUri(folderUri);
                                updateUIAndLoad(folderUri);
                            } catch (Exception e) {
                                showSnackbar("Error picking folder: " + e.getMessage());
                            }
                        }
                    }
            );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupRecyclerView();
        setupListeners();
        loadSavedFolder();
    }

    private void bindViews(View view) {
        btnChooseFolder   = view.findViewById(R.id.btn_choose_gallery_folder);
        txtSelectedFolder = view.findViewById(R.id.txt_selected_folder);
        recyclerGallery   = view.findViewById(R.id.recycler_gallery);
        layoutEmptyState  = view.findViewById(R.id.layout_empty_state);
        txtEmptyState     = view.findViewById(R.id.txt_empty_state);
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        recyclerGallery.setLayoutManager(gridLayoutManager);
        imageAdapter = new ImageAdapter(new ArrayList<>(imageUriList), this);
        recyclerGallery.setAdapter(imageAdapter);
    }

    private void setupListeners() {
        btnChooseFolder.setOnClickListener(v -> folderPickerLauncher.launch(null));
    }

    private void loadSavedFolder() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String uriString = prefs.getString(KEY_GALLERY_URI, null);
        if (uriString != null) {
            Uri folderUri = Uri.parse(uriString);
            updateUIAndLoad(folderUri);
        }
    }

    private void saveFolderUri(Uri uri) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_GALLERY_URI, uri.toString()).apply();
    }

    private void updateUIAndLoad(Uri folderUri) {
        String path = folderUri.getPath();
        String displayPath = path != null ? path.replace("/tree/primary:", "📁 /") : folderUri.toString();
        txtSelectedFolder.setText(displayPath);
        loadImagesFromFolder(folderUri);
    }

    private void loadImagesFromFolder(Uri folderUri) {
        imageUriList.clear();
        String folderPath = getFolderPathFromUri(folderUri);
        if (folderPath.isEmpty()) {
            showEmptyState("Invalid folder selected");
            return;
        }

        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA
        };

        String selection = MediaStore.Images.Media.DATA + " LIKE ?";
        String[] selectionArgs = {folderPath + "/%"};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = requireContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                do {
                    long id = cursor.getLong(idColumn);
                    Uri imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
                    imageUriList.add(imageUri);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            showSnackbar("Error loading images: " + e.getMessage());
        }

        if (imageUriList.isEmpty()) {
            showEmptyState(getString(R.string.no_images_found));
        } else {
            showGrid();
            imageAdapter.updateImages(imageUriList);
        }
    }

    private String getFolderPathFromUri(Uri uri) {
        String path = uri.getPath();
        if (path != null && path.contains(":")) {
            String relativePath = path.split(":")[1];
            return "/storage/emulated/0/" + relativePath;
        }
        return "";
    }

    @Override
    public void onImageClick(Uri imageUri, int position) {
        Intent intent = new Intent(requireContext(), ImageDetailActivity.class);
        intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_PATH, imageUri.toString());
        startActivity(intent);
    }

    private void showEmptyState(String message) {
        layoutEmptyState.setVisibility(View.VISIBLE);
        recyclerGallery.setVisibility(View.GONE);
        txtEmptyState.setText(message);
    }

    private void showGrid() {
        layoutEmptyState.setVisibility(View.GONE);
        recyclerGallery.setVisibility(View.VISIBLE);
    }

    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}