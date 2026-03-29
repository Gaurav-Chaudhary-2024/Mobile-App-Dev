package com.example.mediasense;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * AudioFragment — Day 2: Full Audio Player
 *
   * Features:
 * - Open audio file from device storage using file picker
 * - Play / Pause / Stop / Restart controls
 * - SeekBar with current time and total duration timestamps
 * - Permission handling for Android 6+ and Android 13+
 * - ExoPlayer (Media3) for robust audio playback
 */
public class AudioFragment extends Fragment {

    // ---------------------------------------------------------------
    // UI Components
    // ---------------------------------------------------------------
    private MaterialButton  btnOpenFile;
    private MaterialButton  btnPlayPause;
    private MaterialButton  btnStop;
    private MaterialButton  btnRestart;
    private SeekBar         seekBar;
    private TextView        txtFileName;
    private TextView        txtCurrentTime;
    private TextView        txtTotalTime;

    // ---------------------------------------------------------------
    // ExoPlayer
    // ---------------------------------------------------------------
    private ExoPlayer exoPlayer;

    // ---------------------------------------------------------------
    // Handler — updates seekbar every second while playing
    // ---------------------------------------------------------------
    private final Handler   handler     = new Handler(Looper.getMainLooper());
    private       Runnable  seekUpdater;

    // ---------------------------------------------------------------
    // State
    // ---------------------------------------------------------------
    private Uri selectedAudioUri = null;
    private boolean isPlaying    = false;

