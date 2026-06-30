(function () {
  'use strict';

  // --- SUBFRAME GATE (Task #5) ---
  // In subframes, only activate if explicitly delegated via postMessage.
  // This prevents dozens of ad-iframe instances from running the full script.
  const isTopFrame = (window.top === window);

  function detectTvMode() {
    try {
      if (typeof window !== 'undefined') {
        if (window.__NEEW_TV_DISABLED__ === true) return false;
        if (window.__NEEW_TV_MODE__ === true) return true;
      }
      try {
        const localOff = typeof localStorage !== 'undefined' ? localStorage.getItem('neew.tv.disabled') : null;
        if (localOff === '1' || localOff === 'true') return false;
        const sessionFlag = typeof sessionStorage !== 'undefined' ? sessionStorage.getItem('neew.tv.enabled') : null;
        const localFlag = typeof localStorage !== 'undefined' ? localStorage.getItem('neew.tv.enabled') : null;
        if (sessionFlag === '1' || sessionFlag === 'true' || localFlag === '1' || localFlag === 'true') return true;
      } catch (_) {}
      const ua = (typeof navigator !== 'undefined' && navigator.userAgent) ? navigator.userAgent : '';
      return /(Android\s+TV|BRAVIA|AFTB|AFTM|AFTS|AFT|SmartTV|Tizen|Web0S|WebOS|AppleTV|Chromecast|Roku|Shield|MiBOX|\bTV\b)/i.test(ua);
    } catch (_) {
      return false;
    }
  }

  if (typeof window !== 'undefined') {
    try { window.__NEEW_TV_MODE__ = true; } catch (_) {}
  }
  if (!detectTvMode()) { return; }

  // --- Subframe: only listen for delegated postMessage, skip everything else ---
  if (!isTopFrame) {
    window.addEventListener('message', (evt) => {
      const data = evt && evt.data;
      if (!data || typeof data !== 'object') return;
      if (data.type === 'tv-frame-focus' && data.payload) {
        handleFocusAtPoint({ ...data.payload, local: true });
      } else if (data.type === 'tv-frame-scroll' && data.payload) {
        handleScrollAtPoint({ ...data.payload, local: true });
      }
    }, true);
    // No warmup, no browser.runtime listener, no hover/cursor — only delegated scroll/focus
    return;
  }

  // --- TOP FRAME ONLY from here ---

  function warmUp() {
    try {
      browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ type: 'tv-warmup' });
    } catch (_) {}
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', warmUp, { once: true });
  } else {
    warmUp();
  }

  // --- SCROLLABLE CACHE (Task #3) ---
  // Cache the last-used element -> scrollable container mapping to avoid DOM walk on every press.
  let cachedScrollElement = null;
  let cachedScrollTarget = null;
  let cacheInvalidateTimer = null;
  const CACHE_TTL_MS = 2000; // invalidate after 2s idle

  function invalidateScrollCache() {
    cachedScrollElement = null;
    cachedScrollTarget = null;
  }

  function touchScrollCache() {
    if (cacheInvalidateTimer) clearTimeout(cacheInvalidateTimer);
    cacheInvalidateTimer = setTimeout(invalidateScrollCache, CACHE_TTL_MS);
  }

  // Invalidate cache on navigation within the page
  try {
    const obs = new MutationObserver(() => { invalidateScrollCache(); });
    obs.observe(document.documentElement, { childList: true, subtree: false });
  } catch (_) {}

  const scrollHandledIds = new Set();
  const focusHandledIds = new Set();
  let lastHover = null;

  function sendMenuNavAck() {
    try {
      browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ type: 'tv-menu-nav-ack' });
    } catch (_) {}
  }

  function deepElementFromPoint(x, y) {
    const vv = window.visualViewport;
    const vx = vv ? x - (vv.offsetLeft || 0) : x;
    const vy = vv ? y - (vv.offsetTop || 0) : y;
    let el = document.elementFromPoint(vx, vy);
    while (el && el.shadowRoot && el.shadowRoot.elementFromPoint) {
      const shadowEl = el.shadowRoot.elementFromPoint(vx, vy);
      if (!shadowEl || shadowEl === el) break;
      el = shadowEl;
    }
    return el;
  }

  function getScrollableAncestor(el) {
    if (!el || el === document) {
      return document.scrollingElement || document.documentElement || document.body;
    }
    let current = el;
    while (current && current !== document && current !== document.documentElement && current !== document.body) {
      const style = window.getComputedStyle(current);
      const overflowY = style.overflowY;
      const overflowX = style.overflowX;
      const canScrollY = (overflowY && overflowY !== 'visible') && (current.scrollHeight - current.clientHeight > 1);
      const canScrollX = (overflowX && overflowX !== 'visible') && (current.scrollWidth - current.clientWidth > 1);
      if (canScrollY || canScrollX) return current;
      current = current.parentElement || current.parentNode;
    }
    return document.scrollingElement || document.documentElement || document.body;
  }

  function getCachedScrollable(element) {
    // If the element under cursor matches our cache, skip the DOM walk
    if (cachedScrollElement && cachedScrollElement === element && cachedScrollTarget) {
      // Quick validation: is the cached target still scrollable?
      if (cachedScrollTarget.scrollHeight - cachedScrollTarget.clientHeight > 1) {
        return cachedScrollTarget;
      }
      invalidateScrollCache();
    }
    const target = getScrollableAncestor(element);
    cachedScrollElement = element;
    cachedScrollTarget = target;
    touchScrollCache();
    return target;
  }

  function clampToViewport(value, max) {
    return Math.max(0, Math.min(max, value));
  }

  function sendFocusDone(id, ok, used) {
    if (!id) return;
    try {
      browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ id, type: 'focusAtPoint:done', ok, used });
    } catch (_) {}
  }

  function sendScrollDone(id, ok, used) {
    if (!id) return;
    try {
      browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ id, type: 'scrollAtPoint:done', ok, used });
    } catch (_) {}
  }

  function delegateToIframe(type, targetIframe, payload) {
    try {
      targetIframe.contentWindow && targetIframe.contentWindow.postMessage({ type, payload }, '*');
      return true;
    } catch (_) { return false; }
  }

  function findIframeUnderPoint(x, y) {
    const stack = (document.elementsFromPoint && document.elementsFromPoint(x, y)) || [];
    return stack.find(node => node && node.tagName === 'IFRAME');
  }

  async function handleFocusAtPoint(msg) {
    const { id, x = 0, y = 0, dpr, local } = msg;
    if (!local && !isTopFrame) return;
    if (id && focusHandledIds.has(id)) return;
    if (id) focusHandledIds.add(id);

    const devicePixelRatio = Number(dpr) || window.devicePixelRatio || 1;
    const xCss = clampToViewport(x / devicePixelRatio, window.innerWidth - 1);
    const yCss = clampToViewport(y / devicePixelRatio, window.innerHeight - 1);

    // Delegate to iframe if point falls within one
    const iframe = !local ? findIframeUnderPoint(xCss, yCss) : null;
    if (!local && iframe && delegateToIframe('tv-frame-focus', iframe, { id, x: xCss, y: yCss, dpr: devicePixelRatio, local: true })) {
      return;
    }

    let target = deepElementFromPoint(xCss, yCss);
    if (!target) {
      sendFocusDone(id, false, 'none');
      if (id) focusHandledIds.delete(id);
      return;
    }
    if (target.nodeType === Node.TEXT_NODE) target = target.parentElement;
    if (target) {
      if (target.tabIndex < 0) target.setAttribute('tabindex', '0');
      try { target.focus({ preventScroll: true }); } catch (_) {}
    }
    sendFocusDone(id, true, target && target.tagName ? target.tagName.toLowerCase() : 'unknown');
    if (id) focusHandledIds.delete(id);
  }

  function tryScrollElement(node, deltaY) {
    if (!node) return false;
    function attempt(target) {
      if (!target) return false;
      if (target === window || target === document || target === document.body) {
        const before = window.scrollY;
        window.scrollBy(0, deltaY);
        return window.scrollY !== before;
      }
      const beforeTop = target.scrollTop;
      if (typeof target.scrollBy === 'function') {
        target.scrollBy({ top: deltaY, behavior: 'auto' });
      } else {
        target.scrollTop = beforeTop + deltaY;
      }
      return target.scrollTop !== beforeTop;
    }

    if (attempt(node)) return true;

    // Escalate to a higher ancestor once
    const parent = node.parentElement || node.parentNode;
    const higher = parent ? getScrollableAncestor(parent) : null;
    if (higher && higher !== node && attempt(higher)) return true;

    // Last resort: synthesize wheel event for custom JS scrollers
    try {
      const evt = new WheelEvent('wheel', { bubbles: true, cancelable: true, deltaY, deltaMode: 0 });
      node.dispatchEvent(evt);
      return true;
    } catch (_) {}
    return false;
  }

  async function handleScrollAtPoint(msg) {
    const { id, x = 0, y = 0, dy = 0, dpr, local } = msg;
    if (!local && !isTopFrame) return;
    if (id && scrollHandledIds.has(id)) return;
    if (id) scrollHandledIds.add(id);

    const devicePixelRatio = Number(dpr) || window.devicePixelRatio || 1;
    const xCss = clampToViewport(x / devicePixelRatio, window.innerWidth - 1);
    const yCss = clampToViewport(y / devicePixelRatio, window.innerHeight - 1);

    // Delegate to iframe if point is inside one
    const iframe = !local ? findIframeUnderPoint(xCss, yCss) : null;
    if (!local && iframe && delegateToIframe('tv-frame-scroll', iframe, { id, x: xCss, y: yCss, dy, dpr: devicePixelRatio, local: true })) {
      return;
    }

    const element = deepElementFromPoint(xCss, yCss);

    // YouTube playlist panels: handle explicitly
    const playlistPanel = element && element.closest
      ? element.closest('ytd-playlist-panel-renderer, ytm-playlist-panel-renderer')
      : null;
    if (playlistPanel) {
      const moved = tryScrollElement(playlistPanel, dy);
      sendScrollDone(id, moved, playlistPanel.tagName ? playlistPanel.tagName.toLowerCase() : 'playlist-panel');
      if (id) scrollHandledIds.delete(id);
      return;
    }

    // Use cached scrollable ancestor (Task #3 optimization)
    const base = element || document.body;
    const target = getCachedScrollable(base);

    const root = document.scrollingElement || document.documentElement || document.body;
    const isRootTarget = !target || target === root || target === document.documentElement || target === document.body;
    const moved = isRootTarget ? false : tryScrollElement(target, dy);

    let used = 'none';
    if (isRootTarget) used = 'root';
    else if (target && target.tagName) used = target.tagName.toLowerCase();

    sendScrollDone(id, moved, used);
    if (id) scrollHandledIds.delete(id);
  }

  function handleHoverAtPoint(msg) {
    const { x = 0, y = 0, dpr } = msg || {};
    const devicePixelRatio = Number(dpr) || window.devicePixelRatio || 1;
    const xCss = clampToViewport(x / devicePixelRatio, window.innerWidth - 1);
    const yCss = clampToViewport(y / devicePixelRatio, window.innerHeight - 1);
    const target = deepElementFromPoint(xCss, yCss);
    if (!target) return;
    if (lastHover && lastHover !== target) {
      try {
        lastHover.dispatchEvent(new MouseEvent('mouseout', { bubbles: true, cancelable: true, clientX: xCss, clientY: yCss }));
      } catch (_) {}
    }
    lastHover = target;
    // Batch hover events into a single pointermove + mouseover
    try {
      target.dispatchEvent(new PointerEvent('pointermove', { bubbles: true, cancelable: true, clientX: xCss, clientY: yCss, pointerId: 1, pointerType: 'mouse' }));
      target.dispatchEvent(new MouseEvent('mouseover', { bubbles: true, cancelable: true, view: window, clientX: xCss, clientY: yCss }));
    } catch (_) {}
  }

  function handleMoveCursor(msg) {
    const { x = 0, y = 0, dpr } = msg || {};
    const devicePixelRatio = Number(dpr) || window.devicePixelRatio || 1;
    const xCss = clampToViewport(x / devicePixelRatio, window.innerWidth - 1);
    const yCss = clampToViewport(y / devicePixelRatio, window.innerHeight - 1);
    const target = deepElementFromPoint(xCss, yCss);
    if (!target) return;
    try {
      target.dispatchEvent(new PointerEvent('pointermove', { bubbles: true, cancelable: true, clientX: xCss, clientY: yCss, pointerId: 1, pointerType: 'mouse' }));
    } catch (_) {}
  }

  // Listen for delegated messages from iframes bubbling results up
  window.addEventListener('message', (evt) => {
    const data = evt && evt.data;
    if (!data || typeof data !== 'object') return;
    // Forward iframe scroll/focus results up to background via runtime
    if (data.type === 'tv-frame-scroll:done' && data.id) {
      sendScrollDone(data.id, data.ok, data.used);
    } else if (data.type === 'tv-frame-focus:done' && data.id) {
      sendFocusDone(data.id, data.ok, data.used);
    } else if (data.type === 'tv-menu-nav') {
      sendMenuNavAck();
    }
  }, true);

  // Listen for messages from background script
  if (typeof browser !== 'undefined' && browser.runtime && browser.runtime.onMessage) {
    browser.runtime.onMessage.addListener((msg) => {
      if (!msg || typeof msg !== 'object') return;
      if (msg.cmd === 'focusScrollableAtPoint') {
        handleFocusAtPoint(msg);
      } else if (msg.cmd === 'scrollAtPoint') {
        handleScrollAtPoint(msg);
      } else if (msg.cmd === 'tv-menu-nav' || msg.type === 'tv-menu-nav') {
        sendMenuNavAck();
      } else if (msg.cmd === 'hoverAtPoint') {
        handleHoverAtPoint(msg);
      } else if (msg.cmd === 'moveCursor') {
        handleMoveCursor(msg);
      }
    });
  }
})();
