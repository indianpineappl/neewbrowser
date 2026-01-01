package com.neew.browser;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.provider.MediaStore;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.core.app.NotificationCompat;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;

import java.io.File;

public class TorrentService extends Service implements TorrentListener {

    private static final String TAG = "TorrentService";
    
    // Actions
    public static final String ACTION_START_STREAM = "com.neew.browser.action.START_STREAM";
    public static final String ACTION_START_DOWNLOAD = "com.neew.browser.action.START_DOWNLOAD";
    public static final String ACTION_STOP = "com.neew.browser.action.STOP";
    public static final String EXTRA_MAGNET_URL = "extra_magnet_url";

    private static final int NOTIFICATION_ID = 1337;
    private static final String CHANNEL_ID = "TorrentChannel";

    private TorrentStream torrentStream;
    private final IBinder binder = new LocalBinder();
    private boolean isStreaming = false; // true if UI requested stream (temp location)
    private boolean shouldSave = false; // true if user requested to save (move from temp to download)
    private Torrent currentTorrent;
    private String currentMagnetUrl;
    private StreamStatus lastStatus;
    private int lastLoggedProgress = -1;
    private long lastNotificationTime = 0;

    private final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable metadataTimeoutRunnable;
    private static final long METADATA_TIMEOUT_MS = 45000L;

    public class LocalBinder extends Binder {
        public TorrentService getService() {
            return TorrentService.this;
        }
    }
    
    public String getCurrentMagnetUrl() {
        return currentMagnetUrl;
    }
    
    public StreamStatus getLastStatus() {
        return lastStatus;
    }
    
    public File getCurrentVideoFile() {
        return (currentTorrent != null) ? currentTorrent.getVideoFile() : null;
    }
    
    public void setSaveVideo(boolean save) {
        this.shouldSave = save;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand received intent: " + intent);
        if (intent != null) {
            String action = intent.getAction();
            Log.d(TAG, "onStartCommand received action: " + action);
            if (ACTION_STOP.equals(action)) {
                stopTorrent();
                stopSelf();
                return START_NOT_STICKY;
            }

            String magnet = intent.getStringExtra(EXTRA_MAGNET_URL);
            if (magnet != null) {
                if (ACTION_START_STREAM.equals(action)) {
                    isStreaming = true;
                    startTorrent(magnet, true);
                } else if (ACTION_START_DOWNLOAD.equals(action)) {
                    isStreaming = false;
                    startTorrent(magnet, false);
                }
            }
        }
        return START_STICKY;
    }

    private void startTorrent(String magnetUrl, boolean isStreamMode) {
        // Append default trackers if it's a magnet link to speed up metadata retrieval
        if (magnetUrl != null && magnetUrl.startsWith("magnet:?")) {
            try {
                StringBuilder sb = new StringBuilder(magnetUrl);
                String[] trackers = {
                    "udp://tracker.opentrackr.org:1337/announce",
                    "udp://9.rarbg.com:2810/announce",
                    "udp://tracker.openbittorrent.com:80/announce",
                    "udp://tracker.torrent.eu.org:451/announce",
                    "udp://opentracker.i2p.rocks:6969/announce",
                    "https://opentracker.i2p.rocks:443/announce"
                };
                for (String tr : trackers) {
                    // Check if tracker is already present (roughly)
                    if (!magnetUrl.contains(java.net.URLEncoder.encode(tr, "UTF-8"))) {
                         sb.append("&tr=").append(java.net.URLEncoder.encode(tr, "UTF-8"));
                    }
                }
                magnetUrl = sb.toString();
            } catch (Exception e) {
                Log.w(TAG, "Failed to append trackers to magnet link", e);
            }
        }

        Log.i(TAG, "Starting Torrent. Mode: " + (isStreamMode ? "Stream" : "Download") + ", Magnet: " + magnetUrl);
        this.currentMagnetUrl = magnetUrl;
        this.lastStatus = null; // Reset status on new start
        this.lastLoggedProgress = -1;
        if (torrentStream != null) {
            // If already running same torrent, ignore.
            // If new torrent, stop old one.
            if (torrentStream.isStreaming()) {
                 stopTorrent();
            }
        }

        File saveDir;
        if (isStreamMode) {
            saveDir = getExternalFilesDir("torrent_temp");
        } else {
            saveDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        }

        if (saveDir == null) {
            Log.e(TAG, "Save directory is null");
            stopSelf();
            return;
        }

        TorrentOptions options = new TorrentOptions.Builder()
                .saveLocation(saveDir)
                .removeFilesAfterStop(false) // Always keep, manage manually
                .build();

        torrentStream = TorrentStream.init(options);
        torrentStream.addListener(this);
        torrentStream.startStream(magnetUrl);

        scheduleMetadataTimeout();

        startForeground(NOTIFICATION_ID, createNotification("Initializing... (Fetching Metadata)", 0, 0));
    }
    