    // ---------------------------------------------------------------
    // File Picker Launcher
    // Opens the device file manager and filters for audio files
    // ---------------------------------------------------------------
    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            selectedAudioUri = uri;
                            // Extract file name from URI and show it
                            String fileName = getFileNameFromUri(uri);
                            txtFileName.setText(fileName);
                            // Load into ExoPlayer immediately
                            loadAudioFile(uri);
                        }
                    }
            );

    // ---------------------------------------------------------------
    // Permission Launcher
    // Requests storage permission then opens file picker if granted
    // ---------------------------------------------------------------
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            openFilePicker();
                        } else {
                            showToast(getString(R.string.permission_denied));
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
        return inflater.inflate(R.layout.fragment_audio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind all UI components
        bindViews(view);

        // Initialize ExoPlayer
        initExoPlayer();

        // Set up all button click listeners
        setupListeners();

        // Set up seekbar update runnable
        setupSeekUpdater();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Pause playback when user leaves the fragment
        // to avoid audio playing in background unexpectedly
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
            updatePlayPauseButton(false);
        }
        // Stop seekbar updates
        handler.removeCallbacks(seekUpdater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release ExoPlayer resources when view is destroyed
        // This is critical to avoid memory leaks
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        handler.removeCallbacks(seekUpdater);
    }

    // ---------------------------------------------------------------
    // Setup Methods
    // ---------------------------------------------------------------

    /**
     * Binds all XML views to Java variables using findViewById.
     */
    private void bindViews(View view) {
        btnOpenFile    = view.findViewById(R.id.btn_open_file);
        btnPlayPause   = view.findViewById(R.id.btn_play_pause);
        btnStop        = view.findViewById(R.id.btn_stop);
        btnRestart     = view.findViewById(R.id.btn_restart);
        seekBar        = view.findViewById(R.id.seekbar_audio);
        txtFileName    = view.findViewById(R.id.txt_file_name);
        txtCurrentTime = view.findViewById(R.id.txt_current_time);
        txtTotalTime   = view.findViewById(R.id.txt_total_time);
    }

    /**
     * Initializes ExoPlayer and adds a listener to detect
     * when playback ends naturally (song finishes).
     */
    private void initExoPlayer() {
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();

        // Listen for playback state changes
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    // Song finished — reset UI to stopped state
                    updatePlayPauseButton(false);
                    seekBar.setProgress(0);
                    txtCurrentTime.setText(getString(R.string.default_time));
                    isPlaying = false;
                    handler.removeCallbacks(seekUpdater);
                }
                if (state == Player.STATE_READY) {
                    // Update total duration once media is loaded
                    long duration = exoPlayer.getDuration();
                    txtTotalTime.setText(formatTime(duration));
                    seekBar.setMax((int) duration);
                }
            }
        });
    }

    /**
     * Sets up click listeners for all buttons and the seekbar.
     */
    private void setupListeners() {

        // Open File button — checks permission then opens file picker
        btnOpenFile.setOnClickListener(v -> checkPermissionAndOpenFile());

        // Play/Pause button — toggles between play and pause
        btnPlayPause.setOnClickListener(v -> {
            if (selectedAudioUri == null) {
                showToast(getString(R.string.select_audio));
                return;
            }
            if (isPlaying) {
                pauseAudio();
            } else {
                playAudio();
            }
        });

        // Stop button — stops playback and resets to beginning
        btnStop.setOnClickListener(v -> stopAudio());

        // Restart button — seeks to beginning and plays
        btnRestart.setOnClickListener(v -> restartAudio());

        // SeekBar — user drags to seek to a position in the audio
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && exoPlayer != null) {
                    // Only seek if user dragged (fromUser = true)
                    exoPlayer.seekTo(progress);
                    txtCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause seekbar auto-update while user is dragging
                handler.removeCallbacks(seekUpdater);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume seekbar auto-update after user releases
                if (isPlaying) {
                    handler.post(seekUpdater);
                }
            }
        });
    }

    /**
     * Creates the runnable that updates the seekbar
     * and current time every 500ms while audio is playing.
     */
    private void setupSeekUpdater() {
        seekUpdater = new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && exoPlayer.isPlaying()) {
                    long currentPos = exoPlayer.getCurrentPosition();
                    seekBar.setProgress((int) currentPos);
                    txtCurrentTime.setText(formatTime(currentPos));
                    // Schedule next update in 500ms
                    handler.postDelayed(this, 500);
                }
            }
        };
    }

    // ---------------------------------------------------------------
    // Playback Controls
    // ---------------------------------------------------------------

    /**
     * Plays audio from current position.
     */
    private void playAudio() {
        if (exoPlayer != null) {
            exoPlayer.play();
            isPlaying = true;
            updatePlayPauseButton(true);
            // Start updating seekbar
            handler.post(seekUpdater);
        }
    }

    /**
     * Pauses audio at current position.
     */
    private void pauseAudio() {
        if (exoPlayer != null) {
            exoPlayer.pause();
            isPlaying = false;
            updatePlayPauseButton(false);
            handler.removeCallbacks(seekUpdater);
        }
    }

    /**
     * Stops audio and resets to beginning.
     */
    private void stopAudio() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.seekTo(0);
            isPlaying = false;
            updatePlayPauseButton(false);
            seekBar.setProgress(0);
            txtCurrentTime.setText(getString(R.string.default_time));
            handler.removeCallbacks(seekUpdater);
        }
    }

    /**
     * Seeks to beginning and plays from start.
     */
    private void restartAudio() {
        if (selectedAudioUri == null) {
            showToast(getString(R.string.select_audio));
            return;
        }
        if (exoPlayer != null) {
            exoPlayer.seekTo(0);
            exoPlayer.play();
            isPlaying = true;
            updatePlayPauseButton(true);
            handler.post(seekUpdater);
        }
    }

    /**
     * Loads an audio file URI into ExoPlayer.
     * Prepares but does NOT auto-play.
     */
    private void loadAudioFile(Uri uri) {
        if (exoPlayer != null) {
            MediaItem mediaItem = MediaItem.fromUri(uri);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            // Reset UI state
            seekBar.setProgress(0);
            txtCurrentTime.setText(getString(R.string.default_time));
            isPlaying = false;
            updatePlayPauseButton(false);
        }
    }

    // ---------------------------------------------------------------
    // Permission & File Picker
    // ---------------------------------------------------------------

    /**
     * Checks storage permission based on Android version:
     * - Android 13+ (API 33): READ_MEDIA_AUDIO
     * - Android 6-12 (API 23-32): READ_EXTERNAL_STORAGE
     * - Below Android 6: No runtime permission needed
     */
    private void checkPermissionAndOpenFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses granular media permissions
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6-12
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            // Below Android 6 — no runtime permission needed
            openFilePicker();
        }
    }

    /**
     * Opens the system file picker filtered for audio files.
     */
    private void openFilePicker() {
        filePickerLauncher.launch("audio/*");
    }

    // ---------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------

    /**
     * Updates Play/Pause button icon and text based on state.
     */
    private void updatePlayPauseButton(boolean playing) {
        if (playing) {
            btnPlayPause.setIconResource(android.R.drawable.ic_media_pause);
            btnPlayPause.setText(getString(R.string.btn_pause));
        } else {
            btnPlayPause.setIconResource(android.R.drawable.ic_media_play);
            btnPlayPause.setText(getString(R.string.btn_play));
        }
    }

    /**
     * Formats milliseconds into MM:SS format.
     * Example: 65000ms → "01:05"
     */
    private String formatTime(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    /**
     * Extracts a readable file name from a URI.
     * Falls back to the raw URI string if name can't be determined.
     */
    private String getFileNameFromUri(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            int cut = path.lastIndexOf('/');
            if (cut != -1) {
                return path.substring(cut + 1);
            }
        }
        return uri.toString();
    }

    /**
     * Shows a short Toast message.
     */
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}