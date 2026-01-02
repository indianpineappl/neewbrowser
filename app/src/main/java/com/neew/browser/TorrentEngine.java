package com.neew.browser;

import java.io.File;

public interface TorrentEngine {

    interface Listener {
        void onPrepared(String sessionId);
        void onStarted(String sessionId);
        void onProgress(String sessionId, float progress, int seeds, long downloadSpeedBytesPerSecond);
        void onReadyForPlayback(String sessionId, File mediaFile);
        void onError(String sessionId, Throwable error);
        void onStopped(String sessionId);
    }

    String start(String uri, boolean streamMode, Listener listener);

    void stop(String sessionId);

    boolean isStreaming(String sessionId);

    File getMediaFile(String sessionId);
}
