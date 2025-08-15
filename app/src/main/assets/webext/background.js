(function(){
  'use strict';

  // On Android GeckoView, the native app owns the background port via WebExtensionController.
  // The background page receives that connection via browser.runtime.onConnect.
  // We use that Port to relay messages between the app and content scripts.

  /** @type {browser.runtime.Port|null} */
  let appPort = null;
  let connectTimer = null;
  let attemptsLeft = 0;
  let lastContentTabId = null;

  // Helper to wire an app Port with relays
  function bindAppPort(p) {
    appPort = p;
    try { console.log('[TV][EXT][BG] appPort bound'); } catch (_) {}
    p.onMessage.addListener(async (msg) => {
      try { console.log('[TV][EXT][BG] App->BG msg', msg); } catch (_) {}
      const id = msg && msg.id; const cmd = msg && msg.cmd;
      try {
        if (lastContentTabId != null) {
          // Special-case tv-menu-nav: broadcast to all frames so iframes receive it
          if (msg && msg.type === 'tv-menu-nav') {
            try {
              const frames = await browser.webNavigation.getAllFrames({ tabId: lastContentTabId });
              for (const f of frames || []) {
                if (typeof f.frameId === 'number') {
                  try { await browser.tabs.sendMessage(lastContentTabId, msg, { frameId: f.frameId }); } catch (_) {}
                }
              }
              try { console.log('[TV][EXT][BG] BG->CT broadcast tv-menu-nav to all frames in tab', lastContentTabId); } catch (_) {}
            } catch (e) {
              try { console.log('[TV][EXT][BG] getAllFrames failed, falling back to top-only', String(e)); } catch (_) {}
              await browser.tabs.sendMessage(lastContentTabId, msg);
            }
          } else {
            await browser.tabs.sendMessage(lastContentTabId, msg);
            try { console.log('[TV][EXT][BG] BG->CT forwarded via tabs.sendMessage tab=', lastContentTabId, 'cmd=', cmd); } catch (_) {}
          }
        } else {
          throw new Error('No known content tab to forward to');
        }
        // Send diagnostic echo to app (will not clear timeouts since type != cmd+':done')
        try { appPort && appPort.postMessage({ id, type: 'bg_forwarded', cmd }); } catch (_) {}
      } catch (e) {
        try { console.log('[TV][EXT][BG] BG->CT forward error', String(e)); } catch (_) {}
        try { appPort && appPort.postMessage({ id, type: 'bg_forward_error', cmd, error: String(e) }); } catch (_) {}
      }
    });
    p.onDisconnect.addListener(() => {
      try { console.log('[TV][EXT][BG] appPort disconnected'); } catch (_) {}
      appPort = null;
      // restart connect loop on disconnect
      startConnectLoop(50, 600);
    });
    // Send hello so Android can verify BG is alive
    try { appPort.postMessage({ type: 'bg_ready' }); } catch (_) {}
  }

  // Try to actively connect to the native app. In GeckoView, connectNative is bridged to the embedding app.
  function ensureNativeConnection() {
    if (appPort) return;
    try {
      // Use connectNative to reach the Android MessageDelegate namespace 'neewbrowser'
      const p = browser.runtime.connectNative('neewbrowser');
      bindAppPort(p);
      try { console.log('[TV][EXT][BG] connectNative initiated'); } catch (_) {}
    } catch (e) {
      try { console.log('[TV][EXT][BG] connectNative failed', String(e)); } catch (_) {}
    }
  }

  function startConnectLoop(maxAttempts, intervalMs) {
    attemptsLeft = maxAttempts;
    if (connectTimer) { clearInterval(connectTimer); connectTimer = null; }
    connectTimer = setInterval(() => {
      if (appPort) { clearInterval(connectTimer); connectTimer = null; return; }
      if (attemptsLeft-- <= 0) { clearInterval(connectTimer); connectTimer = null; return; }
      try { console.log('[TV][EXT][BG] connect attempt, remaining=', attemptsLeft); } catch (_) {}
      ensureNativeConnection();
    }, intervalMs);
  }

  // Eagerly try to connect on background load and common lifecycle events
  startConnectLoop(50, 600);
  browser.runtime.onStartup.addListener(() => {
    startConnectLoop(50, 600);
  });
  browser.runtime.onInstalled.addListener(() => {
    startConnectLoop(50, 600);
  });
  

  // Content -> BG: warm-up and general messages
  browser.runtime.onMessage.addListener((msg, sender) => {
    try {
      if (sender && sender.tab && typeof sender.tab.id === 'number') {
        lastContentTabId = sender.tab.id;
        try { console.log('[TV][EXT][BG] noted content tabId', lastContentTabId); } catch (_) {}
      }
      if (msg && msg.type === 'tv-warmup') {
        console.log('[TV][EXT][BG] received warmup from content, ensuring native connection');
        ensureNativeConnection();
      }
    } catch (_) {}
  });

  // Receive a connection from the Android app (if the app initiates it)
  browser.runtime.onConnect.addListener((p) => {
    try { console.log('[TV][EXT][BG] onConnect port', p && p.name); } catch (_) {}
    bindAppPort(p);
  });

  // Content -> App: forward responses from content scripts back to the app
  browser.runtime.onMessage.addListener((msg) => {
    try { console.log('[TV][EXT][BG] Content->BG msg', msg); } catch (_) {}
    if (!appPort) return; // app not connected yet
    try {
      appPort.postMessage(msg);
      try { console.log('[TV][EXT][BG] BG->APP forwarded', msg && msg.type); } catch (_) {}
    } catch (e) {
      try { console.log('[TV][EXT][BG] BG->APP forward error', String(e)); } catch (_) {}
    }
  });

  // Kick off the connection at startup
  ensureNativeConnection();
})();
