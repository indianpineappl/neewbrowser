package com.neew.browser;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Adapter that wraps TorrentStream-Android behind the generic TorrentEngine interface.
 * Currently supports a single active session (matching existing TorrentService behavior).
 */
public class TorrentStreamEngine implements TorrentEngine, TorrentListener {

    private static final String TAG = "TorrentStreamEngine";

    private final Context appContext;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final AtomicLong idGenerator = new AtomicLong(1);

    private TorrentStream torrentStream;
    private Torrent currentTorrent;
    private StreamStatus lastStatus;
    private String currentSessionId;
    private boolean currentStreamMode;
    private Listener currentListener;
    
    // Audio file handling - library doesn't send progress for audio files
    private static final Set<String> AUDIO_EXTENSIONS = new HashSet<>(Arrays.asList(
            "mp3", "flac", "aac", "ogg", "wav", "wma", "m4a", "opus", "alac"
    ));
    private boolean isAudioFile = false;
    private boolean hasReceivedLibraryProgress = false;
    private boolean audioFileReadyNotified = false;
    private long totalFileSize = 0;
    private long lastReportedSize = 0;
    private long lastReportedTime = 0;
    private Runnable audioProgressRunnable;
    private String currentMediaExtension;

    public TorrentStreamEngine(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public synchronized String start(String uri, boolean streamMode, Listener listener) {
        // Stop any existing session first (single-session model).
        if (currentSessionId != null) {
            stop(currentSessionId);
        }

        this.currentSessionId = "ts-" + idGenerator.getAndIncrement();
        this.currentStreamMode = streamMode;
        this.currentListener = listener;
        this.currentTorrent = null;
        this.lastStatus = null;
        this.isAudioFile = false;
        this.hasReceivedLibraryProgress = false;
        this.audioFileReadyNotified = false;
        this.totalFileSize = 0;
        this.lastReportedSize = 0;
        this.lastReportedTime = 0;
        this.audioProgressRunnable = null;
        this.currentMediaExtension = null;

        File saveDir;
        if (streamMode) {
            saveDir = appContext.getExternalFilesDir("torrent_temp");
            // Clean up old temp files before starting a new stream
            if (saveDir != null && saveDir.exists()) {
                cleanDirectory(saveDir);
                Log.i(TAG, "Cleaned temp directory before starting stream");
            }
        } else {
            saveDir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        }

        if (saveDir == null) {
            Log.e(TAG, "Save directory is null for uri=" + uri);
            notifyError(new IllegalStateException("Save directory is null"));
            return currentSessionId;
        }

        TorrentOptions options = new TorrentOptions.Builder()
                .saveLocation(saveDir)
                .removeFilesAfterStop(false)
                .build();

        torrentStream = TorrentStream.init(options);
        torrentStream.addListener(this);
        torrentStream.startStream(uri);

        Log.i(TAG, "Started TorrentStream session " + currentSessionId + " mode=" + (streamMode ? "STREAM" : "DOWNLOAD") + " uri=" + uri);

        return currentSessionId;
    }

    @Override
    public synchronized void stop(String sessionId) {
        if (currentSessionId == null || !currentSessionId.equals(sessionId)) {
            return; // Nothing to stop or different session
        }

        // Cancel any pending audio progress polling
        if (audioProgressRunnable != null) {
            mainHandler.removeCallbacks(audioProgressRunnable);
            audioProgressRunnable = null;
        }

        if (torrentStream != null) {
            torrentStream.removeListener(this);
            torrentStream.stopStream();
            torrentStream = null;
        }

        currentTorrent = null;
        lastStatus = null;

        final Listener listener = currentListener;
        final String stoppedId = currentSessionId;
        currentSessionId = null;
        currentListener = null;

        if (listener != null) {
            mainHandler.post(() -> listener.onStopped(stoppedId));
        }
    }

    @Override
    public synchronized boolean isStreaming(String sessionId) {
        return currentSessionId != null
                && currentSessionId.equals(sessionId)
                && torrentStream != null
                && torrentStream.isStreaming();
    }

    @Override
    public synchronized File getMediaFile(String sessionId) {
        if (currentSessionId == null || !currentSessionId.equals(sessionId)) {
            return null;
        }
        return (currentTorrent != null) ? currentTorrent.getVideoFile() : null;
    }

    // --- TorrentListener implementation ---

    @Override
    public synchronized void onStreamPrepared(Torrent torrent) {
        Log.i(TAG, "[CALLBACK] onStreamPrepared called, torrent=" + (torrent != null ? "present" : "null"));
        currentTorrent = torrent;
        final Listener listener = currentListener;
        final String sessionId = currentSessionId;
        try {
            torrent.startDownload();
            Log.d(TAG, "Invoked torrent.startDownload()");

            // Improve streaming behavior: prioritize sequential pieces so the file header becomes valid early.
            try {
                if (torrent.getTorrentHandle() != null) {
                    try {
                        torrent.getTorrentHandle().getClass()
                                .getMethod("setSequentialDownload", boolean.class)
                                .invoke(torrent.getTorrentHandle(), true);
                        Log.i(TAG, "Enabled sequential download on torrent handle");
                    } catch (Throwable ignored) {
                        // Method not available in this jlibtorrent version.
                    }
                }
            } catch (Throwable t) {
                Log.w(TAG, "Could not enable sequential download", t);
            }
            
            // Detect if this is an audio file by checking the file extension
            File mediaFile = torrent.getVideoFile();
            if (mediaFile != null) {
                Log.i(TAG, "onStreamPrepared: mediaFile=" + mediaFile.getAbsolutePath());
                String fileName = mediaFile.getName().toLowerCase();
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    String extension = fileName.substring(dotIndex + 1);
                    isAudioFile = AUDIO_EXTENSIONS.contains(extension);
                    currentMediaExtension = extension;
                    if (isAudioFile) {
                        Log.i(TAG, "onStreamPrepared: Detected AUDIO file by extension: " + extension);
                    } else {
                        Log.i(TAG, "onStreamPrepared: Detected VIDEO file by extension: " + extension);
                    }
                }
            } else {
                Log.w(TAG, "onStreamPrepared: mediaFile is NULL");
            }
            
            // Get total file size from torrent metadata
            try {
                if (torrent.getTorrentHandle() != null && torrent.getTorrentHandle().torrentFile() != null) {
                    totalFileSize = torrent.getTorrentHandle().torrentFile().totalSize();
                    Log.i(TAG, "onStreamPrepared: Total file size from metadata: " + totalFileSize + " bytes");
                } else {
                    Log.w(TAG, "onStreamPrepared: TorrentHandle or torrentFile is null");
                }
            } catch (Exception e) {
                Log.w(TAG, "onStreamPrepared: Could not get total size from metadata", e);
            }
            
        } catch (Exception e) {
            Log.w(TAG, "Could not call startDownload on torrent", e);
        }
        if (listener != null && sessionId != null) {
            mainHandler.post(() -> listener.onPrepared(sessionId));
        }
    }

