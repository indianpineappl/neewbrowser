  function isToggleCandidateVisible(el) {
    try {
      if (!(el instanceof Element)) return false;
      // If our strict visibility says true, accept
      if (isVisible(el)) return true;
      // Otherwise, treat ARIA/menu-marked controls as eligible if in viewport and has size
      const marked = el.matches && el.matches('[aria-haspopup], [role="combobox"], .dropdown-toggle, [data-toggle], [data-bs-toggle]');
      if (!marked) return false;
      const r = el.getBoundingClientRect();
      const inViewport = r.bottom > 0 && r.right > 0 && r.left < (window.innerWidth||0) && r.top < (window.innerHeight||0);
      const hasSize = r.width > 1 && r.height > 1;
      return inViewport && hasSize;
    } catch(_) { return false; }
  }

  // Detect if we are in TV mode; allow explicit overrides via globals or storage.
  function detectTvMode() {
    try {
      // Explicit app-provided global takes precedence
      if (typeof window !== 'undefined') {
        if (window.__NEEW_TV_DISABLED__ === true) return false;
        if (window.__NEEW_TV_MODE__ === true) return true;
      }
      // Storage overrides for debugging
      try {
        const ss = (typeof sessionStorage !== 'undefined') ? sessionStorage.getItem('neew.tv.enabled') : null;
        const ls = (typeof localStorage !== 'undefined') ? localStorage.getItem('neew.tv.enabled') : null;
        const off = (typeof localStorage !== 'undefined') ? localStorage.getItem('neew.tv.disabled') : null;
        if (off === '1' || off === 'true') return false;
        if (ss === '1' || ss === 'true' || ls === '1' || ls === 'true') return true;
      } catch(_) {}
      // Heuristic UA hints for TV devices
      const ua = (typeof navigator !== 'undefined' && navigator.userAgent) ? navigator.userAgent : '';
      const tvHints = /(Android\s+TV|BRAVIA|AFTB|AFTM|AFTS|AFT|SmartTV|Tizen|Web0S|WebOS|AppleTV|Chromecast|Roku|Shield|MiBOX|\bTV\b)/i;
      return !!tvHints.test(ua);
    } catch(_) { return false; }
  }

  // Label-based heuristic: find elements whose text/aria-label suggest seasons menu and choose a clickable ancestor
  function findToggleByLabelHeuristic(x, y) {
    try {
      const LABEL_RX = /(season|сезон|seasons|сезоны|temporada|serie|series|season\s*\d+)/i;
      const isClickable = (el) => {
        if (!el || !(el instanceof Element)) return false;
        if (el.matches && el.matches('button, a[href], [role="button"], [tabindex], [aria-haspopup], .dropdown-toggle')) return true;
        if (typeof el.onclick === 'function') return true;
        return false;
      };
      const chooseClickableAncestor = (n) => {
        let cur = n;
        while (cur && cur !== document && cur instanceof Element) {
          if (cur.matches && cur.matches(GENERIC_TOGGLE_CAND_SEL)) return cur;
          if (isClickable(cur)) return cur;
          const host = (cur.getRootNode && cur.getRootNode() instanceof ShadowRoot) ? cur.getRootNode().host : null;
          cur = cur.assignedSlot || cur.parentElement || host;
        }
        return null;
      };
      const vv = window.visualViewport;
      const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
      const vy = vv ? (y - (vv.offsetTop || 0)) : y;
      const stack = (document.elementsFromPoint && document.elementsFromPoint(vx, vy)) || [];
      for (const n of stack) {
        if (!(n instanceof Element)) continue;
        const txt = ((n.getAttribute && (n.getAttribute('aria-label') || '')) + ' ' + (n.textContent || '')).trim();
        if (LABEL_RX.test(txt)) {
          const pick = chooseClickableAncestor(n);
          if (pick && isToggleCandidateVisible(pick)) {
            try { ctEmit(`[TV][EXT][CT] label_heuristic pick=${describeNode(pick)} from=${describeNode(n)} txt="${txt.slice(0,60)}"`); } catch(_) {}
            return pick;
          }
        }
      }
      // Fallback: global search for nodes with matching text and choose nearest clickable ancestor
      const all = Array.from(document.querySelectorAll('body *'))
        .filter(el => el instanceof Element && el.textContent && LABEL_RX.test((el.getAttribute('aria-label')||'') + ' ' + el.textContent));
      let best = null; let bestD2 = 1e15;
      for (const n of all.slice(0, 500)) { // cap for safety
        const pick = chooseClickableAncestor(n);
        if (!pick || !isToggleCandidateVisible(pick)) continue;
        const r = pick.getBoundingClientRect();
        const cx = Math.max(r.left, Math.min(vx, r.right));
        const cy = Math.max(r.top, Math.min(vy, r.bottom));
        const dx = cx - vx, dy = cy - vy; const d2 = dx*dx + dy*dy;
        if (d2 < bestD2) { bestD2 = d2; best = pick; }
      }
      if (best) {
        try { ctEmit(`[TV][EXT][CT] label_heuristic global pick -> ${describeNode(best)} d2=${Math.round(bestD2)}`); } catch(_) {}
        return best;
      }
      try { ctEmit('[TV][EXT][CT] label_heuristic no match'); } catch(_) {}
      return null;
    } catch(_) {
      try { ctEmit('[TV][EXT][CT] label_heuristic error'); } catch(_) {}
      return null;
    }
  }
