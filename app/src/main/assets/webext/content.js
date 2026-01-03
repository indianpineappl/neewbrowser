(function () {
  'use strict';

  function detectTvMode() {
    try {
      if (typeof window !== 'undefined') {
        if (window.__NEEW_TV_DISABLED__ === true) return false;
        if (window.__NEEW_TV_MODE__ === true) return true;
      }

      try {
        const sessionFlag = typeof sessionStorage !== 'undefined' ? sessionStorage.getItem('neew.tv.enabled') : null;
        const localFlag = typeof localStorage !== 'undefined' ? localStorage.getItem('neew.tv.enabled') : null;
        const localOff = typeof localStorage !== 'undefined' ? localStorage.getItem('neew.tv.disabled') : null;
        if (localOff === '1' || localOff === 'true') return false;
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
  if (!detectTvMode()) {
    try { console.debug('[TV][EXT][CT] disabled: non-TV mode'); } catch (_) {}
    return;
  }

  function ctEmit(text) {
    try { console.log(text); } catch (_) {}
    try { browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ type: 'tv-ext-log', text }); } catch (_) {}
  }

  function warmUp() {
    try {
      browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ type: 'tv-warmup' });
      ctEmit('[TV][EXT][CT] warmup sent');
    } catch (e) {
      try { console.warn('[TV][EXT][CT] warmup failed', e); } catch (_) {}
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', warmUp, { once: true });
  } else {
    warmUp();
  }

  const focusHandledIds = new Set();
  const scrollHandledIds = new Set();

  let lastHover = null;

  function sendMenuNavAck() {
    try {
      if (window.top === window) {
        browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ type: 'tv-menu-nav-ack' });
      } else {
        window.parent && window.parent.postMessage({ type: 'tv-menu-nav-ack' }, '*');
      }
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
    // Generic scrollable ancestor detection that works across a wide range of layouts.
    // We intentionally treat any non-"visible" overflow as potentially scrollable
    // and then require that the scrollable dimension actually has extra content.
    if (!el || el === document) {
      return document.scrollingElement || document.documentElement || document.body;
    }

    const root = document.scrollingElement || document.documentElement || document.body;
    let current = el;

    while (current && current !== document && current !== document.documentElement && current !== document.body) {
      const style = window.getComputedStyle(current);
      const overflowY = style.overflowY;
      const overflowX = style.overflowX;

      const scrollableY = overflowY && overflowY !== 'visible';
      const scrollableX = overflowX && overflowX !== 'visible';
      const canScrollY = scrollableY && (current.scrollHeight - current.clientHeight > 1);
      const canScrollX = scrollableX && (current.scrollWidth - current.clientWidth > 1);

      if (canScrollY || canScrollX) {
        return current;
      }

      current = current.parentElement || current.parentNode;
    }

    return root;
  }

  function clampToViewport(value, max) {
    return Math.max(0, Math.min(max, value));
  }

  function sendFocusDone(id, ok, used) {
    if (!id) return;
    try {
      if (window.top === window) {
        browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ id, type: 'focusAtPoint:done', ok, used });
      } else {
        window.parent && window.parent.postMessage({ type: 'tv-frame-focus:done', id, ok, used }, '*');
      }
    } catch (_) {}
  }

  function sendScrollDone(id, ok, used) {
    if (!id) return;
    try {
      if (window.top === window) {
        browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ id, type: 'scrollAtPoint:done', ok, used });
      } else {
        window.parent && window.parent.postMessage({ type: 'tv-frame-scroll:done', id, ok, used }, '*');
      }
    } catch (_) {}
  }

  function delegateToIframe(type, targetIframe, payload) {
    try {
      targetIframe.contentWindow && targetIframe.contentWindow.postMessage({ type, payload }, '*');
      return true;
    } catch (_) {
      return false;
    }
  }

  function findIframeUnderPoint(x, y) {
    const stack = (document.elementsFromPoint && document.elementsFromPoint(x, y)) || [];
    return stack.find(node => node && node.tagName === 'IFRAME');
  }

  async function handleFocusAtPoint(msg) {
    const { id, x = 0, y = 0, dpr, local } = msg;
    if (!local && window.top !== window) return;
    if (id && focusHandledIds.has(id)) return;
    if (id) focusHandledIds.add(id);

    const devicePixelRatio = Number(dpr) || window.devicePixelRatio || 1;
    const xCss = clampToViewport(x / devicePixelRatio, window.innerWidth - 1);
    const yCss = clampToViewport(y / devicePixelRatio, window.innerHeight - 1);

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

    if (target.nodeType === Node.TEXT_NODE) {
      target = target.parentElement;
    }

    if (target) {
      if (target.tabIndex < 0) {
        target.setAttribute('tabindex', '0');
      }
      try {
        target.focus({ preventScroll: true });
      } catch (_) {}
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

    // First try on the provided node
    if (attempt(node)) {
      return true;
    }

    // If it didn't move, escalate once to a higher scrollable ancestor.
    // This helps with deeply nested layouts where the immediate container
    // isn't actually the scroll owner.
    const parent = node.parentElement || node.parentNode;
    const higher = parent ? getScrollableAncestor(parent) : null;
    if (higher && higher !== node) {
      return attempt(higher);
    }

    // As a last resort, synthesize a wheel event so that sites with
    // custom JS-based scrollers (e.g., translateY animations) can
    // handle the scroll even when scrollTop does not change.
    try {
      const evt = new WheelEvent('wheel', {
        bubbles: true,
        cancelable: true,
        deltaY: deltaY,
        deltaMode: 0
      });
      node.dispatchEvent(evt);
      return true;
    } catch (_) {
      try {
        const evt = document.createEvent('WheelEvent');
        // Some older engines require initWheelEvent, but GeckoView should
        // support the constructor path above. We keep this as a best-effort.
        if (evt && evt.initEvent) {
          evt.initEvent('wheel', true, true);
          node.dispatchEvent(evt);
          return true;
        }
      } catch (_) {}
    }

    return false;
  }

  function sendHoverEvent(target, clientX, clientY) {
    if (!target) return;
    const types = ['pointerover', 'mouseover', 'mousemove'];
    for (const type of types) {
      try {
        const evt = new MouseEvent(type, {
          bubbles: true,
          cancelable: true,
          view: window,
          clientX,
          clientY,
          button: 0
        });
        target.dispatchEvent(evt);
      } catch (_) {}
    }
  }

  function sendPointerMove(target, clientX, clientY) {
    if (!target) return;
    try {
      const evt = new PointerEvent('pointermove', {
        bubbles: true,
        cancelable: true,
        clientX,
        clientY,
        pointerId: 1,
        pointerType: 'mouse'
      });
      target.dispatchEvent(evt);
    } catch (_) {}
  }

  async function handleScrollAtPoint(msg) {
    const { id, x = 0, y = 0, dy = 0, dpr, local } = msg;
    if (!local && window.top !== window) return;
    if (id && scrollHandledIds.has(id)) return;
    if (id) scrollHandledIds.add(id);

    const devicePixelRatio = Number(dpr) || window.devicePixelRatio || 1;
    const xCss = clampToViewport(x / devicePixelRatio, window.innerWidth - 1);
    const yCss = clampToViewport(y / devicePixelRatio, window.innerHeight - 1);

    const iframe = !local ? findIframeUnderPoint(xCss, yCss) : null;
    if (!local && iframe && delegateToIframe('tv-frame-scroll', iframe, { id, x: xCss, y: yCss, dy, dpr: devicePixelRatio, local: true })) {
      return;
    }

    const element = deepElementFromPoint(xCss, yCss);
    // First, handle YouTube playlist panel overlays explicitly. These often
    // use custom JS-driven scrolling, so we scroll the panel element itself
    // and let tryScrollElement fall back to synthesizing a wheel event.
    const playlistPanel = element && element.closest
      ? element.closest('ytd-playlist-panel-renderer, ytm-playlist-panel-renderer')
      : null;
    if (playlistPanel) {
      const movedPanel = tryScrollElement(playlistPanel, dy);
      const usedPanel = playlistPanel.tagName ? playlistPanel.tagName.toLowerCase() : 'playlist-panel';
      sendScrollDone(id, movedPanel, usedPanel);
      if (id) scrollHandledIds.delete(id);
      return;
    }

    // Generic + other YouTube containers
    // Try to normalize special YouTube playlist containers, but always
    // run the final candidate through getScrollableAncestor so we end up
    // scrolling the *actual* scrollable container (not just an inner div).
    const panel = element && element.closest ? element.closest('ytd-playlist-panel-renderer') : null;
    // Desktop playlist side panel: prefer the main items/contents container inside the panel
    const items = panel
      ? panel.querySelector('#items.playlist-items, #items, #contents')
      : null;
    // Mobile (ytm) variant: prefer the inner #items container if inside the mobile playlist panel
    const mPanel = element && element.closest ? element.closest('ytm-playlist-panel-renderer') : null;
    const mItems = mPanel
      ? mPanel.querySelector('#items.playlist-items, #items')
      : (element && element.closest ? element.closest('#items.playlist-items, #items') : null);

    const base = mItems || items || element || document.body;
    const target = getScrollableAncestor(base);

    // If the best scrollable ancestor is the root (html/body/scrollingElement),
    // we deliberately report failure so that the Android side can fall back to
    // its existing PanZoomController root scrolling. The content script focuses
    // purely on nested/element-level scrolling.
    const root = document.scrollingElement || document.documentElement || document.body;
    const isRootTarget = !target || target === root || target === document.documentElement || target === document.body;
    const moved = isRootTarget ? false : tryScrollElement(target, dy);

    let used = 'none';
    if (isRootTarget) {
      used = 'root';
    } else if (target && target.tagName) {
      used = target.tagName.toLowerCase();
    } else if (!target) {
      used = 'unknown';
    }

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
        const leaveEvt = new MouseEvent('mouseout', {
          bubbles: true,
          cancelable: true,
          clientX: xCss,
          clientY: yCss,
          button: 0
        });
        lastHover.dispatchEvent(leaveEvt);
      } catch (_) {}
    }
    lastHover = target;
    sendHoverEvent(target, xCss, yCss);
  }

  function handleMoveCursor(msg) {
    const { x = 0, y = 0, dpr } = msg || {};
    const devicePixelRatio = Number(dpr) || window.devicePixelRatio || 1;
    const xCss = clampToViewport(x / devicePixelRatio, window.innerWidth - 1);
    const yCss = clampToViewport(y / devicePixelRatio, window.innerHeight - 1);
    const target = deepElementFromPoint(xCss, yCss);
    if (!target) return;
    sendPointerMove(target, xCss, yCss);
  }

  function onMessage(evt) {
    const data = evt && evt.data;
    if (!data || typeof data !== 'object') return;
    if (data.type === 'tv-menu-nav') {
      sendMenuNavAck();
    } else if (data.type === 'tv-menu-nav-local') {
      sendMenuNavAck();
    } else if (data.type === 'tv-frame-focus' && data.payload) {
      handleFocusAtPoint({ ...data.payload, local: true });
    } else if (data.type === 'tv-frame-scroll' && data.payload) {
      handleScrollAtPoint({ ...data.payload, local: true });
    } else if (data.type === 'tv-frame-hover' && data.payload) {
      handleHoverAtPoint({ ...data.payload, local: true });
    } else if (data.type === 'tv-frame-move-cursor' && data.payload) {
      handleMoveCursor({ ...data.payload, local: true });
    }
  }

  window.addEventListener('message', onMessage, true);

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
