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

import java.io.File;

public class TorrentService extends Service {

    private static final String TAG = "TorrentService";
    
    // Actions
    public static final String ACTION_START_STREAM = "com.neew.browser.action.START_STREAM";
    public static final String ACTION_START_DOWNLOAD = "com.neew.browser.action.START_DOWNLOAD";
    public static final String ACTION_STOP = "com.neew.browser.action.STOP";
    public static final String EXTRA_MAGNET_URL = "extra_magnet_url";

    private static final int NOTIFICATION_ID = 1337;
    private static final String CHANNEL_ID = "TorrentChannel";

    private TorrentEngine torrentEngine;
    private String currentSessionId;
    private final IBinder binder = new LocalBinder();
    private boolean isStreaming = false; // true if UI requested stream (temp location)
    private boolean shouldSave = false; // true if user requested to save (move from temp to download)
    private File currentVideoFile;
    private String currentMagnetUrl;
    private float lastProgress = 0f;
    private int lastSeeds = 0;
    private long lastDownloadSpeedBytes = 0L;
    private boolean hasLastStatus = false;
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
    
    public File getCurrentVideoFile() {
        return currentVideoFile;
    }

    public boolean hasLastStatus() {
        return hasLastStatus;
    }

    public float getLastProgress() {
        return lastProgress;
    }

    public int getLastSeeds() {
        return lastSeeds;
    }

    public long getLastDownloadSpeedBytes() {
        return lastDownloadSpeedBytes;
    }
    
    public void setSaveVideo(boolean save) {
        this.shouldSave = save;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        torrentEngine = new TorrentStreamEngine(getApplicationContext());
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
        this.currentVideoFile = null;
        this.lastProgress = 0f;
        this.lastSeeds = 0;
        this.lastDownloadSpeedBytes = 0L;
        this.hasLastStatus = false;
        this.lastLoggedProgress = -1;
        if (torrentEngine == null) {
            torrentEngine = new TorrentStreamEngine(getApplicationContext());
        }

        if (currentSessionId != null) {
            torrentEngine.stop(currentSessionId);
            currentSessionId = null;
        }

        currentSessionId = torrentEngine.start(magnetUrl, isStreamMode, engineListener);

        scheduleMetadataTimeout();

        startForeground(NOTIFICATION_ID, createNotification("Initializing... (Fetching Metadata)", 0, 0));
    }
    
    public void stopTorrent() {
        clearMetadataTimeout();
        File videoFile = currentVideoFile;

        if (torrentEngine != null && currentSessionId != null) {
            torrentEngine.stop(currentSessionId);
        }

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

        currentSessionId = null;
        currentVideoFile = null;
        hasLastStatus = false;
        lastProgress = 0f;
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
        if (currentSessionId == null) return;
        if (currentVideoFile != null) return;
        if (hasLastStatus) return;

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
        
        String title = (currentVideoFile != null) 
                ? currentVideoFile.getName() 
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
        // Update if > 1s passed OR progress is 100 (immediate update for completion)
        if (now - lastNotificationTime > 1000 || progress >= 100) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(NOTIFICATION_ID, createNotification(status, progress, seeds));
                lastNotificationTime = now;
            }
        }
    }

    // --- TorrentEngine Listener Implementation ---

    private final TorrentEngine.Listener engineListener = new TorrentEngine.Listener() {
        @Override
        public void onPrepared(String sessionId) {
            clearMetadataTimeout();
            Log.i(TAG, "Torrent Prepared. Metadata retrieved. Session: " + sessionId);
            updateNotification("Metadata retrieved. Starting...", 0, 0);
        }

        @Override
        public void onStarted(String sessionId) {
            Log.i(TAG, "Torrent Stream Started. Session: " + sessionId);
            updateNotification("Downloading...", 0, 0);
        }

        @Override
        public void onProgress(String sessionId, float progress, int seeds, long downloadSpeedBytesPerSecond) {
            clearMetadataTimeout();
            hasLastStatus = true;
            lastProgress = progress;
            lastSeeds = seeds;
            lastDownloadSpeedBytes = downloadSpeedBytesPerSecond;

            float speedKb = downloadSpeedBytesPerSecond / 1024f;
            String speedStr;
            if (speedKb > 1024) {
                speedStr = String.format("%.1f MB/s", speedKb / 1024f);
            } else {
                speedStr = String.format("%.0f KB/s", speedKb);
            }

            String content = String.format("%.1f%% | Seeds: %d | Speed: %s",
                    progress, seeds, speedStr);

            updateNotification(content, (int) progress, seeds);

            int currentProgress = (int) progress;
            if (currentProgress > lastLoggedProgress + 5 || currentProgress == 100) {
                Log.d(TAG, String.format("Torrent Progress: %.1f%%, Seeds: %d, Speed: %s", progress, seeds, speedStr));
                lastLoggedProgress = currentProgress;
            }

            if (progress >= 99.8f) {
                Log.i(TAG, "Download/Stream reached >= 99.8%. Initiating stop and save/cleanup.");
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> stopTorrent());
            }
        }

        @Override
        public void onReadyForPlayback(String sessionId, File mediaFile) {
            Log.i(TAG, "Media ready for playback. Session: " + sessionId + ", file=" +
                    (mediaFile != null ? mediaFile.getAbsolutePath() : "null"));
            currentVideoFile = mediaFile;
        }

        @Override
        public void onError(String sessionId, Throwable error) {
            clearMetadataTimeout();
            Log.e(TAG, "Torrent Error occurred.", error);
            String msg = (error != null && error.getMessage() != null) ? error.getMessage() : "Unknown error";
            updateNotification("Error: " + msg, 0, 0);
        }

        @Override
        public void onStopped(String sessionId) {
            stopForeground(true);
        }
    };
}