    @Override
    public synchronized void onStreamStarted(Torrent torrent) {
        Log.i(TAG, "[CALLBACK] onStreamStarted called, torrent=" + (torrent != null ? "present" : "null"));
        currentTorrent = torrent;
        final Listener listener = currentListener;
        final String sessionId = currentSessionId;
        
        Log.i(TAG, "onStreamStarted: isAudioFile=" + isAudioFile + ", totalFileSize=" + totalFileSize);
        
        if (listener != null && sessionId != null) {
            mainHandler.post(() -> listener.onStarted(sessionId));
        }
        
        // For audio files, start polling for progress since library may not send onStreamProgress
        if (isAudioFile && totalFileSize > 0) {
            Log.i(TAG, "onStreamStarted: Starting audio progress polling for audio file");
            startAudioProgressPolling(sessionId);
        } else if (isAudioFile) {
            // totalFileSize not yet available, schedule polling start after a delay
            Log.i(TAG, "onStreamStarted: Audio file detected but size not ready, scheduling delayed polling start");
            final String sid = sessionId;
            mainHandler.postDelayed(() -> {
                if (totalFileSize > 0 && sid.equals(currentSessionId) && !hasReceivedLibraryProgress) {
                    Log.i(TAG, "onStreamStarted: Delayed polling start, totalFileSize=" + totalFileSize);
                    startAudioProgressPolling(sid);
                }
            }, 1000);
        } else {
            // For video files, also start polling as fallback since onStreamProgress may not fire
            Log.i(TAG, "onStreamStarted: Video file - starting fallback progress polling");
            startVideoProgressPolling(sessionId);
        }
    }
    
    /**
     * Start polling file size for video files as fallback since onStreamProgress may not fire.
     */
    private void startVideoProgressPolling(final String sessionId) {
        Runnable videoProgressRunnable = new Runnable() {
            @Override
            public void run() {
                // Stop polling if we've received library progress or session changed
                if (hasReceivedLibraryProgress || currentSessionId == null || !currentSessionId.equals(sessionId)) {
                    Log.d(TAG, "Video polling stopped: libraryProgress=" + hasReceivedLibraryProgress);
                    return;
                }
                
                checkFileProgress(sessionId);
                
                // Continue polling every 2 seconds
                mainHandler.postDelayed(this, 2000);
            }
        };
        mainHandler.postDelayed(videoProgressRunnable, 2000);
    }
    
