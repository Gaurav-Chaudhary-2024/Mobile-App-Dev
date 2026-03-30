package com.example.mediasense;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * VideoFragment — Final Day: Full Screen Video Player
 *
 * Features:
 * - Enter any video URL (MP4, HLS, DASH)
 * - Stream using ExoPlayer Media3
 * - Full screen PlayerView
 * - Play / Pause / Stop / Restart
 * - SeekBar with timestamps
 * - Buffering spinner
 * - Snackbar error handling
 * - Sample URL pre-filled for immediate testing
 */
public class VideoFragment extends Fragment {

    // UI
    private PlayerView          playerView;
    private TextInputEditText   editVideoUrl;
    private MaterialButton      btnOpenUrl;
    private MaterialButton      btnPlayPause;
    private MaterialButton      btnStop;
    private MaterialButton      btnRestart;
    private SeekBar             seekBar;
    private TextView            txtCurrentTime;
    private TextView            txtTotalTime;
    private ProgressBar         progressBuffering;
    private View                rootView;

    // ExoPlayer
    private ExoPlayer exoPlayer;

    // SeekBar updater
    private final Handler  handler     = new Handler(Looper.getMainLooper());
    private       Runnable seekUpdater;

    // State
    private boolean isPlaying = false;
    private boolean isLoaded  = false;

    // Free public test video — works without any login
    private static final String SAMPLE_URL =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_video, container, false);
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
        // Pre-fill sample URL
        editVideoUrl.setText(SAMPLE_URL);
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
        playerView        = view.findViewById(R.id.player_view);
        editVideoUrl      = view.findViewById(R.id.edit_video_url);
        btnOpenUrl        = view.findViewById(R.id.btn_open_url);
        btnPlayPause      = view.findViewById(R.id.btn_video_play_pause);
        btnStop           = view.findViewById(R.id.btn_video_stop);
        btnRestart        = view.findViewById(R.id.btn_video_restart);
        seekBar           = view.findViewById(R.id.seekbar_video);
        txtCurrentTime    = view.findViewById(R.id.txt_video_current_time);
        txtTotalTime      = view.findViewById(R.id.txt_video_total_time);
        progressBuffering = view.findViewById(R.id.progress_buffering);
    }

    private void initExoPlayer() {
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(exoPlayer);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case Player.STATE_BUFFERING:
                        progressBuffering.setVisibility(View.VISIBLE);
                        break;
                    case Player.STATE_READY:
                        progressBuffering.setVisibility(View.GONE);
                        isLoaded = true;
                        long dur = exoPlayer.getDuration();
                        txtTotalTime.setText(formatTime(dur));
                        seekBar.setMax((int) dur);
                        showSnackbar(getString(R.string.video_ready));
                        break;
                    case Player.STATE_ENDED:
                        progressBuffering.setVisibility(View.GONE);
                        updatePlayPauseButton(false);
                        seekBar.setProgress(0);
                        txtCurrentTime.setText(getString(R.string.default_time));
                        isPlaying = false;
                        handler.removeCallbacks(seekUpdater);
                        break;
                    case Player.STATE_IDLE:
                        progressBuffering.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                progressBuffering.setVisibility(View.GONE);
                showSnackbar(getString(R.string.video_error));
                isLoaded  = false;
                isPlaying = false;
                updatePlayPauseButton(false);
            }
        });
    }

    private void setupListeners() {
        btnOpenUrl.setOnClickListener(v -> loadVideoFromUrl());

        // Allow keyboard "Go" button to load URL
        editVideoUrl.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                loadVideoFromUrl();
                return true;
            }
            return false;
        });

        btnPlayPause.setOnClickListener(v -> {
            if (!isLoaded) {
                showSnackbar(getString(R.string.select_video_first));
                return;
            }
            if (isPlaying) pauseVideo();
            else           playVideo();
        });

        btnStop.setOnClickListener(v    -> stopVideo());
        btnRestart.setOnClickListener(v -> restartVideo());

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

    private void loadVideoFromUrl() {
        String url = editVideoUrl.getText() != null
                ? editVideoUrl.getText().toString().trim() : "";

        if (TextUtils.isEmpty(url)) {
            showSnackbar(getString(R.string.enter_url_first));
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            showSnackbar(getString(R.string.invalid_url));
            return;
        }

        stopVideo();
        exoPlayer.setMediaItem(MediaItem.fromUri(url));
        exoPlayer.prepare();
        isLoaded  = false;
        isPlaying = false;
        seekBar.setProgress(0);
        txtCurrentTime.setText(getString(R.string.default_time));
        txtTotalTime.setText(getString(R.string.default_time));
        updatePlayPauseButton(false);
        showSnackbar(getString(R.string.loading_video));
    }

    private void playVideo() {
        exoPlayer.play();
        isPlaying = true;
        updatePlayPauseButton(true);
        handler.post(seekUpdater);
    }

    private void pauseVideo() {
        exoPlayer.pause();
        isPlaying = false;
        updatePlayPauseButton(false);
        handler.removeCallbacks(seekUpdater);
    }

    private void stopVideo() {
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

    private void restartVideo() {
        if (!isLoaded) {
            showSnackbar(getString(R.string.select_video_first));
            return;
        }
        exoPlayer.seekTo(0);
        exoPlayer.play();
        isPlaying = true;
        updatePlayPauseButton(true);
        handler.post(seekUpdater);
    }

    private void updatePlayPauseButton(boolean playing) {
        if (playing) {
            btnPlayPause.setIconResource(android.R.drawable.ic_media_pause);
            btnPlayPause.setText(getString(R.string.btn_pause_video));
        } else {
            btnPlayPause.setIconResource(android.R.drawable.ic_media_play);
            btnPlayPause.setText(getString(R.string.btn_play_video));
        }
    }

    private String formatTime(long millis) {
        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(min);
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec);
    }

    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}