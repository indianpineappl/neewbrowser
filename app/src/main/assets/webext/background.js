(function(){
  'use strict';

  // Background script for TV Scroll Helper extension.
  // Relays messages between the Android app (via native port) and content scripts.
  // OPTIMIZED: Sends to top frame only (frameId: 0); content.js delegates to iframes as needed.

  /** @type {browser.runtime.Port|null} */
  let appPort = null;
  let connectTimer = null;
  let attemptsLeft = 0;
  let lastContentTabId = null;

  /** @type {browser.contentScripts.RegisteredContentScript|null} */
  let tvScriptRegistration = null;
  let tvEnabled = false;

  // Rate-limit re-injection to avoid spamming on repeated failures
  let lastInjectAttemptMs = 0;
  const INJECT_COOLDOWN_MS = 2000;

  async function registerTvScript() {
    if (tvScriptRegistration) return;
    try {
      tvScriptRegistration = await browser.contentScripts.register({
        matches: ['<all_urls>'],
        js: [{ file: 'content.js' }],
        runAt: 'document_idle',
        allFrames: true
      });
    } catch (_) {}
  }

  async function unregisterTvScript() {
    if (!tvScriptRegistration) return;
    try {
      await tvScriptRegistration.unregister();
      tvScriptRegistration = null;
    } catch (_) {}
  }

  async function setTvEnabled(flag) {
    tvEnabled = !!flag;
    try { await browser.storage.local.set({ tvEnabled }); } catch(_) {}
    if (tvEnabled) {
      await registerTvScript();
      await injectIntoExistingTabs();
    } else {
      await unregisterTvScript();
    }
  }

  async function injectIntoExistingTabs() {
    try {
      const tabs = await browser.tabs.query({});
      for (const t of tabs || []) {
        try {
          if (typeof t.id === 'number') {
            await browser.tabs.executeScript(t.id, { file: 'content.js', allFrames: true, runAt: 'document_idle' });
          }
        } catch (_) {}
      }
    } catch (_) {}
  }

  // Send message to content script in the active tab's TOP FRAME ONLY.
  // Content.js handles delegation to iframes via postMessage when needed.
  async function sendToTopFrame(msg) {
    if (lastContentTabId == null) {
      try {
        const activeTabs = await browser.tabs.query({ active: true, currentWindow: true });
        if (activeTabs && activeTabs[0] && typeof activeTabs[0].id === 'number') {
          lastContentTabId = activeTabs[0].id;
        }
      } catch (_) {}
    }

    if (lastContentTabId == null) return false;

    try {
      // Send to top frame only (frameId: 0) — massive perf win over broadcasting to all frames
      await browser.tabs.sendMessage(lastContentTabId, msg, { frameId: 0 });
      return true;
    } catch (_) {
      // Content script may not be injected yet — try once with cooldown
      const now = Date.now();
      if (now - lastInjectAttemptMs > INJECT_COOLDOWN_MS) {
        lastInjectAttemptMs = now;
        try {
          await browser.tabs.executeScript(lastContentTabId, { file: 'content.js', allFrames: true, runAt: 'document_idle' });
          await new Promise(r => setTimeout(r, 16));
          await browser.tabs.sendMessage(lastContentTabId, msg, { frameId: 0 });
          return true;
        } catch (_) {}
      }
      return false;
    }
  }

  function bindAppPort(p) {
    appPort = p;

    p.onMessage.addListener(async (msg) => {
      const id = msg && msg.id;
      const cmd = msg && msg.cmd;
      try {
        // TV enablement control message
        if (msg && msg.type === 'tv-enabled') {
          await setTvEnabled(!!msg.enabled);
          if (!!msg.enabled) await injectIntoExistingTabs();
          return;
        }

        // Forward to content script (top frame only)
        await sendToTopFrame(msg);
      } catch (e) {
        try { appPort && appPort.postMessage({ id, type: 'bg_forward_error', cmd, error: String(e) }); } catch (_) {}
      }
    });

    p.onDisconnect.addListener(() => {
      appPort = null;
      startConnectLoop(20, 1000); // Use exponential-ish backoff: fewer attempts, longer interval
    });

    try { appPort.postMessage({ type: 'bg_ready' }); } catch (_) {}
  }

  function ensureNativeConnection() {
    if (appPort) return;
    try {
      const p = browser.runtime.connectNative('neewbrowser');
      bindAppPort(p);
    } catch (_) {}
  }

  function startConnectLoop(maxAttempts, intervalMs) {
    attemptsLeft = maxAttempts;
    if (connectTimer) { clearInterval(connectTimer); connectTimer = null; }
    connectTimer = setInterval(() => {
      if (appPort) { clearInterval(connectTimer); connectTimer = null; return; }
      if (attemptsLeft-- <= 0) { clearInterval(connectTimer); connectTimer = null; return; }
      ensureNativeConnection();
    }, intervalMs);
  }

  // Initial connection
  startConnectLoop(30, 800);
  browser.runtime.onStartup.addListener(() => startConnectLoop(20, 1000));
  browser.runtime.onInstalled.addListener(() => startConnectLoop(20, 1000));

  // Restore tvEnabled from storage
  (async () => {
    try {
      const st = await browser.storage.local.get('tvEnabled');
      if (st && typeof st.tvEnabled === 'boolean') {
        await setTvEnabled(st.tvEnabled);
      }
    } catch(_) {}
  })();

  // Track active tab
  try {
    browser.tabs.onActivated.addListener((info) => {
      if (info && typeof info.tabId === 'number') lastContentTabId = info.tabId;
    });
    browser.tabs.onUpdated.addListener((tabId, changeInfo) => {
      if (tvEnabled && changeInfo && changeInfo.status === 'complete') lastContentTabId = tabId;
    });
  } catch(_) {}

  // Content -> BG: handle warmup and forward responses to app
  browser.runtime.onMessage.addListener((msg, sender) => {
    try {
      if (sender && sender.tab && typeof sender.tab.id === 'number') {
        lastContentTabId = sender.tab.id;
      }
      // Warmup: ensure native connection
      if (msg && msg.type === 'tv-warmup') {
        ensureNativeConnection();
        return;
      }
      // Forward content script responses (scroll:done, focus:done, etc.) to the app
      if (msg && msg.id && appPort) {
        appPort.postMessage(msg);
      }
      // Noisy ack messages — drop silently
      if (msg && msg.type === 'tv-menu-nav-ack') return;
      if (msg && msg.type === 'tv-ext-log') return;
    } catch (_) {}
  });

  // Accept connection from the Android app
  browser.runtime.onConnect.addListener((p) => { bindAppPort(p); });

  ensureNativeConnection();
})();
