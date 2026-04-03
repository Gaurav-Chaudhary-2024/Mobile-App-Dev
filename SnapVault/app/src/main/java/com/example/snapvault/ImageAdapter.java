package com.example.snapvault;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * ImageAdapter — RecyclerView Adapter for the gallery grid.
 *
 * Key concepts used (from class syllabus):
 *
 * RecyclerView Adapter:
 *   Bridges data (list of image URIs) with UI (ImageView cards)
 *   Three mandatory methods:
 *     onCreateViewHolder() → inflates item_image.xml
 *     onBindViewHolder()   → loads image into ImageView using Glide
 *     getItemCount()       → tells RecyclerView how many items
 *
 * ViewHolder Pattern:
 *   Holds references to views inside each grid item.
 *   Avoids calling findViewById() repeatedly —
 *   each view is found ONCE and reused.
 *
 * Click Listener Interface:
 *   We define OnImageClickListener interface here.
 *   GalleryFragment implements it.
 *   This is the standard Android pattern for
 *   RecyclerView item click handling.
 *
 * Glide:
 *   Loads image from URI into ImageView efficiently.
 *   Handles:
 *     - Async loading (no UI freeze)
 *     - Memory caching
 *     - Disk caching
 *     - Placeholder while loading
 *     - Error placeholder if load fails
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    // ---------------------------------------------------------------
    // Click listener interface
    // ---------------------------------------------------------------

    /**
     * Interface for handling image item clicks.
     * Implemented by GalleryFragment.
     */
    public interface OnImageClickListener {
        void onImageClick(Uri imageUri, int position);
    }

    // ---------------------------------------------------------------
    // Data + Listener
    // ---------------------------------------------------------------
    private final List<Uri>             imageUris;
    private final OnImageClickListener  clickListener;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------
    public ImageAdapter(List<Uri> imageUris,
                        OnImageClickListener clickListener) {
        this.imageUris     = imageUris;
        this.clickListener = clickListener;
    }

    // ---------------------------------------------------------------
    // ViewHolder — holds views for ONE grid item
    // ---------------------------------------------------------------

    /**
     * ViewHolder stores the ImageView reference for one grid cell.
     * Created ONCE per visible cell, then RECYCLED as user scrolls.
     * This is the "Recycler" in RecyclerView!
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imgThumbnail;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find views ONCE here — not in onBindViewHolder!
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
        }
    }

    // ---------------------------------------------------------------
    // RecyclerView.Adapter methods
    // ---------------------------------------------------------------

    /**
     * Called when RecyclerView needs a new ViewHolder.
     * Inflates item_image.xml and wraps it in a ViewHolder.
     * Called only for VISIBLE cells (not all items at once).
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * Called to display data for a specific position.
     * Binds image URI to the ImageView using Glide.
     * Also sets click listener for navigation to detail screen.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder,
                                 int position) {
        Uri imageUri = imageUris.get(position);

        // Load image using Glide
        Glide.with(holder.imgThumbnail.getContext())
                .load(imageUri)
                .centerCrop()          // crop to fill square
                .placeholder(android.R.drawable.ic_menu_gallery) // shown while loading
                .error(android.R.drawable.ic_menu_report_image)  // shown if error
                .into(holder.imgThumbnail);

        // Set click listener — navigates to ImageDetailActivity
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onImageClick(imageUri, position);
            }
        });
    }

    /**
     * Returns total number of images in the list.
     * RecyclerView uses this to know when to stop creating cells.
     */
    @Override
    public int getItemCount() {
        return imageUris != null ? imageUris.size() : 0;
    }

    // ---------------------------------------------------------------
    // Data update method
    // ---------------------------------------------------------------

    /**
     * Updates the adapter with a new list of images.
     * Called from GalleryFragment when user picks a new folder.
     */
    public void updateImages(List<Uri> newImages) {
        imageUris.clear();
        imageUris.addAll(newImages);
        // notifyDataSetChanged tells RecyclerView to redraw all items
        notifyDataSetChanged();
    }
}