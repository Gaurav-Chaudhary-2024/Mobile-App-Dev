package com.example.mediasense;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.datasource.okhttp.OkHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;

public class VideoFragment extends Fragment {

    private static final String TAG = "VideoFragment";

    // UI
    private PlayerView          playerView;
    private YouTubePlayerView   youtubePlayerView;
    private View                layoutExoControls;
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

    // Players
    private ExoPlayer    exoPlayer;
    private YouTubePlayer activeYouTubePlayer;
    private String        queuedYouTubeId = null;

    // SeekBar updater
    private final Handler  handler     = new Handler(Looper.getMainLooper());
    private       Runnable seekUpdater;

    // State
    private boolean isYouTube = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_video,
                container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        initExoPlayer();
        initYouTubePlayer();
        setupListeners();
        setupSeekUpdater();
        
        // Using a very standard test URL as default
        editVideoUrl.setText("https://www.w3schools.com/html/mov_bbb.mp4");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
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
        if (youtubePlayerView != null) youtubePlayerView.release();
        handler.removeCallbacks(seekUpdater);
    }

    private void bindViews(View view) {
        playerView        = view.findViewById(R.id.player_view);
        youtubePlayerView = view.findViewById(R.id.youtube_player_view);
        layoutExoControls = view.findViewById(R.id.layout_exo_controls);
        editVideoUrl      = view.findViewById(R.id.edit_video_url);
        btnOpenUrl        = view.findViewById(R.id.btn_open_url);
        btnPlayPause      = view.findViewById(R.id.btn_video_play_pause);
        btnStop           = view.findViewById(R.id.btn_video_stop);
        btnRestart        = view.findViewById(R.id.btn_video_restart);
        seekBar           = view.findViewById(R.id.seekbar_video);
        txtCurrentTime    = view.findViewById(R.id.txt_video_current_time);
        txtTotalTime      = view.findViewById(R.id.txt_video_total_time);
        progressBuffering = view.findViewById(R.id.progress_buffering);
        getLifecycle().addObserver(youtubePlayerView);
    }

    /**
     * Checks if the device has an active internet connection.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) 
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    private void initExoPlayer() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        DataSource.Factory httpDataSourceFactory = new OkHttpDataSource.Factory(okHttpClient)
                .setUserAgent("ExoPlayer");

        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(
                requireContext(), httpDataSourceFactory);

        exoPlayer = new ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory))
                .build();
                
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
                        long dur = exoPlayer.getDuration();
                        if (dur > 0) {
                            txtTotalTime.setText(formatTime(dur));
                            seekBar.setMax((int) dur);
                        }
                        updatePlayPauseButton(exoPlayer.getPlayWhenReady());
                        break;
                    case Player.STATE_ENDED:
                        progressBuffering.setVisibility(View.GONE);
                        updatePlayPauseButton(false);
                        seekBar.setProgress(0);
                        txtCurrentTime.setText("00:00");
                        handler.removeCallbacks(seekUpdater);
                        break;
                    case Player.STATE_IDLE:
                        progressBuffering.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    handler.post(seekUpdater);
                } else {
                    handler.removeCallbacks(seekUpdater);
                }
                updatePlayPauseButton(isPlaying);
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                progressBuffering.setVisibility(View.GONE);
                Log.e(TAG, "ExoPlayer Error: " + error.getMessage(), error);
                
                String uiError;
                if (!isNetworkAvailable()) {
                    uiError = "No internet connection. Please check your network.";
                } else {
                    uiError = "Player Error: " + error.getErrorCodeName();
                    Throwable cause = error.getCause();
                    if (cause instanceof HttpDataSource.InvalidResponseCodeException) {
                        HttpDataSource.InvalidResponseCodeException httpError = (HttpDataSource.InvalidResponseCodeException) cause;
                        Log.e(TAG, "HTTP Status: " + httpError.responseCode);
                        uiError = "HTTP Error " + httpError.responseCode + ": Access Denied or Invalid URL.";
                    }
                }
                
                showSnackbar(uiError);
            }
        });
    }

    private void initYouTubePlayer() {
        IFramePlayerOptions options = new IFramePlayerOptions
                .Builder().controls(1).build();
        youtubePlayerView.initialize(
                new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        activeYouTubePlayer = youTubePlayer;
                        if (queuedYouTubeId != null) {
                            activeYouTubePlayer.loadVideo(
                                    queuedYouTubeId, 0f);
                            queuedYouTubeId = null;
                        }
                    }
                }, options);
    }

    private void setupListeners() {
        btnOpenUrl.setOnClickListener(v -> loadVideo());
        btnPlayPause.setOnClickListener(v -> togglePlayback());
        btnStop.setOnClickListener(v -> stopAll());
        btnRestart.setOnClickListener(v -> restartPlayback());

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar sb, int progress,
                                                  boolean fromUser) {
                        if (fromUser && exoPlayer != null) {
                            exoPlayer.seekTo(progress);
                            txtCurrentTime.setText(formatTime(progress));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar sb) {
                        handler.removeCallbacks(seekUpdater);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar sb) {
                        if (exoPlayer != null && exoPlayer.isPlaying()) {
                            handler.post(seekUpdater);
                        }
                    }
                });
    }

    private void setupSeekUpdater() {
        seekUpdater = new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && exoPlayer.isPlaying()) {
                    long pos = exoPlayer.getCurrentPosition();
                    seekBar.setProgress((int) pos);
                    txtCurrentTime.setText(formatTime(pos));
                    handler.postDelayed(this, 500);
                }
            }
        };
    }

    private void loadVideo() {
        if (!isNetworkAvailable()) {
            showSnackbar("No internet connection available.");
            return;
        }

        String url = editVideoUrl.getText() != null
                ? editVideoUrl.getText().toString().trim() : "";

        if (TextUtils.isEmpty(url)) {
            showSnackbar("Please enter a URL");
            return;
        }

        stopAll();
        String youtubeId = extractYouTubeId(url);

        if (youtubeId != null) {
            isYouTube = true;
            playerView.setVisibility(View.GONE);
            youtubePlayerView.setVisibility(View.VISIBLE);
            layoutExoControls.setVisibility(View.GONE);

            if (activeYouTubePlayer != null) {
                activeYouTubePlayer.loadVideo(youtubeId, 0f);
            } else {
                queuedYouTubeId = youtubeId;
                showSnackbar("Preparing YouTube Player...");
            }

        } else {
            if (!url.startsWith("http://")
                    && !url.startsWith("https://")) {
                showSnackbar("Invalid URL — must start with http/https");
                return;
            }

            isYouTube = false;
            playerView.setVisibility(View.VISIBLE);
            youtubePlayerView.setVisibility(View.GONE);
            layoutExoControls.setVisibility(View.VISIBLE);

            exoPlayer.setMediaItem(MediaItem.fromUri(url));
            exoPlayer.prepare();
            exoPlayer.play();
            showSnackbar("Loading video...");
        }
    }

    private String extractYouTubeId(String url) {
        String regex = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+"
                + "\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)"
                + "|youtu\\.be\\/|youtube\\.com\\/shorts\\/"
                + ")([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(regex,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

    private void togglePlayback() {
        if (isYouTube) return;
        if (exoPlayer == null) return;

        if (exoPlayer.isPlaying()) {
            exoPlayer.pause();
        } else {
            exoPlayer.play();
        }
    }

    private void stopAll() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.clearMediaItems();
            exoPlayer.seekTo(0);
        }
        if (activeYouTubePlayer != null) {
            activeYouTubePlayer.pause();
        }
        updatePlayPauseButton(false);
        seekBar.setProgress(0);
        txtCurrentTime.setText("00:00");
        handler.removeCallbacks(seekUpdater);
    }

    private void restartPlayback() {
        if (isYouTube && activeYouTubePlayer != null) {
            activeYouTubePlayer.seekTo(0f);
            activeYouTubePlayer.play();
        } else if (exoPlayer != null) {
            exoPlayer.seekTo(0);
            exoPlayer.play();
        }
    }

    private void updatePlayPauseButton(boolean playing) {
        btnPlayPause.setIconResource(playing
                ? android.R.drawable.ic_media_pause
                : android.R.drawable.ic_media_play);
        btnPlayPause.setText(playing ? "Pause" : "Play");
    }

    private String formatTime(long millis) {
        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec);
    }

    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
