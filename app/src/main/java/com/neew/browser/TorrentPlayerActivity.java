package com.neew.browser;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog; // or androidx
import android.app.PictureInPictureParams;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.Rational;

import com.neew.browser.TorrentService; // Import Service

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.io.File;
import java.util.ArrayList;

public class TorrentPlayerActivity extends AppCompatActivity {

    private static final String TAG = "TorrentPlayerActivity";

    // Service Binding
    private TorrentService torrentService;
    private boolean isBound = false;

    private CheckBox keepFileCheck;
    private CheckBox overlayKeepCheck; // Added overlay check
    private Button playButton;
    private SurfaceView videoSurface;
    private ProgressBar loadingProgress;
    private TextView statusText;
    private View inputContainer;

    // Core Components
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;

    // Controls UI
    private View controlsOverlay;
    private android.widget.ImageButton btnPlayPause;
    private android.widget.ImageButton btnFullscreen;
    private android.widget.ImageButton btnBack; // Added field
    private android.widget.ImageButton btnInfo; // Added Info button
    private android.widget.ImageButton btnPip; // PiP button
    private TextView textOverlayStats; // Stats overlay
    private android.widget.SeekBar seekBar;
    private TextView textCurrentTime, textTotalTime;
    private TextView textVideoTitle;
    private android.os.Handler handler = new android.os.Handler();
    private Runnable updateProgressAction;
    private Runnable fileReadyWatcher;
    
    // Store aspect ratio for resizing on rotation
    private double mVideoAspectRatio = 0;
    private boolean isInPipMode = false;

