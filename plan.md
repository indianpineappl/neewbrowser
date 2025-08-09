# TV Mode: YouTube Sidebar Scrolling Fix — Plan

Owner: Cascade
Last updated: 2025-08-08
Target GeckoView version: v129

## Goals
- Make DPAD scrolling work on nested scroll containers (e.g., YouTube right sidebar) in TV mode.
- Keep existing cursor UI and DPAD behavior intact; changes must be additive and safe.
- Provide robust logging to diagnose edge cases and regressions.

## Milestones
1) Instrumentation & Diagnostics
2) JS Content-Script Scroll at Cursor (WebExtension)
3) Android wiring to send scrollAtPoint messages + fallback to PZC
4) Testing & Validation on YouTube and other sites
5) Rollout toggles, cleanup, and docs

---
## 1) Instrumentation & Diagnostics
Add structured, low-noise logs:
- dispatchKeyEvent (TV mode)
  - Log: keyCode, action, isUiFocused, cursor(left, top), currentStepSize
  - Log when at top/bottom edges and before simulateScroll
  - Log UI-focus transitions
- simulateScroll
  - Log session presence, delta, behavior used (PZC.scrollBy)
- onScrollChanged
  - Log scrollX, scrollY, dy, mCanScrollUp; bar hide/show decisions
- onLocationChange
  - Log URL, mCanScrollUp, updateFocusableRects() call

Deliverables:
- Precise Log.d(TAG, ...) statements guarded by isTvDevice() where appropriate
- Consistent prefixes: [TV][DPAD], [TV][SCROLL], [TV][EXT], [TV][FOCUS]

---
## 2) WebExtension: Content Script for Element-Level Scroll
Rationale: YouTube watch page uses multiple scroll containers. Root scrolling stops while sidebar should continue.

Extension structure (assets):
- assets/webext/manifest.json
- assets/webext/content.js

Manifest highlights:
- manifest_version: 2 (compatible with GV)
- content_scripts: matches: ["<all_urls>"] (or limit to YouTube for initial testing)
- run_at: document_idle

content.js responsibilities:
- Listen for messages (namespace: "tv.scroll").
- Command: scrollAtPoint { x, y, dy }
  - Convert x/y from CSS px (caller must provide CSS px or provide devicePixelRatio to convert)
  - const el = document.elementFromPoint(x, y)
  - Find nearest scrollable ancestor:
    - overflowY not 'visible' AND scrollHeight > clientHeight
  - Try el.scrollBy({ top: dy, behavior: 'auto' })
  - If none found, fallback to window.scrollBy({ top: dy, behavior: 'auto' })
  - Reply with { ok: true|false, used: 'element'|'window'|'none' }

Deliverables:
- Minimal extension packaged under assets and ensured via GeckoRuntime.getWebExtensionController().ensureBuiltIn(...)
- Message delegate wired per-session

---
## 3) Android Wiring (MainActivity)
- Initialization
  - Ensure built-in webextension on runtime init
  - On session open/active, setMessageDelegate(session, delegate, "tv.scroll")

- DPAD Up/Down flow changes (TV-mode)
  - Compute cursor coordinates
  - Convert to CSS px before send (divide by devicePixelRatio; obtain via cached value from content.js or query per page)
  - Send message { cmd: 'scrollAtPoint', x, y, dy }
  - Await reply (with timeout ~120ms)
    - If success, consume event
    - If fail/timeout, fallback to PanZoomController.scrollBy

- Fallback path
  - Keep existing simulateScroll(scrollUp) using PanZoomController.scrollBy(ScreenLength.fromPixels(...))

- Logging
  - Log message send, payload, reply, and fallback usage

Deliverables:
- New fields: webExtension ref, message delegate
- Helper: sendScrollAtPoint(xCss, yCss, dyCss)
- Safe threading: UI changes on runOnUiThread

---
## 4) Testing & Validation
Test matrix:
- YouTube watch page (two-column)
  - Cursor on right sidebar: DPAD Down/Up scrolls sidebar independently
  - Cursor on left comments: DPAD Down/Up scrolls comments; at end, root/page scrolls appropriately
- Other long pages with single root scroll
- Pages with fixed headers and inner scroll areas

Checks:
- onScrollChanged fires when root scroll occurs; when element-level scroll occurs, we expect no root movement (acceptable)
- No regressions to UI focus transitions
- No crashes on tab switch or navigation

---
## 5) Rollout & Controls
- Add a developer toggle in settings: "Element-targeted scrolling (TV)"
  - Default ON once validated; OFF allows quick rollback to root PZC scrolling only
- Metrics via logs; optional lightweight in-memory counters for success/fallback

---
## Compatibility: GeckoView v129
- GeckoSession.getPanZoomController(): OK
- PanZoomController.scrollBy(ScreenLength, ScreenLength, SCROLL_BEHAVIOR_AUTO): OK
- ScreenLength.fromPixels(float): OK
- WebExtension controller + built-in extension + message delegate: OK
- Use GeckoResult.then / exceptionally, avoid UI calls off main thread

---
## Risks & Mitigations
- Coordinate mismatch (CSS vs device px): add devicePixelRatio exchange and tests
- Extension not yet ready on first message: queue or fallback to PZC
- Some sites intercept wheel/scroll differently: add alternative path dispatching WheelEvent

---
## Implementation Checklist
[ ] Add logs per Section 1
[ ] Add assets/webext with manifest.json + content.js
[ ] Ensure extension at runtime init; store reference
[ ] Set message delegate per session (on open/active)
[ ] Implement sendScrollAtPoint with timeout and reply parsing
[ ] Modify DPAD Up/Down to try content-script path first, fallback to PZC
[ ] Add developer toggle to enable/disable element-targeted scrolling
[ ] Test matrix (YouTube + others) and iterate

---
## Rollback Plan
- Toggle OFF element-targeted scrolling in settings
- Retain current PZC root scrolling behavior only

---
## Open Questions / TODOs
- Decide whether to cache devicePixelRatio per page or query on each message
- Consider adding hit testing (if available) to enrich telemetry without blocking feature
- Tune dy magnitude for comfortable TV scrolling

---
## Next Up (Immediate Steps)
- Runtime/session wiring (Android):
  - ensureBuiltIn("resource://android/assets/webext/") during runtime init.
  - Register message delegate per GeckoSession under namespace "tv.scroll".
- DPR handling:
  - Query devicePixelRatio from content script once per page and cache.
  - Convert DPAD cursor device px -> CSS px for element hit-testing.
- DPAD integration:
  - On Up/Down at viewport edges: send {cmd: 'scrollAtPoint', x, y, dy}.
  - Use ~120ms timeout; on fail/timeout, fallback to PanZoomController.scrollBy.
- Logging:
  - [TV][EXT] send/receive payloads, results, and fallback decisions.
- Messaging interface reconciliation:
  - Ensure content script listens on the same channel used by Android ("tv.scroll") or adapt Android to the currently exposed handler.
