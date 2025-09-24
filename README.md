# NeewBrowser

NeewBrowser is a GeckoView-based Android browser focused on extreme privacy, no tracking, robust ad blocking, and a first-class Leanback/TV experience.

## Highlights

- **Extreme privacy, by default**
  - No analytics of your browsing. No background telemetry. No user tracking.
  - Third-party requests are minimized; user data never leaves the device. We never maintain or track your history of websites.

- **Built-in ad blocking**
  - Integrated uBlock Origin (MV2) as a bundled WebExtension for GeckoView.
  - Local filter lists with update support; effective against ads, trackers, and annoyances.

- **Smart User-Agent handling (PiP-friendly)**
  - Some sites disable Picture-in-Picture (PiP) on mobile web. NeewBrowser applies a desktop-like User-Agent where needed so PiP can run smoothly without site-imposed mobile restrictions.
  - UA overrides are applied conservatively and may be scoped per-domain to balance feature access and layout compatibility.

- **Powered by GeckoView**
  - Modern Mozilla engine with WebRender, excellent standards support, and strong media capabilities.
  - Carefully managed `GeckoSession` lifecycle for stability across app start/stop/resume.

- **TV mode (Leanback) experience**
  - DPAD navigation with a visible on-screen cursor for precise selection on big screens.
  - TV-optimized control bars (expanded/minimized) and remote-first interactions.
  - TV Scroll Helper WebExtension enables scrolling complex or nested scroll containers on sites that don’t respond to DPAD by default.

- **Advanced scrolling for complex pages**
  - Focuses the nearest scrollable element under the cursor when DPAD scrolling fails.
  - Falls back to simulated pointer/touch via Gecko’s PanZoom when needed.

- **Resume stability & black-screen mitigation**
  - Minimal, standards-aligned lifecycle: reattach session and call `setActive(true)` on resume; deactivate on pause/stop.
  - One-time post-resume probe (TV-aware) to recover only when the compositor truly failed to repaint.
  - Guard to ignore transient `about:blank` top-level location changes that can momentarily blank restored tabs.

## Build and Run

Prerequisites

- Android Studio Hedgehog or newer
- Android SDK 34+

Build

```bash
./gradlew assembleDebug
```

Install

```bash
./gradlew installDebug
```

Launch

```bash
adb shell monkey -p com.neew.browser -c android.intent.category.LAUNCHER 1
```

## Emulator Notes (if applicable)

- Some emulator GPU backends (notably on Apple Silicon hosts) can exhibit compositor glitches. If you encounter blank frames after resume:
  - Switch AVD Graphics to ANGLE or Software (Swiftshader) and cold boot the emulator.
  - Prefer testing on a physical device for final verification.

## Development Notes

- Remote debugging can be enabled in development via `GeckoRuntimeSettings`.
- uBlock Origin MV2 is bundled as an extension; ensure its assets are present and not LFS pointers.

## License

This project is currently proprietary to the NeewBrowser team unless stated otherwise.