    public void stopTorrent() {
        clearMetadataTimeout();
        if (torrentStream != null) {
            torrentStream.removeListener(this);
            // Capture file before stop
            File videoFile = (currentTorrent != null) ? currentTorrent.getVideoFile() : null;
            
            torrentStream.stopStream();
            torrentStream = null;
            
            // Handle file persistence
            if (videoFile != null && videoFile.exists()) {
                if (shouldSave || !isStreaming) {
                    // Move/Copy to Public Downloads
                    saveFileToPublicStorage(videoFile);
                    // Return early to let thread finish
                    return; 
                } else {
                    // Streaming and NOT saving -> Delete temp
                    if (videoFile.delete()) {
                        Log.i(TAG, "Deleted temp file");
                    }
                }
            }
        }
        currentTorrent = null;
        stopForeground(true);
    }
    
    private void saveFileToPublicStorage(File srcFile) {
        Log.d(TAG, "Starting saveFileToPublicStorage. Source: " + srcFile.getAbsolutePath() + ", Size: " + srcFile.length() + " bytes");
        // Show saving notification
        updateNotification("Saving to Downloads...", 100, 0);
        
        new Thread(() -> {
            boolean success = false;
            String fileName = srcFile.getName();
            android.net.Uri savedUri = null;
            String mimeType = getMimeType(fileName);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.d(TAG, "Saving via MediaStore (API 29+)");
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                
                ContentResolver resolver = getContentResolver();
                android.net.Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try (OutputStream out = resolver.openOutputStream(uri);
                         FileInputStream in = new FileInputStream(srcFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                        success = true;
                        savedUri = uri;
                        Log.d(TAG, "MediaStore save success: " + uri.toString());
                    } catch (IOException e) {
                        Log.e(TAG, "Error copying to public downloads", e);
                    }
                } else {
                    Log.e(TAG, "MediaStore insert failed (uri is null)");
                }
            } else {
                Log.d(TAG, "Saving via Legacy File IO");
                // Legacy
                File publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File dest = new File(publicDir, fileName);
                try (FileInputStream in = new FileInputStream(srcFile);
                     FileOutputStream out = new FileOutputStream(dest)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    success = true;
                    // Get URI for the file using FileProvider
                    savedUri = androidx.core.content.FileProvider.getUriForFile(TorrentService.this, getPackageName() + ".provider", dest);
                    Log.d(TAG, "Legacy save success: " + dest.getAbsolutePath());
                } catch (Exception e) {
                    Log.e(TAG, "Error copying legacy", e);
                }
            }
            
            if (success) {
                srcFile.delete(); // Remove temp file
                showCompletionNotification(fileName, true, savedUri, mimeType);
            } else {
                showCompletionNotification(fileName, false, null, null);
            }
            
            currentTorrent = null;
            stopForeground(true); 
            stopSelf();
        }).start();
    }
    
    private void showCompletionNotification(String fileName, boolean success, android.net.Uri fileUri, String mimeType) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(success ? "Download Saved" : "Save Failed")
                .setContentText(success ? fileName + " saved to Downloads" : "Could not save " + fileName)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setAutoCancel(true);
            
            if (success && fileUri != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, mimeType);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                builder.setContentIntent(pendingIntent);
            }
            
            manager.notify(NOTIFICATION_ID + 1, builder.build());
        }
    }
    
    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public TorrentStream getTorrentStream() {
        return torrentStream;
    }

    private void scheduleMetadataTimeout() {
        clearMetadataTimeout();
        metadataTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                handleMetadataTimeout();
            }
        };
        mainHandler.postDelayed(metadataTimeoutRunnable, METADATA_TIMEOUT_MS);
    }

    private void clearMetadataTimeout() {
        if (metadataTimeoutRunnable != null) {
            mainHandler.removeCallbacks(metadataTimeoutRunnable);
            metadataTimeoutRunnable = null;
        }
    }

    private boolean isTvDevice() {
        android.content.pm.PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(android.content.pm.PackageManager.FEATURE_LEANBACK);
    }

    private void handleMetadataTimeout() {
        if (torrentStream == null) return;
        if (currentTorrent != null) return;
        if (lastStatus != null) return;

        String msg;
        if (isTvDevice()) {
            msg = "This TV's system network policy blocks torrent connections, so torrent streaming and downloading are not supported here.";
        } else {
            msg = "Could not start torrent. This device or network may be blocking torrent connections, or the link may be dead.";
        }

        android.widget.Toast.makeText(getApplicationContext(), msg, android.widget.Toast.LENGTH_LONG).show();

        stopTorrent();
    }
    
    public boolean isStreaming() {
        return isStreaming;
    }

    // --- Notification Logic ---
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Torrent Downloads",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification(String status, int progress, int seeds) {
        Intent stopIntent = new Intent(this, TorrentService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        // Intent to open Player or Browser?
        // If Streaming, open Player. If Download, open Browser?
        // For simplicity, pending intent opens nothing for now, or maybe Player if active.
        
        String title = (currentTorrent != null && currentTorrent.getVideoFile() != null) 
                ? currentTorrent.getVideoFile().getName() 
                : "Torrent Download";

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(status)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(status))
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setOngoing(true)
                .setProgress(100, progress, progress == 0)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
                .build();
    }
    
    private void updateNotification(String status, int progress, int seeds) {
        long now = System.currentTimeMillis();
        // Update if > 1s passed OR progress is 100/0 (immediate updates for critical states)
        if (now - lastNotificationTime > 1000 || progress >= 100 || progress == 0) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(NOTIFICATION_ID, createNotification(status, progress, seeds));
                lastNotificationTime = now;
            }
        }
    }

    // --- TorrentListener Implementation ---

    @Override
    public void onStreamPrepared(Torrent torrent) {
        clearMetadataTimeout();
        Log.i(TAG, "Torrent Prepared. Metadata retrieved. File: " + (torrent.getVideoFile() != null ? torrent.getVideoFile().getName() : "Unknown"));
        currentTorrent = torrent;

        // Inspect Torrent API via reflection to find way to download all files
        try {
            java.lang.reflect.Method[] methods = torrent.getClass().getMethods();
            for (java.lang.reflect.Method m : methods) {
                // Log interesting methods
                if (m.getName().contains("File") || m.getName().contains("Priority") || m.getName().contains("start") || m.getName().contains("handle")) {
                     Log.d(TAG, "Torrent Method: " + m.getName());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Reflection error", e);
        }
        
        // Force start download explicitly to ensure it doesn't wait for playback
        try {
            torrent.startDownload();
            Log.d(TAG, "Invoked torrent.startDownload()");
        } catch (Exception e) {
            Log.w(TAG, "Could not call startDownload on torrent", e);
        }

        updateNotification("Metadata retrieved. Starting...", 0, 0);
    }

    @Override
    public void onStreamStarted(Torrent torrent) {
        Log.i(TAG, "Torrent Stream Started.");
        currentTorrent = torrent;
        updateNotification("Downloading...", 0, 0);
    }

    @Override
    public void onStreamError(Torrent torrent, Exception e) {
        clearMetadataTimeout();
        Log.e(TAG, "Torrent Error occurred.", e);
        updateNotification("Error: " + e.getMessage(), 0, 0);
    }

    @Override
    public void onStreamReady(Torrent torrent) {
        currentTorrent = torrent;
        // Don't force progress to 100% here, let onStreamProgress handle valid updates
        // updateNotification("Ready to play", 100, 0);
    }

    @Override
    public void onStreamProgress(Torrent torrent, StreamStatus status) {
        clearMetadataTimeout();
        this.lastStatus = status;
        float speedKb = status.downloadSpeed / 1024f;
        String speedStr;
        if (speedKb > 1024) {
            speedStr = String.format("%.1f MB/s", speedKb / 1024f);
        } else {
            speedStr = String.format("%.0f KB/s", speedKb);
        }
        
        String content = String.format("%.1f%% | Seeds: %d | Speed: %s", 
                status.progress, status.seeds, speedStr);
        
        updateNotification(content, (int)status.progress, status.seeds);

        // Debug Log for Progress (Periodic)
        int currentProgress = (int) status.progress;
        if (currentProgress > lastLoggedProgress + 5 || currentProgress == 100) { // Log every 5% or at completion
            Log.d(TAG, String.format("Torrent Progress: %.1f%%, Seeds: %d, Speed: %s", status.progress, status.seeds, speedStr));
            lastLoggedProgress = currentProgress;
        }

        // Auto-stop when transfer is effectively complete (download or stream)
        // Relax threshold further to 99.8% to avoid endless near-complete states
        if (status.progress >= 99.8f) {
            Log.i(TAG, "Download/Stream reached >= 99.8%. Initiating stop and save/cleanup.");
            // Post to main thread to avoid re-entrancy issues in library callbacks
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                if (torrentStream != null) {
                    stopTorrent();
                }
            });
        }
    }

    @Override
    public void onStreamStopped() {
        stopForeground(true);
    }
}
