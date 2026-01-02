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

        File saveDir;
        if (streamMode) {
            saveDir = appContext.getExternalFilesDir("torrent_temp");
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
        currentTorrent = torrent;
        final Listener listener = currentListener;
        final String sessionId = currentSessionId;
        try {
            torrent.startDownload();
            Log.d(TAG, "Invoked torrent.startDownload()");
        } catch (Exception e) {
            Log.w(TAG, "Could not call startDownload on torrent", e);
        }
        if (listener != null && sessionId != null) {
            mainHandler.post(() -> listener.onPrepared(sessionId));
        }
    }

    @Override
    public synchronized void onStreamStarted(Torrent torrent) {
        currentTorrent = torrent;
        final Listener listener = currentListener;
        final String sessionId = currentSessionId;
        if (listener != null && sessionId != null) {
            mainHandler.post(() -> listener.onStarted(sessionId));
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
            mainHandler.post(() -> listener.onReadyForPlayback(sessionId, mediaFile));
        }
    }

    @Override
    public synchronized void onStreamProgress(Torrent torrent, StreamStatus status) {
        currentTorrent = torrent;
        lastStatus = status;

        final Listener listener = currentListener;
        final String sessionId = currentSessionId;
        if (listener != null && sessionId != null && status != null) {
            final float progress = status.progress;
            final int seeds = status.seeds;
            final long speedBytesPerSecond = status.downloadSpeed;
            mainHandler.post(() -> listener.onProgress(sessionId, progress, seeds, speedBytesPerSecond));
        }
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
}
