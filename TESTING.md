# Automated Testing Setup (Indic Browser)

This document summarizes the end-to-end automated testing workflow for the project. It covers local runs, device/emulator usage, release gating via pre-tag script, and CI behavior on tag pushes.

## Overview

- UI and instrumentation tests: Espresso + UiAutomator
- Unit tests: JUnit4
- Device options:
  - Connected emulator/physical device (`connectedDebugAndroidTest`)
  - Gradle Managed Virtual Device (AOSP Pixel 6, API 33)
- Logcat is captured during instrumentation tests and analyzed for critical issues (FATAL EXCEPTION / ANR / process crash). The run fails if any are found.
- Pre-tag release script guarantees tests pass locally before any tag is created and pushed.
- CI runs on tag pushes (e.g., `1.8.7`) to verify on GitHub Actions and publish artifacts.

## Important Files

- App Gradle configuration: `app/build.gradle`
  - `testInstrumentationRunner`: `androidx.test.runner.AndroidJUnitRunner`
  - AndroidX Test deps: Espresso (core, intents, contrib), Runner/Rules, UiAutomator, Orchestrator
  - `testOptions.execution = 'ANDROIDX_TEST_ORCHESTRATOR'`
  - Managed device: `pixel6Api33`
- Test suites
  - `app/src/androidTest/java/com/neew/browser/SmokeTest.java`
  - `app/src/androidTest/java/com/neew/browser/BrowserFlowsTest.java`
  - `app/src/androidTest/java/com/neew/browser/TabSwitcherTest.java`
  - `app/src/androidTest/java/com/neew/browser/DownloadsFlowTest.java`
  - `app/src/androidTest/java/com/neew/browser/testutil/RecyclerViewItemCountAssertion.java`
  - `app/src/test/java/com/neew/browser/VersioningUnitTest.java`
- Scripts
  - Local runner: `scripts/run_android_tests.sh`
    - Flags: `--connected` to run on an already-running emulator/physical device
    - Artifacts: `scripts/test_artifacts/`
  - Pre-tag gate: `scripts/release_tag_with_tests.sh`
    - Usage: `scripts/release_tag_with_tests.sh 1.8.7 [--connected]`
- CI workflow
  - `.github/workflows/tag-tests.yml` (runs only on tag pushes matching `X.Y.Z`)

## Running Tests Locally

Prerequisites:
- JDK 17, Android SDK, and ADB in PATH
- Gradle wrapper provided in repo

Commands:
- Unit + instrumentation (managed device):
  ```bash
  chmod +x scripts/run_android_tests.sh
  ./scripts/run_android_tests.sh
  ```
- Unit + instrumentation (connected device/emulator):
  ```bash
  # Ensure an emulator or device is connected and shows as 'device' in `adb devices`
  ./scripts/run_android_tests.sh --connected
  ```

Artifacts:
- Reports and logcat saved to `scripts/test_artifacts/`
- Script fails if logcat contains critical issues

## Release Gating (Local)

Use the pre-tag script to enforce test success locally before creating/pushing a tag.

```bash
chmod +x scripts/release_tag_with_tests.sh
# With connected device/emulator
scripts/release_tag_with_tests.sh 1.8.7 --connected
# Or fallback to managed device automatically
scripts/release_tag_with_tests.sh 1.8.7
```

Behavior:
- Validates tag format `X.Y.Z`
- Ensures git working tree is clean
- Ensures tag does not already exist locally/remotely
- Runs full local test workflow (`scripts/run_android_tests.sh`)
- Creates and pushes the tag only if tests and logcat checks pass

## CI on Tag Push

- Triggers when pushing tags matching `X.Y.Z` (e.g., `1.8.7`):
  - `git tag 1.8.7 && git push origin 1.8.7`
- Workflow: `.github/workflows/tag-tests.yml`
  - Sets up Java and Android SDK
  - Runs `scripts/run_android_tests.sh`
  - Uploads `scripts/test_artifacts/**` as GitHub Actions artifacts

## Current UI Coverage (High-Level)

- `onCreate()/onResume()` stability and control bar visibility
- New Tab button behavior (no crash, GeckoView visible)
- Desktop Mode toggle via Settings panel (apply and remain stable)
- Immersive Mode toggle via Settings panel (apply and remain stable)
- Control bar hide/unhide via swipe gestures (UiAutomator)
- Downloads flow (navigate to DownloadsActivity, toolbar + list/empty state visible)
- Tab switcher: add new tab increases RecyclerView item count

## Extending Tests (Next Steps)

- Desktop mode: expose state/flag to assert UA or requestDesktopSite mode
- Immersive mode: assert window insets/flags
- Real download flow: navigate to a test URL and verify DownloadManager entry appears in the list
- Tab count from `MainActivity`: add visible indicator or navigate to TabSwitcher from Main and assert counts
- Add `TESTING.md` links or comments near controls in code for easier test hooks (IDs/matchers)

## Troubleshooting

- Managed device provisioning is slow the first time (system image download). Subsequent runs are faster.
- If `adb` is missing, the script skips logcat capture; install platform-tools and add to PATH.
- Failing due to logcat errors:
  - Check `scripts/test_artifacts/logcat_<timestamp>.txt`
  - Look for `FATAL EXCEPTION`, `ANR in`, or process crash lines
- Espresso Intents flakiness:
  - Ensure `Intents.init()`/`Intents.release()` used
  - If strict matching is needed, stub relevant intents
- UiAutomator swipes:
  - Adjust swipe distances/steps if device resolution differs significantly

## Maintenance Tips

- Keep AndroidX test libs in sync with project AGP.
- Update managed device API level as compile/target SDKs change.
- Prefer stable selectors: always add IDs to new UI elements that tests need to touch.
- Record flake-prone tests and consider `IdlingResource` for async operations.
