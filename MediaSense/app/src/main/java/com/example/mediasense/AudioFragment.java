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
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * AudioFragment — Final Day: Polished Audio Player
 *
 * Polish improvements:
 * - Snackbar replaces Toast for better UX
 * - Open File button moved to top
 * - Cleaner code organization
 */
public class AudioFragment extends Fragment {

    // UI
    private MaterialButton btnOpenFile;
    private MaterialButton btnPlayPause;
    private MaterialButton btnStop;
    private MaterialButton btnRestart;
    private SeekBar        seekBar;
    private TextView       txtFileName;
    private TextView       txtCurrentTime;
    private TextView       txtTotalTime;
    private View           rootView;

    // ExoPlayer
    private ExoPlayer exoPlayer;

    // SeekBar updater
    private final Handler  handler     = new Handler(Looper.getMainLooper());
    private       Runnable seekUpdater;

    // State
    private Uri     selectedAudioUri = null;
    private boolean isPlaying        = false;

    // File picker — opens system file manager filtered for audio
    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            selectedAudioUri = uri;
                            txtFileName.setText(getFileNameFromUri(uri));
                            loadAudioFile(uri);
                            showSnackbar(getString(R.string.audio_loaded));
                        }
                    }
            );

    // Permission launcher
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            openFilePicker();
                        } else {
                            showSnackbar(getString(R.string.permission_denied));
                        }
                    }
            );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_audio, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        initExoPlayer();
        setupListeners();
        setupSeekUpdater();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
            updatePlayPauseButton(false);
        }
        handler.removeCallbacks(seekUpdater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        handler.removeCallbacks(seekUpdater);
    }

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

    private void initExoPlayer() {
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    updatePlayPauseButton(false);
                    seekBar.setProgress(0);
                    txtCurrentTime.setText(getString(R.string.default_time));
                    isPlaying = false;
                    handler.removeCallbacks(seekUpdater);
                }
                if (state == Player.STATE_READY) {
                    long duration = exoPlayer.getDuration();
                    txtTotalTime.setText(formatTime(duration));
                    seekBar.setMax((int) duration);
                }
            }
        });
    }

    private void setupListeners() {
        btnOpenFile.setOnClickListener(v -> checkPermissionAndOpenFile());

        btnPlayPause.setOnClickListener(v -> {
            if (selectedAudioUri == null) {
                showSnackbar(getString(R.string.select_audio));
                return;
            }
            if (isPlaying) pauseAudio();
            else           playAudio();
        });

        btnStop.setOnClickListener(v    -> stopAudio());
        btnRestart.setOnClickListener(v -> restartAudio());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (fromUser && exoPlayer != null) {
                    exoPlayer.seekTo(progress);
                    txtCurrentTime.setText(formatTime(progress));
                }
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {
                handler.removeCallbacks(seekUpdater);
            }
            @Override public void onStopTrackingTouch(SeekBar sb) {
                if (isPlaying) handler.post(seekUpdater);
            }
        });
    }

    private void setupSeekUpdater() {
        seekUpdater = new Runnable() {
            @Override public void run() {
                if (exoPlayer != null && exoPlayer.isPlaying()) {
                    long pos = exoPlayer.getCurrentPosition();
                    seekBar.setProgress((int) pos);
                    txtCurrentTime.setText(formatTime(pos));
                    handler.postDelayed(this, 500);
                }
            }
        };
    }

    private void playAudio() {
        exoPlayer.play();
        isPlaying = true;
        updatePlayPauseButton(true);
        handler.post(seekUpdater);
    }

    private void pauseAudio() {
        exoPlayer.pause();
        isPlaying = false;
        updatePlayPauseButton(false);
        handler.removeCallbacks(seekUpdater);
    }

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

    private void restartAudio() {
        if (selectedAudioUri == null) {
            showSnackbar(getString(R.string.select_audio));
            return;
        }
        exoPlayer.seekTo(0);
        exoPlayer.play();
        isPlaying = true;
        updatePlayPauseButton(true);
        handler.post(seekUpdater);
    }

    private void loadAudioFile(Uri uri) {
        if (exoPlayer != null) {
            exoPlayer.setMediaItem(MediaItem.fromUri(uri));
            exoPlayer.prepare();
            seekBar.setProgress(0);
            txtCurrentTime.setText(getString(R.string.default_time));
            isPlaying = false;
            updatePlayPauseButton(false);
        }
    }

    private void checkPermissionAndOpenFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            openFilePicker();
        }
    }

    private void openFilePicker() {
        filePickerLauncher.launch("audio/*");
    }

    private void updatePlayPauseButton(boolean playing) {
        if (playing) {
            btnPlayPause.setIconResource(android.R.drawable.ic_media_pause);
            btnPlayPause.setText(getString(R.string.btn_pause));
        } else {
            btnPlayPause.setIconResource(android.R.drawable.ic_media_play);
            btnPlayPause.setText(getString(R.string.btn_play));
        }
    }

    private String formatTime(long millis) {
        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(min);
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec);
    }

    private String getFileNameFromUri(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            int cut = path.lastIndexOf('/');
            if (cut != -1) return path.substring(cut + 1);
        }
        return uri.toString();
    }

    /**
     * Shows a Snackbar at the bottom of the screen.
     * Better UX than Toast — dismissible, styled with Material 3.
     */
    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}