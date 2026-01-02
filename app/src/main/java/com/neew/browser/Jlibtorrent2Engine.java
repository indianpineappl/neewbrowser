package com.neew.browser;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Jlibtorrent2Engine implements TorrentEngine {

    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<String, Boolean> streamModeBySession = new ConcurrentHashMap<>();

    @Override
    public String start(String uri, boolean streamMode, Listener listener) {
        String sessionId = "session-" + idGenerator.getAndIncrement();
        streamModeBySession.put(sessionId, streamMode);
        return sessionId;
    }

    @Override
    public void stop(String sessionId) {
        streamModeBySession.remove(sessionId);
    }

    @Override
    public boolean isStreaming(String sessionId) {
        Boolean mode = streamModeBySession.get(sessionId);
        return mode != null && mode;
    }

    @Override
    public File getMediaFile(String sessionId) {
        return null;
    }
}