(function() {
  'use strict';

  // Gate: if not in TV mode, do nothing.
  try {
    // Since background registers/injects this only for TV, force-enable the flag
    try { if (typeof window !== 'undefined') { window.__NEEW_TV_MODE__ = true; } } catch(_) {}
    if (!detectTvMode()) {
      try { console && console.debug && console.debug('[TV][EXT][CT] disabled: non-TV mode'); } catch(_) {}
      return;
    }
  } catch(_) { return; }

  // Runtime flag to enable/disable heavier menu features
  let MENU_FEATURE_ENABLED = false;
  let MENU_DISABLE_TIMER = null;
  function enableMenuFeatures(ttlMs) {
    MENU_FEATURE_ENABLED = true;
    if (MENU_DISABLE_TIMER) { clearTimeout(MENU_DISABLE_TIMER); MENU_DISABLE_TIMER = null; }
    if (typeof ttlMs === 'number' && ttlMs > 0) {
      MENU_DISABLE_TIMER = setTimeout(() => { MENU_FEATURE_ENABLED = false; MENU_DISABLE_TIMER = null; }, Math.min(ttlMs, 5000));
    }
    try { ctEmit && ctEmit('[TV][EXT][CT] menu features ENABLED'); } catch(_) {}
  }
  function disableMenuFeatures() {
    MENU_FEATURE_ENABLED = false;
    if (MENU_DISABLE_TIMER) { clearTimeout(MENU_DISABLE_TIMER); MENU_DISABLE_TIMER = null; }
    try { ctEmit && ctEmit('[TV][EXT][CT] menu features DISABLED'); } catch(_) {}
  }

  // Emit a per-frame ready log to confirm injection and frame context
  try {
    const inFrame = (() => { try { return window.top !== window; } catch(_) { return true; } })();
    ctEmit && ctEmit(`[TV][EXT][CT] content_ready url=${location.href} inFrame=${inFrame}`);
  } catch(_) {}

  // Warmup: notify background so it notes this tab id for message forwarding
  try {
    setTimeout(() => {
      try { browser && browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ type: 'tv-warmup' }); } catch(_) {}
    }, 50);
  } catch(_) {}

  // Remember the last successfully scrolled element to improve reverse-direction targeting
  let lastScrollableTarget = null;

  function isRootLike(node) {
    if (!node) return false;
    const root = document.scrollingElement || document.documentElement || document.body;
    return node === root || node === document.documentElement || node === document.body || node === document;
  }

  // Guarded click listener: when host app sends a plain click (no focus message), try to open nearby dropdowns/combos.
  // This is a lightweight fallback and only triggers when we detect a plausible toggle near the click point.
  function focusNextMenuItem(menu, dir = 1) {
    if (!menu || !(menu instanceof Element)) return null;
    const items = Array.from(menu.querySelectorAll('a, button, [role="menuitem"], [role="option"], .dropdown-item, .mdc-list-item, .v-list-item, .ant-dropdown-menu-item'))
      .filter(el => el instanceof Element && !el.hasAttribute('disabled') && el.getAttribute('aria-disabled') !== 'true' && isToggleCandidateVisible(el));
    if (!items.length) return null;
    const active = document.activeElement && menu.contains(document.activeElement) ? document.activeElement : null;
    let idx = active ? items.indexOf(active) : -1;
    idx = (idx + (dir >= 0 ? 1 : -1));
    if (idx < 0) idx = 0;
    if (idx >= items.length) idx = items.length - 1;
    const target = items[idx];
    try { target.setAttribute('tabindex', '0'); } catch(_) {}
    try { target.focus({ preventScroll: true }); } catch(_) {}
    try { target.scrollIntoView({ block: 'nearest' }); } catch(_) {}
    try { ctEmit(`[TV][EXT][CT] menu_next dir=${dir} idx=${idx}/${items.length} -> ${describeNode(target)}`); } catch(_) {}
    return target;
  }

  try {
    let lastClickHandledAt = 0;
    document.addEventListener('click', async (ev) => {
      if (!MENU_FEATURE_ENABLED) return;
      try {
        if (!ev || !ev.isTrusted) return;
        const now = Date.now();
        if (now - lastClickHandledAt < 200) { // debounce
          try { ctEmit('[TV][EXT][CT] click_fallback skipped (debounce)'); } catch(_) {}
          return;
        }
        const x = ev.clientX, y = ev.clientY;
        // If already on or inside a visible menu, advance focus within the menu instead of reopening
        const menuUnder = (() => {
          const stack = (document.elementsFromPoint && document.elementsFromPoint(x, y)) || [];
          return stack.find(n => n instanceof Element && n.matches && n.matches(MENU_SELECTOR));
        })();
        if (menuUnder) {
          lastClickHandledAt = now;
          const next = focusNextMenuItem(menuUnder, +1) || menuUnder;
          if (next && next !== menuUnder) {
            await enforceFocusStick(next);
            try { ctEmit(`[TV][EXT][CT] click_fallback advance -> ${describeNode(next)}`); } catch(_) {}
          } else {
            try { ctEmit('[TV][EXT][CT] click_fallback advance no-op'); } catch(_) {}
          }
          return;
        }
        // Try exact toggle hit
        const TOGGLE_SEL = [
          '[data-toggle="dropdown"]', '[data-bs-toggle="dropdown"]', '.dropdown-toggle', '.seasons .dropdown-toggle', '.season .dropdown-toggle',
          '[aria-haspopup="listbox"]', '[aria-haspopup="menu"]', '#current-season', '.btn.btn-seasons.dropdown-toggle', '.btn-seasons'
        ].join(', ');
        let toggle = null;
        if (ev.target && ev.target.closest) toggle = ev.target.closest(TOGGLE_SEL);
        if (!toggle) toggle = findNearestSeasonsToggleNear(x, y);
        if (!toggle) toggle = findToggleByProximity(x, y);
        if (!toggle) toggle = findToggleByLabelHeuristic(x, y);
        // Remap non-clickable current-season span to its real toggle button within the same dropdown
        if (toggle && toggle.id === 'current-season') {
          try {
            const drop = toggle.closest && toggle.closest('.dropdown, .seasons, .season');
            const real = drop && drop.querySelector && drop.querySelector('.dropdown-toggle, button, [role="button"]');
            if (real) {
              try { ctEmit(`[TV][EXT][CT] remap current-season -> ${describeNode(real)}`); } catch(_) {}
              toggle = real;
            }
          } catch(_) {}
        }
        if (!toggle) return;
        const before = String(toggle.getAttribute('aria-expanded') || '').toLowerCase();
        // Don't spam if already expanded
        if (before === 'true') return;
        lastClickHandledAt = now;
        // Prefer keyboard activation to avoid click-away races
        try { toggle.focus && toggle.focus({ preventScroll: true }); } catch(_) {}
        dispatchKey(toggle, 'keydown', 'Enter', 'Enter');
        dispatchKey(toggle, 'keyup', 'Enter', 'Enter');
        dispatchKey(toggle, 'keydown', 'ArrowDown', 'ArrowDown');
        dispatchKey(toggle, 'keyup', 'ArrowDown', 'ArrowDown');
        try { ctEmit(`[TV][EXT][CT] click_fallback dispatched keys on ${describeNode(toggle)} at (${x},${y})`); } catch(_) {}
        // Briefly poll for a visible menu and focus its item
        const deadline = Date.now() + 900;
        let menu = null;
        while (!menu && Date.now() < deadline) {
          await new Promise(r => setTimeout(r, 80));
          // If a visible dropdown menu is under the pointer, advance focus to the next item and return early
          try {
            const stack = (document.elementsFromPoint && document.elementsFromPoint(x, y)) || [];
            const menuUnder = stack.find(n => n instanceof Element && n.matches && n.matches(MENU_SELECTOR));
            if (menuUnder) {
              // Only advance if there are focusable items
              const items = Array.from(menuUnder.querySelectorAll('a, button, [role="menuitem"], [role="option"], .dropdown-item, .mdc-list-item, .v-list-item, .ant-dropdown-menu-item'))
                .filter(el => el instanceof Element && !el.hasAttribute('disabled') && el.getAttribute('aria-disabled') !== 'true' && isToggleCandidateVisible(el));
              if (items.length > 0) {
                const next = focusNextMenuItem(menuUnder, +1) || items[0];
                await enforceFocusStick(next);
                try { ctEmit(`[TV][EXT][CT] focusAtPoint menu advance -> ${describeNode(next)}`); } catch(_) {}
                return;
              }
            }
          } catch(_) {}
          menu = findVisibleMenuNear(x, y) || findFloatingScrollableNear(x, y);
        }
        if (menu) {
          const item = focusMenuOrItem(menu) || menu;
          await enforceFocusStick(item);
          try { ctEmit(`[TV][EXT][CT] click_fallback menu -> ${describeNode(menu)} | focused=${describeNode(item)}`); } catch(_) {}
        } else {
          try { ctEmit('[TV][EXT][CT] click_fallback no menu'); } catch(_) {}
        }
      } catch(_) {}
    }, true);

    // Keyboard navigation within open menus: ArrowDown/ArrowUp/PageDown/PageUp/Home/End
    document.addEventListener('keydown', async (ev) => {
      if (!MENU_FEATURE_ENABLED) return;
      try {
        if (!ev || !ev.isTrusted) return;
        const key = ev.key;
        if (!key) return;
        if (!('ArrowDown ArrowUp PageDown PageUp Home End').includes(key)) return;
        // Determine active/visible menu context
        const active = document.activeElement;
        let menu = (active && active.closest && active.closest(MENU_SELECTOR)) || null;
        if (!menu) {
          // fallback: pick any visible menu
          const all = Array.from(document.querySelectorAll(MENU_SELECTOR));
          menu = all.find(m => m instanceof Element && isToggleCandidateVisible(m));
        }
        if (!menu) return;
        // Build items list for Home/End
        const items = Array.from(menu.querySelectorAll('a, button, [role="menuitem"], [role="option"], .dropdown-item, .mdc-list-item, .v-list-item, .ant-dropdown-menu-item'))
          .filter(el => el instanceof Element && !el.hasAttribute('disabled') && el.getAttribute('aria-disabled') !== 'true' && isToggleCandidateVisible(el));
        if (!items.length) return;
        let handled = false;
        if (key === 'Home') {
          try { items[0].setAttribute('tabindex','0'); items[0].focus({ preventScroll: true }); items[0].scrollIntoView({ block: 'nearest' }); await enforceFocusStick(items[0]); } catch(_) {}
          handled = true;
          try { ctEmit(`[TV][EXT][CT] menu_key key=Home -> ${describeNode(items[0])}`); } catch(_) {}
        } else if (key === 'End') {
          const last = items[items.length - 1];
          try { last.setAttribute('tabindex','0'); last.focus({ preventScroll: true }); last.scrollIntoView({ block: 'nearest' }); await enforceFocusStick(last); } catch(_) {}
          handled = true;
          try { ctEmit(`[TV][EXT][CT] menu_key key=End -> ${describeNode(last)}`); } catch(_) {}
        } else if (key === 'ArrowDown' || key === 'PageDown') {
          const steps = key === 'PageDown' ? 5 : 1;
          let target = null;
          for (let i = 0; i < steps; i++) target = focusNextMenuItem(menu, +1) || target;
          if (!target) target = items[Math.min(items.length - 1, 0)];
          if (target) { await enforceFocusStick(target); handled = true; try { ctEmit(`[TV][EXT][CT] menu_key key=${key} -> ${describeNode(target)}`); } catch(_) {} }
        } else if (key === 'ArrowUp' || key === 'PageUp') {
          const steps = key === 'PageUp' ? 5 : 1;
          let target = null;
          for (let i = 0; i < steps; i++) target = focusNextMenuItem(menu, -1) || target;
          if (!target) target = items[0];
          if (target) { await enforceFocusStick(target); handled = true; try { ctEmit(`[TV][EXT][CT] menu_key key=${key} -> ${describeNode(target)}`); } catch(_) {} }
        }
        if (handled) {
          try { ev.preventDefault(); ev.stopPropagation(); } catch(_) {}
        }
      } catch(_) {}
    }, true);
  } catch(_) {}

  // Message-driven menu navigation for DPAD events that don't reach the page
  try {
    const EXT = (typeof browser !== 'undefined' ? browser : (typeof chrome !== 'undefined' ? chrome : null));
    if (EXT && EXT.runtime && EXT.runtime.onMessage && typeof EXT.runtime.onMessage.addListener === 'function') {
      // Helper to try menu navigation in THIS frame only
      async function tryMenuNavLocal(dir) {
        try {
          // Find a visible menu context
          const active = document.activeElement;
          let menu = (active && active.closest && active.closest(MENU_SELECTOR)) || null;
          // Reuse cached menu if still visible and recent
          const nowTs = (performance && performance.now) ? performance.now() : Date.now();
          if (!menu && TV_LAST_MENU_EL && (nowTs - TV_LAST_MENU_TS) < TV_MENU_CACHE_MS) {
            if (TV_LAST_MENU_EL.isConnected && isVisible(TV_LAST_MENU_EL)) {
              menu = TV_LAST_MENU_EL;
              try { ctEmit('[TV][EXT][CT] menu_nav using cached menu'); } catch(_) {}
            }
          }
          if (!menu) {
            const all = Array.from(document.querySelectorAll(MENU_SELECTOR));
            menu = all.find(m => m instanceof Element && isVisible(m));
          }
          // Seasons-specific explicit container fallback
          if (!menu) {
            const ss = document.querySelector('div.dropdown-menu.dropdown-menu-new.show');
            if (ss && isVisible(ss)) menu = ss;
          }
          if (!menu) return false;
          // Update cache
          TV_LAST_MENU_EL = menu;
          TV_LAST_MENU_TS = nowTs;
          const items = Array.from(menu.querySelectorAll('a, button, [role="menuitem"], [role="option"], .dropdown-item, .mdc-list-item, .v-list-item, .ant-dropdown-menu-item'))
            .filter(el => el instanceof Element && !el.hasAttribute('disabled') && el.getAttribute('aria-disabled') !== 'true' && isVisible(el));
          if (!items.length) return false;
          // Determine current index
          const activeEl = (document.activeElement && menu.contains(document.activeElement)) ? document.activeElement : null;
          let idx = activeEl ? items.indexOf(activeEl) : -1;
          if (idx < 0) {
            // Try a pre-highlighted item (e.g., .active)
            const highlighted = items.find(el => el.classList && (el.classList.contains('active') || el.getAttribute('aria-selected') === 'true'));
            idx = highlighted ? items.indexOf(highlighted) : 0;
          }
          // Compute target index
          let targetIdx = idx;
          switch (dir) {
            case 'home': targetIdx = 0; break;
            case 'end': targetIdx = items.length - 1; break;
            case 'pagedown': targetIdx = Math.min(items.length - 1, idx + 5); break;
            case 'pageup': targetIdx = Math.max(0, idx - 5); break;
            case 'up': case '-1': case 'prev': targetIdx = Math.max(0, idx - 1); break;
            case 'down': case '+1': case 'next': default: targetIdx = Math.min(items.length - 1, idx + 1); break;
          }
          const target = items[Math.max(0, Math.min(items.length - 1, targetIdx))];
          if (!target) return false;
          try { target.setAttribute('tabindex','0'); target.focus({ preventScroll: true }); target.scrollIntoView({ block: 'nearest' }); } catch(_) {}
          await enforceFocusStick(target);
          try { ctEmit(`[TV][EXT][CT] menu_nav msg dir=${dir} -> ${describeNode(target)}`); } catch(_) {}
          // Notify app/background to keep cursor in menu-mode window
          try {
            const EXT2 = (typeof browser !== 'undefined' ? browser : (typeof chrome !== 'undefined' ? chrome : null));
            if (EXT2 && EXT2.runtime && typeof EXT2.runtime.sendMessage === 'function') {
              EXT2.runtime.sendMessage({ type: 'tv-menu-nav-ack' });
            }
          } catch(_) {}
          return true;
        } catch(_) { return false; }
      }

      EXT.runtime.onMessage.addListener((msg, sender, sendResponse) => {
        (async () => {
          try {
            if (!msg) return;
            if (msg.type === 'menuProbe:arm') {
              const ttlMs = Math.max(0, Math.min(5000, Number(msg.ttlMs) || 1500));
              enableMenuFeatures(ttlMs);
              return;
            }
            if (msg.type === 'tv-menu-nav') {
              const dir = String(msg.dir || '').toLowerCase();
              const okHere = await tryMenuNavLocal(dir);
              if (okHere) return;
              try { ctEmit('[TV][EXT][CT] menu_nav msg but no visible menu (relaying to child iframes)'); } catch(_) {}
              // Relay to child iframes so the correct frame can handle it
              try {
                const iframes = Array.from(document.querySelectorAll('iframe'));
                for (const f of iframes) {
                  try { f.contentWindow && f.contentWindow.postMessage({ type: 'tv-menu-nav-local', dir }, '*'); } catch(_) {}
                }
              } catch(_) {}
              if (target) {
                try { target.setAttribute('tabindex','0'); target.focus({ preventScroll: true }); target.scrollIntoView({ block: 'nearest' }); } catch(_) {}
                await enforceFocusStick(target);
                try { ctEmit(`[TV][EXT][CT] menu_nav msg dir=${dir} -> ${describeNode(target)}`); } catch(_) {}
              }
            }
          } catch(_) {}
        })();
        return false; // no async response
      });
    }
  } catch(_) {}

  // Listen for cross-frame tv-menu-nav delivery within the page
  try {
    window.addEventListener('message', async (ev) => {
      try {
        const data = ev && ev.data;
        if (!data || data.type !== 'tv-menu-nav-local') return;
        const dir = String(data.dir || '').toLowerCase();
        const okHere = await tryMenuNavLocal(dir);
        if (!okHere) {
          // Optional: bubble up to parent if this frame also cannot handle
          try { window.parent !== window && window.parent.postMessage({ type: 'tv-menu-nav-local', dir }, '*'); } catch(_) {}
        }
      } catch(_) {}
    }, true);
  } catch(_) {}

  // Proximity-based generic toggle finder
  function findToggleByProximity(x, y) {
    try {
      const vv = window.visualViewport;
      const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
      const vy = vv ? (y - (vv.offsetTop || 0)) : y;

      // 1) Check elements directly under the point and their ancestors
      const stack = (document.elementsFromPoint && document.elementsFromPoint(vx, vy)) || [];
      for (const n of stack) {
        if (!(n instanceof Element)) continue;
        let cur = n;
        while (cur && cur !== document && cur instanceof Element) {
          if (cur.matches && cur.matches(GENERIC_TOGGLE_CAND_SEL) && isToggleCandidateVisible(cur)) {
            try { ctEmit(`[TV][EXT][CT] toggle proximity search result=${describeNode(cur)} (stack)`); } catch(_) {}
            return cur;
          }
          const host = (cur.getRootNode && cur.getRootNode() instanceof ShadowRoot) ? cur.getRootNode().host : null;
          cur = cur.assignedSlot || cur.parentElement || host;
        }
      }
      // 2) Global candidates, choose nearest, bias to ones mentioning "season"
      const all = Array.from(document.querySelectorAll(GENERIC_TOGGLE_CAND_SEL)).filter(isToggleCandidateVisible);
      if (!all.length) {
        try { ctEmit('[TV][EXT][CT] toggle proximity search result=none'); } catch(_) {}
        return null;
      }
      let best = null; let bestScore = 1e15;
      for (const t of all) {
        const r = t.getBoundingClientRect();
        const cx = Math.max(r.left, Math.min(vx, r.right));
        const cy = Math.max(r.top, Math.min(vy, r.bottom));
        const dx = cx - vx, dy = cy - vy; const d2 = dx*dx + dy*dy;
        const s = String(t.id || '') + ' ' + String(t.className || '') + ' ' + String(t.getAttribute('aria-label') || '') + ' ' + String(t.textContent || '');
        const hasSeason = /season/i.test(s);
        const score = d2 * (hasSeason ? 0.5 : 1);
        if (score < bestScore) { bestScore = score; best = t; }
      }
      const MAX_DIST = 360; // px
      if (!best || bestScore > (MAX_DIST * MAX_DIST)) {
        try { ctEmit('[TV][EXT][CT] toggle proximity search result=none'); } catch(_) {}
        try { logToggleDiagnostics(x, y); } catch(_) {}
        return null;
      }
      try { ctEmit(`[TV][EXT][CT] toggle proximity pick -> ${describeNode(best)} score=${Math.round(bestScore)}`); } catch(_) {}
      return best;
    } catch(_) {
      try { ctEmit('[TV][EXT][CT] toggle proximity search error'); } catch(_) {}
      return null;
    }
  }

  // Diagnostics: log why a toggle wasn't found
  function logToggleDiagnostics(x, y) {
    try {
      const vv = window.visualViewport;
      const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
      const vy = vv ? (y - (vv.offsetTop || 0)) : y;
      const CAND_SEL = [
        '[data-toggle="dropdown"]', '[data-bs-toggle="dropdown"]', '.dropdown-toggle', '.seasons .dropdown-toggle', '.season .dropdown-toggle', '.btn-seasons', '#current-season', '.btn.btn-seasons.dropdown-toggle'
      ].join(', ');
      const all = Array.from(document.querySelectorAll(CAND_SEL)).filter(isVisible);
      const total = all.length;
      let nearest = null; let bestD2 = 1e12;
      for (const t of all) {
        const r = t.getBoundingClientRect();
        const cx = Math.max(r.left, Math.min(vx, r.right));
        const cy = Math.max(r.top, Math.min(vy, r.bottom));
        const dx = cx - vx, dy = cy - vy; const d2 = dx*dx + dy*dy;
        if (d2 < bestD2) { bestD2 = d2; nearest = t; }
      }
      const d = nearest ? Math.round(Math.sqrt(bestD2)) : -1;
      const under = (document.elementsFromPoint && document.elementsFromPoint(vx, vy)) || [];
      const underStr = under.slice(0, 6).map(n => (n && n.tagName ? `${n.tagName.toLowerCase()}${n.id?('#'+n.id):''}${n.className?('.'+String(n.className).trim().split(/\s+/).slice(0,3).join('.')):''}` : String(n))).join(' > ');
      try { ctEmit(`[TV][EXT][CT] toggle_diag: cand_total=${total} nearest=${nearest?describeNode(nearest):'none'} dist=${d}px under=${underStr}`); } catch(_) {}
    } catch(_) {}
  }

  // Re-assert focus briefly if the page scripts steal it (common in custom UIs)
  async function enforceFocusStick(target, durationMs = 700) {
    try {
      if (!target || !(target instanceof Element)) return;
      const start = Date.now();
      // First assert immediately
      try { target.focus && target.focus({ preventScroll: true }); } catch(_) {}
      while (Date.now() - start < durationMs) {
        await new Promise(r => setTimeout(r, 60));
        if (document.activeElement === target) break;
        if (!document.contains(target) || !isVisible(target)) break;
        try { target.focus && target.focus({ preventScroll: true }); } catch(_) {}
      }
      try { ctEmit(`[TV][EXT][CT] focus_stick result active=${describeNode(document.activeElement)} target=${describeNode(target)}`); } catch(_) {}
    } catch(_) {}
  }

  function isInteractiveLeaf(node) {
    if (!node || node === document) return false;
    const tag = (node.tagName || '').toLowerCase();
    return tag === 'a' || tag === 'button' || tag === 'input' || tag === 'select' || tag === 'textarea';
  }

  function hasScrollableOverflow(cs) {
    if (!cs) return false;
    const oy = cs.overflowY || '';
    const ox = cs.overflowX || '';
    return oy === 'auto' || oy === 'scroll' || ox === 'auto' || ox === 'scroll';
  }

  function hasScrollbarsLike(node, cs) {
    if (!node) return false;
    const sh = node.scrollHeight, ch = node.clientHeight;
    const sw = node.scrollWidth, cw = node.clientWidth;
    if (typeof sh === 'number' && typeof ch === 'number' && sh > (ch + 1)) return true;
    if (typeof sw === 'number' && typeof cw === 'number' && sw > (cw + 1)) return true;
    // As a heuristic, fixed/max height with overflow hidden may still be JS-scroller
    if (cs) {
      const mh = cs.maxHeight, h = cs.height;
      if ((mh && mh !== 'none') || (h && h !== 'auto' && h !== '0px')) {
        return (typeof sh === 'number' && typeof ch === 'number' && sh > ch);
      }
    }
    return false;
  }

  function hasAriaScrollRole(node) {
    if (!node || !node.getAttribute) return false;
    const role = (node.getAttribute('role') || '').toLowerCase();
    return role === 'menu' || role === 'listbox' || role === 'tree' || role === 'grid' || role === 'dialog';
  }

  function ctEmit(text) {
    try { console.log(text); } catch (_) {}
    try { browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ type: 'tv-ext-log', text }); } catch (_) {}
  }

  // Announce that content script is active on this page
  try {
    const t = `[TV][EXT][CT] content_ready url=${location.href}`;
    console.log(t);
    browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ type: 'tv-warmup' });
    ctEmit(t);
  } catch (_) {}

  // Track handled focus IDs to dedupe background vs frame messages
  const handledFocusIds = new Set();
  // Menu probe gating: only run dropdown/menu heuristics when explicitly armed by host app
  let menuProbeActive = false;
  let menuProbeExpiresAt = 0;

  // Debounce maps to avoid repeated/accidental activations while scrolling
  const lastToggleActivation = new WeakMap();
  let lastAnyToggleActivationAt = 0;
  const TOGGLE_ACTIVATE_MIN_INTERVAL_MS = 600;

  function shouldActivateToggle(toggle, x, y) {
    try {
      if (!toggle || !(toggle instanceof Element)) return false;
      // Don't reopen if already expanded
      const exp = String(toggle.getAttribute('aria-expanded') || '').toLowerCase();
      if (exp === 'true') return false;
      // Proximity check
      const r = toggle.getBoundingClientRect();
      const cx = Math.max(r.left, Math.min(x, r.right));
      const cy = Math.max(r.top, Math.min(y, r.bottom));
      const dx = cx - x, dy = cy - y; const d2 = dx*dx + dy*dy;
      const MAX_DIST = 32; // px
      if (d2 > (MAX_DIST * MAX_DIST)) return false;
      // Debounce per-toggle and global
      const now = Date.now();
      const lastLocal = lastToggleActivation.get(toggle) || 0;
      if (now - lastLocal < TOGGLE_ACTIVATE_MIN_INTERVAL_MS) return false;
      if (now - lastAnyToggleActivationAt < TOGGLE_ACTIVATE_MIN_INTERVAL_MS) return false;
      lastToggleActivation.set(toggle, now);
      lastAnyToggleActivationAt = now;
      return true;
    } catch(_) { return false; }
  }

  // Listen for iframe delegation requests from parent frames
  try {
    window.addEventListener('message', (ev) => {
      const data = ev && ev.data;
      if (!data || data.type !== 'tv-frame-focus') return;
      // The payload contains { cmd: 'focusScrollableAtPoint', local: true, id, x, y, dpr }
      try { handleFocusAtPoint(data.payload); } catch (_) {}
    });
  } catch (_) {}

  // In the top frame, forward child iframe completion up to background
  try {
    let installedForwarder = false;
    const installForwarderIfTop = () => {
      try {
        const isTop = (window.top === window);
        if (!isTop || installedForwarder) return;
        installedForwarder = true;
        window.addEventListener('message', (ev) => {
          const data = ev && ev.data;
          if (!data || data.type !== 'tv-frame-focus:done') return;
          try { browser.runtime && browser.runtime.sendMessage && browser.runtime.sendMessage({ id: data.id, type: 'focusAtPoint:done', ok: !!data.ok, used: data.used || 'iframe' }); } catch (_) {}
        });
      } catch (_) {}
    };
    installForwarderIfTop();
  } catch (_) {}

  function isVisible(el) {
    if (!el) return false;
    const cs = window.getComputedStyle(el);
    const isJWMenu = el.matches && el.matches('.jw-settings-menu, .jw-menu, .jw-overlay, .jw-rightclick');
    const isMenuLike = el.matches && el.matches(MENU_SELECTOR);
    const opacity = parseFloat(cs.opacity || '1');
    if (cs.display === 'none' || cs.visibility === 'hidden') return false;
    // Grace period for menus under fade-in/out animations
    if (!isJWMenu && !isMenuLike && opacity === 0) return false;
    const r = el.getBoundingClientRect();
    const inViewport = r.bottom > 0 && r.right > 0 && r.left < (window.innerWidth||0) && r.top < (window.innerHeight||0);
    const hasSize = r.width > 1 && r.height > 1;
    if (hasSize) return inViewport;
    // For menu-like containers with zero-size boxes, consider them visible if they contain visible menu items
    if (isMenuLike && inViewport) {
      try {
        const item = el.querySelector && el.querySelector(MENU_ITEM_SELECTOR);
        if (item) {
          const ir = item.getBoundingClientRect();
          if (ir.width > 1 && ir.height > 1 && ir.bottom > 0 && ir.right > 0 && ir.left < (window.innerWidth||0) && ir.top < (window.innerHeight||0)) {
            return true;
          }
        }
      } catch(_) {}
    }
    return false;
  }

  const MENU_SELECTOR = [
    '[role="menu"]', '[role="listbox"]', '[role="tree"]', '[role="grid"]', '[role="dialog"]',
    '.dropdown-menu', '.dropdown__menu', '.menu', '.menu-list', '.menu__list', '.mdc-menu', '.mdc-list',
    '.ant-dropdown-menu', '.ant-select-dropdown', '.select2-results', '.ui-menu', '.v-menu__content', '.mat-select-panel',
    '.popup', '.popover', '.context-menu', '.el-select-dropdown', '.el-dropdown-menu', '.tippy-box', '.rc-dropdown',
    // JW Player specific menus/overlays
    '.jw-settings-menu', '.jw-settings-submenu', '.jw-rightclick', '.jw-menu', '.jw-overlay'
  ].join(',');

  // Cache the last detected visible menu to reduce intermittent misses between DPAD presses
  let TV_LAST_MENU_EL = null;
  let TV_LAST_MENU_TS = 0;
  const TV_MENU_CACHE_MS = 1500;

  // Seasons-specific anchor items
  const SEASON_ITEM_SELECTOR = 'a.dropdown-item.ss-item[id^="ss-"] , a.dropdown-item.ss-item';

  // Generic toggle candidate selectors used for diagnostics and proximity search
  const GENERIC_TOGGLE_CAND_SEL = [
    // Bootstrap/ARIA
    '[data-bs-toggle="dropdown"]', '[data-toggle="dropdown"]', '.dropdown-toggle', '.dropdown > .dropdown-toggle',
    // Seasons-themed
    '.seasons .dropdown-toggle', '.season .dropdown-toggle', '.btn-seasons', '#current-season', '.btn.btn-seasons.dropdown-toggle',
    // ARIA menu/combobox/button
    'button[aria-haspopup]', '[role="button"][aria-haspopup]', '[aria-expanded]', '[aria-controls]', '[aria-haspopup="true"]', '[aria-haspopup="dialog"]',
    // Combobox/select UIs
    '[role="combobox"]', 'button[role="combobox"]', '[data-toggle="select"]', '.select2-selection', '.mdc-select__anchor', '.mat-select-trigger',
    // Common menu triggers
    '.menu-toggle', '.js-dropdown-toggle', '.dropdown__toggle', '[data-menu-toggle]','[data-toggle*="menu"]', '[data-action="toggle-menu"]',
    // Link-like buttons
    'a[role="button"]', 'a.dropdown-toggle'
  ].join(', ');

  // Find closest visible Seasons dropdown toggle near a point
  function findNearestSeasonsToggleNear(x, y) {
    try {
      const vv = window.visualViewport;
      const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
      const vy = vv ? (y - (vv.offsetTop || 0)) : y;
      const CAND_SEL = [
        '.btn.btn-seasons.dropdown-toggle', '.btn-seasons.dropdown-toggle', '.btn-seasons',
        '#current-season', '.seasons .dropdown-toggle', '.season .dropdown-toggle',
        '[data-bs-toggle="dropdown"]', '[data-toggle="dropdown"]'
      ].join(', ');
      const all = Array.from(document.querySelectorAll(CAND_SEL)).filter(isVisible);
      if (!all.length) return null;
      let best = null; let bestD2 = 1e12;
      for (const t of all) {
        const r = t.getBoundingClientRect();
        const cx = Math.max(r.left, Math.min(vx, r.right));
        const cy = Math.max(r.top, Math.min(vy, r.bottom));
        const dx = cx - vx, dy = cy - vy; const d2 = dx*dx + dy*dy;
        if (d2 < bestD2) { bestD2 = d2; best = t; }
      }
      // Only consider reasonably close
      const MAX_DIST = 140; // px
      if (best && bestD2 <= (MAX_DIST * MAX_DIST)) return best;
      return null;
    } catch(_) { return null; }
  }

  // Common interactive menu item selectors
  const MENU_ITEM_SELECTOR = [
    // Seasons dropdown specific (anchors like <a class="dropdown-item ss-item" id="ss-73">)
    'a.dropdown-item.ss-item[id^="ss-"]', 'a.dropdown-item.ss-item',
    '[role="option"]', '[role="menuitem"]', '[role="menuitemradio"]', '[role="menuitemcheckbox"]',
    '[role="treeitem"]', '[role="gridcell"]', '[role="row"] [role="gridcell"]',
    'li[tabindex]', 'button', 'a[href]', '[tabindex]'
  ].join(', ');

  // Find a visible seasons item near a point; return the item element (and optionally a container)
  function findSeasonsItemNear(x, y) {
    try {
      const vv = window.visualViewport;
      const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
      const vy = vv ? (y - (vv.offsetTop || 0)) : y;
      const stack = (document.elementsFromPoint && document.elementsFromPoint(vx, vy)) || [];
      // First, search under the point for a visible seasons item
      for (const n of stack) {
        if (!(n instanceof Element)) continue;
        const a = n.closest && n.closest(SEASON_ITEM_SELECTOR);
        if (a && isVisible(a)) {
          return a;
        }
      }
      // Fallback: global visible candidates, choose closest to the point, with distance threshold
      const all = Array.from(document.querySelectorAll(SEASON_ITEM_SELECTOR)).filter(isVisible);
      if (all.length === 0) return null;
      let best = null; let bestD2 = 1e12;
      for (const a of all) {
        const r = a.getBoundingClientRect();
        const cx = Math.max(r.left, Math.min(vx, r.right));
        const cy = Math.max(r.top, Math.min(vy, r.bottom));
        const dx = cx - vx, dy = cy - vy; const d2 = dx*dx + dy*dy;
        if (d2 < bestD2) { bestD2 = d2; best = a; }
      }
      // Only accept if reasonably close (avoid picking unrelated anchors on scroll)
      const MAX_DIST = 96; // px
      if (best && bestD2 <= (MAX_DIST * MAX_DIST)) {
        return best;
      }
      return null;
    } catch(_) {
      return null;
    }
  }

  // Focus a menu or the first interactive item within it for better DPAD behavior
  function focusMenuOrItem(menu) {
    if (!menu) return null;
    try {
      const items = Array.from(menu.querySelectorAll(MENU_ITEM_SELECTOR)).filter(isVisible);
      let pick = null;
      // Prefer explicitly selected/active items
      if (!pick && items.length) {
        const preferred = items.find(el => el.matches('[aria-selected="true"], .active, [aria-current]'));
        if (preferred) pick = preferred;
      }
      if (items.length > 0) {
        // Prefer the top-most visible item (common for pop lists), otherwise first
        pick = pick || items.sort((a, b) => a.getBoundingClientRect().top - b.getBoundingClientRect().top)[0];
      }
      const target = pick || menu;
      if (target && !target.hasAttribute('tabindex')) {
        const tag = (target.tagName || '').toLowerCase();
        // For native interactive items (anchors/buttons), prefer tabindex="0" so site scripts treat them as focusable-in-taborder
        if (tag === 'a' || tag === 'button') {
          target.setAttribute('tabindex', '0');
        } else {
          // For generic containers, use -1 to avoid affecting tab order
          target.setAttribute('tabindex', '-1');
        }
      }
      try { target && target.focus && target.focus({ preventScroll: true }); } catch(_) {}
      return target;
    } catch(_) {
      return null;
    }
  }

  // Fallback: detect any floating (absolute/fixed) visible scrollable container near a point
  function findFloatingScrollableNear(x, y) {
    const vv = window.visualViewport;
    const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
    const vy = vv ? (y - (vv.offsetTop || 0)) : y;
    const samples = (() => {
      const radius = 48, d = Math.round(radius/1.4);
      return [ [vx,vy],[vx+radius,vy],[vx-radius,vy],[vx,vy+radius],[vx,vy-radius], [vx+d,vy+d],[vx-d,vy+d],[vx+d,vy-d],[vx-d,vy-d] ];
    })();
    const seen = new Set();
    for (const [sx, sy] of samples) {
      const stack = (document.elementsFromPoint && document.elementsFromPoint(sx, sy)) || [];
      for (const n of stack) {
        if (!(n instanceof Element)) continue;
        let cur = n;
        let steps = 0;
        while (cur && cur !== document && steps < 6) {
          steps++;
          if (seen.has(cur)) { cur = cur.parentElement; continue; }
          seen.add(cur);
          try {
            const cs = window.getComputedStyle(cur);
            const pos = cs.position;
            if ((pos === 'absolute' || pos === 'fixed') && isVisible(cur)) {
              const scrollish = hasScrollableOverflow(cs) || hasScrollbarsLike(cur, cs) || (cur.scrollHeight > (cur.clientHeight + 1));
              if (scrollish) return cur;
            }
          } catch(_) {}
          cur = cur.parentElement;
        }
      }
    }
    return null;
  }

  // Attempt to open ARIA combobox/select-like widgets using keyboard, avoiding click-away closures
  function findNearbyCombobox(x, y) {
    const COMBO_SEL = [
      '[role="combobox"]',
      'input[role="combobox"]',
      'div[role="combobox"]',
      'button[role="combobox"]',
      'input[aria-haspopup="listbox"]',
      'div[aria-haspopup="listbox"]',
      '.ant-select', '.el-select', '.mat-select', '.select2-selection', '.rc-select', '.v-select'
    ].join(', ');
    let combo = null;
    try {
      const stack = (document.elementsFromPoint && document.elementsFromPoint(x, y)) || [];
      for (const n of stack) {
        if (!(n instanceof Element)) continue;
        combo = n.closest && n.closest(COMBO_SEL);
        if (combo) break;
      }
      if (!combo) {
        const radius = 48, d = Math.round(radius/1.4);
        const samples = [
          [x, y], [x+radius, y], [x-radius, y], [x, y+radius], [x, y-radius],
          [x+d, y+d], [x-d, y+d], [x+d, y-d], [x-d, y-d]
        ];
        outer: for (const [sx, sy] of samples) {
          const st = (document.elementsFromPoint && document.elementsFromPoint(sx, sy)) || [];
          for (const n of st) {
            if (!(n instanceof Element)) continue;
            const c = n.closest && n.closest(COMBO_SEL);
            if (c) { combo = c; break outer; }
          }
        }
      }
    } catch(_) {}
    return combo;
  }

  function dispatchKey(target, type, key, code) {
    try {
      const ev = new KeyboardEvent(type, { key, code, bubbles: true, cancelable: true });
      target.dispatchEvent(ev);
    } catch(_) {}
  }

  // Try to locate a visible menu container near a point after a toggle
  function findVisibleMenuNear(x, y) {
    const selectors = MENU_SELECTOR;
    const all = Array.from(document.querySelectorAll(selectors));
    const cands = all.filter(isVisible);
    try { ctEmit(`[TV][EXT][CT] findVisibleMenuNear: total=${all.length} visible=${cands.length}`); } catch(_) {}
    if (cands.length === 0) return null;
    // choose the container whose rect contains point or is closest to it; prefer scrollable
    let best = null; let bestDist = 1e12; let bestReason = '';
    for (const el of cands) {
      const r = el.getBoundingClientRect();
      const inside = (x >= r.left && x <= r.right && y >= r.top && y <= r.bottom);
      const cs = window.getComputedStyle(el);
      const scrollish = hasScrollableOverflow(cs) || hasScrollbarsLike(el, cs);
      const isJW = /\bjw-/.test((el.className || '').toString());
      const cx = Math.max(r.left, Math.min(x, r.right));
      const cy = Math.max(r.top, Math.min(y, r.bottom));
      const dx = cx - x, dy = cy - y; const d2 = dx*dx + dy*dy;
      try { ctEmit(`[TV][EXT][CT] menu_cand ${describeNode(el)} inside=${inside} d2=${Math.round(d2)} scroll=${scrollish} jw=${isJW} rect=${Math.round(r.left)},${Math.round(r.top)},${Math.round(r.width)}x${Math.round(r.height)}`); } catch(_) {}
      // Prefer JW menus even if not scrollable, if the point is inside
      if (inside && isJW) { best = el; bestReason = 'inside+JW'; break; }
      if (inside && scrollish) { best = el; bestReason = 'inside+scrollable'; break; }
      if (inside && !best) { best = el; bestReason = 'inside'; continue; }
      // Slightly bias JW menus when choosing closest
      const bias = isJW ? 0.8 : 1.0;
      const adj = d2 * bias;
      if (adj < bestDist) { bestDist = adj; best = el; bestReason = `closest d2=${Math.round(d2)} scroll=${scrollish} jw=${isJW}`; }
    }
    if (best) { try { ctEmit(`[TV][EXT][CT] menu_pick ${describeNode(best)} reason=${bestReason}`); } catch(_) {} }
    return best;
  }

  // Observe DOM for visible menu nodes appearing and log once in a while
  try {
    let observed = 0;
    const mo = new MutationObserver((mutations) => {
      if (observed >= 5) { mo.disconnect(); return; }
      for (const m of mutations) {
        for (const n of m.addedNodes || []) {
          if (!(n instanceof Element)) continue;
          if (n.matches && n.matches(MENU_SELECTOR) && isVisible(n)) {
            observed++;
            ctEmit(`[TV][EXT][CT] menu_node_added visible -> ${describeNode(n)} rect=${JSON.stringify(n.getBoundingClientRect())}`);
            if (observed >= 5) { mo.disconnect(); return; }
          }
          // Also check descendants quickly
          if (n.querySelector) {
            const found = Array.from(n.querySelectorAll(MENU_SELECTOR)).filter(isVisible);
            for (const f of found) {
              observed++;
              ctEmit(`[TV][EXT][CT] menu_descendant_visible -> ${describeNode(f)} rect=${JSON.stringify(f.getBoundingClientRect())}`);
              if (observed >= 5) { mo.disconnect(); return; }
            }
          }
        }
      }
    });
    mo.observe(document.documentElement || document.body, { childList: true, subtree: true });
    // Safety stop after 15s
    setTimeout(() => { try { mo.disconnect(); } catch(_){} }, 15000);
  } catch (_) {}

  // Prefer a container suitable for focusing before scrolling
  function findScrollableForFocus(fromEl) {
    let current = fromEl;
    const root = document.scrollingElement || document.documentElement || document.body;
    while (current && current !== document) {
      const host = (current.getRootNode && current.getRootNode() instanceof ShadowRoot) ? current.getRootNode().host : null;
      const parent = current.assignedSlot || current.parentElement || host;
      const cs = current === document ? null : window.getComputedStyle(current);
      if (current !== document && current !== root) {
        const overflowish = hasScrollableOverflow(cs);
        const scrollbars = hasScrollbarsLike(current, cs);
        const aria = hasAriaScrollRole(current);
        if ((overflowish && scrollbars) || (aria && scrollbars)) {
          // Avoid leaf focus; prefer the container even if the leaf is interactive
          return current;
        }
      }
      current = parent;
    }
    // As a fallback, pick what chooseTargetForDirection would pick without direction
    // Prefer non-root if possible
    const el = fromEl;
    const chain = scrollableChainFrom(el);
    for (let i = 0; i < chain.length; i++) {
      const n = chain[i];
      if (n && !isRootLike(n)) return n;
    }
    return root;
  }

  /**
   * Make an element programmatically focusable (if needed) and focus it without scrolling the page.
   * Returns the element that ended up focused, or null.
   */
  function ensureFocusableAndFocus(el) {
    if (!el) return null;
    try {
      let target = el;
      // Some containers are not focusable by default; give them a temporary tabindex
      if (target.tabIndex < 0) {
        target.setAttribute('tabindex', '0');
        target.setAttribute('data-tv-temp-tabindex', '1');
      }
      if (typeof target.focus === 'function') {
        target.focus({ preventScroll: true });
      }
      return target;
    } catch (_) {
      return null;
    }
  }

  /**
   * Focus the nearest scrollable ancestor at a given point, piercing iframes via delegation.
   */
  async function handleFocusAtPoint(msg) {
    if (!msg || msg.cmd !== 'focusScrollableAtPoint') return;
    const isLocal = !!msg.local;
    try { if (!isLocal && window.top !== window) return; } catch (_) {}

    const { id, x: xDev, y: yDev, dpr: appDpr } = msg;
    // Ensure defined to avoid ReferenceError in finally
    let shouldReportDoneFromHere = true;
    if (id && handledFocusIds.has(id)) {
      try { ctEmit(`[TV][EXT][CT] focusAtPoint id=${id} deduped (already handling)`); } catch(_) {}
      return;
    }
    if (id) handledFocusIds.add(id);
    let used = 'none';
    let ok = false;
    try {
      const dpr = appDpr || window.devicePixelRatio || 1;
      let x = xDev / dpr;
      let y = yDev / dpr;
      x = Math.max(0, Math.min(window.innerWidth - 1, x));
      y = Math.max(0, Math.min(window.innerHeight - 1, y));

      let el = deepElementFromPoint(x, y);
      let iframeUnderPoint = null;
      if (!isLocal) {
        try {
          // If top hit is or covers an iframe, find the iframe under the point (or nearby) to delegate accurately
          const stack = (document.elementsFromPoint && document.elementsFromPoint(x, y)) || [];
          for (const n of stack) {
            if (n && n.tagName === 'IFRAME' && n.contentWindow) { iframeUnderPoint = n; break; }
          }
          if (!iframeUnderPoint) {
            const radius = 36; // px
            const d = Math.round(radius / 1.4);
            const samples = [
              [x+radius, y], [x-radius, y], [x, y+radius], [x, y-radius],
              [x+d, y+d], [x-d, y+d], [x+d, y-d], [x-d, y-d]
            ];
            outer: for (const [sx, sy] of samples) {
              const st = (document.elementsFromPoint && document.elementsFromPoint(sx, sy)) || [];
              for (const n of st) {
                if (n && n.tagName === 'IFRAME' && n.contentWindow) { iframeUnderPoint = n; break outer; }
              }
            }
          }
          if (iframeUnderPoint) { try { ctEmit(`[TV][EXT][CT] frame_focus: using iframe under overlay ${describeNode(iframeUnderPoint)} id=${id}`); } catch(_) {} }
        } catch (_) {}
      }
      if (!isLocal && ((el && el.tagName === 'IFRAME' && el.contentWindow) || iframeUnderPoint)) {
        if (iframeUnderPoint) el = iframeUnderPoint;
        try { ctEmit(`[TV][EXT][CT] frame_focus: hit iframe=${describeNode(el)} src=${(el.getAttribute('src')||'').slice(0,120)} id=${id}`); } catch(_) {}
        const rect = el.getBoundingClientRect();
        const childX = x - rect.left;
        const childY = y - rect.top;
        try {
          (el.contentWindow || window).postMessage({ type: 'tv-frame-focus', id, payload: { cmd: 'focusScrollableAtPoint', local: true, id, x: childX, y: childY, dpr } }, '*');
          ctEmit(`[TV][EXT][CT] frame_focus delegated -> child (${Math.round(childX)},${Math.round(childY)}) id=${id}`);
        } catch (_) {}
        return;
      }

      // We will handle locally in this frame

      // JS-menu heuristics: prefer nearby known menu/list containers
      const selectorHeuristics = [
        '[role="menu"]', '[role="listbox"]', '[role="tree"]', '[role="grid"]', '[role="dialog"] .content',
        '.menu', '.dropdown-menu', '.dropdown__menu', '.menu-list', '.menu__list', '.mdc-menu', '.mdc-list',
        '.select2-results', '.ui-menu', '.ant-select-dropdown', '.ant-dropdown-menu', '.v-menu__content', '.mat-select-panel'
      ].join(',');
      let focusCand = null;
      try {
        if (el && el.closest) {
          const near = el.closest(selectorHeuristics);
          if (near && near.scrollHeight > (near.clientHeight + 1)) {
            focusCand = near;
          }
          try { if (near) console.log(`[TV][EXT][CT] heuristics matched near=${describeNode(near)}`); } catch (_) {}
        }
      } catch (_) {}

      // If no heuristic hit, fall back to general detection
      let scrollTarget = focusCand || findScrollableForFocus(el) || (document.scrollingElement || document.documentElement || document.body);
      try {
        const cs0 = scrollTarget && window.getComputedStyle(scrollTarget);
        const t = `[TV][EXT][CT] initial focus target=${describeNode(scrollTarget)} sh=${scrollTarget?.scrollHeight} ch=${scrollTarget?.clientHeight} oy=${cs0?.overflowY}`;
        console.log(t); ctEmit(t);
      } catch (_) {}

      const allowMenuProbe = menuProbeActive && (Date.now() <= menuProbeExpiresAt);
      // Special path: ARIA combobox/listbox style dropdowns (only when menu probe is active)
      let usedComboPath = false;
      let usedMenuPath = false;
      try {
        if (!allowMenuProbe) throw new Error('menu-probe-disabled');
        const vv = window.visualViewport;
        const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
        const vy = vv ? (y - (vv.offsetTop || 0)) : y;
        const combo = findNearbyCombobox(vx, vy);
        if (combo) {
          usedComboPath = true;
          const before = String(combo.getAttribute('aria-expanded') || '');
          ctEmit(`[TV][EXT][CT] combo_path target=${describeNode(combo)} aria-expanded(before)=${before}`);
          // Try keyboard-first to avoid click-away logic
          combo.focus && combo.focus({ preventScroll: true });
          dispatchKey(combo, 'keydown', 'Enter', 'Enter');
          dispatchKey(combo, 'keyup', 'Enter', 'Enter');
          // Also try ArrowDown in case Enter is ignored
          dispatchKey(combo, 'keydown', 'ArrowDown', 'ArrowDown');
          dispatchKey(combo, 'keyup', 'ArrowDown', 'ArrowDown');
          // Wait briefly, then poll for popup/listbox
          await new Promise(r => setTimeout(r, 120));
          let menu = null;
          const deadline = Date.now() + 900;
          const controls = (combo.getAttribute && combo.getAttribute('aria-controls')) || '';
          while (!menu && Date.now() < deadline) {
            // Check aria-controls first
            if (controls) {
              try {
                const m0 = document.getElementById(controls);
                if (m0 && isVisible(m0)) menu = m0;
              } catch(_) {}
            }
            if (!menu) {
              // Common popup containers near body
              const cands = Array.from(document.querySelectorAll('[role="listbox"], [role="menu"], .ant-select-dropdown, .select2-results, .el-select-dropdown, .mat-select-panel, .rc-select-dropdown'));
              menu = cands.find(isVisible) || null;
            }
            if (!menu) {
              // Fallback to spatial search
              menu = findVisibleMenuNear(vx, vy);
              if (!menu) menu = findFloatingScrollableNear(vx, vy);
            }
            if (menu) break;
            await new Promise(r => setTimeout(r, 80));
          }
          if (menu) {
            usedMenuPath = true;
            const item = focusMenuOrItem(menu) || menu;
            scrollTarget = item;
            await enforceFocusStick(item);
            ctEmit(`[TV][EXT][CT] combo_path popup -> ${describeNode(menu)} | focused=${describeNode(item)} activeNow=${describeNode(document.activeElement)}`);
          } else {
            // Seasons-specific fallback: directly focus visible seasons item near point
            const seasonsItem = findSeasonsItemNear(vx, vy);
            if (seasonsItem) {
              usedMenuPath = true;
              scrollTarget = seasonsItem;
              ensureFocusableAndFocus(seasonsItem);
              await enforceFocusStick(seasonsItem);
              ctEmit(`[TV][EXT][CT] combo_path seasons item fallback -> focused=${describeNode(seasonsItem)} activeNow=${describeNode(document.activeElement)}`);
            } else {
              ctEmit('[TV][EXT][CT] combo_path no popup after key sequence');
            }
          }
          const after = String(combo.getAttribute('aria-expanded') || '');
          ctEmit(`[TV][EXT][CT] combo_path aria-expanded(after)=${after}`);
        }
      } catch(_) {}

      // Even without a toggle, if a visible menu container exists near the point, prefer it (only when menu probe is active)
      try {
        if (!allowMenuProbe) throw new Error('menu-probe-disabled');
        const vv = window.visualViewport;
        const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
        const vy = vv ? (y - (vv.offsetTop || 0)) : y;
        let nearbyMenu = findVisibleMenuNear(vx, vy);
        if (!nearbyMenu) nearbyMenu = findFloatingScrollableNear(vx, vy);
        if (nearbyMenu) {
          usedMenuPath = true;
          const item = focusMenuOrItem(nearbyMenu) || nearbyMenu;
          scrollTarget = item;
          await enforceFocusStick(item);
          ctEmit(`[TV][EXT][CT] nearby visible menu prioritized -> ${describeNode(nearbyMenu)} | focused=${describeNode(item)} activeNow=${describeNode(document.activeElement)}`);
        } else {
          // Seasons-specific fallback if no menu container was detected
          const seasonsItem = findSeasonsItemNear(vx, vy);
          if (seasonsItem) {
            usedMenuPath = true;
            scrollTarget = seasonsItem;
            ensureFocusableAndFocus(seasonsItem);
            await enforceFocusStick(seasonsItem);
            ctEmit(`[TV][EXT][CT] nearby seasons item fallback -> focused=${describeNode(seasonsItem)} activeNow=${describeNode(document.activeElement)}`);
          }
        }
      } catch (_) {}

      // Disable generic synthetic hover to avoid unintended UI reveals during scrolling
      // (JW-specific fallback still does targeted hover when explicitly triggered)

      // Detect dropdown toggle buttons and click to open menu if closed (local handling only). Skip if combo path was used. (only when menu probe is active)
      try {
        if (!allowMenuProbe) throw new Error('menu-probe-disabled');
        if (usedComboPath) throw new Error('skip-toggle-due-combo-path');
        const TOGGLE_SEL = [
          '[data-toggle="dropdown"]', '[data-bs-toggle="dropdown"]', '.dropdown-toggle', '.seasons .dropdown-toggle', '.season .dropdown-toggle', '[aria-haspopup="listbox"]', '[aria-haspopup="menu"]',
          // JW Player buttons and toggles (kept for explicit JW flows)
          '.jw-settings', '.jw-toggle', '.jw-icon-settings', '[aria-label="Settings"]',
          // Seasons-specific toggles
          '#current-season', '.btn.btn-seasons.dropdown-toggle', '.btn-seasons'
        ].join(', ');
        let toggle = el && el.closest && el.closest(TOGGLE_SEL);
        // If the hit was the span#current-season, remap to its real toggle button for reliable activation
        if (toggle && toggle.matches && toggle.matches('#current-season')) {
          try {
            const root = toggle.closest('.dropdown, .btn-group, .seasons, .season, .seasons-wrapper') || document;
            const b = root.querySelector('.btn.btn-seasons.dropdown-toggle, button.btn.btn-seasons.dropdown-toggle');
            if (b) { ctEmit(`[TV][EXT][CT] remap toggle span#current-season -> ${describeNode(b)}`); toggle = b; }
          } catch(_) {}
        }
        // If not hit exactly, try proximity search within radius
        if (!toggle) {
          const radius = 56; // px
          const centerX = x, centerY = y;
          // Expand search by sampling a 3x3 grid (including diagonals)
          const d = Math.round(radius / 1.4);
          const samples = [
            [centerX, centerY],
            [centerX+radius, centerY], [centerX-radius, centerY], [centerX, centerY+radius], [centerX, centerY-radius],
            [centerX+d, centerY+d], [centerX-d, centerY+d], [centerX+d, centerY-d], [centerX-d, centerY-d]
          ];
          for (const [sx, sy] of samples) {
            for (const n of (document.elementsFromPoint(sx, sy) || [])) {
              if (!(n instanceof Element)) continue;
              const cand = n.closest && n.closest(TOGGLE_SEL);
              if (cand) { toggle = cand; break; }
            }
            if (toggle) break;
          }
          try { ctEmit(`[TV][EXT][CT] toggle proximity search result=${toggle ? describeNode(toggle) : 'none'}`); } catch(_) {}
          // Try seasons-specific nearest toggle fallback
          if (!toggle) {
            const nearT = findNearestSeasonsToggleNear(x, y);
            if (nearT) {
              toggle = nearT;
              try { ctEmit(`[TV][EXT][CT] seasons toggle nearest -> ${describeNode(toggle)}`); } catch(_) {}
            }
          }
          // Try generic proximity-based toggle finder (broader selectors, relaxed visibility)
          if (!toggle) {
            const prox = findToggleByProximity(x, y);
            if (prox) toggle = prox;
          }
          // Remap non-clickable current-season span to its real toggle button within the same dropdown
          if (toggle && toggle.id === 'current-season') {
            try {
              const drop = toggle.closest && toggle.closest('.dropdown, .seasons, .season');
              const real = drop && drop.querySelector && drop.querySelector('.dropdown-toggle, button, [role="button"]');
              if (real) {
                try { ctEmit(`[TV][EXT][CT] remap current-season -> ${describeNode(real)}`); } catch(_) {}
                toggle = real;
              }
            } catch(_) {}
          }
          // Label-based heuristic if still no toggle
          if (!toggle) {
            const lbl = findToggleByLabelHeuristic(x, y);
            if (lbl) toggle = lbl;
          }
          // JW fallback: if still no toggle, look for JW controlbar and only hover to reveal controls (no auto-click)
          if (!toggle) {
            try {
              const jwRoot = (el && el.closest && (el.closest('.jwplayer, .jw-controls, .jw-controlbar') || null)) ||
                             document.querySelector('.jwplayer, .jw-controls, .jw-controlbar');
              if (jwRoot) {
                ctEmit(`[TV][EXT][CT] jw_fallback root=${describeNode(jwRoot)}`);
                // Try to reveal controls by hovering near the bottom of the player
                try {
                  const rr = jwRoot.getBoundingClientRect();
                  const hx = Math.round(rr.left + rr.width / 2);
                  const hy = Math.round(rr.bottom - Math.min(20, rr.height * 0.1));
                  for (const et of ['pointerover','mouseover','mousemove']) {
                    const evt = new MouseEvent(et, { bubbles: true, cancelable: true, clientX: hx, clientY: hy });
                    jwRoot.dispatchEvent(evt);
                  }
                  ctEmit(`[TV][EXT][CT] jw_fallback hover to reveal controls at (${hx},${hy})`);
                } catch (_) {}
                const btnSelectors = [
                  '.jw-controls .jw-settings', '.jw-controlbar .jw-settings', '.jw-icon-settings', '[aria-label="Settings"]',
                  '.jw-icon-cc', '[aria-label*="Subtitles"]', '[aria-label*="Captions"]',
                  '.jw-icon-more', '[aria-label*="More"]'
                ];
                let btn = null;
                for (const sel of btnSelectors) {
                  btn = jwRoot.querySelector(sel);
                  if (btn) { ctEmit(`[TV][EXT][CT] jw_fallback found btn ${sel} -> ${describeNode(btn)}`); break; }
                }
                if (btn) {
                  const rbtn = btn.getBoundingClientRect();
                  const bx = Math.round(rbtn.left + rbtn.width / 2);
                  const by = Math.round(rbtn.top + rbtn.height / 2);
                  for (const et of ['pointerover','mouseover','mousemove']) {
                    const evt = new MouseEvent(et, { bubbles: true, cancelable: true, clientX: bx, clientY: by });
                    btn.dispatchEvent(evt);
                  }
                  try { btn.focus && btn.focus(); } catch (_) {}
                  ctEmit(`[TV][EXT][CT] jw_fallback hover-only on ${describeNode(btn)} at (${bx},${by})`);
                }
                // Poll for JW menus near the player center
                const rroot = jwRoot.getBoundingClientRect();
                const px = Math.round(rroot.left + rroot.width/2);
                const py = Math.round(rroot.top + rroot.height/2);
                await new Promise(r => setTimeout(r, 120));
                // First, try to find a visible menu inside the jwRoot itself
                let menu = null;
                try {
                  const localMenus = Array.from(jwRoot.querySelectorAll(MENU_SELECTOR));
                  for (const m of localMenus) { if (isVisible(m)) { menu = m; break; } }
                  ctEmit(`[TV][EXT][CT] jw_fallback localMenus visible=${!!menu}`);
                } catch (_) {}
                if (!menu) menu = findVisibleMenuNear(px, py);
                if (!menu) {
                  const deadline = Date.now() + 600;
                  while (!menu && Date.now() < deadline) {
                    await new Promise(r => setTimeout(r, 80));
                    // Re-check within JW root first, then global
                    try {
                      const localMenus2 = Array.from(jwRoot.querySelectorAll(MENU_SELECTOR));
                      menu = localMenus2.find(isVisible) || null;
                    } catch (_) {}
                    if (!menu) menu = findVisibleMenuNear(px, py);
                    try { ctEmit(`[TV][EXT][CT] jw_fallback polling -> ${menu ? describeNode(menu) : 'none'}`); } catch(_) {}
                  }
                }
                if (menu) {
                  scrollTarget = menu;
                  try {
                    // Make non-focusable containers focusable for a11y/DPAD
                    if (!menu.hasAttribute('tabindex')) menu.setAttribute('tabindex', '-1');
                    menu.focus && menu.focus();
                  } catch (_) {}
                  ctEmit(`[TV][EXT][CT] jw_fallback menu -> ${describeNode(menu)}`);
                } else {
                  ctEmit('[TV][EXT][CT] jw_fallback no menu after clicks');
                }
              }
            } catch (_) {}
          }
        }
        if (toggle) {
          const expandedBefore = String(toggle.getAttribute('aria-expanded') || '');
          try { const t = `[TV][EXT][CT] toggle detected=${describeNode(toggle)} aria-expanded(before)=${expandedBefore}`; console.log(t); ctEmit(t); } catch (_) {}
          // Guard: only activate if close to point and debounced
          const vv0 = window.visualViewport;
          const vx0 = vv0 ? (x - (vv0.offsetLeft || 0)) : x;
          const vy0 = vv0 ? (y - (vv0.offsetTop || 0)) : y;
          const isSeasonsToggle = toggle.matches && (toggle.matches('.btn.btn-seasons.dropdown-toggle') || toggle.matches('#current-season'));
          if (isSeasonsToggle || shouldActivateToggle(toggle, vx0, vy0)) {
            // Safer activation: focus then use keyboard events to open menus (avoid clicks on generic buttons/links)
            try { toggle.focus && toggle.focus({ preventScroll: true }); } catch(_) {}
            dispatchKey(toggle, 'keydown', 'Enter', 'Enter');
            dispatchKey(toggle, 'keyup', 'Enter', 'Enter');
            dispatchKey(toggle, 'keydown', 'ArrowDown', 'ArrowDown');
            dispatchKey(toggle, 'keyup', 'ArrowDown', 'ArrowDown');
            try { ctEmit(`[TV][EXT][CT] dispatched toggle keyboard (Enter, ArrowDown) on ${describeNode(toggle)}`); } catch(_) {}
          } else {
            try { ctEmit('[TV][EXT][CT] toggle activation skipped (debounced or too far)'); } catch(_) {}
          }
          // Two-phase wait: short delay then poll for up to 600ms
          const vv = window.visualViewport;
          const vx = vv ? (x - (vv.offsetLeft || 0)) : x;
          const vy = vv ? (y - (vv.offsetTop || 0)) : y;
          await new Promise(r => setTimeout(r, 140));
          let menu = findVisibleMenuNear(vx, vy) || findFloatingScrollableNear(vx, vy);
          if (!menu) {
            const deadline = Date.now() + 1200;
            while (!menu && Date.now() < deadline) {
              await new Promise(r => setTimeout(r, 80));
              menu = findVisibleMenuNear(vx, vy) || findFloatingScrollableNear(vx, vy);
              try { ctEmit(`[TV][EXT][CT] polling for menu -> ${menu ? describeNode(menu) : 'none'}`); } catch(_) {}
            }
          }
          // If pointer sequence did not reveal a menu and aria-expanded did not change, try a keyboard activation fallback (Enter/ArrowDown)
          if (!menu) {
            const expandedMid = String(toggle.getAttribute('aria-expanded') || '');
            if (expandedMid === expandedBefore) {
              try {
                dispatchKey(toggle, 'keydown', 'Enter', 'Enter');
                dispatchKey(toggle, 'keyup', 'Enter', 'Enter');
                dispatchKey(toggle, 'keydown', 'ArrowDown', 'ArrowDown');
                dispatchKey(toggle, 'keyup', 'ArrowDown', 'ArrowDown');
                ctEmit('[TV][EXT][CT] toggle keyboard fallback dispatched (Enter, ArrowDown)');
              } catch(_) {}
              const kbDeadline = Date.now() + 1200;
              while (!menu && Date.now() < kbDeadline) {
                await new Promise(r => setTimeout(r, 80));
                menu = findVisibleMenuNear(vx, vy) || findFloatingScrollableNear(vx, vy);
                try { ctEmit(`[TV][EXT][CT] kb-fallback polling for menu -> ${menu ? describeNode(menu) : 'none'}`); } catch(_) {}
              }
              // Seasons-specific guarded pointer activation if keyboard failed
              if (!menu && toggle.matches && toggle.matches('.btn.btn-seasons.dropdown-toggle')) {
                try {
                  const r2 = toggle.getBoundingClientRect();
                  const tx2 = Math.round(r2.left + r2.width / 2);
                  const ty2 = Math.round(r2.top + r2.height / 2);
                  for (const et of ['pointerover','mouseover','mousemove','pointerdown','mousedown','mouseup','pointerup','click']) {
                    const evt = new MouseEvent(et, { bubbles: true, cancelable: true, clientX: tx2, clientY: ty2, button: 0 });
                    toggle.dispatchEvent(evt);
                  }
                  ctEmit(`[TV][EXT][CT] seasons guarded click fallback on ${describeNode(toggle)} at (${tx2},${ty2})`);
                } catch(_) {}
                const clickDeadline = Date.now() + 1000;
                while (!menu && Date.now() < clickDeadline) {
                  await new Promise(r => setTimeout(r, 80));
                  menu = findVisibleMenuNear(vx, vy) || findFloatingScrollableNear(vx, vy);
                  try { ctEmit(`[TV][EXT][CT] seasons-click polling for menu -> ${menu ? describeNode(menu) : 'none'}`); } catch(_) {}
                }
              }
            }
          }
          if (menu) {
            usedMenuPath = true;
            const item = focusMenuOrItem(menu) || menu;
            scrollTarget = item;
            await enforceFocusStick(item);
            try { const t = `[TV][EXT][CT] visible menu found -> ${describeNode(menu)} | focused=${describeNode(item)} activeNow=${describeNode(document.activeElement)}`; console.log(t); ctEmit(t); } catch (_) {}
          } else {
            // Seasons-specific fallback: directly focus a visible seasons item near the point
            const seasonsItem = findSeasonsItemNear(vx, vy);
            if (seasonsItem) {
              usedMenuPath = true;
              scrollTarget = seasonsItem;
              ensureFocusableAndFocus(seasonsItem);
              await enforceFocusStick(seasonsItem);
              try { const t = `[TV][EXT][CT] toggle seasons item fallback -> focused=${describeNode(seasonsItem)} activeNow=${describeNode(document.activeElement)}`; console.log(t); ctEmit(t); } catch (_) {}
            } else {
              try { const t = '[TV][EXT][CT] no visible menu found after toggle events'; console.log(t); ctEmit(t); } catch (_) {}
            }
          }
          try { const t = `[TV][EXT][CT] aria-expanded(after)=${String(toggle.getAttribute('aria-expanded') || '')}`; console.log(t); ctEmit(t); } catch (_) {}
        }
      } catch (_) {}

      const focused = ensureFocusableAndFocus(scrollTarget);
      ok = !!focused;
      used = focused && focused.tagName ? focused.tagName.toLowerCase() : 'document';
      try {
        const t = `[TV][EXT][CT] focus final target=${describeNode(scrollTarget)} sh=${scrollTarget.scrollHeight} ch=${scrollTarget.clientHeight} oy=${window.getComputedStyle(scrollTarget)?.overflowY} ok=${ok} active=${describeNode(document.activeElement)}`;
        console.log(t); ctEmit(t);
      } catch (_) {}
    } catch (_) {
      ok = false;
    } finally {
      if (shouldReportDoneFromHere) {
        // Always report up to parent when running inside an iframe, regardless of origin of message.
        let inFrame = false;
        try { inFrame = (window.top !== window); } catch(_) { inFrame = true; }
        if (inFrame) {
          try { window.parent && window.parent.postMessage({ type: 'tv-frame-focus:done', id, ok, used }, '*'); } catch (_) {}
        } else {
          try { browser.runtime.sendMessage({ id, type: 'focusAtPoint:done', ok, used }); } catch (_) {}
        }
        if (id) handledFocusIds.delete(id);
      }
    }
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
        const host = String(location && location.host || '').toLowerCase();
        const protectLocalOnly = /(^|\.)youtube\.com$/.test(host);
        // If we have a non-root local target on YouTube, do NOT fall back to wheel/root.
        if (protectLocalOnly && scrollTarget && !isRootLike(scrollTarget)) {
          console.log(`[TV][EXT][CT] id=${id} | Protect local-only scroll on YouTube -> no root fallback`);
          used = describeNode(scrollTarget);
          // leave success=false so the app won't force a root scroll
        } else {
          const root = document.scrollingElement || document.documentElement || document.body;
          // prefer native scroll on the nearest target to minimize latency
          const nearTarget = scrollTarget || el;
          let movedNear = false;
          if (nearTarget) {
            try {
              movedNear = !!tryNativeScroll(nearTarget, dy);
            } catch(_) { movedNear = false; }
          }
          if (movedNear) {
            success = true;
            used = 'near-native';
          } else {
            const nearBefore = nearTarget && typeof nearTarget.scrollTop === 'number' ? nearTarget.scrollTop : null;
            const rootBefore = root ? root.scrollTop : window.scrollY;
            dispatchWheelAt(x, y, dy);
            // wait a frame to let scroll handlers/layout settle without adding noticeable latency
            await new Promise(r => (typeof requestAnimationFrame === 'function' ? requestAnimationFrame(() => r()) : setTimeout(r, 8)));
            const nearAfter = nearTarget && typeof nearTarget.scrollTop === 'number' ? nearTarget.scrollTop : null;
            const rootAfter = root ? root.scrollTop : window.scrollY;
            movedNear = nearBefore !== null && nearAfter !== null && nearAfter !== nearBefore;
            const movedRoot = rootAfter !== rootBefore;
            success = movedNear || movedRoot;
            used = 'wheel';
          }
          if (!success) {
            // wheel had no effect -> fall back to root scroll explicitly
            success = root ? tryNativeScroll(root, dy) : (window.scrollBy(0, dy), true);
            used = root ? (root.tagName ? root.tagName.toLowerCase() : 'document') : 'window';
            console.log(`[TV][EXT][CT] id=${id} | Wheel no-op, fallback root: ${used}, success=${success}`);
          } else {
            console.log(`[TV][EXT][CT] id=${id} | Wheel moved near=${movedNear} rootDelta=${rootAfter - rootBefore}`);
          }
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
    } else if (msg && msg.cmd === 'focusScrollableAtPoint') {
      console.log(`[TV][EXT][CT] Received focusScrollableAtPoint, id=${msg.id}`);
      // If we're inside an iframe, coerce to local handling so parent gets a frame-done message
      let inFrame = false;
      try { inFrame = (window.top !== window); } catch(_) { inFrame = true; }
      if (inFrame) {
        handleFocusAtPoint({ ...msg, local: true });
      } else {
        handleFocusAtPoint(msg);
      }
    } else if (msg && (msg.cmd === 'menuProbe:arm' || msg.type === 'menuProbe:arm')) {
      try {
        const ttlMs = Math.max(0, Math.min(5000, Number(msg.ttlMs) || 1500));
        enableMenuFeatures(ttlMs);
      } catch (_) {}
    } else if (msg && (msg.cmd === 'menuProbe:toggle' || msg.type === 'menuProbe:toggle')) {
      try {
        menuProbeActive = !menuProbeActive;
        if (menuProbeActive) {
          menuProbeExpiresAt = Date.now() + 1500; // initial active window
          console.log('[TV][EXT][CT] menuProbe toggled ON');
        } else {
          menuProbeExpiresAt = 0;
          console.log('[TV][EXT][CT] menuProbe toggled OFF');
        }
      } catch (_) {}
    } else if (msg && (msg.cmd === 'menuProbe:ping' || msg.type === 'menuProbe:ping')) {
      try {
        if (menuProbeActive) {
          menuProbeExpiresAt = Date.now() + 800; // extend while navigating
        }
      } catch (_) {}
    }
    return false; // We are not using sendResponse, so return false.
  });

  // Listen for delegated frame scrolls
  window.addEventListener('message', (ev) => {
    const data = ev && ev.data;
    if (!data || !data.id) return;
    if (data.type === 'tv-frame-scroll' && data.payload) {
      const { payload } = data;
      try { handleScrollAtPoint(payload); } catch (e) { try { window.parent && window.parent.postMessage({ type: 'tv-frame-scroll:done', id: data.id, used: 'frame-error' }, '*'); } catch (_) {} }
    } else if (data.type === 'tv-frame-focus' && data.payload) {
      const { payload } = data;
      try { handleFocusAtPoint(payload); } catch (e) { try { window.parent && window.parent.postMessage({ type: 'tv-frame-focus:done', id: data.id, used: 'frame-error' }, '*'); } catch (_) {} }
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
