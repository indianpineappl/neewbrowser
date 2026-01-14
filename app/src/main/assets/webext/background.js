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

  // Handle dynamic injection of TV content script
  /** @type {browser.contentScripts.RegisteredContentScript|null} */
  let tvScriptRegistration = null;
  let tvEnabled = false;

  async function registerTvScript() {
    if (tvScriptRegistration) return;
    try {
      tvScriptRegistration = await browser.contentScripts.register({
        matches: ['<all_urls>'],
        js: [{ file: 'content.js' }],
        runAt: 'document_idle',
        allFrames: true
      });
      try { console.log('[TV][EXT][BG] content.js registered'); } catch(_) {}
    } catch (e) {
      try { console.log('[TV][EXT][BG] register error', String(e)); } catch(_) {}
    }
  }

  async function unregisterTvScript() {
    if (!tvScriptRegistration) return;
    try {
      await tvScriptRegistration.unregister();
      tvScriptRegistration = null;
      try { console.log('[TV][EXT][BG] content.js unregistered'); } catch(_) {}
    } catch (e) {
      try { console.log('[TV][EXT][BG] unregister error', String(e)); } catch(_) {}
    }
  }

  async function setTvEnabled(flag) {
    tvEnabled = !!flag;
    try { await browser.storage.local.set({ tvEnabled }); } catch(_) {}
    if (tvEnabled) {
      await registerTvScript();
      // Also inject into already loaded tabs so warmup can bind lastContentTabId
      try {
        const tabs = await browser.tabs.query({});
        for (const t of tabs || []) {
          try {
            if (typeof t.id === 'number') {
              await browser.tabs.executeScript(t.id, { file: 'content.js', allFrames: true, runAt: 'document_idle' });
            }
          } catch (e) {
            try { console.log('[TV][EXT][BG] executeScript failed tab=', t && t.id, String(e)); } catch(_) {}
          }
        }
      } catch (e) {
        try { console.log('[TV][EXT][BG] tabs.query/executeScript error', String(e)); } catch(_) {}
      }
    } else {
      await unregisterTvScript();
    }
  }

  // Helper to re-inject content script into all existing tabs
  async function reinjectContentScript() {
    try {
      const tabs = await browser.tabs.query({});
      for (const t of tabs || []) {
        try {
          if (typeof t.id === 'number') {
            await browser.tabs.executeScript(t.id, { file: 'content.js', allFrames: true, runAt: 'document_idle' });
            try { console.log('[TV][EXT][BG] re-injected content.js into tab', t.id); } catch(_) {}
          }
        } catch (e) {
          // Tab might not be ready or script already injected, ignore
        }
      }
    } catch (e) {
      try { console.log('[TV][EXT][BG] re-inject failed', String(e)); } catch(_) {}
    }
  }

  // Helper to wire an app Port with relays
  function bindAppPort(p) {
    appPort = p;
    try { console.log('[TV][EXT][BG] appPort bound'); } catch (_) {}
    
    p.onMessage.addListener(async (msg) => {
      try { console.log('[TV][EXT][BG] App->BG msg', msg); } catch (_) {}
      const id = msg && msg.id; const cmd = msg && msg.cmd;
      try {
        // Special: source of truth for TV enablement
        if (msg && msg.type === 'tv-enabled') {
          await setTvEnabled(!!msg.enabled);
          try { console.log('[TV][EXT][BG] tv-enabled set to', !!msg.enabled); } catch(_) {}
          // Re-inject into existing tabs whenever TV is enabled (including on reconnect)
          if (!!msg.enabled) {
            await reinjectContentScript();
          }
          return;
        }
        if (lastContentTabId == null) {
          // Try to pick current active tab
          try {
            const activeTabs = await browser.tabs.query({ active: true, currentWindow: true });
            if (activeTabs && activeTabs[0] && typeof activeTabs[0].id === 'number') {
              lastContentTabId = activeTabs[0].id;
              try { console.log('[TV][EXT][BG] inferred active tabId', lastContentTabId); } catch(_) {}
            }
          } catch (_) {}
        }
        if (lastContentTabId != null) {
          // Special-case tv-menu-nav and menuProbe:*: broadcast to all frames so iframes receive it
          const isMenuProbe = !!(msg && (
            (typeof msg.cmd === 'string' && msg.cmd.indexOf('menuProbe:') === 0) ||
            (typeof msg.type === 'string' && msg.type.indexOf('menuProbe:') === 0)
          ));
          if ((msg && msg.type === 'tv-menu-nav') || isMenuProbe) {
            try {
              const frames = await browser.webNavigation.getAllFrames({ tabId: lastContentTabId });
              for (const f of frames || []) {
                if (typeof f.frameId === 'number') {
                  try { await browser.tabs.sendMessage(lastContentTabId, msg, { frameId: f.frameId }); } catch (_) {}
                }
              }
              try { console.log('[TV][EXT][BG] BG->CT broadcast', (msg && (msg.type || msg.cmd)), 'to all frames in tab', lastContentTabId); } catch (_) {}
            } catch (e) {
              try { console.log('[TV][EXT][BG] getAllFrames failed, falling back to top-only', String(e)); } catch (_) {}
              try {
                await browser.tabs.sendMessage(lastContentTabId, msg);
              } catch (sendErr) {
                // Attempt to inject script and retry once with slight delay
                try {
                  await browser.tabs.executeScript(lastContentTabId, { file: 'content.js', allFrames: true, runAt: 'document_idle' });
                  await new Promise(r => setTimeout(r, 16));
                  await browser.tabs.sendMessage(lastContentTabId, msg);
                  try { console.log('[TV][EXT][BG] healed by injecting content.js and retried broadcast for', (msg && (msg.type || msg.cmd))); } catch(_) {}
                } catch (healErr) {
                  // As a last resort, broadcast to all tabs' top frames
                  try {
                    const tabs = await browser.tabs.query({});
                    for (const t of tabs || []) {
                      try { typeof t.id === 'number' && (await browser.tabs.sendMessage(t.id, msg)); } catch (_) {}
                    }
                    try { console.log('[TV][EXT][BG] broadcasted', (msg && (msg.type || msg.cmd)), 'to all tabs after heal failure'); } catch(_) {}
                  } catch (e2) {}
                }
              }
            }
          } else {
            try {
              await browser.tabs.sendMessage(lastContentTabId, msg);
              try { console.log('[TV][EXT][BG] BG->CT forwarded via tabs.sendMessage tab=', lastContentTabId, 'cmd=', cmd); } catch (_) {}
            } catch (sendErr) {
              // Attempt to inject script and retry once with slight delay
              try {
                await browser.tabs.executeScript(lastContentTabId, { file: 'content.js', allFrames: true, runAt: 'document_idle' });
                await new Promise(r => setTimeout(r, 16));
                await browser.tabs.sendMessage(lastContentTabId, msg);
                try { console.log('[TV][EXT][BG] healed by injecting content.js and retried cmd=', cmd); } catch(_) {}
              } catch (healErr) {
                // As a last resort, broadcast to all tabs' top frames
                try {
                  const tabs = await browser.tabs.query({});
                  for (const t of tabs || []) {
                    try { typeof t.id === 'number' && (await browser.tabs.sendMessage(t.id, msg)); } catch (_) {}
                  }
                  try { console.log('[TV][EXT][BG] broadcasted generic cmd to all tabs after heal failure'); } catch(_) {}
                } catch (e2) {}
              }
            }
          }
        } else {
          // Fallback: try to broadcast to all tabs' top frames
          try {
            const tabs = await browser.tabs.query({});
            for (const t of tabs || []) {
              try { typeof t.id === 'number' && (await browser.tabs.sendMessage(t.id, msg)); } catch (_) {}
            }
            try { console.log('[TV][EXT][BG] broadcasted to all tabs (no known content tab)'); } catch(_) {}
          } catch (e) {
            throw new Error('No known content tab to forward to');
          }
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

  // Initialize tvEnabled state from storage and register accordingly
  (async () => {
    try {
      const st = await browser.storage.local.get('tvEnabled');
      if (st && typeof st.tvEnabled === 'boolean') {
        await setTvEnabled(st.tvEnabled);
      }
    } catch(_) {}
  })();

  // Track active tab to improve forwarding reliability
  try {
    browser.tabs.onActivated.addListener((info) => {
      if (info && typeof info.tabId === 'number') {
        lastContentTabId = info.tabId;
        try { console.log('[TV][EXT][BG] onActivated tabId', lastContentTabId); } catch(_) {}
      }
    });
    browser.tabs.onUpdated.addListener((tabId, changeInfo) => {
      if (tvEnabled && changeInfo && changeInfo.status === 'complete') {
        lastContentTabId = tabId;
        try { console.log('[TV][EXT][BG] onUpdated complete tabId', lastContentTabId); } catch(_) {}
      }
    });
  } catch(_) {}

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
  const pendingDedupe = new Map(); // id -> { timer, bestMsg, final }
  const DONE_SUFFIX_RE = /:done$/;
  const GRACE_MS = 50; // wait briefly to allow a better ok:true to arrive

  function forwardToApp(msg) {
    if (!appPort) return;
    try {
      appPort.postMessage(msg);
      try { console.log('[TV][EXT][BG] BG->APP forwarded', msg && msg.type, 'id=', msg && msg.id, 'ok=', msg && msg.ok); } catch (_) {}
    } catch (e) {
      try { console.log('[TV][EXT][BG] BG->APP forward error', String(e)); } catch (_) {}
    }
  }

  browser.runtime.onMessage.addListener((msg) => {
    try { console.log('[TV][EXT][BG] Content->BG msg', msg); } catch (_) {}
    // Only de-dupe messages that have an id and a *:done type
    const id = msg && msg.id;
    const type = msg && msg.type;
    const isDone = !!(type && DONE_SUFFIX_RE.test(type));
    if (!id || !isDone) {
      forwardToApp(msg);
      return;
    }

    // De-duplication path
    let entry = pendingDedupe.get(id);
    if (!entry) {
      // First arrival for this id
      if (msg && msg.ok === true) {
        forwardToApp(msg); // definitive success -> forward immediately
        pendingDedupe.set(id, { final: true, timer: null, bestMsg: msg });
        // Cleanup soon to avoid leaks
        setTimeout(() => { pendingDedupe.delete(id); }, 500);
      } else {
        // Not ok or unknown -> hold briefly to see if a better reply arrives
        const hold = { final: false, bestMsg: msg, timer: null };
        hold.timer = setTimeout(() => {
          try { forwardToApp(hold.bestMsg); } finally { pendingDedupe.delete(id); }
        }, GRACE_MS);
        pendingDedupe.set(id, hold);
      }
      return;
    }

    // Subsequent arrivals for same id
    if (entry.final) {
      // Already finalized; drop duplicates
      return;
    }

    if (msg && msg.ok === true) {
      // Upgrade to success within grace window
      try { entry.timer && clearTimeout(entry.timer); } catch (_) {}
      forwardToApp(msg);
      entry.final = true;
      pendingDedupe.delete(id);
      return;
    }

    // Another non-ok: keep the first one (or the better one if we want later). For now keep first.
    // Optionally update bestMsg heuristically here.
  });

  // Kick off the connection at startup
  ensureNativeConnection();
})();