    /**
     * Start polling file size for audio files since the library doesn't reliably
     * send onStreamProgress callbacks for non-video files.
     */
    private void startAudioProgressPolling(final String sessionId) {
        audioProgressRunnable = new Runnable() {
            @Override
            public void run() {
                // Stop polling if we've received library progress or session changed
                if (hasReceivedLibraryProgress || currentSessionId == null || !currentSessionId.equals(sessionId)) {
                    Log.d(TAG, "Audio polling stopped: libraryProgress=" + hasReceivedLibraryProgress);
                    return;
                }
                
                checkFileProgress(sessionId);
                
                // Continue polling every 2 seconds
                mainHandler.postDelayed(this, 2000);
            }
        };
        mainHandler.postDelayed(audioProgressRunnable, 2000);
    }
    
    /**
     * Check file download progress by measuring file size on disk.
     * Works for both audio and video files.
     */
    private synchronized void checkFileProgress(String sessionId) {
        if (currentTorrent == null || totalFileSize <= 0) {
            return;
        }
        
        try {
            File mediaFile = currentTorrent.getVideoFile();
            if (mediaFile == null || !mediaFile.exists()) {
                return;
            }
            
            long downloadedBytes = getDownloadedBytesFromHandle(currentTorrent);
            if (downloadedBytes < 0) {
                // Fallback to file length (can be misleading if libtorrent preallocates).
                downloadedBytes = mediaFile.length();
            }

            if (downloadedBytes == lastReportedSize) {
                return; // No change
            }
            
            // Calculate download speed based on file growth over time
            long currentTime = System.currentTimeMillis();
            long bytesDownloaded = downloadedBytes - lastReportedSize;
            long timeElapsed = currentTime - lastReportedTime;
            long speedBytesPerSecond = 0;
            
            if (lastReportedTime > 0 && timeElapsed > 0) {
                // Speed = bytes downloaded / seconds elapsed
                speedBytesPerSecond = (bytesDownloaded * 1000) / timeElapsed;
            }
            
            lastReportedSize = downloadedBytes;
            lastReportedTime = currentTime;

            float progress = Math.min((float) downloadedBytes / totalFileSize * 100f, 99.9f);
            
            final Listener listener = currentListener;
            if (listener != null && sessionId.equals(currentSessionId)) {
                final long speed = speedBytesPerSecond;
                mainHandler.post(() -> listener.onProgress(sessionId, progress, 0, speed));
                Log.d(TAG, "File progress (polling): " + downloadedBytes + "/" + totalFileSize +
                        " bytes (" + String.format("%.1f", progress) + "%)");

                // Manually trigger onReadyForPlayback when we have enough data (5%)
                // since the library's onStreamReady callback is not firing reliably
                if (!audioFileReadyNotified && progress >= 0.5f && isMediaHeaderReady(mediaFile)) {
                    audioFileReadyNotified = true;
                    final File file = mediaFile;
                    Log.i(TAG, "Media file ready for playback (polling): " + file.getAbsolutePath() + " (" + String.format("%.1f", progress) + "%)");
                    mainHandler.post(() -> listener.onReadyForPlayback(sessionId, file));
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error checking audio file progress", e);
        }
    }

    @Override
    public synchronized void onStreamError(Torrent torrent, Exception e) {
        Log.e(TAG, "TorrentStream error", e);
        notifyError(e);
    }

    @Override
    public synchronized void onStreamReady(Torrent torrent) {
        currentTorrent = torrent;
        final Listener listener = currentListener;
        final String sessionId = currentSessionId;
        final File mediaFile = (torrent != null) ? torrent.getVideoFile() : null;
        if (mediaFile != null) {
            Log.i(TAG, "onStreamReady: mediaFile=" + mediaFile.getAbsolutePath() + ", exists=" + mediaFile.exists());
        } else {
            Log.w(TAG, "onStreamReady: mediaFile is null");
        }
        if (listener != null && sessionId != null && mediaFile != null) {
            // Gate readiness on a real file header AND enough actually-downloaded bytes.
            // For video (especially MKV), the header can appear long before the first playable clusters are available.
            if (!audioFileReadyNotified && isMediaHeaderReady(mediaFile) && isEnoughDataForPlayback(torrent)) {
                audioFileReadyNotified = true;
                mainHandler.post(() -> listener.onReadyForPlayback(sessionId, mediaFile));
            }
        }
    }

    @Override
    public synchronized void onStreamProgress(Torrent torrent, StreamStatus status) {
        Log.d(TAG, "[CALLBACK] onStreamProgress called, status=" + (status != null ? String.format("%.1f%%", status.progress) : "null"));
        currentTorrent = torrent;
        lastStatus = status;

        // Only stop polling once the library is providing meaningful progress.
        if (status != null && (status.progress > 0.1f || status.downloadSpeed > 0)) {
            hasReceivedLibraryProgress = true;
        }

        final Listener listener = currentListener;
        final String sessionId = currentSessionId;
        if (listener != null && sessionId != null && status != null) {
            final float progress = status.progress;
            final int seeds = status.seeds;
            final long speedBytesPerSecond = status.downloadSpeed;
            mainHandler.post(() -> listener.onProgress(sessionId, progress, seeds, speedBytesPerSecond));
            
            // Gate readiness on a real file header AND enough actually-downloaded bytes.
            if (!audioFileReadyNotified && torrent != null) {
                File mediaFile = torrent.getVideoFile();
                if (mediaFile != null && mediaFile.exists() && isMediaHeaderReady(mediaFile) && isEnoughDataForPlayback(torrent)) {
                    audioFileReadyNotified = true;
                    final File file = mediaFile;
                    Log.i(TAG, "Media file ready for playback (via progress): " + file.getAbsolutePath() + " (" + String.format("%.1f", progress) + "%)");
                    mainHandler.post(() -> listener.onReadyForPlayback(sessionId, file));
                } else if (mediaFile != null && mediaFile.exists() && isMediaHeaderReady(mediaFile)) {
                    long done = getDownloadedBytesFromHandle(torrent);
                    Log.d(TAG, "Header ready but not enough data yet (via progress). totalDone=" + done + " bytes, progress=" + String.format("%.1f", progress) + "%");
                }
            }
        }
    }

    private boolean isEnoughDataForPlayback(Torrent torrent) {
        // For audio we can start much earlier.
        if (isAudioFile) return true;

        long done = getDownloadedBytesFromHandle(torrent);
        if (done < 0) {
            // If we can't get totalDone(), fall back to being conservative.
            return false;
        }

        // Video minimum buffer:
        // - absolute minimum: 32 MiB
        // - or 1% of total size (whichever is larger), capped to 128 MiB
        long minBytes = 32L * 1024L * 1024L;
        if (totalFileSize > 0) {
            long onePercent = totalFileSize / 100L;
            if (onePercent > minBytes) minBytes = onePercent;
        }
        long maxBytes = 128L * 1024L * 1024L;
        if (minBytes > maxBytes) minBytes = maxBytes;

        return done >= minBytes;
    }

    private long getDownloadedBytesFromHandle(Torrent torrent) {
        try {
            if (torrent == null || torrent.getTorrentHandle() == null) return -1;
            Object status = torrent.getTorrentHandle().status();
            if (status == null) return -1;
            // jlibtorrent exposes totalDone() on TorrentStatus
            return (long) status.getClass().getMethod("totalDone").invoke(status);
        } catch (Throwable ignored) {
            return -1;
        }
    }

    private boolean isMediaHeaderReady(File f) {
        if (f == null || !f.exists() || !f.canRead()) return false;

        // If we don't know the extension yet, just check that the header isn't all zeros.
        String ext = currentMediaExtension;
        if (ext == null) {
            ext = "";
        }

        byte[] header = new byte[16];
        try (FileInputStream in = new FileInputStream(f)) {
            int n = in.read(header);
            if (n <= 0) return false;
        } catch (IOException e) {
            return false;
        }

        boolean allZero = true;
        for (byte b : header) {
            if (b != 0) {
                allZero = false;
                break;
            }
        }
        if (allZero) return false;

        // MKV/WebM: EBML header begins with 1A 45 DF A3
        if ("mkv".equals(ext) || "webm".equals(ext)) {
            return (header[0] == (byte) 0x1A
                    && header[1] == (byte) 0x45
                    && header[2] == (byte) 0xDF
                    && header[3] == (byte) 0xA3);
        }

        // MP4/M4V: should contain 'ftyp' in bytes 4..7
        if ("mp4".equals(ext) || "m4v".equals(ext)) {
            return (header.length >= 8
                    && header[4] == 'f'
                    && header[5] == 't'
                    && header[6] == 'y'
                    && header[7] == 'p');
        }

        // For other formats, non-zero header is a decent heuristic.
        return true;
    }

    @Override
    public synchronized void onStreamStopped() {
        // TorrentStream invokes this when it stops; propagate via stop() to keep state consistent.
        if (currentSessionId != null) {
            String sessionId = currentSessionId;
            stop(sessionId);
        }
    }

    private void notifyError(Throwable t) {
        final Listener listener = currentListener;
        final String sessionId = currentSessionId;
        if (listener != null && sessionId != null) {
            mainHandler.post(() -> listener.onError(sessionId, t));
        }
    }
    
    /**
     * Recursively delete all files in a directory (but not the directory itself).
     */
    private void cleanDirectory(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    cleanDirectory(file);
                }
                if (file.delete()) {
                    Log.d(TAG, "Deleted old file: " + file.getName());
                }
            }
        }
    }
}
