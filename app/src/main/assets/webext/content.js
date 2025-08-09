(function() {
  'use strict';

  // Remember the last successfully scrolled element to improve reverse-direction targeting
  let lastScrollableTarget = null;

  function isRootLike(node) {
    if (!node) return false;
    const root = document.scrollingElement || document.documentElement || document.body;
    return node === root || node === document.documentElement || node === document.body || node === document;
  }

  function canScrollInDirection(node, dy) {
    if (!node) return false;
    const sh = node.scrollHeight;
    const ch = node.clientHeight;
    const st = node.scrollTop;
    if (typeof sh !== 'number' || typeof ch !== 'number' || typeof st !== 'number') return false;
    if (dy > 0) return st + ch < sh;
    if (dy < 0) return st > 0;
    return false;
  }

  function scrollableChainFrom(el) {
    const chain = [];
    let cur = el;
    const seen = new Set();
    while (cur && cur !== document && !seen.has(cur)) {
      seen.add(cur);
      if (cur instanceof HTMLElement || cur instanceof Document) {
        const sc = getScrollableAncestor(cur);
        if (sc && (chain.length === 0 || chain[chain.length - 1] !== sc)) {
          chain.push(sc);
          if (isRootLike(sc)) break;
          // progress upwards from the scroll container
          cur = sc.parentElement || sc.parentNode;
          continue;
        }
      }
      cur = cur.parentElement || cur.parentNode;
    }
    // Ensure root is last
    const root = document.scrollingElement || document.documentElement || document.body;
    if (root && chain[chain.length - 1] !== root) chain.push(root);
    return chain;
  }

  function chooseTargetForDirection(el, dy) {
    const chain = scrollableChainFrom(el);
    // Prefer deepest that can move in requested direction
    for (let i = 0; i < chain.length; i++) {
      const node = chain[i];
      if (node && canScrollInDirection(node, dy)) return node;
    }
    return chain[0] || null;
  }

  /**
   * Finds the deepest element at a point, piercing through shadow DOMs.
   */
  function deepElementFromPoint(x, y) {
    // Adjust for visual viewport offset if present: elementFromPoint expects visual-viewport-relative coords
    const vv = window.visualViewport;
    const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
    const vy = vv ? (y - (vv.offsetTop || 0)) : y;
    let el = document.elementFromPoint(vx, vy);
    while (el && el.shadowRoot && el.shadowRoot.elementFromPoint(vx, vy)) {
      el = el.shadowRoot.elementFromPoint(vx, vy);
    }
    return el;
  }

  // Helper utilities (top-level)
  function describeNode(n) {
    if (!n || !n.tagName) return 'document';
    const id = n.id ? `#${n.id}` : '';
    const cls = n.className && typeof n.className === 'string' ? `.${n.className.split(/\s+/).filter(Boolean).join('.')}` : '';
    return `${n.tagName.toLowerCase()}${id}${cls}`;
  }

  function logAncestorDiagnostics(el) {
    try {
      let curr = el;
      let depth = 0;
      while (curr && depth < 6) {
        const cs = curr === document ? null : window.getComputedStyle(curr);
        const line = curr === document
          ? `document (vv.off=(${window.visualViewport?.offsetLeft||0},${window.visualViewport?.offsetTop||0}))`
          : `${describeNode(curr)} h=${curr.scrollHeight} ch=${curr.clientHeight} st=${curr.scrollTop} oy=${cs?.overflowY} ox=${cs?.overflowX}`;
        console.log(`[TV][EXT][CT] ancestor[${depth}]: ${line}`);
        curr = curr && curr !== document && (curr.assignedSlot || curr.parentElement || (curr.getRootNode() instanceof ShadowRoot && curr.getRootNode().host)) || document;
        depth++;
        if (curr === document) break;
      }
    } catch (e) {
      console.warn('[TV][EXT][CT] ancestor diagnostics failed:', e.message);
    }
  }

  function tryNativeScroll(target, dy) {
    if (!target) return false;
    const root = document.scrollingElement || document.documentElement || document.body;
    const beforeRootY = root ? root.scrollTop : window.scrollY;
    const before = typeof target.scrollTop === 'number' ? target.scrollTop : null;
    let changed = false;
    try {
      if (typeof target.scrollBy === 'function') {
        target.scrollBy({ top: dy, behavior: 'auto' });
      } else if (typeof target.scrollTop === 'number') {
        target.scrollTop = (before || 0) + dy;
      } else if (target === window || target === document || target === document.body) {
        window.scrollBy(0, dy);
      }
    } catch (_) {
      // ignore
    }
    const after = typeof target.scrollTop === 'number' ? target.scrollTop : null;
    const afterRootY = root ? root.scrollTop : window.scrollY;
    changed = (after !== null && before !== null && after !== before) || afterRootY !== beforeRootY;
    return changed;
  }

  function dispatchWheelAt(x, y, dy) {
    const vv = window.visualViewport;
    const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
    const vy = vv ? (y - (vv.offsetTop || 0)) : y;
    const evt = new WheelEvent('wheel', {
      bubbles: true,
      cancelable: true,
      deltaY: dy,
      clientX: vx,
      clientY: vy,
    });
    const target = document.elementFromPoint(vx, vy);
    (target || document).dispatchEvent(evt);
  }

  /**
   * Finds the nearest ancestor of an element that is scrollable.
   */
  function getScrollableAncestor(el) {
    if (!el) return null;
    let current = el;
    while (current && current !== document.body && current !== document.documentElement) {
      // Fast path: content bigger than box
      const overflowsY = current.scrollHeight > current.clientHeight;
      const overflowsX = current.scrollWidth > current.clientWidth;

      // Probe-based detection: attempt to mutate scrollTop/scrollLeft
      let scrollable = false;
      if (overflowsY) {
        const before = current.scrollTop;
        current.scrollTop = before + 1;
        const changed = current.scrollTop !== before;
        // revert
        if (changed) current.scrollTop = before;
        scrollable = changed;
      } else if (overflowsX) {
        const beforeX = current.scrollLeft;
        current.scrollLeft = beforeX + 1;
        const changedX = current.scrollLeft !== beforeX;
        if (changedX) current.scrollLeft = beforeX;
        scrollable = changedX;
      }

      if (scrollable) {
        return current;
      }
      current = current.assignedSlot || current.parentElement || (current.getRootNode() instanceof ShadowRoot && current.getRootNode().host);
    }
    // If no scrollable ancestor found, consider the document's scrolling element
    const root = document.scrollingElement || document.documentElement || document.body;
    if (root && root.scrollHeight > root.clientHeight) {
      return root;
    }
    return null;
  }

  /**
   * Handles the 'scrollAtPoint' command from the background script.
   */
  async function handleScrollAtPoint(msg) {
    if (!msg || msg.cmd !== 'scrollAtPoint') return;
    const isLocal = !!msg.local; // true when delegated into this frame
    // Only the top frame should handle global coordinates. Subframes handle only delegated (local) requests.
    try { if (!isLocal && window.top !== window) return; } catch (_) { /* cross-origin, assume not top */ }

    const { id, x: xDev, y: yDev, dy, dpr: appDpr } = msg;
    let used = 'none';
    let success = false;

    try {
      const dpr = appDpr || window.devicePixelRatio || 1;
      let x = xDev / dpr;
      let y = yDev / dpr;
      // Clamp to viewport to ensure elementFromPoint works reliably
      x = Math.max(0, Math.min(window.innerWidth - 1, x));
      y = Math.max(0, Math.min(window.innerHeight - 1, y));

      const el = deepElementFromPoint(x, y);
      console.log(`[TV][EXT][CT] id=${id} | El at dev(${xDev},${yDev}) -> css(${x},${y}) with dpr ${dpr}: ${el ? el.tagName : 'null'}`);

      // If the hit element is an IFRAME (and this is the top frame handling a global request), delegate into it
      if (!isLocal && el && el.tagName === 'IFRAME') {
        const rect = el.getBoundingClientRect();
        const childX = x - rect.left;
        const childY = y - rect.top;
        let resolved = false;
        const doneHandler = (ev) => {
          const data = ev && ev.data;
          if (!data || data.type !== 'tv-frame-scroll:done' || data.id !== id) return;
          resolved = true;
          window.removeEventListener('message', doneHandler);
          try {
            browser.runtime.sendMessage({ id, type: 'scrollAtPoint:done', ok: true, used: data.used || 'iframe' });
          } catch (e) {
            console.error(`[TV][EXT][CT] id=${id} | Failed to send done after iframe delegation: ${e.message}`);
          }
        };
        window.addEventListener('message', doneHandler);
        try {
          (el.contentWindow || window).postMessage({ type: 'tv-frame-scroll', id, payload: { cmd: 'scrollAtPoint', local: true, id, x: childX, y: childY, dy, dpr } }, '*');
        } catch (e) {
          console.warn(`[TV][EXT][CT] id=${id} | postMessage to iframe failed: ${e.message}`);
          window.removeEventListener('message', doneHandler);
        }
        // Give the child a short window to respond; if not, continue in this frame as fallback
        await new Promise(r => setTimeout(r, 120));
        if (resolved) return; // child handled and parent already replied
        window.removeEventListener('message', doneHandler);
        console.log(`[TV][EXT][CT] id=${id} | iframe did not respond, continuing with local fallbacks`);
      }

      // Direction-aware selection: pick deepest ancestor that can move in requested direction
      let scrollTarget = chooseTargetForDirection(el, dy) || getScrollableAncestor(el);
      if (scrollTarget) {
        console.log(`[TV][EXT][CT] id=${id} | Scroll Target: ${scrollTarget.tagName || 'document'} (h=${scrollTarget.scrollHeight}, ch=${scrollTarget.clientHeight}, st=${scrollTarget.scrollTop})`);
      } else {
        console.log(`[TV][EXT][CT] id=${id} | No explicit scroll target, will try wheel/root`);
      }
      logAncestorDiagnostics(el);

      if (scrollTarget) {
        success = tryNativeScroll(scrollTarget, dy);
        used = scrollTarget.tagName ? scrollTarget.tagName.toLowerCase() : 'document';
        if (success && !isRootLike(scrollTarget)) {
          // Do not overwrite with root/html; keep memory of a nested container if any
          lastScrollableTarget = scrollTarget;
        }
      }
      // If no success and we're scrolling up, try the last known good target (e.g., nested container) before falling back
      if (!success && dy < 0 && lastScrollableTarget && document.contains(lastScrollableTarget)) {
        const st = lastScrollableTarget.scrollTop;
        if (typeof st === 'number' && st > 0) {
          const alt = tryNativeScroll(lastScrollableTarget, dy);
          if (alt) {
            success = true;
            used = lastScrollableTarget.tagName ? lastScrollableTarget.tagName.toLowerCase() : 'document';
          }
        }
      }

      if (!success) {
        const root = document.scrollingElement || document.documentElement || document.body;
        // try wheel on the hit element first for custom scrollers
        const nearTarget = scrollTarget || el;
        const nearBefore = nearTarget && typeof nearTarget.scrollTop === 'number' ? nearTarget.scrollTop : null;
        const rootBefore = root ? root.scrollTop : window.scrollY;
        dispatchWheelAt(x, y, dy);
        // await micro-delay to allow script handlers to run
        await new Promise(r => setTimeout(r, 40));
        const nearAfter = nearTarget && typeof nearTarget.scrollTop === 'number' ? nearTarget.scrollTop : null;
        const rootAfter = root ? root.scrollTop : window.scrollY;
        const movedNear = nearBefore !== null && nearAfter !== null && nearAfter !== nearBefore;
        const movedRoot = rootAfter !== rootBefore;
        success = movedNear || movedRoot;
        used = 'wheel';
        if (!success) {
          // wheel had no effect -> fall back to root scroll explicitly
          success = root ? tryNativeScroll(root, dy) : (window.scrollBy(0, dy), true);
          used = root ? (root.tagName ? root.tagName.toLowerCase() : 'document') : 'window';
          console.log(`[TV][EXT][CT] id=${id} | Wheel no-op, fallback root: ${used}, success=${success}`);
        } else {
          console.log(`[TV][EXT][CT] id=${id} | Wheel moved near=${movedNear} rootDelta=${rootAfter - rootBefore}`);
        }
      }
      console.log(`[TV][EXT][CT] id=${id} | Scrolled used=${used} success=${success}`);

    } catch (e) {
      console.error(`[TV][EXT][CT] id=${id} | Scroll error: ${e.message}`);
      window.scrollBy({ top: dy, behavior: 'instant' });
      used = 'window-on-error';
      success = false;

    } finally {
      // IMPORTANT: Always reply to avoid a timeout in the native app.
      if (!isLocal) {
        try {
          browser.runtime.sendMessage({ id, type: 'scrollAtPoint:done', ok: !!success, used });
        } catch (e) {
          console.error(`[TV][EXT][CT] id=${id} | CRITICAL: Failed to send 'done' message. ${e.message}`);
        }
      } else {
        // Local (delegated) handling: report back to parent via postMessage
        try {
          window.parent && window.parent.postMessage({ type: 'tv-frame-scroll:done', id, used, ok: !!success }, '*');
        } catch (e) {
          // ignore
        }
      }
    }
  }

  /**
   * Listen for messages from the background script.
   */
  browser.runtime.onMessage.addListener((msg) => {
    if (msg && msg.cmd === 'scrollAtPoint') {
      console.log(`[TV][EXT][CT] Received scrollAtPoint, id=${msg.id}`);
      // Don't await. Let it run in the background.
      handleScrollAtPoint(msg);
    }
    return false; // We are not using sendResponse, so return false.
  });

  // Listen for delegated frame scrolls
  window.addEventListener('message', (ev) => {
    const data = ev && ev.data;
    if (!data || data.type !== 'tv-frame-scroll' || !data.id || !data.payload) return;
    const { payload } = data;
    try {
      handleScrollAtPoint(payload);
    } catch (e) {
      try { window.parent && window.parent.postMessage({ type: 'tv-frame-scroll:done', id: data.id, used: 'frame-error' }, '*'); } catch (_) {}
    }
  });

  /**
   * Send a warm-up message to the background script to establish the connection
   * and let it know which tab is active.
   */
  function warmUp() {
    try {
      console.log('[TV][EXT][CT] Content script loaded. Sending tv-warmup.');
      browser.runtime.sendMessage({ type: 'tv-warmup' });
    } catch (e) {
      console.error(`[TV][EXT][CT] Warm-up failed: ${e.message}`);
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', warmUp, { once: true });
  } else {
    warmUp();
  }

})();