    private boolean isTvDevice() {
        android.content.pm.PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(android.content.pm.PackageManager.FEATURE_LEANBACK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torrent_player);

        keepFileCheck = findViewById(R.id.keep_file_check);
        playButton = findViewById(R.id.play_button);
        playButton.setVisibility(View.GONE); // Hide button, managed by service
        videoSurface = findViewById(R.id.video_surface);
        loadingProgress = findViewById(R.id.loading_progress);
        statusText = findViewById(R.id.status_text);
        inputContainer = findViewById(R.id.input_container);
        inputContainer.setVisibility(View.GONE); // Always hide input
        loadingProgress.setVisibility(View.VISIBLE); // Assume loading initially

        // Initialize Controls
        controlsOverlay = findViewById(R.id.controls_overlay);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnFullscreen = findViewById(R.id.btn_fullscreen);
        btnBack = findViewById(R.id.btn_back);
        btnInfo = findViewById(R.id.btn_info);
        btnPip = findViewById(R.id.btn_pip);
        textOverlayStats = findViewById(R.id.text_overlay_stats);
        overlayKeepCheck = findViewById(R.id.overlay_keep_check);
        seekBar = findViewById(R.id.seek_bar);
        textCurrentTime = findViewById(R.id.text_current_time);
        textTotalTime = findViewById(R.id.text_total_time);
        textVideoTitle = findViewById(R.id.text_video_title);

        setupControls();
        
        // Initial system UI setup
        updateSystemUi();
        
        // Listen for layout changes to resize video on rotation
        View parentView = (View) videoSurface.getParent();
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (mVideoAspectRatio != 0) {
                updateVideoSurfaceLayout(parentView.getWidth(), parentView.getHeight());
            }
        });

        // TV-specific behavior: keep controls visible and focusable for DPAD navigation
        if (isTvDevice()) {
            if (controlsOverlay != null) {
                controlsOverlay.setVisibility(View.VISIBLE);
                controlsOverlay.setFocusable(true);
                controlsOverlay.setFocusableInTouchMode(true);
            }
            if (btnPlayPause != null) {
                btnPlayPause.requestFocus();
            }
        }

        // Intent handling moved to MainActivity / Service
        // We just wait for Service binding
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, TorrentService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            TorrentService.LocalBinder binder = (TorrentService.LocalBinder) service;
            torrentService = binder.getService();
            isBound = true;

            // If the service already has a video file, start playback immediately
            File videoFile = torrentService.getCurrentVideoFile();
            if (videoFile != null && videoFile.exists()) {
                loadingProgress.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                textVideoTitle.setText(videoFile.getName());
                initPlayer(videoFile);
            } else if (torrentService.isStreaming()) {
                statusText.setText("Connecting to stream...");
                startFileReadyWatcher();
            } else {
                // Service exists but no active stream
                Toast.makeText(TorrentPlayerActivity.this, "No active torrent", Toast.LENGTH_SHORT).show();
                finish();
            }
            
            // Sync overlay check
            overlayKeepCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (torrentService != null) torrentService.setSaveVideo(isChecked);
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            torrentService = null;
        }
    };
    
    // Removed startTorrent() method
    
    private void updateVideoSurfaceLayout(int screenWidth, int screenHeight) {
        if (screenWidth == 0 || screenHeight == 0 || mVideoAspectRatio == 0) return;
        
        int finalWidth = screenWidth;
        int finalHeight = (int) (screenWidth / mVideoAspectRatio);
        
        if (finalHeight > screenHeight) {
            finalHeight = screenHeight;
            finalWidth = (int) (screenHeight * mVideoAspectRatio);
        }
        
        Log.d(TAG, "updateVideoSurfaceLayout: Screen=" + screenWidth + "x" + screenHeight + " | Final=" + finalWidth + "x" + finalHeight + " | AR=" + mVideoAspectRatio);

        android.view.ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();
        if (lp.width != finalWidth || lp.height != finalHeight) {
            lp.width = finalWidth;
            lp.height = finalHeight;
            videoSurface.setLayoutParams(lp);
            videoSurface.requestLayout(); // Force re-layout to ensure centering logic applies
        }
    }

    private void adjustAspectRatio() {
        if (mediaPlayer == null) return;
        Media.VideoTrack track = mediaPlayer.getCurrentVideoTrack();
        if (track == null) return;

        int width = track.width;
        int height = track.height;
        if (width * height == 0) return;

        int sarNum = track.sarNum;
        int sarDen = track.sarDen;

        Log.d(TAG, "adjustAspectRatio: " + width + "x" + height + " SAR:" + sarNum + "/" + sarDen);

        // Calculate Aspect Ratio
        mVideoAspectRatio = (double) width / height;
        if (sarDen != 0) mVideoAspectRatio = mVideoAspectRatio * sarNum / sarDen;

        // Fix: Set the surface buffer size to match the video source size.
        if (videoSurface != null) {
            videoSurface.getHolder().setFixedSize(width, height);
        }

        View parent = (View) videoSurface.getParent();
        if (parent != null) {
            updateVideoSurfaceLayout(parent.getWidth(), parent.getHeight());
        }
    }

    private void setupControls() {
        // Toggle overlay visibility on surface tap
        videoSurface.setOnClickListener(v -> {
            if (isTvDevice()) {
                // On TV, keep overlay visible; simply ensure focus is on controls
                controlsOverlay.setVisibility(View.VISIBLE);
                if (btnPlayPause != null) btnPlayPause.requestFocus();
            } else {
                if (controlsOverlay.getVisibility() == View.VISIBLE) {
                    controlsOverlay.setVisibility(View.GONE);
                    updateSystemUi(); // Re-hide bars if needed
                } else {
                    controlsOverlay.setVisibility(View.VISIBLE);
                    // Auto-hide after 5 seconds
                    handler.removeCallbacks(hideOverlayAction);
                    handler.postDelayed(hideOverlayAction, 5000);
                }
            }
        });
        
        // Also allow tapping the overlay background to hide it
        controlsOverlay.setOnClickListener(v -> {
            controlsOverlay.setVisibility(View.GONE);
            updateSystemUi();
        });

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer == null) return;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setImageResource(R.drawable.ic_play_arrow);
            } else {
                mediaPlayer.play();
                btnPlayPause.setImageResource(R.drawable.ic_pause);
                handler.removeCallbacks(hideOverlayAction);
                handler.postDelayed(hideOverlayAction, 3000);
            }
        });

        // Fullscreen Toggle logic
        btnFullscreen.setOnClickListener(v -> toggleFullscreen());
        
        // PiP button
        if (btnPip != null) {
            btnPip.setOnClickListener(v -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
                    getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
                    enterPipMode();
                } else {
                    Toast.makeText(this, "Picture-in-Picture not supported on this device", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        btnBack.setOnClickListener(v -> onBackPressed());
        
        if (btnInfo != null) {
            btnInfo.setOnClickListener(v -> {
                if (textOverlayStats != null) {
                    textOverlayStats.setVisibility(textOverlayStats.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });
        }

        seekBar.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    float pos = (float) progress / 100f;
                    mediaPlayer.setPosition(pos);
                }
            }
            @Override public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        });

        updateProgressAction = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    long current = mediaPlayer.getTime();
                    long total = mediaPlayer.getLength();
                    float pos = mediaPlayer.getPosition();
                    
                    if (total > 0) {
                        seekBar.setProgress((int) (pos * 100));
                        textCurrentTime.setText(formatTime(current));
                        textTotalTime.setText(formatTime(total));
                    }

                    // Refresh torrent stats overlay while playing
                    updateStatsFromService();
                }
                handler.postDelayed(this, 1000);
            }
        };
    }

    private void toggleFullscreen() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onConfigurationChanged(@androidx.annotation.NonNull android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateSystemUi();
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateSystemUi();
        }
    }

    private void updateSystemUi() {
        // Immersive mode: hide nav & status bars
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private Runnable hideOverlayAction = () -> {
        if (!isTvDevice()) {
            controlsOverlay.setVisibility(View.GONE);
            updateSystemUi();
        }
    };

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    // Replaced startTorrent with Service logic
    // private void startTorrent() { ... } removed

    private void initPlayer(File videoFile) {
        try {
            if (libVLC == null) {
                ArrayList<String> options = new ArrayList<>();
                options.add("--no-drop-late-frames");
                options.add("--no-skip-frames");
                options.add("--rtsp-tcp");
                options.add("-vvv"); // Verbosity
                libVLC = new LibVLC(this, options);
                mediaPlayer = new MediaPlayer(libVLC);
                
                IVLCVout vout = mediaPlayer.getVLCVout();
                vout.setVideoView(videoSurface);
                // Attach a listener to resize surface when video size is known
                vout.addCallback(new IVLCVout.Callback() {
                    @Override
                    public void onSurfacesCreated(IVLCVout vout) {}

                    @Override
                    public void onSurfacesDestroyed(IVLCVout vout) {}
                });
                vout.attachViews();
                
                // Set Event Listener for playback start and errors
                mediaPlayer.setEventListener(event -> {
                    if (event.type == MediaPlayer.Event.Playing) {
                        runOnUiThread(() -> {
                            controlsOverlay.setVisibility(View.GONE); // Hide initially
                            handler.post(updateProgressAction);
                            adjustAspectRatio();
                        });
                    }
                    if (event.type == MediaPlayer.Event.Vout) {
                        runOnUiThread(this::adjustAspectRatio);
                    }
                    if (event.type == MediaPlayer.Event.EncounteredError) {
                        runOnUiThread(() -> {
                            Log.w(TAG, "VLC playback error - file may need more download time");
                            statusText.setText("File is still downloading. Playback will start when more data is available...");
                            statusText.setVisibility(View.VISIBLE);
                        });
                    }
                    if (event.type == MediaPlayer.Event.EndReached) {
                        runOnUiThread(() -> {
                            // Check if torrent is still downloading
                            if (torrentService != null && torrentService.getLastProgress() < 99.0f) {
                                Log.i(TAG, "Playback ended but download incomplete - file may be corrupted or needs more data");
                                statusText.setText("Download continuing... Playback will resume when more data is available.");
                                statusText.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }

            Media media = new Media(libVLC, Uri.fromFile(videoFile));
            // Network caching hint for smoother playback of growing files
            media.addOption(":network-caching=1500");
            media.addOption(":clock-jitter=0");
            media.addOption(":clock-synchro=0");
            
            mediaPlayer.setMedia(media);
            mediaPlayer.play();

        } catch (Exception e) {
            Log.e(TAG, "Error initializing player", e);
            Toast.makeText(this, "Player error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startFileReadyWatcher() {
        if (fileReadyWatcher != null) {
            handler.removeCallbacks(fileReadyWatcher);
        }

        fileReadyWatcher = new Runnable() {
            @Override
            public void run() {
                if (torrentService == null) return;

                File videoFile = torrentService.getCurrentVideoFile();
                if (videoFile != null && videoFile.exists()) {
                    loadingProgress.setVisibility(View.GONE);
                    statusText.setVisibility(View.GONE);
                    textVideoTitle.setText(videoFile.getName());
                    initPlayer(videoFile);
                    return; // Do not reschedule, we're playing now
                }

                // Update overlay stats while waiting
                updateStatsFromService();

                // Re-check in 1s while streaming is ongoing
                if (torrentService != null && torrentService.isStreaming()) {
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.post(fileReadyWatcher);
    }

    private void updateStatsFromService() {
        if (torrentService == null) return;

        if (!torrentService.hasLastStatus()) {
            // Still fetching metadata or connecting
            return;
        }

        float totalProgress = torrentService.getLastProgress();
        int seeds = torrentService.getLastSeeds();
        long downloadSpeed = torrentService.getLastDownloadSpeedBytes();

        float speedKb = downloadSpeed / 1024f;
        String speedStr = (speedKb > 1024) ? String.format("%.1f MB/s", speedKb / 1024f) : String.format("%.0f KB/s", speedKb);

        String overlayInfo = String.format("Total Progress: %.1f%% \nSeeds: %d | Speed: %s", 
                    totalProgress, seeds, speedStr);

        if (textOverlayStats != null) {
            textOverlayStats.setText(overlayInfo);
        }
    }

    // --- Picture-in-Picture Support ---
    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        // Enter PiP when user presses home while video is playing
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
            mediaPlayer != null && mediaPlayer.isPlaying() &&
            getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            
            Log.d(TAG, "onUserLeaveHint: Entering PiP mode");
            enterPipMode();
        }
    }

    private void enterPipMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                Rational aspectRatio = new Rational(16, 9);
                if (mVideoAspectRatio > 0) {
                    // Use actual video aspect ratio if available
                    int width = (int) (mVideoAspectRatio * 1000);
                    int height = 1000;
                    aspectRatio = new Rational(width, height);
                }
                PictureInPictureParams params = new PictureInPictureParams.Builder()
                        .setAspectRatio(aspectRatio)
                        .build();
                enterPictureInPictureMode(params);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error entering PiP mode", e);
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        isInPipMode = isInPictureInPictureMode;
        
        if (isInPictureInPictureMode) {
            Log.d(TAG, "Entered PiP mode");
            // Hide all controls in PiP
            if (controlsOverlay != null) controlsOverlay.setVisibility(View.GONE);
            if (loadingProgress != null) loadingProgress.setVisibility(View.GONE);
            if (statusText != null) statusText.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "Exited PiP mode");
            // Restore controls visibility based on playback state
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                // Keep controls hidden during playback, user can tap to show
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't pause video if entering PiP mode
        if (!isInPipMode && mediaPlayer != null && mediaPlayer.isPlaying()) {
            // Optionally pause when leaving activity (not PiP)
            // mediaPlayer.pause();
        }
    }
    // --- End Picture-in-Picture Support ---

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (fileReadyWatcher != null) {
            handler.removeCallbacks(fileReadyWatcher);
        }
        if (updateProgressAction != null) {
            handler.removeCallbacks(updateProgressAction);
        }

        // Cleanup Media Player
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (libVLC != null) {
            libVLC.release();
            libVLC = null;
        }
    }
    
    @Override
    public void onBackPressed() {
        if (torrentService != null) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Exit Playback")
                .setMessage("Do you want to stop the download or keep it running in background?")
                .setPositiveButton("Stop", (d, w) -> {
                    torrentService.stopTorrent(); 
                    finish();
                })
                .setNegativeButton("Background", (d, w) -> {
                    torrentService.setSaveVideo(true); // Assume they want to keep it
                    finish();
                })
                .setNeutralButton("Cancel", null)
                .show();
        } else {
            super.onBackPressed();
        }
    }
}
